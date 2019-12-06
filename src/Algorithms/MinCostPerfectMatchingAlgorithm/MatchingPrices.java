package Algorithms.MinCostPerfectMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.HashMap;

public class MatchingPrices {

    private Matching matching;
    private HashMap<Integer, Float> housePrices = new HashMap<Integer, Float>();
    private HashMap<Integer, Float> householdPrices = new HashMap<Integer, Float>();
    private MatchingPrices previousPrices;

    public MatchingPrices(Matching matching, MatchingPrices previousPrices) {
        this.matching = matching;
        previousPrices.nullifyPreviousPrices();
        this.previousPrices = previousPrices;
    }

    public void findPrices() throws Matching.MatchingEvaluator.HouseholdIncomeTooHighException {
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);
        if (matching.countEdges() == 0) {
            for (House house : matching.getHouses()) {
                housePrices.put(house.getID(), (float) 0.0);
            }
            for (Household household : matching.getHouseholds()) {
                float minScore = 1; // = 1 - 0;
                for (House house : matching.getHouses()) {
                    float candidateScore = 1 - matchingEvaluator.evaluateIndividualTotalFit(house.getID(), household.getID());
                    if (candidateScore < minScore) {
                        minScore = candidateScore;
                    }
                }
                householdPrices.put(household.getID(), 1-minScore);
            }
        }
        else if (previousPrices == null) {
            System.err.println("Non-empty matching requires previous priceset!");
        } else {
            for (House house : matching.getHouses()) {
                float previousPrice = previousPrices.getHousePrice(house.getID());
                float distInPreviousMatching = previousPrices.calculateShortestDistanceToHouse(house.getID());
                // TODO
            }
            for (Household household : matching.getHouseholds()) {
                float previousPrice = previousPrices.getHouseholdPrice(household.getID());
                float distInPreviousMatching = previousPrices.calculateShortestDistanceToHousehold(household.getID(), matchingEvaluator);
                // TODO
            }
        }
    }


    public void nullifyPreviousPrices() {
        this.previousPrices = null;
    }

    public float getHousePrice(int houseID) {
        return this.housePrices.get(houseID);
    }

    public float getHouseholdPrice(int householdID) {
        return this.householdPrices.get(householdID);
    }

    //TODO: Check if these ways of calculating the shortest distance hold under the assumptions given.
    public float calculateShortestDistanceToHouse(int houseID) {
        // Decided not to use Dijkstra's algorithm; I believe the result can be calculated more easily.
        return (float) 0.0;
    }

    public float calculateShortestDistanceToHousehold(int householdID, MatchingEvaluator matchingEvaluator) throws Matching.MatchingEvaluator.HouseholdIncomeTooHighException {
        // Decided not to use Dijkstra's algorithm; I believe the result can be calculated more easily.
        // TODO: Check if calculation method is indeed correct.
        // I assume that in a given matching M, the distance to some household y
        // equals 0 + minimum_dist_to_any_household + 0 + 0 = minimum_cost_of_edge_to_any_household.
        // Actually, let me reread this whole thing...
        float minScore = 1; // = 1 - 0;
        for (House house : matching.getHouses()) {
            float candidateScore = 1 - matchingEvaluator.evaluateIndividualTotalFit(house.getID(), householdID);
            if (candidateScore < minScore) {
                minScore = candidateScore;
            }
        }
        return minScore;
    }
}
