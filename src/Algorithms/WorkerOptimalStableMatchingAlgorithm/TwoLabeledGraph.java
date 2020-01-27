package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    //  Also note that this functionality is only used when findMax is true.
    public void wireHouseholds(ArrayList<Integer> householdIDs, boolean findMax) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
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
                        DefaultWeightedEdge edge;
                        if (fitWithOtherHouse > fitWithCurrentHouse) {
                            underlyingStrictGraph.addEdge(householdID, nil);
                            underlyingStrictGraph.setEdgeWeight(householdID, nil, fitWithOtherHouse - fitWithCurrentHouse);
                        } else if (!findMax) { // fitWithOtherHouse == fitWithCurrentHouse
                            underlyingStrictGraph.addEdge(householdID, nil);
                            underlyingStrictGraph.setEdgeWeight(householdID, nil, 0);
                        }
                    } else {
                        // Add type 1 edge
                        DefaultWeightedEdge edge;
                        if (fitWithOtherHouse > fitWithCurrentHouse) {
                            underlyingStrictGraph.addEdge(householdID, householdOfOtherHouse.getID());
                            underlyingStrictGraph.setEdgeWeight(householdID, householdOfOtherHouse.getID(), fitWithOtherHouse - fitWithCurrentHouse);
                        } else if (!findMax) { // fitWithOtherHouse == fitWithCurrentHouse
                            underlyingStrictGraph.addEdge(householdID, householdOfOtherHouse.getID());
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

    public void updateAfterCycleExecution(List<Integer> cycle, Matching newMatching, boolean findMax) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException {
        this.matching = newMatching;

        // First update the edges of the households that were present in the cycle and which have thus been moved.
        ArrayList<DefaultWeightedEdge> cycleEdgesToRemove = new ArrayList<DefaultWeightedEdge>();
        for (int householdID : cycle) {
            if (householdID != nil) {
                for (DefaultWeightedEdge edge : (Set<DefaultWeightedEdge>) this.underlyingStrictGraph.incomingEdgesOf(householdID)) {
                    cycleEdgesToRemove.add(edge);
                }
                for (DefaultWeightedEdge edge : (Set<DefaultWeightedEdge>) this.underlyingStrictGraph.outgoingEdgesOf(householdID)) {
                    cycleEdgesToRemove.add(edge);
                }
            }
        }
        for (DefaultWeightedEdge edge : cycleEdgesToRemove) {
            this.underlyingStrictGraph.removeEdge(edge);
        }

        ArrayList<Integer> cycleWithoutNil = new ArrayList<Integer>(cycle);
        cycleWithoutNil.remove(Integer.valueOf(nil));
        this.wireHouseholds(cycleWithoutNil, findMax);


        // Then, for each households w, re-check the edges w->nil
        // to see if there's still other preferred or equally-good houses for w.
        // Note that we technically needn't check the households that were present in the cycle,
        // because their edges are already A-OK due to the above _wireHouseholds_-call;
        // but it's little harm to re-check them anyway.
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);

        Set<DefaultWeightedEdge> edges = (Set<DefaultWeightedEdge>) this.underlyingStrictGraph.incomingEdgesOf(nil);
        ArrayList<DefaultWeightedEdge> edgesToRemove = new ArrayList<DefaultWeightedEdge>();
        for (DefaultWeightedEdge edge : edges) {
            int householdID = (int) this.underlyingStrictGraph.getEdgeSource(edge);
            float fitWithCurrentHouse;
            float highScore = 0;

            House currentHouse = matching.getHouseFromHousehold(householdID);

            if (currentHouse == null) {
                fitWithCurrentHouse = 0;
            } else {
                fitWithCurrentHouse = matchingEvaluator.evaluateIndividualTotalFit(currentHouse.getID(), householdID);
            }
            boolean foundOnlyWorseHouseholdlessHouses = true;
            for (House otherHouse : this.matching.getHouses()) {
                if (currentHouse != null) {
                    if (otherHouse.getID() == currentHouse.getID()) {
                        continue;
                    }
                }
                Household householdOfOtherHouse = this.matching.getHouseholdFromHouse(otherHouse.getID());
                if (householdOfOtherHouse == null) {
                    float fitWithOtherHouse = matchingEvaluator.evaluateIndividualTotalFit(otherHouse.getID(), householdID);
                    if (fitWithOtherHouse > fitWithCurrentHouse) {
                        foundOnlyWorseHouseholdlessHouses = false;
                        if (!findMax) {
                            this.underlyingStrictGraph.setEdgeWeight(edge, fitWithOtherHouse - fitWithCurrentHouse);
                            break; // Break, because we know this edge must be strict now;
                            // after all, we've definitively found a preferred house.
                        }
                        else {
                            if (fitWithOtherHouse - fitWithCurrentHouse > highScore) {
                                highScore = fitWithOtherHouse - fitWithCurrentHouse;
                            }
                        }
                    } else if (fitWithOtherHouse == fitWithCurrentHouse && !findMax) {
                        foundOnlyWorseHouseholdlessHouses = false;
                        this.underlyingStrictGraph.setEdgeWeight(edge, 0);
                        // No break, because we don't yet know if there is any house
                        // that this household strictly prefers.
                    }
                }
            }
            if (foundOnlyWorseHouseholdlessHouses) {
                edgesToRemove.add(edge);
            } else if (findMax) {
                this.underlyingStrictGraph.setEdgeWeight(edge, highScore);
            }
        }

        for (DefaultWeightedEdge edge : edgesToRemove) {
            this.underlyingStrictGraph.removeEdge(edge);
        }
    }

    public List<Integer> findCycle(boolean findMax, boolean print) throws CycleFinder.FullyExploredVertexDiscoveredException {
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
//            ArrayList<DefaultWeightedEdge> edges = new ArrayList<DefaultWeightedEdge>(this.underlyingStrictGraph.edgeSet());
//            Set<DefaultWeightedEdge> edgesToAdd = edges.stream().filter(e -> underlyingStrictGraph.getEdgeWeight(e) > 0).collect(toSet());
//            Set<DefaultWeightedEdge> edgesFromNil = edges.stream().filter(e -> underlyingStrictGraph.getEdgeSource(e) == nil).collect(toSet());
//            underlyingStrictGraph.removeAllEdges(edges);
//            for (DefaultWeightedEdge edge : edgesToAdd) {
//                int source = (int) underlyingStrictGraph.getEdgeSource(edge);
//                int target = (int) underlyingStrictGraph.getEdgeTarget(edge);
//                underlyingStrictGraph.addEdge(source, target, edge);
//            }
//            for (DefaultWeightedEdge edge : edgesFromNil) {
//                int source = (int) underlyingStrictGraph.getEdgeSource(edge);
//                int target = (int) underlyingStrictGraph.getEdgeTarget(edge);
//                underlyingStrictGraph.addEdge(source, target, edge); // Doesn't matter now that it's weight 0.
//            }
            TarjanSimpleCycles<Integer, DefaultWeightedEdge> tarjanSimpleCycles
                    = new TarjanSimpleCycles<>(underlyingStrictGraph);
            List<List<Integer>> cycles = tarjanSimpleCycles.findSimpleCycles();
            if (print) { System.out.println("Tarjan found " + cycles.size() + " cycles."); }
            return findBestCycle(cycles);
        }
    }

    public List<Integer> findBestCycle(List<List<Integer>> cycles) {
        double bestScore = 0.0;
        List<Integer> bestCycle = null;
        for (List<Integer> cycle : cycles) {
            double candidateScore = calculateCycleScore(cycle);
            if (candidateScore > bestScore) {
                bestScore = candidateScore;
                bestCycle = cycle;
            }
        }
        return bestCycle;
    }

    public double calculateCycleScore(List<Integer> cycle) {
        double score = 0;
        for (int i = 0; i < cycle.size(); i++) {
            int source = cycle.get(i);
            int target = cycle.get((i + 1) % cycle.size());
            DefaultWeightedEdge edge = (DefaultWeightedEdge) underlyingStrictGraph.getEdge(source, target);
            score = score + underlyingStrictGraph.getEdgeWeight(edge);
        }
        return score;
    }
}
