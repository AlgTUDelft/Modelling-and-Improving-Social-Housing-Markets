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
    //  * Matching where PR is expected to outperform AR. -- DONE
    //    -> I mean, I wouldn't ever expect this to happen, but clearly it does, so...
    //       But okay, it's probably something to do with the order in which edges are
    //       removed and added again?
    //       -> Located one such matching. See function. Time to analyze.

    public ArtificialMatching AFoutperformingARMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException {
        // Matching where AF is expected to outperform AR.
        //   -> Two timesteps. Household f1 plus house h1 are added and prefer each other enough that
        //       they, being the first cycle found, are matched. Then household f2 plus house h2 are added,
        //       where f1 only weakly disprefers h2, but f2 very strongly prefers h1 to h2.
        //       It would globally be best to match f2 and h1, and f1 and h2; but because AR picks the first cycle it finds,
        //       it matches f1 with h1, which afterwards cannot be undone.
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null, 2);
        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        // ArtificialDynamicMatching removes houses and households in reverse of the order in which they were added.

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
        System.out.println("House 1: " + h1ID);
        System.out.println("House 2: " + h2ID);
        System.out.println("Household 1: " + f1ID);
        System.out.println("Household 2: " + f2ID);
        return artificialMatching;
    }

    public ArtificialMatching ARoutperformingAFMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        // TODO: Fix this so it works.
        //
        //  * Matching where AR is expected to outperform AF.
        //    -> This would be a matching where running the single best cycle would
        //       prohibit, say, two other mutually compatible cycles
        //       that together score higher.
        //       Furthermore, those two other cycles would have to be placed in such a way
        //       that indeed AR would find them both before it found the larger cycle.
        int timeStepCount = 0;
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null,timeStepCount);

        // The optimal cycle could just be: some household f>1, needed for the other
        // cycles, prefers strongly (0.4diff) some house h5. Nobody else wants h5.
        //
        // The other two cycles could be something like...
        // 1) A (0.1diff)-strength next-house cycle that makes the second cycle possible.
        // This goes f1->f2->f3.
        // 2) Meanwhile there is f4 in h4, who prefers h2, and f1 (who gets h2) prefers
        // h4; but f2 does not, and f4 prefers none other.
        //
        // findMax would: switch f3 to h5 (0.5incr), then... what?
        //  -> Actually, turns out it's found f4->f2->f3->f1 as a cycle. Huh?
        //  -> Huh. It checks out. Dang it!
        //  ---> OK, let's do the math here. f1->f2->f3 is f2->f3->f1.
        //  ---- After that one, we have f1->f4 which is f4->f1.
        //  ---- ... What? Huh? I'm so confused. OK, let's draw this out.
        // regular would: switch f1->f2->f3 (0.3incr), then f1->f4 (say, 0.4incr).
        // ...but then regular could still switch f3 to h5 after all for extra (0.4incr).
        // ...oh, but that wouldn't be bad. OK, let's try this.
        //
        // Drawing:
        // State 1: f1 | f2 | f3    |   f4
        // -------- h1 | h2 | h3    |   h4
        //
        // State 2: f3 | f1 | f2    |   f4
        // -------- h1 | h2 | h3    |   h4
        //
        // State 3: f3 | f4 | f2    |   f1
        // -------- h1 | h2 | h3    |   h4

        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        House h3 = new House("test", "h3", 0,0, false);
        House h4 = new House("test", "h4", 0,0, false);
        House h5 = new House("test", "h5", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        Household f3 = new Household("test", "test", "f3", 0, 20, HouseholdType.ONE,0, false);
        Household f4 = new Household("test", "test", "f4", 0, 20, HouseholdType.ONE,0, false);

        // ArtificialDynamicMatching removes houses and households in reverse of the order in which they were added.

        int h5ID = artificialMatching.addHouse(h5);
        int h4ID = artificialMatching.addHouse(h4);
        int h3ID = artificialMatching.addHouse(h3);
        int h2ID = artificialMatching.addHouse(h2);
        int h1ID = artificialMatching.addHouse(h1);
        int f4ID = artificialMatching.addHousehold(f4);
        int f3ID = artificialMatching.addHousehold(f3);
        int f2ID = artificialMatching.addHousehold(f2);
        int f1ID = artificialMatching.addHousehold(f1);

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);


        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 0.2);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 0.2);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f3ID), 0.2);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f4ID), 0.2);

        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 0.3);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f2ID), 0.3);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f3ID), 0.3);

        scores.put(new HouseAndHouseholdIDPair(h5ID, f3ID), 0.6);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f4ID), 0.4);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f1ID), 0.5);

        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f4ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f3ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f1ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f4ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f2ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f3ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f1ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f2ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f4ID), 0.0);





        artificialMatching.setScores(scores);
        System.out.println("House 1: " + h1ID);
        System.out.println("House 2: " + h2ID);
        System.out.println("House 3: " + h3ID);
        System.out.println("House 4: " + h4ID);
        System.out.println("House 5: " + h5ID);
        System.out.println("Household 1: " + f1ID);
        System.out.println("Household 2: " + f2ID);
        System.out.println("Household 3: " + f3ID);
        System.out.println("Household 4: " + f4ID);
        return artificialMatching;
    }

    public ArtificialMatching ARoutperformingAFMatching2() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        // TODO: Fix this so it works.
        int timeStepCount = 3;
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null,timeStepCount);

        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        House h3 = new House("test", "h3", 0,0, false);
        House h4 = new House("test", "h4", 0,0, false);
        House h5 = new House("test", "h5", 0,0, false);
        House h6 = new House("test", "h6", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        Household f3 = new Household("test", "test", "f3", 0, 20, HouseholdType.ONE,0, false);
        Household f4 = new Household("test", "test", "f4", 0, 20, HouseholdType.ONE,0, false);
        Household f5 = new Household("test", "test", "f5", 0, 20, HouseholdType.ONE,0, false);
        Household f6 = new Household("test", "test", "f6", 0, 20, HouseholdType.ONE,0, false);

        int h1ID = artificialMatching.addHouse(h1);
        int h2ID = artificialMatching.addHouse(h2);
        int h3ID = artificialMatching.addHouse(h3);
        int h4ID = artificialMatching.addHouse(h4);
        int h5ID = artificialMatching.addHouse(h5);
        int h6ID = artificialMatching.addHouse(h6);
        int f1ID = artificialMatching.addHousehold(f1);
        int f2ID = artificialMatching.addHousehold(f2);
        int f3ID = artificialMatching.addHousehold(f3);
        int f4ID = artificialMatching.addHousehold(f4);
        int f5ID = artificialMatching.addHousehold(f5);
        int f6ID = artificialMatching.addHousehold(f6);

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);
        artificialMatching.connect(h5ID, f5ID);
        artificialMatching.connect(h6ID, f6ID);


        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f3ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f5ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f6ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f3ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f5ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f6ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f6ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f3ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f5ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f6ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f1ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f6ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f1ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f6ID), 0.33333334);


        artificialMatching.setScores(scores);
        System.out.println("House 1: " + h1ID);
        System.out.println("House 2: " + h2ID);
        System.out.println("House 3: " + h3ID);
        System.out.println("House 4: " + h4ID);
        System.out.println("Household 1: " + f1ID);
        System.out.println("Household 2: " + f2ID);
        System.out.println("Household 3: " + f3ID);
        System.out.println("Household 4: " + f4ID);
        return artificialMatching;
    }

    public ArtificialMatching PRoutperformingARMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        // TODO: Fix this so it works.
        int timeStepCount = 3;
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null,timeStepCount);

        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        House h3 = new House("test", "h3", 0,0, false);
        House h4 = new House("test", "h4", 0,0, false);
        House h5 = new House("test", "h5", 0,0, false);
        House h6 = new House("test", "h6", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        Household f3 = new Household("test", "test", "f3", 0, 20, HouseholdType.ONE,0, false);
        Household f4 = new Household("test", "test", "f4", 0, 20, HouseholdType.ONE,0, false);
        Household f5 = new Household("test", "test", "f5", 0, 20, HouseholdType.ONE,0, false);
        Household f6 = new Household("test", "test", "f6", 0, 20, HouseholdType.ONE,0, false);

        int h1ID = artificialMatching.addHouse(h1);
        int h2ID = artificialMatching.addHouse(h2);
        int h3ID = artificialMatching.addHouse(h3);
        int h4ID = artificialMatching.addHouse(h4);
        int h5ID = artificialMatching.addHouse(h5);
        int h6ID = artificialMatching.addHouse(h6);
        int f1ID = artificialMatching.addHousehold(f1);
        int f2ID = artificialMatching.addHousehold(f2);
        int f3ID = artificialMatching.addHousehold(f3);
        int f4ID = artificialMatching.addHousehold(f4);
        int f5ID = artificialMatching.addHousehold(f5);
        int f6ID = artificialMatching.addHousehold(f6);

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);
        artificialMatching.connect(h5ID, f5ID);
        artificialMatching.connect(h6ID, f6ID);


        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 0.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f3ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f5ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f6ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f6ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f5ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f6ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f2ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f3ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f4ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f6ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f6ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f5ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f6ID), 1.0);


        artificialMatching.setScores(scores);
        System.out.println("House 1: " + h1ID);
        System.out.println("House 2: " + h2ID);
        System.out.println("House 3: " + h3ID);
        System.out.println("House 4: " + h4ID);
        System.out.println("Household 1: " + f1ID);
        System.out.println("Household 2: " + f2ID);
        System.out.println("Household 3: " + f3ID);
        System.out.println("Household 4: " + f4ID);
        return artificialMatching;
    }

}
