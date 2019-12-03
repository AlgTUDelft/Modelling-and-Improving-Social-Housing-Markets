import Algorithms.OptimizationAlgorithm;
import Algorithms.Result;
import Algorithms.ResultProcessor;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.Matching;
import Matching.MatchingEvaluator;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        test3();
    }

    public static void test1() {
        Matching matching;
        try {
            HousingMarket housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            try {
                matching = dataProcessor.csvToMatching("../../../Olivier Data [On Laptop]//test (small).csv", 0.96);

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
            matching = dataProcessor.csvToMatching("../../../Olivier Data [On Laptop]//test (small).csv", 0.95);

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

    public static void test3() {
        String filename = "100run.csv";
        ArrayList<Result> results = new ArrayList<Result>();
        try {
            for (int i = 0; i < 100; i++) {
                Matching matching;
                HousingMarket housingMarket = new HousingMarket(2017, 100);
                DataProcessor dataProcessor = new DataProcessor(housingMarket);
                matching = dataProcessor.csvToMatching("../../../Olivier Data [On Laptop]//test (small).csv", 1);

                MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);
                OptimizationAlgorithm alg = new OptimizationAlgorithm(matching);
//                Result result = alg.optimizeAvailables();
                Result result = alg.optimizeN((int) Math.floor(i/25) + 1); // 1 through 4.
                results.add(result);
            }

            ResultProcessor resultProcessor = new ResultProcessor(results);
            resultProcessor.resultsToCSV(filename);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}