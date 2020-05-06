package Algorithms;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;

import java.util.ArrayList;
import java.util.Collections;

import static Miscellaneous.DeepCloner.deepClone;

public class Simple {

    private Matching matching;

    public Simple(Matching matching) {
        this.matching = matching;
    }

    public Matching run() throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        ArrayList<Household> households = (ArrayList<Household>) deepClone(matching.getHouseholds());
        Collections.shuffle(households);
        ArrayList<Integer> initiallyEmptyHouseIDs = new ArrayList<>();
        initiallyEmptyHouseIDs.addAll(matching.getHouseholdlessHousesIDs());
        for (Household household : households) {
            float currentFit = 0;
            boolean hasHouse = false;
            if (matching.getHouseFromHousehold(household.getID()) != null) {
                House currentHouse = matching.getHouseFromHousehold(household.getID());
                currentFit = matching.grade(currentHouse.getID(), household.getID());
                hasHouse = true;
            }
            float highscore = currentFit;
            int bestHouseID = -1;
            for (int ID : initiallyEmptyHouseIDs) {
                float candidateFit = matching.grade(ID, household.getID());
                if (candidateFit > highscore) {
                    highscore = candidateFit;
                    bestHouseID = ID;
                }
            }
            if (bestHouseID != -1) {
                if (hasHouse) {
                    matching.disconnect(matching.getHouseFromHousehold(household.getID()).getID(), household.getID());
                }
                matching.connect(bestHouseID, household.getID());
                initiallyEmptyHouseIDs.remove(Integer.valueOf(bestHouseID));
            }
        }
        return matching;
    }
}
