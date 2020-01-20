package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StrictGraph {

    private Matching matching;
    private SimpleDirectedGraph underlyingStrictGraph = new SimpleDirectedGraph(DefaultEdge.class);
    private ArrayList<Integer> householdIDs = new ArrayList<Integer>();
    private Integer nil = -1;


    public StrictGraph(Matching matching) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        this.matching = matching;


        // Add vertices.
        underlyingStrictGraph.addVertex(nil); // _nil_ vertex.
        for (Household household : this.matching.getHouseholds()) {
            int householdID = household.getID();
            underlyingStrictGraph.addVertex(householdID);
            householdIDs.add(householdID);
        }

        this.wireHouseholds(householdIDs);
    }

    public void wireHouseholds(ArrayList<Integer> householdIDs) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        // Add edges. Types here refer to the first three types noted in the paper's description of the WOSMA-algorithm.
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(this.matching);

        for (Integer householdID : householdIDs) {
            float fitWithCurrentHouse;
            House currentHouse = matching.getHouseFromHousehold(householdID);

            if (currentHouse == null) {
                // Add type 3 edge, condition 1.
                underlyingStrictGraph.addEdge(nil, householdID);
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
                if (fitWithOtherHouse > fitWithCurrentHouse) { // Note the strict greater-than relation.
                    Household householdOfOtherHouse = this.matching.getHouseholdFromHouse(otherHouse.getID());
                    if (householdOfOtherHouse == null) {
                        // Add type 2 edge
                        underlyingStrictGraph.addEdge(householdID, nil);
                    } else {
                        // Add type 1 edge
                        underlyingStrictGraph.addEdge(householdID, householdOfOtherHouse.getID());
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
                }
            }
        }
    }

    public int getNil() {
        return this.nil;
    }

    public List<Integer> findStrictCycle() throws CycleFinder.FullyExploredVertexDiscoveredException {
        // TODO: Note somewhere that we're not using Tarjan, and that we're picking the first cycle we come across.
        GabowStrongConnectivityInspector gabowStrongConnectivityInspector = new GabowStrongConnectivityInspector(underlyingStrictGraph);
        List<AsSubgraph<Integer,DefaultEdge>> components = gabowStrongConnectivityInspector.getStronglyConnectedComponents();
        List<Integer> cycle = null;
        for (AsSubgraph<Integer, DefaultEdge> component : components) {
            if (component.vertexSet().size() > 1) {
                CycleFinder cycleFinder = new CycleFinder(component);
                cycle = cycleFinder.findCycle();
                if (cycle != null) {
                    break;
                }
            }
        }
        return cycle;
    }

    public void update(List<Integer> cycle, Matching newMatching) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        // We assume that, in reality, households who have been part of a realized cycle won't want to move again right
        // away, even if we were able to offer them a better house.
        // Thus we remove all the households that are in this cycle, from the graph.
        // This process also automatically removes all edges that needed to be removed.
        // TODO: Describe modification where we now no longer remove households.
//        for (int householdID : cycle) {
//            if (householdID != nil) {
//                this.underlyingStrictGraph.removeVertex(householdID);
//            }
//        }

        this.matching = newMatching;

        ArrayList<DefaultEdge> cycleEdgesToRemove = new ArrayList<DefaultEdge>();
        for (int householdID : cycle) {
            if (householdID != nil) {
                for (DefaultEdge edge : (Set<DefaultEdge>) this.underlyingStrictGraph.incomingEdgesOf(householdID)) {
                    cycleEdgesToRemove.add(edge);
                }
                for (DefaultEdge edge : (Set<DefaultEdge>) this.underlyingStrictGraph.outgoingEdgesOf(householdID)) {
                    cycleEdgesToRemove.add(edge);
                }
            }
        }
        for (DefaultEdge edge : cycleEdgesToRemove) {
            this.underlyingStrictGraph.removeEdge(edge);
        }

        ArrayList<Integer> cycleWithoutNil = new ArrayList<Integer>(cycle);
        cycleWithoutNil.remove(Integer.valueOf(nil));
        this.wireHouseholds(cycleWithoutNil);


        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);

        Set<DefaultEdge> edges = (Set<DefaultEdge>) this.underlyingStrictGraph.incomingEdgesOf(nil);
        ArrayList<DefaultEdge> edgesToRemove = new ArrayList<DefaultEdge>();
        for (DefaultEdge edge : edges) {
            int householdID = (int) this.underlyingStrictGraph.getEdgeSource(edge);
            // Check if there still exists some preferred house for this household.
            float fitWithCurrentHouse;

            House currentHouse = matching.getHouseFromHousehold(householdID);

            if (currentHouse == null) {
                fitWithCurrentHouse = 0;
            } else {
                fitWithCurrentHouse = matchingEvaluator.evaluateIndividualTotalFit(currentHouse.getID(), householdID);
            }
            boolean foundBetterHouseholdlessHouse = false;
            for (House otherHouse : this.matching.getHouses()) {
                if (currentHouse != null) {
                    if (otherHouse.getID() == currentHouse.getID()) {
                        continue;
                    }
                }
                Household householdOfOtherHouse = this.matching.getHouseholdFromHouse(otherHouse.getID());
                if (householdOfOtherHouse == null) {
                    float fitWithOtherHouse = matchingEvaluator.evaluateIndividualTotalFit(otherHouse.getID(), householdID);
                    if (fitWithOtherHouse > fitWithCurrentHouse) { // Note the strict greater-than relation.
                        foundBetterHouseholdlessHouse = true;
                        break;
                    }
                }
            }
            if (!foundBetterHouseholdlessHouse) {
                edgesToRemove.add(edge);
            }
        }

        for (DefaultEdge edge : edgesToRemove) {
            this.underlyingStrictGraph.removeEdge(edge);
        }

    }
}
