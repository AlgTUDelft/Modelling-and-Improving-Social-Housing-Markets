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

    // DONE :)
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

    // DONE but TODO: to analyze.
    public ArtificialMatching ARoutperformingAFMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        // State afterwards:
        // h2-f2 are linked.
        //
        // - f1 prefers h4 (1/3)
        // - f2 prefers h3/h4 (1/3)
        // - f3 prefers h1/h4 (1/3)
        // - f4 prefers h4 (1/3)
        //
        // PR: h4-f1, h1-f4, h2-f3, h3-f2. Total of: 2/3 + 1/3 + 2/3 + 2/3 = 2 1/3.
        //
        // AR...
        // Cycle 1: f1 gets empty house h4. (0->2/3)
        // Cycle 2: f4 gets empty house h3. (0->1/3)
        // Cycle 3: f3 gets empty house h1. (0->1)
        // Cycle 4: null. Total: 1/3 + 2 = 2 1/3.
        //
        // AF...
        // Cycle 1: 6 found. Best: nil->f3->f2. So f3 gets h2, and h2 gets h4. (0->2/3 + 1/3->2/3).
        // Cycle 2: 3 found. Best: nil->f1. So f1 gets h3. (0->1/3).
        // Cycle 3: 2 found. Best: nil->f4. So f4 gets h1. (0->1/3).
        // Cycle 4: null. Total: 1/3 + 1 2/3 = 2.

        int timeStepCount = 2;
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null,timeStepCount);

        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        House h3 = new House("test", "h3", 0,0, false);
        House h4 = new House("test", "h4", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        Household f3 = new Household("test", "test", "f3", 0, 20, HouseholdType.ONE,0, false);
        Household f4 = new Household("test", "test", "f4", 0, 20, HouseholdType.ONE,0, false);

        int h1ID = artificialMatching.addHouse(h1);
        int h2ID = artificialMatching.addHouse(h2);
        int h3ID = artificialMatching.addHouse(h3);
        int h4ID = artificialMatching.addHouse(h4);
        int f1ID = artificialMatching.addHousehold(f1);
        int f2ID = artificialMatching.addHousehold(f2);
        int f3ID = artificialMatching.addHousehold(f3);
        int f4ID = artificialMatching.addHousehold(f4);


        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f3ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f4ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f4ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f1ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f4ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f3ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f4ID), 0.6666667);

        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);

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

    // DONE but TODO: to analyze.
    public ArtificialMatching PRoutperformingARMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        // PR...
        // State 0: h1-f1, h2-f2. (1/3, 2/3).
        // State 1: +h4,f4.
        // Cycle 1: nil->f4->f1. So f4 gets h1, f1 gets h4. (0->2/3, 1/3->2/3).
        // State 2: +h3,f3.
        // Cycle 2: nil->f3->f4. So f3 gets h1, and f4 gets h3. (0->2/3,2/3->1).
        // Total: 1 -> 3.
        //
        // AR...
        // State after: h1-f1, h2-f2. (1/3, 2/3).
        // Cycle 1: nil->f4->f1. So f4 gets h1, f1 gets h4. (0->2/3, 1/3->2/3).
        // Cycle 2: nil->f3. So f3 gets h3. (0->2/3).
        // Total: 1 -> 2 2/3.



        int timeStepCount = 2;
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null,timeStepCount);

        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        House h3 = new House("test", "h3", 0,0, false);
        House h4 = new House("test", "h4", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        Household f3 = new Household("test", "test", "f3", 0, 20, HouseholdType.ONE,0, false);
        Household f4 = new Household("test", "test", "f4", 0, 20, HouseholdType.ONE,0, false);

        int h1ID = artificialMatching.addHouse(h1);
        int h2ID = artificialMatching.addHouse(h2);
        int h3ID = artificialMatching.addHouse(h3);
        int h4ID = artificialMatching.addHouse(h4);
        int f1ID = artificialMatching.addHousehold(f1);
        int f2ID = artificialMatching.addHousehold(f2);
        int f3ID = artificialMatching.addHousehold(f3);
        int f4ID = artificialMatching.addHousehold(f4);



        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f2ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f2ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f4ID), 0.6666667);

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);


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

    // DONE but TODO: to analyze.
    public ArtificialMatching ARoutperformingPRMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        // PR:
        // State 0: h1-f1, h2-f2
        // Cycle 1: h4-f4
        // Cycle 2: h3->f3->f2 ==> h1-f1, h4-f4, h3-f2, h2-f3 [1 + 1/3 + 1 + 2/3 = 3]
        //
        // AR:
        // State 0: h1-f1, h2-f2
        // Cycle 1: h3->f4->f2 ==> h1-f1, h2-f4, h3-f2
        // Cycle 2: h4->f3 ==> h1-f1, h2-f4, h3-f2, h4-f3 [1 + 2/3 + 1 + 2/3 = 3 1/3]

        int timeStepCount = 2;
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null,timeStepCount);

        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        House h3 = new House("test", "h3", 0,0, false);
        House h4 = new House("test", "h4", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        Household f3 = new Household("test", "test", "f3", 0, 20, HouseholdType.ONE,0, false);
        Household f4 = new Household("test", "test", "f4", 0, 20, HouseholdType.ONE,0, false);

        int h1ID = artificialMatching.addHouse(h1);
        int h2ID = artificialMatching.addHouse(h2);
        int h3ID = artificialMatching.addHouse(h3);
        int h4ID = artificialMatching.addHouse(h4);
        int f1ID = artificialMatching.addHousehold(f1);
        int f2ID = artificialMatching.addHousehold(f2);
        int f3ID = artificialMatching.addHousehold(f3);
        int f4ID = artificialMatching.addHousehold(f4);

        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f3ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f4ID), 0.33333334);

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);

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

    // TODO
    public ArtificialMatching PRoutperformingAFMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        // PR:
        // State 0: h1-f1, h2-f2
        // Cycle 1: h4->f4->f1 [0->2/3, 1/3->2/3]
        // Cycle 2: h3->f3->f4 [0->2/3, 2/3->1]
        //
        // AF:
        // State 0: h1-f1, h2-f2
        // Cycle 1: h4->f4->f1 [0->2/3, 1/3->2/3]
        // State 1: h1-f4, h2-f2, h4-f1
        // Cycle 2: h3->f3 [0->2/3]
        // TODO: ERROR: At state 1, 7->8 is missing (but must have been present in PR)! Why?
        //  -> Node 8 first has its outgoing AND incoming edges removed,
        //     and then has its edges of types (nil->8, 8->nil, 8->w) added back.
        //     But this does not add back the edge 7->8! To get this, we need to rewire households that
        //     were not part of the cycle as well. So... let's just try and rewire all households
        //     instead of just the cycle.

        // FindMax optimizes for values, not for improvement!
        int timeStepCount = 2;
        ArtificialMatching artificialMatching = new ArtificialMatching(new HousingMarket(2017, 100), null,timeStepCount);

        House h1 = new House("test", "h1", 0, 0, false);
        House h2 = new House("test", "h2", 0,0, false);
        House h3 = new House("test", "h3", 0,0, false);
        House h4 = new House("test", "h4", 0,0, false);
        Household f1 = new Household("test", "test", "f1", 0, 20, HouseholdType.ONE,0, false);
        Household f2 = new Household("test", "test", "f2", 0, 20, HouseholdType.ONE,0, false);
        Household f3 = new Household("test", "test", "f3", 0, 20, HouseholdType.ONE,0, false);
        Household f4 = new Household("test", "test", "f4", 0, 20, HouseholdType.ONE,0, false);

        int h1ID = artificialMatching.addHouse(h1);
        int h2ID = artificialMatching.addHouse(h2);
        int h3ID = artificialMatching.addHouse(h3);
        int h4ID = artificialMatching.addHouse(h4);
        int f1ID = artificialMatching.addHousehold(f1);
        int f2ID = artificialMatching.addHousehold(f2);
        int f3ID = artificialMatching.addHousehold(f3);
        int f4ID = artificialMatching.addHousehold(f4);

        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f2ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f2ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f4ID), 0.6666667);

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);



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
