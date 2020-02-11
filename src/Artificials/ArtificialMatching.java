package Artificials;

import HousingMarket.House.House;
import HousingMarket.HouseAndHouseholdIDPair;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.MatchingEvaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ArtificialMatching extends Matching.Matching {

    private HashMap<HouseAndHouseholdIDPair, Double> scores;
    private int timestepCount;

    public ArtificialMatching(HousingMarket housingMarket, HashMap<HouseAndHouseholdIDPair, Double> scores, int timestepCount) {
        super(housingMarket);
        this.scores = scores;
        this.timestepCount = timestepCount;
    }

    public void setScores(HashMap<HouseAndHouseholdIDPair, Double> newScores) {
        this.scores = newScores;
    }

    public HashMap<HouseAndHouseholdIDPair, Double> getScores() {
        return this.scores;
    }

    public double calculateGlobalScore() throws HouseholdLinkedToMultipleException, HouseholdLinkedToHouseholdException {
        double score = 0;
        for (Household household : this.getHouseholds()) {
            House house = getHouseFromHousehold(household.getID());
            if (house != null) {
                score = score + this.scores.get(new HouseAndHouseholdIDPair(house.getID(), household.getID()));
            }
        }
        return score;
    }

    // Override.
    public void executeCycle(List<Integer> cycle, int nilValue, boolean print) throws HouseholdLinkedToMultipleException, HouseholdLinkedToHouseholdException, HouseholdAlreadyMatchedException, HouseAlreadyMatchedException, MatchingEvaluator.HouseholdIncomeTooHighException, PreferredNoHouseholdlessHouseException {
        int edgesCount = cycle.size();

        boolean isChain = false;

        // Disconnect all households from whatever houses they own, and keep a list of these houses.
        ArrayList<Integer> housesList = new ArrayList<Integer>();
        for (int i = 0; i<edgesCount; i++) {
            int householdID = cycle.get(i);
            if (householdID != nilValue) {
                House house = getHouseFromHousehold(householdID);
                if (house != null) {
                    housesList.add(house.getID());
                    disconnect(house.getID(), householdID);
                } else {
                    housesList.add(null);
                }
                this.householdsMovedByWOSMA.add(householdID);
            } else {
                isChain = true;
                housesList.add(null);
            }
        }
        if(print) {
            if (isChain) {
                System.out.println("Chain has size: " + edgesCount);
            } else { System.out.println("Cycle has size: " + edgesCount);
            }
        }


        for (int i = 0; i<edgesCount; i++) {
            int sourceVertex;
            int targetVertex;
            if (i == edgesCount-1) {
                sourceVertex = cycle.get(i);
                targetVertex = cycle.get(0);
            } else {
                sourceVertex = cycle.get(i);
                targetVertex = cycle.get(i+1);
            }

            if (sourceVertex != nilValue && targetVertex != nilValue) {
                if (i+1 < housesList.size()) {
                    connect(housesList.get(i + 1), sourceVertex);
                } else {
                    connect(housesList.get(0), sourceVertex);
                }
            } else if (sourceVertex == nilValue) {
                continue; // Household was already previously disconnected, so no change.
            } else { // targetVertex == nilValue, so there is an empty house that the household prefers to their own.
                // We now choose to connect him with that house amongst the empty houses, that they prefer most,
                // so long as they do indeed prefer it to their current house.
                // TODO: Is that method of picking a house legit, though?
                Set<Integer> householdlessHouses = getHouseholdlessHousesIDs();
                double highestScore;
                if (housesList.get(i) == null) {
                    highestScore = 0;
                } else {
                    highestScore = scores.get(new HouseAndHouseholdIDPair(housesList.get(i),sourceVertex));
                }
                House bestHouse = null;
                for (int houseID : householdlessHouses) {
                    // _housesList_ houses will either go to *another* household in the chain,
                    // or this household didn't want it anyway, since this is not a cycle.
                    if (!housesList.contains(houseID)) {
                        double candidateScore = scores.get(new HouseAndHouseholdIDPair(houseID, sourceVertex));
                        if (candidateScore >= highestScore) {
                            highestScore = candidateScore;
                            bestHouse = getHouse(houseID);
                        }
                    }
                }
                if (bestHouse == null) {
                    throw new PreferredNoHouseholdlessHouseException("Cycle indicated that household would prefer some" +
                            " other house to their current house, but no such house was found.");
                } else {
                    System.out.println("Connecting " + cycle.toString() + " where nil is house " + bestHouse.getID());
                    connect(bestHouse.getID(), sourceVertex);
                }
            }
        }

        if (isChain) {
            SWIChainLengths.add(edgesCount);
            SWICycleLengths.add(0);
        } else {
            SWIChainLengths.add(0);
            SWICycleLengths.add(edgesCount);
        }
    }

    public int getTimestepCount() {
        return timestepCount;
    }

}
