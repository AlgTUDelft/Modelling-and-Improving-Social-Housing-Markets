import Algorithms.MinCostPerfectMatchingAlgorithm.MinCostPerfectMatchingAlgorithm;
import Algorithms.OptimizationAlgorithm.OptimizationAlgorithm;
import Algorithms.OptimizationAlgorithm.OptimizationAlgorithmResult;
import Algorithms.OptimizationAlgorithm.OptimizationAlgorithmResultProcessor;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.Matching;
import Matching.MatchingEvaluator;
import Algorithms.MinCostPerfectMatchingAlgorithm.ResidualGraph;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        test5();
    }

    public static void test1() {
        Matching matching;
        try {
            HousingMarket housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            try {
                matching = dataProcessor.csvToMatching("../../../Olivier Data [On Laptop]//test (small).csv", 0.96, 0, 100);

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
            } catch (IOException e) {
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
            matching = dataProcessor.csvToMatching("../../../Olivier Data [On Laptop]//test (small).csv", 0.95, 0, 100);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test3() {
        String filename = "1000run.csv";
        ArrayList<OptimizationAlgorithmResult> optimizationAlgorithmResults = new ArrayList<OptimizationAlgorithmResult>();
        try {
            for (int i = 0; i < 1000; i++) {
                Matching matching;
                HousingMarket housingMarket = new HousingMarket(2017, 100);
                DataProcessor dataProcessor = new DataProcessor(housingMarket);
                matching = dataProcessor.csvToMatching("../../../Olivier Data [On Laptop]//test.csv", 1, 0, 100);

                MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);
                OptimizationAlgorithm alg = new OptimizationAlgorithm(matching);
//                Result result = alg.optimizeAvailables();
                OptimizationAlgorithmResult optimizationAlgorithmResult = alg.optimizeN((int) Math.floor(i/250) + 1); // 1 through 4.
                optimizationAlgorithmResults.add(optimizationAlgorithmResult);
                System.out.println("Done with sample: " + Integer.toString(i) + ".");
            }

            OptimizationAlgorithmResultProcessor optimizationAlgorithmResultProcessor = new OptimizationAlgorithmResultProcessor(optimizationAlgorithmResults);
            optimizationAlgorithmResultProcessor.resultsToCSV(filename);

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

    public static void test4(String filename, int startLine, int lineCount) {
        Matching matching;
        HousingMarket housingMarket = null;
        try {
            housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            matching = dataProcessor.csvToMatching(filename, 1, startLine, lineCount);

            MatchingEvaluator oldMatchingEvaluator = new MatchingEvaluator(matching);
            oldMatchingEvaluator.evaluateTotal(true);

            MinCostPerfectMatchingAlgorithm minCostPerfectMatchingAlgorithm
                    = new MinCostPerfectMatchingAlgorithm(matching);

            Matching minCostPerfectMatching = minCostPerfectMatchingAlgorithm.findMinCostPerfectMatching();
            MatchingEvaluator newMatchingEvaluator = new MatchingEvaluator(minCostPerfectMatching);
            newMatchingEvaluator.evaluateTotal(true);

        } catch (HousingMarket.FreeSpaceException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Household.InvalidHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (MinCostPerfectMatchingAlgorithm.BipartiteSidesUnequalSize e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (Matching.IDNotPresentException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchingNotEmptyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test5() {
        test4("../../../Olivier Data [On Laptop]//test2.csv", 0, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 100, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 200, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 300, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 400, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 500, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 600, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 700, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 800, 100);
        test4("../../../Olivier Data [On Laptop]//test2.csv", 900, 100);
    }
}