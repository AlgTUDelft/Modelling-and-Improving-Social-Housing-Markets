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

    // DONE
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

    // TODO
    public ArtificialMatching ARoutperformingAFMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {

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

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);
        artificialMatching.connect(h5ID, f5ID);
        artificialMatching.connect(h6ID, f6ID);


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
    public ArtificialMatching PRoutperformingARMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
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
        System.out.println("House 5: " + h5ID);
        System.out.println("House 6: " + h6ID);
        System.out.println("Household 1: " + f1ID);
        System.out.println("Household 2: " + f2ID);
        System.out.println("Household 3: " + f3ID);
        System.out.println("Household 4: " + f4ID);
        System.out.println("Household 5: " + f5ID);
        System.out.println("Household 6: " + f6ID);
        return artificialMatching;
    }

    // TODO
    public ArtificialMatching ARoutperformingPRMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
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

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);
        artificialMatching.connect(h5ID, f5ID);
        artificialMatching.connect(h6ID, f6ID);

        artificialMatching.setScores(scores);
        System.out.println("House 1: " + h1ID);
        System.out.println("House 2: " + h2ID);
        System.out.println("House 3: " + h3ID);
        System.out.println("House 4: " + h4ID);
        System.out.println("House 5: " + h5ID);
        System.out.println("House 6: " + h6ID);
        System.out.println("Household 1: " + f1ID);
        System.out.println("Household 2: " + f2ID);
        System.out.println("Household 3: " + f3ID);
        System.out.println("Household 4: " + f4ID);
        System.out.println("Household 5: " + f5ID);
        System.out.println("Household 6: " + f6ID);
        return artificialMatching;
    }

    // TODO
    public ArtificialMatching PRoutperformingAFMatching() throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
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

        HashMap<HouseAndHouseholdIDPair, Double> scores = new HashMap<>();
        scores.put(new HouseAndHouseholdIDPair(h1ID, f1ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f3ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f5ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h1ID, f6ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f1ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h2ID, f6ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f4ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h3ID, f6ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f4ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h4ID, f6ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f2ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f3ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f4ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f5ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h5ID, f6ID), 0.33333334);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f1ID), 0.6666667);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f2ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f3ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f4ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f5ID), 1.0);
        scores.put(new HouseAndHouseholdIDPair(h6ID, f6ID), 0.6666667);

        artificialMatching.connect(h1ID, f1ID);
        artificialMatching.connect(h2ID, f2ID);
        artificialMatching.connect(h3ID, f3ID);
        artificialMatching.connect(h4ID, f4ID);
        artificialMatching.connect(h5ID, f5ID);
        artificialMatching.connect(h6ID, f6ID);


        artificialMatching.setScores(scores);
        System.out.println("House 1: " + h1ID);
        System.out.println("House 2: " + h2ID);
        System.out.println("House 3: " + h3ID);
        System.out.println("House 4: " + h4ID);
        System.out.println("House 5: " + h5ID);
        System.out.println("House 6: " + h6ID);
        System.out.println("Household 1: " + f1ID);
        System.out.println("Household 2: " + f2ID);
        System.out.println("Household 3: " + f3ID);
        System.out.println("Household 4: " + f4ID);
        System.out.println("Household 5: " + f5ID);
        System.out.println("Household 6: " + f6ID);
        return artificialMatching;
    }

}