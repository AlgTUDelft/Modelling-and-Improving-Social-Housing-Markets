import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.Household.LabelType;
import HousingMarket.Household.PersonalityType;
import HousingMarket.Household.HouseholdType;
import HousingMarket.HousingMarket;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args){
        test2();
    }

//    public static void test1() {
//        try {
//            HousingMarket housingMarket = new HousingMarket(2018, 25.0);
//            Matching matching = new Matching(housingMarket);
//            seedTestMatching(matching);
//            MatchingEvaluator matchingEvaluator= new MatchingEvaluator(matching);
//            matchingEvaluator.evaluateOverallHouseholdlessHouses();
//            matchingEvaluator.evaluateOverallHouselessHouseholds();
//            matchingEvaluator.evaluateOverallAccessibilityFit();
//        } catch (MatchingEvaluator.InvalidMatchingException
//                | HousingMarket.FreeSpaceException
//                | Household.InvalidHouseholdException
//                | Matching.HouseholdAlreadyMatchedException
//                | Matching.HouseAlreadyMatchedException
//                | Matching.HouseholdLinkedToMultipleException
//                | Matching.HouseholdLinkedToHouseholdException e) {
//            System.err.println(e.getMessage());
//        }
//    }

    public static void test2() {
        Matching matching;
        try {
            HousingMarket housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            try {
                matching = dataProcessor.csv_to_data("../../../Olivier Data [On Laptop]//test.csv");

                MatchingEvaluator matchingEvaluator= new MatchingEvaluator(matching);

                matchingEvaluator.evaluateOverallHouseholdlessHouses();
                matchingEvaluator.evaluateOverallHouselessHouseholds();
                matchingEvaluator.evaluateTotal();

            } catch (Matching.HouseAlreadyMatchedException e) {
                System.err.println(e.getMessage());
            } catch (Household.InvalidHouseholdException e) {
                System.err.println(e.getMessage());
            } catch (Matching.HouseholdAlreadyMatchedException e) {
                System.err.println(e.getMessage());
            } catch (Matching.HouseholdLinkedToMultipleException e) {
                e.printStackTrace();
            } catch (Matching.HouseholdLinkedToHouseholdException e) {
                e.printStackTrace();
            } catch (MatchingEvaluator.InvalidMatchingException e) {
                e.printStackTrace();
            } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
                e.printStackTrace();
            }
        } catch (HousingMarket.FreeSpaceException e) {
            System.err.println(e.getMessage());
        }
    }

//    public static void seedTestMatching(Matching matching)
//            throws Household.InvalidHouseholdException,
//            Matching.HouseholdAlreadyMatchedException,
//            Matching.HouseAlreadyMatchedException {
//
//        House house1 = new House(false, 500,3,50, false, 2011);
//        House house2 = new House(false, 600, 4, 60, true, 2016);
//        House house3 = new House(false, 600, 4, 60, false, 2016);
//        Household household1 = new Household(43, 25000, HouseholdType.HH2, 4, 2, LabelType.DOORSTROMER , PersonalityType.RED, 12 * 8, false);
//        Household household2 = new Household(20, 20000, HouseholdType.ONE, 1, 0, LabelType.STARTER, PersonalityType.GREEN, 12 * 1, false);
//        Household household3 = new Household(36, 35000, HouseholdType.TWO, 2, 0, LabelType.DOORSTROMER, PersonalityType.BLUE, 12 * 4, true);
//        Household household4 = new Household(70, 25000, HouseholdType.HH2, 2, 0, LabelType.DOORSTROMER, PersonalityType.GREEN, 12*6, true);
//        Household household5 = new Household(70, 25000, HouseholdType.HH2, 2, 0, LabelType.DOORSTROMER, PersonalityType.GREEN, 12*6, true);
//
//        matching.addHouses(house1, house2, house3);
//        matching.addHouseholds(household1, household2, household3, household4, household5);
//        matching.connect(house1, household1);
//        matching.connect(house2, household4);
//        matching.connect(house3, household5);
//
//    }
}
