import Algorithms.OptimizationAlgorithm;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.Matching;
import Matching.MatchingEvaluator;

public class Main {

    public static void main(String[] args) {
        test2();
    }

    public static void test1() {
        Matching matching;
        try {
            HousingMarket housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            try {
                matching = dataProcessor.csv_to_matching("../../../Olivier Data [On Laptop]//test (small).csv", 0.96);

                MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);

                matchingEvaluator.evaluateOverallHouseholdlessHouses();
                matchingEvaluator.evaluateOverallHouselessHouseholds();
                matchingEvaluator.evaluateTotal(true);

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

    public static void test2() {
        Matching matching;
        try {
            HousingMarket housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            matching = dataProcessor.csv_to_matching("../../../Olivier Data [On Laptop]//test (small).csv", 0.95);

            MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);
            OptimizationAlgorithm alg = new OptimizationAlgorithm(matching);
            alg.optimizeAvailables();

        } catch (Household.InvalidHouseholdException e) {
            e.printStackTrace();
        } catch (HousingMarket.FreeSpaceException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        }
    }
}