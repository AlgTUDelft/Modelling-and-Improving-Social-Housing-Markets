package Artificials;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.Household.HouseholdType;
import HousingMarket.HousingMarket;
import HousingMarket.HouseAndHouseholdIDPair;
import Matching.Matching;

import java.util.HashMap;

public class ArtificialMatchingCreator {

    // Designed to test algorithms and assumptions.

    // TODO:
    //  * Matching where AR is expected to outperform PR.
    //    -> Since both just pick the first cycle they find... What would this look like?
    //  * Matching where AF is expected to outperform AR.
    //   -> Two timesteps. Household f1 plus house h1 are added and prefer each other enough that
    //       they, being the first cycle found, are matched. Then household f2 plus house h2 are added,
    //       where f1 only weakly disprefers h2, but f2 very strongly prefers h1 to h2.
    //       It would globally be best to match f2 and h1, and f1 and h2; but because AR picks the first cycle it finds,
    //       it matches f1 with h1, which afterwards cannot be undone.

    public ArtificialMatching AFoutperformingARMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException {
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null);
        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        // ArtificialDynamicMatching removes houses and households in reverse of the order in which they were added.

        h1.getID();
        int h2ID = artificialMatching.addHouse(h2);
        int h1ID = artificialMatching.addHouse(h1);
        int f2ID = artificialMatching.addHousehold(f2);
        int f1ID = artificialMatching.addHousehold(f1);
        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 0.5);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 0.4);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 0.2);
        artificialMatching.setScores(scores);
        return artificialMatching;
    }


}
