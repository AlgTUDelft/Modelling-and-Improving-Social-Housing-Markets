package Algorithms.MinCostPerfectMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.HashMap;

public class MatchingPrices {

    private Matching matching;
    private ResidualGraph residualGraph;
    private HashMap<Integer, Float> housePrices = new HashMap<Integer, Float>();
    private HashMap<Integer, Float> householdPrices = new HashMap<Integer, Float>();
    private MatchingPrices previousPrices;

    public MatchingPrices(Matching matching, ResidualGraph residualGraph, MatchingPrices previousPrices) {
        this.matching = matching;
        this.residualGraph = residualGraph;
        previousPrices.nullifyPreviousPrices();
        this.previousPrices = previousPrices;
    }

    public void findPrices() throws Matching.MatchingEvaluator.HouseholdIncomeTooHighException {
        // TODO: Adjust so that instead of matching, residualgraph is used.
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);
        if (matching.countEdges() == 0) {
            for (House house : matching.getHouses()) {
                housePrices.put(house.getID(), (float) 0.0);
            }
            for (Household household : matching.getHouseholds()) {
                float minScore = 1; // = 1 - 0;
                for (House house : matching.getHouses()) { // In the current residual graph,
                    // household gets edges from all houses.
                    // TODO: Could replace this with an edge-weight check,
                    //  but I shouldn't do that if I decide to have the residualGraph use
                    //  _reduced_ edge costs as weights.
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
        return (float) 0.0;
    }

    public float calculateShortestDistanceToHousehold(int householdID, MatchingEvaluator matchingEvaluator) throws Matching.MatchingEvaluator.HouseholdIncomeTooHighException {
        return (float) 0.0;
    }
}
