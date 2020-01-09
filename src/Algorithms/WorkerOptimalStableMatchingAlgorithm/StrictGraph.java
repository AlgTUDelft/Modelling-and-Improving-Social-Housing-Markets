package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.ArrayList;
import java.util.List;

public class StrictGraph {

    private Matching matching;
    private SimpleDirectedGraph underlyingStrictGraph = new SimpleDirectedGraph(DefaultEdge.class);
    private ArrayList<Integer> householdIDs = new ArrayList<Integer>();
    private Integer nil = -1;


    public StrictGraph(Matching matching) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException {
        this.matching = matching;

        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(this.matching);

        // Add vertices.
        underlyingStrictGraph.addVertex(nil); // _nil_ vertex.
        for (Household household : this.matching.getHouseholds()) {
            int householdID = household.getID();
            underlyingStrictGraph.addVertex(householdID);
            householdIDs.add(householdID);
        }

        // Add edges. Types here refer to the first three types noted in the paper's description of the WOSMA-algorithm.
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

        System.out.println("Finished construction of strict graph.");
    }

    public int getNil() {
        return this.nil;
    }

    public List<Integer> findStrictCycle() {
        System.out.println("Starting Tarjan...");
        TarjanSimpleCycles tarjanSimpleCycles = new TarjanSimpleCycles(underlyingStrictGraph);
        // TODO: Unchecked Assignment error here or does this go well?
        List<List<Integer>> cycles = tarjanSimpleCycles.findSimpleCycles();
        System.out.println("Tarjan finished.");
        if (cycles.isEmpty()) {
            return null;
        } else {
            System.out.println(cycles.size() + " cycles found.");
            // TODO: Only return first cycle, or all? Check if this ever throws up interlocking cycles.
            //  Or: Return the longest cycle?
            System.out.println("Size of chosen cycle is: " + cycles.get(0).size());
            return cycles.get(0);
        }
    }

    public void update(List<Integer> cycle, Matching newMatching) {
        // We assume that, in reality, households who have been part of a realized cycle won't want to move again right
        // away, even if we were able to offer them a better house.
        // Thus we remove all the households that are in this cycle, from the graph.
        // This process also automatically removes all edges that needed to be removed.
        for (int householdID : cycle) {
            if (householdID != nil) {
                this.underlyingStrictGraph.removeVertex(householdID);
            }
        }
        this.matching = newMatching;
    }
}
