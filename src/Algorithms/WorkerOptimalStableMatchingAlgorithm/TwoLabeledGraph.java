package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class TwoLabeledGraph {

    private Matching matching;
    // Edges' weights are 1 if strict, 0 otherwise.
    private SimpleDirectedWeightedGraph underlyingStrictGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
    private ArrayList<Integer> householdIDs = new ArrayList<Integer>();
    private Integer nil = -1;

    public TwoLabeledGraph(Matching matching, boolean findMax) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException {
        this.matching = matching;

        // Add vertices.
        underlyingStrictGraph.addVertex(nil); // _nil_ vertex.
        for (Household household : this.matching.getHouseholds()) {
            int householdID = household.getID();
            underlyingStrictGraph.addVertex(householdID);
            householdIDs.add(householdID);
        }

        this.wireHouseholds(householdIDs, findMax);
    }

    // TODO: Note somewhere that I use edgeweights denoting exactly the preference, rather than "1 = strict".
    //  Also note that this functionality is only implemented when findMax is true.
    //  When findMax is false, I just use edgeweights of 1.
    public void wireHouseholds(ArrayList<Integer> householdIDs, boolean findMax) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        if (findMax) {
            wireHouseholdsFindMax(householdIDs);
        } else {
            wireHouseholdsNormally(householdIDs);
        }
    }

    public void wireHouseholdsNormally(ArrayList<Integer> householdIDs) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        // Add edges. Types here refer to the first three types noted in the paper's description of the WOSMA-algorithm.
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(this.matching);

        for (Integer householdID : householdIDs) {
            float fitWithCurrentHouse;
            House currentHouse = matching.getHouseFromHousehold(householdID);

            if (currentHouse == null) {
                // Add type 3 edge, condition 1.
                underlyingStrictGraph.addEdge(nil, householdID);
                underlyingStrictGraph.setEdgeWeight(nil, householdID, 0);
                fitWithCurrentHouse = 0;
            } else {
                fitWithCurrentHouse = matchingEvaluator.evaluateIndividualTotalFit(currentHouse.getID(), householdID);
            }

            for (House otherHouse : this.matching.getHouses()) {
                if (currentHouse != null) {
                    if (otherHouse.getID() == currentHouse.getID()) {
                        continue;
                    }
                }
                float fitWithOtherHouse = matchingEvaluator.evaluateIndividualTotalFit(otherHouse.getID(), householdID);
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

    public void wireHouseholdsFindMax(ArrayList<Integer> householdIDs) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        // Add edges. Types here refer to the first three types noted in the paper's description of the WOSMA-algorithm.
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(this.matching);

        for (Integer householdID : householdIDs) {
            float fitWithCurrentHouse;
            House currentHouse = matching.getHouseFromHousehold(householdID);

            if (currentHouse == null) {
                // Add type 3 edge, condition 1.
                underlyingStrictGraph.addEdge(nil, householdID);
                underlyingStrictGraph.setEdgeWeight(nil, householdID, 0);
                fitWithCurrentHouse = 0;
            } else {
                fitWithCurrentHouse = matchingEvaluator.evaluateIndividualTotalFit(currentHouse.getID(), householdID);
            }

            float highScore = 0;

            for (House otherHouse : this.matching.getHouses()) {
                if (currentHouse != null) {
                    if (otherHouse.getID() == currentHouse.getID()) {
                        continue;
                    }
                }
                float fitWithOtherHouse = matchingEvaluator.evaluateIndividualTotalFit(otherHouse.getID(), householdID);
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

    public Integer getNil() {
        return nil;
    }

    public void updateAfterCycleExecution(Matching newMatching, boolean findMax) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException {
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
        wireHouseholds(householdIDs, findMax);
    }


    public List<Integer> findCycle(boolean findMax, boolean print) throws CycleFinder.FullyExploredVertexDiscoveredException, OutOfMemoryError {
        if (!findMax) {
            GabowStrongConnectivityInspector gabowStrongConnectivityInspector = new GabowStrongConnectivityInspector(underlyingStrictGraph);
            List<AsSubgraph<Integer, DefaultWeightedEdge>> components = gabowStrongConnectivityInspector.getStronglyConnectedComponents();
            List<Integer> cycle = null;
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
        } else {
            TarjanSimpleCycles<Integer, DefaultWeightedEdge> tarjanSimpleCycles
                    = new TarjanSimpleCycles<>(underlyingStrictGraph);
            try {
                List<List<Integer>> cycles = tarjanSimpleCycles.findSimpleCycles();
                if (print) { System.out.println("Tarjan found " + cycles.size() + " cycles."); }
                return findBestCycle(cycles);
            } catch (OutOfMemoryError e) {
                throw new OutOfMemoryError(e.getMessage());
            }
        }
    }

    public List<Integer> findBestCycle(List<List<Integer>> cycles) {
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

    public double calculateCycleScore(List<Integer> cycle) {
        Set<DefaultWeightedEdge> edges = new HashSet<DefaultWeightedEdge>();
        for (int i = 0; i < cycle.size(); i++) {
            int source = cycle.get(i);
            int target = cycle.get((i + 1) % cycle.size());
            DefaultWeightedEdge edge = (DefaultWeightedEdge) underlyingStrictGraph.getEdge(source, target);
            edges.add(edge);
        }
        return sumWeightOfEdges(edges);
    }

    public double sumWeightOfEdges(Set<DefaultWeightedEdge> edges) {
        double score = 0;
        for (DefaultWeightedEdge edge : edges) {
            score = score + this.underlyingStrictGraph.getEdgeWeight(edge);
        }
        return score;
    }
}
