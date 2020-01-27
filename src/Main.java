import Algorithms.MinCostPerfectMatchingAlgorithm.MinCostPerfectMatchingAlgorithm;
import Algorithms.MinCostPerfectMatchingAlgorithm.MinCostPerfectMatchingResult;
import Algorithms.MinCostPerfectMatchingAlgorithm.MinCostPerfectMatchingResultProcessor;
import Algorithms.OptimizationAlgorithm.OptimizationAlgorithm;
import Algorithms.OptimizationAlgorithm.OptimizationAlgorithmResult;
import Algorithms.OptimizationAlgorithm.OptimizationAlgorithmResultProcessor;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.WMComparisonResult;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.WMComparisonResultProcessor;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.WorkerOptimalStableMatchingAlgorithm;
import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.Matching;
import Matching.MatchingEvaluator;
import Matching.DynamicMatching;
import Matching.DynamicMatchingComparisonResult;
import Matching.DynamicMatchingComparisonResultProcessor;
import Algorithms.MinCostPerfectMatchingAlgorithm.ResidualGraph;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException {
//        comparison_WOSMA_MCPMA();
        runDynamicMatching();
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

    public static MinCostPerfectMatchingResult test4(String filename, int startLine, int lineCount) {
        Matching matching;
        HousingMarket housingMarket = null;
        MinCostPerfectMatchingResult minCostPerfectMatchingResult = null;

        try {
            housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            matching = dataProcessor.csvToMatching(filename, 1, startLine, lineCount);

            MatchingEvaluator oldMatchingEvaluator = new MatchingEvaluator(matching);
            float oldOverallResult = oldMatchingEvaluator.evaluateTotal(true);
            float oldAverageLocalResult = oldMatchingEvaluator.evaluateAverageIndividualTotalFit(false);

            MinCostPerfectMatchingAlgorithm minCostPerfectMatchingAlgorithm
                    = new MinCostPerfectMatchingAlgorithm(matching);

            Matching minCostPerfectMatching = minCostPerfectMatchingAlgorithm.findMinCostPerfectMatching(true);
            MatchingEvaluator newMatchingEvaluator = new MatchingEvaluator(minCostPerfectMatching);
            float newOverallResult = newMatchingEvaluator.evaluateTotal(true);
            float newAverageLocalResult = newMatchingEvaluator.evaluateAverageIndividualTotalFit(false);

            float overallPercentageIncrease = (newOverallResult - oldOverallResult)/oldOverallResult * 100;
            float averageLocalPercentageIncrease = (newAverageLocalResult - oldAverageLocalResult)/oldAverageLocalResult * 100;

            minCostPerfectMatchingResult = new MinCostPerfectMatchingResult(oldOverallResult, newOverallResult, overallPercentageIncrease, oldAverageLocalResult, newAverageLocalResult, averageLocalPercentageIncrease);

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
        } catch (MinCostPerfectMatchingAlgorithm.BipartiteSidesUnequalSizeException e) {
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
        return minCostPerfectMatchingResult;
    }

    public static void test5() throws IOException {
        String filename = "../1times500MinCostPerfectMatchingRun.csv";

        ArrayList<MinCostPerfectMatchingResult> results = new ArrayList<MinCostPerfectMatchingResult>();
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 0, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 100, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 200, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 300, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 400, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 500, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 600, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 700, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 800, 100));
//        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 900, 100));
        results.add(test4("../../../Olivier Data [On Laptop]//test2.csv", 0, 500));

        MinCostPerfectMatchingResultProcessor minCostPerfectMatchingResultProcessor
                = new MinCostPerfectMatchingResultProcessor(results);
        minCostPerfectMatchingResultProcessor.resultsToCSV(filename);
    }

    public static void test6() {
        HousingMarket housingMarket = null;
        try {
            housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            Matching matching = dataProcessor.csvToMatching("../../../Olivier Data [On Laptop]//test2.csv", 1, 500, 100);

            MatchingEvaluator oldMatchingEvaluator = new MatchingEvaluator(matching);
            float oldOverallResult = oldMatchingEvaluator.evaluateTotal(true);
            float oldAverageLocalResult = oldMatchingEvaluator.evaluateAverageIndividualTotalFit(false);

            WorkerOptimalStableMatchingAlgorithm workerOptimalStableMatchingAlgorithm = new WorkerOptimalStableMatchingAlgorithm(matching);
            Matching newMatching = workerOptimalStableMatchingAlgorithm.findWorkerOptimalStableMatching(false,true);
            MatchingEvaluator newMatchingEvaluator = new MatchingEvaluator(newMatching);
            float newOverallResult =newMatchingEvaluator.evaluateTotal(true);
            float newAverageLocalResult = newMatchingEvaluator.evaluateAverageIndividualTotalFit(false);

            float overallPercentageIncrease = (newOverallResult - oldOverallResult)/oldOverallResult * 100;
            float averageLocalPercentageIncrease = (newAverageLocalResult - oldAverageLocalResult)/oldAverageLocalResult * 100;
            System.out.println("Overall percentage increase is: " + overallPercentageIncrease);
            System.out.println("Average local percentage increase is: " + averageLocalPercentageIncrease);

        } catch (HousingMarket.FreeSpaceException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Household.InvalidHouseholdException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (CycleFinder.FullyExploredVertexDiscoveredException e) {
            e.printStackTrace();
        }

    }

    public static void comparison_WOSMA_MCPMA() throws IOException {

        String inputFileName = "../../../Olivier Data [On Laptop]//test2.csv";
        String outputFilename = "../test.csv";

        ArrayList<WMComparisonResult> results = new ArrayList<WMComparisonResult>();
        ArrayList<Integer> startLines = new ArrayList<Integer>(Arrays.asList(0, 500));
        for (int startLine : startLines) {
            results.add(individualComparison_WOSMA_MCPMA(inputFileName,startLine, 500));
        }

        WMComparisonResultProcessor wmComparisonResultProcessor
                = new WMComparisonResultProcessor(results);
        wmComparisonResultProcessor.resultsToCSV(outputFilename);
    }


    public static WMComparisonResult individualComparison_WOSMA_MCPMA(String filename, int startLine, int lineCount) {
        Matching matching;
        HousingMarket housingMarket;
        WMComparisonResult wmComparisonResult = null;

        try {
            housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            matching = dataProcessor.csvToMatching(filename, 1, startLine, lineCount);

            MatchingEvaluator oldMatchingEvaluator = new MatchingEvaluator(matching);
            float oldOverallResult = oldMatchingEvaluator.evaluateTotal(true);
            float oldAverageLocalResult = oldMatchingEvaluator.evaluateAverageIndividualTotalFit(false);

            Matching matchingCopy = (Matching) deepClone(matching);
            WorkerOptimalStableMatchingAlgorithm workerOptimalStableMatchingAlgorithm
                    = new WorkerOptimalStableMatchingAlgorithm(matching);
            MinCostPerfectMatchingAlgorithm minCostPerfectMatchingAlgorithm
                    = new MinCostPerfectMatchingAlgorithm(matchingCopy);


            Matching workerOptimalStableMatching = workerOptimalStableMatchingAlgorithm.findWorkerOptimalStableMatching(false,true);
            MatchingEvaluator workerOptimalMatchingEvaluator = new MatchingEvaluator(workerOptimalStableMatching);
            float WOSMA_OverallResult = workerOptimalMatchingEvaluator.evaluateTotal(true);
            float WOSMA_AverageLocalResult = workerOptimalMatchingEvaluator.evaluateAverageIndividualTotalFit(false);

            Matching minCostPerfectMatching = minCostPerfectMatchingAlgorithm.findMinCostPerfectMatching(true);
            MatchingEvaluator minCostPerfectMatchingEvaluator = new MatchingEvaluator(minCostPerfectMatching);
            float MCPMA_OverallResult = minCostPerfectMatchingEvaluator.evaluateTotal(true);
            float MCPMA_AverageLocalResult = minCostPerfectMatchingEvaluator.evaluateAverageIndividualTotalFit(false);

            float WOSMA_OverallPercentageIncrease = (WOSMA_OverallResult - oldOverallResult)/oldOverallResult * 100;
            float WOSMA_AverageLocalPercentageIncrease = (WOSMA_AverageLocalResult - oldAverageLocalResult)/oldAverageLocalResult * 100;
            float MCPMA_OverallPercentageIncrease = (MCPMA_OverallResult - oldOverallResult)/oldOverallResult * 100;
            float MCPMA_AverageLocalPercentageIncrease = (MCPMA_AverageLocalResult - oldAverageLocalResult)/oldAverageLocalResult * 100;
            float WOSMA_OverallScoreOptimality = WOSMA_OverallResult/MCPMA_OverallResult;
            float WOSMA_AverageLocalScoreOptimality = WOSMA_AverageLocalResult/MCPMA_AverageLocalResult;

            int WOSMA_amtSWIChainsExecuted = matching.getAmtSWIChainsExecuted();
            int WOSMA_amtSWICyclesExecuted = matching.getAmtSWICyclesExecuted();
            float WOSMA_averageSWIChainLength = matching.getAverageSWIChainLength();
            float WOSMA_averageSWICycleLength = matching.getAverageSWICycleLength();

            wmComparisonResult = new WMComparisonResult(oldOverallResult, WOSMA_OverallResult,
                    WOSMA_OverallPercentageIncrease, MCPMA_OverallResult, MCPMA_OverallPercentageIncrease,
                    oldAverageLocalResult, WOSMA_AverageLocalResult, WOSMA_AverageLocalPercentageIncrease,
                    MCPMA_AverageLocalResult, MCPMA_AverageLocalPercentageIncrease, WOSMA_amtSWIChainsExecuted,
                    WOSMA_amtSWICyclesExecuted, WOSMA_averageSWIChainLength, WOSMA_averageSWICycleLength,
                    WOSMA_OverallScoreOptimality,
                    WOSMA_AverageLocalScoreOptimality);

        } catch (HousingMarket.FreeSpaceException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.IDNotPresentException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (MinCostPerfectMatchingAlgorithm.BipartiteSidesUnequalSizeException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (Household.InvalidHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchingNotEmptyException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (CycleFinder.FullyExploredVertexDiscoveredException e) {
            e.printStackTrace();
        }

        return wmComparisonResult;
    }

    public static void runDynamicMatching() throws IOException {
        String inputFileName = "../../../Olivier Data [On Laptop]//test2.csv";
        String outputFilename = "../test.csv";

        ArrayList<DynamicMatchingComparisonResult> dynamicMatchingComparisonResults
                = new ArrayList<DynamicMatchingComparisonResult>();
        ArrayList<Integer> startLines = new ArrayList<Integer>(Arrays.asList(0, 250, 500, 750));
        for (int startLine : startLines) {
            dynamicMatchingComparisonResults.add(individualRunDynamicMatching(inputFileName,startLine, 250));
        }
        DynamicMatchingComparisonResultProcessor dynamicMatchingComparisonResultProcessor
                = new DynamicMatchingComparisonResultProcessor(dynamicMatchingComparisonResults);
        dynamicMatchingComparisonResultProcessor.resultsToCSV(outputFilename);

    }

    public static DynamicMatchingComparisonResult individualRunDynamicMatching(String filename, int startLine, int lineCount) {
        int timestepCount = lineCount/2-1;
        HousingMarket housingMarket = null;
        DynamicMatchingComparisonResult dynamicMatchingComparisonResult = null;
        try {
            housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            Matching matching = dataProcessor.csvToMatching(filename, 0.5, startLine, lineCount);
            DynamicMatching dynamicMatching = new DynamicMatching(matching, lineCount/2, true);
            Matching matching0 = dynamicMatching.getInitialMatching();
            Matching matching1 = dynamicMatching.advanceTimeAndSolvePerStep(timestepCount, false);
            Matching matching2 = dynamicMatching.advanceTimeFullyThenSolve(timestepCount, true);
            Matching matching3 = new MinCostPerfectMatchingAlgorithm(dynamicMatching.getInputMatching())
                    .findMinCostPerfectMatching(false);

            MatchingEvaluator matchingEvaluator0 = new MatchingEvaluator(matching0);
            MatchingEvaluator matchingEvaluator1 = new MatchingEvaluator(matching1);
            MatchingEvaluator matchingEvaluator2 = new MatchingEvaluator(matching2);
            MatchingEvaluator matchingEvaluator3 = new MatchingEvaluator(matching3);

            float score0 = matchingEvaluator0.evaluateAverageIndividualTotalFit(true); // Solved initial matching
            float score1 = matchingEvaluator1.evaluateAverageIndividualTotalFit(true); // Solved final matching, per step
            float score2 = matchingEvaluator2.evaluateAverageIndividualTotalFit(true); // Solved final matching, afterwards
            float score3 = matchingEvaluator3.evaluateAverageIndividualTotalFit(true); // Optimal result

            dynamicMatchingComparisonResult
                    = new DynamicMatchingComparisonResult(score0, score1, score2, score3);

        } catch (HousingMarket.FreeSpaceException e) {
            e.printStackTrace();
        } catch (Household.InvalidHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (DynamicMatching.TooManyTimestepsException e) {
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (CycleFinder.FullyExploredVertexDiscoveredException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchingNotEmptyException e) {
            e.printStackTrace();
        } catch (MinCostPerfectMatchingAlgorithm.BipartiteSidesUnequalSizeException e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (Matching.IDNotPresentException e) {
            e.printStackTrace();
        }

        return dynamicMatchingComparisonResult;
    }

    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}