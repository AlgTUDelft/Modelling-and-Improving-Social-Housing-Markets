package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Algorithms.AlgorithmStrategy;
import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;
import java.util.stream.Collectors;

public class TwoLabeledGraph {

    private Matching matching;
    // Edges' weights are 1 if strict, 0 otherwise.
    private SimpleDirectedWeightedGraph underlyingStrictGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
    private ArrayList<Integer> householdIDs;
    private Integer nil = -1;
    private AlgorithmStrategy algorithmStrategy;
    private HashMap<Integer, Integer> householdInitialHouseMap = new HashMap<Integer,Integer>();

    public TwoLabeledGraph(Matching matching, AlgorithmStrategy algorithmStrategy) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException {
        this.matching = matching;
        this.algorithmStrategy = algorithmStrategy;
        householdIDs = new ArrayList<Integer>(matching.getHouseholds().size());

        // Add vertices.
        underlyingStrictGraph.addVertex(nil); // _nil_ vertex.
        for (Household household : this.matching.getHouseholds()) {
            int householdID = household.getID();
            underlyingStrictGraph.addVertex(householdID);
            householdIDs.add(householdID);
        }

        if (algorithmStrategy == AlgorithmStrategy.WOSMA_IRCYCLES) {
            for (Household household : this.matching.getHouseholds()) {
                House house = this.matching.getHouseFromHousehold(household.getID());
                if (house != null && matching.getGrader().apply(house.getID(), household.getID()) > 0) {
                    householdInitialHouseMap.put(household.getID(), house.getID());
                }
            }
        }

        this.wireHouseholds(householdIDs);
    }

    // TODO: Note somewhere that I use edgeweights denoting exactly the preference, rather than "1 = strict".
    //  Also note that this functionality is only implemented when strategy is findMax.
    //  When strategy is regular, I just use edgeweights of 1.
    private void wireHouseholds(ArrayList<Integer> householdIDs) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        switch (algorithmStrategy) {
            case WOSMA_REGULAR: wireHouseholdsNormally(householdIDs); break;
            case WOSMA_FINDMAX: wireHouseholdsFindMax(householdIDs); break;
            case WOSMA_IRCYCLES: wireHouseholdsIRCycles(householdIDs); break;
        }
    }

    private void wireHouseholdsNormally(ArrayList<Integer> householdIDs) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        // Add edges. Types here refer to the first three types noted in the paper's description of the WOSMA-algorithm.

        for (Integer householdID : householdIDs) {
            House currentHouse = matching.getHouseFromHousehold(householdID);
            float fitWithCurrentHouse = addType3Cond1EdgeToHousehold(householdID, currentHouse);


            for (House otherHouse : this.matching.getHouses()) {
                if (currentHouse != null) {
                    if (otherHouse.getID() == currentHouse.getID()) {
                        continue;
                    }
                }
                float fitWithOtherHouse = this.matching.getGrader().apply(otherHouse.getID(), householdID);
                if (fitWithOtherHouse >= fitWithCurrentHouse) {
                    Household householdOfOtherHouse = this.matching.getHouseholdFromHouse(otherHouse.getID());
                    if (householdOfOtherHouse == null) {
                        // Add type 2 edge
                        underlyingStrictGraph.addEdge(householdID, nil);
                        if (fitWithOtherHouse > fitWithCurrentHouse) {
                            underlyingStrictGraph.setEdgeWeight(householdID, nil, 1);
                            break; // All nonzero weight values are treated the same since !findMax, so no need to continue.

                        } else { // fitWithOtherHouse == fitWithCurrentHouse
                            underlyingStrictGraph.setEdgeWeight(householdID, nil, 0);
                        }
                    } else {
                        // Add type 1 edge
                        underlyingStrictGraph.addEdge(householdID, householdOfOtherHouse.getID());
                        if (fitWithOtherHouse > fitWithCurrentHouse) {
                            underlyingStrictGraph.setEdgeWeight(householdID, householdOfOtherHouse.getID(), 1);
                        } else { // fitWithOtherHouse == fitWithCurrentHouse
                            underlyingStrictGraph.setEdgeWeight(householdID, householdOfOtherHouse.getID(), 0);
                        }
                    }
                }
            }
        }

        for (Integer householdID : householdIDs) {
            House currentHouse = matching.getHouseFromHousehold(householdID);
            // If the household does not own a house, then the following edge will already have been added.
            if (currentHouse != null) {
                if (sumWeightOfEdges(underlyingStrictGraph.incomingEdgesOf(householdID)) == 0) {
                    // Add type 3 edge, condition 2.
                    // If the above edge-additive process did not cause the current household to receive any incoming
                    // edges, then the reduced second condition -- there is no worker who strictly desires the current
                    // household's house -- is fulfilled, meaning the following edge should be added.
                    // Note that if underlyingStrictGraph.incomingEdgesOf(householdID).isEmpty(),
                    // then sumWeightOfEdges(underlyingStrictGraph.incomingEdgesOf(householdID)) == 0.
                    underlyingStrictGraph.addEdge(nil, householdID);
                    underlyingStrictGraph.setEdgeWeight(nil, householdID, 0);
                }
            }
        }
    }

    private void wireHouseholdsFindMax(ArrayList<Integer> householdIDs) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        // Add edges. Types here refer to the first three types noted in the paper's description of the WOSMA-algorithm.

        for (Integer householdID : householdIDs) {
            House currentHouse = matching.getHouseFromHousehold(householdID);
            float fitWithCurrentHouse = addType3Cond1EdgeToHousehold(householdID, currentHouse);

            float highScore = 0;

            for (House otherHouse : this.matching.getHouses()) {
                if (currentHouse != null) {
                    if (otherHouse.getID() == currentHouse.getID()) {
                        continue;
                    }
                }
                float fitWithOtherHouse = this.matching.getGrader().apply(otherHouse.getID(), householdID);
                if (fitWithOtherHouse >= fitWithCurrentHouse) {
                    Household householdOfOtherHouse = this.matching.getHouseholdFromHouse(otherHouse.getID());
                    if (householdOfOtherHouse == null) {
                        // Type 2 edge
                        if (fitWithOtherHouse > fitWithCurrentHouse && fitWithOtherHouse - fitWithCurrentHouse > highScore) {
                            highScore = fitWithOtherHouse - fitWithCurrentHouse;
                        }
                    } else {
                        // Add type 1 edge
                        if (fitWithOtherHouse > fitWithCurrentHouse) {
                            underlyingStrictGraph.addEdge(householdID, householdOfOtherHouse.getID());
                            underlyingStrictGraph.setEdgeWeight(householdID, householdOfOtherHouse.getID(), fitWithOtherHouse - fitWithCurrentHouse);
                        }
                    }
                }
            }
            // Add type 2 edge;
            if (highScore > 0) {
                underlyingStrictGraph.addEdge(householdID, nil);
                underlyingStrictGraph.setEdgeWeight(householdID, nil, highScore);
            }
        }

        for (Integer householdID : householdIDs) {
            House currentHouse = matching.getHouseFromHousehold(householdID);
            // If the household does not own a house, then the following edge will already have been added.
            if (currentHouse != null) {
                if (underlyingStrictGraph.incomingEdgesOf(householdID).isEmpty()) {
                    // Add type 3 edge, condition 2.
                    // If the above edge-additive process did not cause the current household to receive any incoming
                    // edges, then the reduced second condition -- there is no worker who strictly desires the current
                    // household's house -- is fulfilled, meaning the following edge should be added.
                    underlyingStrictGraph.addEdge(nil, householdID);
                    underlyingStrictGraph.setEdgeWeight(nil, householdID, 0);
                }
            }
        }
    }

    private void wireHouseholdsIRCycles(ArrayList<Integer> householdIDs) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        // Include edges between any household and any house that is either better than
        // their initial house, or which equals their initial house.
        // Weight of edge is improvement; may be negative.
        // In other words: edge existence is determined w.r.t. candidateHouse - initialHouse > 0,
        // but edge weight is determined w.r.t. candidateHouse - currentHouse.

        for (Integer householdID : householdIDs) {
            float initialFit = 0;
            House initialHouse = null;

            if (householdInitialHouseMap.containsKey(householdID)) {
                initialHouse = matching.getHouse(householdInitialHouseMap.get(householdID));
                initialFit = this.matching.getGrader().apply(initialHouse.getID(), householdID);
            }
            if (initialFit == 0){
                // Household initially had no house or had a worthless house, so may be 'moved into houselessness' by IR-Cycles.
                // Add type3 edge, condition 1.
                underlyingStrictGraph.addEdge(nil, householdID);
                underlyingStrictGraph.setEdgeWeight(nil, householdID, 0);
            }

            float currentFit = 0;
            House currentHouse = matching.getHouseFromHousehold(householdID);
            if (currentHouse != null) {
                currentFit = this.matching.getGrader().apply(currentHouse.getID(), householdID);
            }

            // HighScoreFree eventually represents the highest improvement that may be gained by moving
            // this household to some other householdless house, compared to the household's initial house.
            // If initialFit is X, then any candidate house must give improvement of strictly more than -1 + X.
            // We use initialfit here instead of currentfit,
            // because the minimum improvement between two houses is -1, so the minimum improvement that we'd want
            // before we'd violate the better-than-initial-house requirement, is equivalent to moving to the worst house
            // and then moving to our initial house, which is an improvement of at least -1 + initialFit.
            float highScoreFree = -1 + initialFit;

            for (House house : this.matching.getHouses()) {
                float candidateFit = this.matching.getGrader().apply(house.getID(), householdID);
                Household householdOfCandidateHouse = matching.getHouseholdFromHouse(house.getID());
                if (householdOfCandidateHouse != null && householdOfCandidateHouse.getID() != householdID) {
                    if (candidateFit > initialFit) {
                        // Add type 1 edge.
                        underlyingStrictGraph.addEdge(householdID, householdOfCandidateHouse.getID());
                        underlyingStrictGraph.setEdgeWeight(householdID, householdOfCandidateHouse.getID(), candidateFit - currentFit);
                    }
                } else {
                    if (candidateFit > initialFit && candidateFit - currentFit > highScoreFree && (currentHouse == null || house.getID() != currentHouse.getID())) {
                        highScoreFree = candidateFit - currentFit;
                    }
                }
            }

            // Add type 2 edge;
            if (highScoreFree > -1 + initialFit) {
                underlyingStrictGraph.addEdge(householdID, nil);
                underlyingStrictGraph.setEdgeWeight(householdID, nil, highScoreFree);
            } else if (initialFit == 0.0) { // However, with an initialFit of 0, we should be okay with moving elsewhere,
                // so long as there exists another unowned house.
                underlyingStrictGraph.addEdge(householdID, nil);
                underlyingStrictGraph.setEdgeWeight(householdID, nil, 0 - currentFit);
            }


        }

        // TODO: Keep this type3cond2-addition here?
        for (Integer householdID : householdIDs) {
            House currentHouse = matching.getHouseFromHousehold(householdID);
            // If the household does not own a house, then the following edge will already have been added.
            if (currentHouse != null) {
                if (underlyingStrictGraph.incomingEdgesOf(householdID).isEmpty()) {
                    // Add type 3 edge, condition 2.
                    // If the above edge-additive process did not cause the current household to receive any incoming
                    // edges, then the reduced second condition -- there is no worker who strictly desires the current
                    // household's house -- is fulfilled, meaning the following edge should be added.
                    underlyingStrictGraph.addEdge(nil, householdID);
                    underlyingStrictGraph.setEdgeWeight(nil, householdID, 0);
                }
            }

            // Add edge to initial house, which is always allowed with weight initialFit - currentFit.
            // Since initial house just misses the conditions for edges above,
            // we add it separately here, either as an edge to some household currenly owning initialHouse, or to nil.
            // However, if we already have some edge to nil, it means there is some other free house which we prefer.
            // In that case we needn't replace that edge with this lower one.
            if (householdInitialHouseMap.containsKey(householdID)) {
                House initialHouse = matching.getHouse(householdInitialHouseMap.get(householdID));
                float initialFit = this.matching.getGrader().apply(initialHouse.getID(), householdID);
                float currentFit = 0;
                if (currentHouse != null) {
                    currentFit = this.matching.getGrader().apply(currentHouse.getID(), householdID);
                }
                Household householdOwningInitialHouse = matching.getHouseholdFromHouse(initialHouse.getID());
                if (householdOwningInitialHouse != null && householdOwningInitialHouse.getID() != householdID) {
                    DefaultWeightedEdge edge = (DefaultWeightedEdge) underlyingStrictGraph.addEdge(householdID, householdOwningInitialHouse.getID());
                    underlyingStrictGraph.setEdgeWeight(edge, initialFit - currentFit);
                } else
                    if (!underlyingStrictGraph.containsEdge(householdID, nil) && (householdOwningInitialHouse == null || householdOwningInitialHouse.getID() != householdID)) {
                        DefaultWeightedEdge edge = (DefaultWeightedEdge) underlyingStrictGraph.addEdge(householdID, nil);
                        underlyingStrictGraph.setEdgeWeight(edge, initialFit - currentFit);
                    }
            }
        }
    }

    public Integer getNil() {
        return nil;
    }

    public void updateAfterCycleExecution(Matching newMatching) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException {
        this.matching = newMatching;

        // Remove all edges
        Set<DefaultWeightedEdge> edges = this.underlyingStrictGraph.edgeSet();
        ArrayList<DefaultWeightedEdge> edgesToRemove = new ArrayList<DefaultWeightedEdge>();
        for (DefaultWeightedEdge edge : edges) {
            edgesToRemove.add(edge);
        }
        for (DefaultWeightedEdge edge : edgesToRemove) {
            this.underlyingStrictGraph.removeEdge(edge);
        }
        // Rewire all households.
        ArrayList<Household> households = this.matching.getHouseholds();
        ArrayList<Integer> householdIDs = new ArrayList<Integer>(households.stream().map(h -> h.getID()).collect(Collectors.toList()));
        wireHouseholds(householdIDs);
    }


    public List<Integer> findCycle(boolean print) throws CycleFinder.FullyExploredVertexDiscoveredException, OutOfMemoryError, InterruptedException {
        List<Integer> cycle = null;
        switch (algorithmStrategy) {
            case WOSMA_REGULAR: cycle = findCycleRegular(print); break;
            case WOSMA_FINDMAX: cycle = findCycleFindMax(print); break;
            case WOSMA_IRCYCLES: cycle = findCycleIRCycles(print); break;
        }
        return cycle;
    }

    private List<Integer> findCycleRegular(boolean print) throws CycleFinder.FullyExploredVertexDiscoveredException, InterruptedException {
        List<Integer> cycle = null;
        GabowStrongConnectivityInspector gabowStrongConnectivityInspector = new GabowStrongConnectivityInspector(underlyingStrictGraph);
        List<AsSubgraph<Integer, DefaultWeightedEdge>> components = gabowStrongConnectivityInspector.getStronglyConnectedComponents();
        for (AsSubgraph<Integer, DefaultWeightedEdge> component : components) {
            if (component.vertexSet().size() > 1) {
                CycleFinder cycleFinder = new CycleFinder(component, this.matching.getHouseholdsMovedByWOSMA(), this.getNil());
                cycle = cycleFinder.findCycle();
                if (cycle != null) {
                    break;
                }
            }
        }
        return cycle;
    }

    private List<Integer> findCycleFindMax(boolean print) throws InterruptedException {
        List<Integer> cycle;
        // Not custom version because custom version has been modified to fit IR-Cycles.
        SzwarcfiterLauerSimpleCycles<Integer, DefaultWeightedEdge> szwarcfiterLauerSimpleCycles
                = new SzwarcfiterLauerSimpleCycles<>(underlyingStrictGraph);
        try {
            List<List<Integer>> cycles = szwarcfiterLauerSimpleCycles.findSimpleCycles();
            if (print) {
                System.out.println("SL found " + cycles.size() + " cycles.");
            }
            cycle = findBestCycle(cycles);
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError(e.getMessage());
        }
        return cycle;
    }

    private List<Integer> findBestCycle(List<List<Integer>> cycles) {
        if (cycles.isEmpty()) {
            return null;
        }
        // Of all cycles with the highest score...
        ArrayList<List<Integer>> cyclesWithHighestScore = new ArrayList<List<Integer>>();
        double bestScore = 0.0;
        for (List<Integer> cycle : cycles) {
            double candidateScore = calculateCycleScore(cycle);
            if (candidateScore > bestScore) {
                cyclesWithHighestScore.clear();
                cyclesWithHighestScore.add(cycle);
                bestScore = candidateScore;
            }
            if (candidateScore == bestScore) {
                cyclesWithHighestScore.add(cycle);
            }
        }

        // ...find that cycle which is the shortest.
        int shortestLength = cyclesWithHighestScore.stream().mapToInt(c -> c.size()).min().getAsInt();
        Optional<List<Integer>> opt = cyclesWithHighestScore.stream().filter(l -> l.size() == shortestLength).findAny();
        return opt.get(); // Value is present for sure.
    }

    private double calculateCycleScore(List<Integer> cycle) {
        Set<DefaultWeightedEdge> edges = new HashSet<DefaultWeightedEdge>();
        for (int i = 0; i < cycle.size(); i++) {
            int source = cycle.get(i);
            int target = cycle.get((i + 1) % cycle.size());
            DefaultWeightedEdge edge = (DefaultWeightedEdge) underlyingStrictGraph.getEdge(source, target);
            edges.add(edge);
        }
        return sumWeightOfEdges(edges);
    }

    private double sumWeightOfEdges(Set<DefaultWeightedEdge> edges) {
        double score = 0;
        for (DefaultWeightedEdge edge : edges) {
            score = score + this.underlyingStrictGraph.getEdgeWeight(edge);
        }
        return score;
    }

    private List<Integer> findCycleIRCycles(boolean print) throws InterruptedException {
        List<Integer> cycle = null;
        try {
            CustomSLSimpleCycles customSLSimpleCycles
                    = new CustomSLSimpleCycles(underlyingStrictGraph);
            cycle = customSLSimpleCycles.findSimpleCycle();
            if (print) { System.out.println("Denied cycles count: " + customSLSimpleCycles.getDeniedCyclesCount()); }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of Memory due to IR-Cycles");
            throw new OutOfMemoryError(e.getMessage());
        }
        return cycle;
    }

    private float addType3Cond1EdgeToHousehold(int householdID, House currentHouse) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException {
        float fitWithCurrentHouse;

        if (currentHouse == null) {
            // Add type 3 edge, condition 1.
            underlyingStrictGraph.addEdge(nil, householdID);
            underlyingStrictGraph.setEdgeWeight(nil, householdID, 0);
            fitWithCurrentHouse = 0;
        } else {
            fitWithCurrentHouse = this.matching.getGrader().apply(currentHouse.getID(), householdID);
        }
        return fitWithCurrentHouse;
    }

    public String toString() {
        return this.underlyingStrictGraph.toString();
    }

    public HashMap<Integer, Integer> getHouseholdInitialHouseMap() {
        return householdInitialHouseMap;
    }
}
