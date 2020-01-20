package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Set;

public class TwoLabeledGraph {

    private Matching matching;
    private SimpleDirectedWeightedGraph underlyingStrictGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
    private ArrayList<Integer> householdIDs = new ArrayList<Integer>();
    private Integer nil = -1;

    public TwoLabeledGraph(Matching matching) {
        this.matching = matching;

        // Add vertices.
        underlyingStrictGraph.addVertex(nil); // _nil_ vertex.
        for (Household household : this.matching.getHouseholds()) {
            int householdID = household.getID();
            underlyingStrictGraph.addVertex(householdID);
            householdIDs.add(householdID);
        }

        this.initalWireHouseholds(householdIDs);
    }

    public void initalWireHouseholds(ArrayList<Integer> householdIDs) throws Matching.Matching.HouseholdLinkedToMultipleException, Matching.Matching.HouseholdLinkedToHouseholdException, Matching.MatchingEvaluator.HouseholdIncomeTooHighException {
        // TODO: finish; add non-strict edges and assign weights.
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

    public Integer getNil() {
        return nil;
    }
}
