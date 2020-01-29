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

    public static Matching setupMatching(double connectionProb, int startLine, int lineCount) throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, IOException {
        String inputFileName = "../../../Olivier Data [On Laptop]//test2.csv";
        HousingMarket housingMarket = new HousingMarket(2017, 100);
        DataProcessor dataProcessor = new DataProcessor(housingMarket);
        return dataProcessor.csvToMatching(inputFileName, 0.9, startLine, lineCount);
    }

    public static void runDynamicMatching() throws IOException {
        String outputFilename = "../test2.csv";

        ArrayList<DynamicMatchingComparisonResult> dynamicMatchingComparisonResults
                = new ArrayList<DynamicMatchingComparisonResult>();
        ArrayList<Integer> startLines = new ArrayList<Integer>(Arrays.asList(0, 125, 250, 375, 500, 625, 750, 875));
        for (int startLine : startLines) {
            dynamicMatchingComparisonResults.add(individualRunDynamicMatching(startLine, 125));
        }
        DynamicMatchingComparisonResultProcessor dynamicMatchingComparisonResultProcessor
                = new DynamicMatchingComparisonResultProcessor(dynamicMatchingComparisonResults);
        dynamicMatchingComparisonResultProcessor.resultsToCSV(outputFilename);

    }

    public static DynamicMatchingComparisonResult individualRunDynamicMatching(int startLine, int lineCount) {
        int timestepCount = lineCount/2 -1;
        DynamicMatchingComparisonResult dynamicMatchingComparisonResult = null;
        try {
            double connectionProb = 0.6;
            Matching matching = setupMatching(connectionProb, startLine, lineCount);
            DynamicMatching dynamicMatching = new DynamicMatching(matching, timestepCount, false);

            Matching[] matchings = new Matching[4];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(timestepCount, false);
            System.out.println("Got here! 0 " + matchings[0].getFindMaxFailed());
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(timestepCount, false,false);
            System.out.println("Got here! 1 " + matchings[1].getFindMaxFailed());
            dynamicMatching.resetState();
            matchings[2] = dynamicMatching.advanceTimeFullyThenSolve(timestepCount, true,false);
            System.out.println("Got here! 2 " + matchings[2].getFindMaxFailed());
            matchings[3] = new MinCostPerfectMatchingAlgorithm((Matching) deepClone(dynamicMatching.getInputMatching()))
                    .findMinCostPerfectMatching(false);
            System.out.println("Got here! 3 " + matchings[3].getFindMaxFailed());

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);
            String[] strings = {
                    "Final per step score",
                    "Final afterwards score",
                    "Final afterwards + findMax score",
                    "Optimal score" };

            prettyPrintResults(strings, scores);

            dynamicMatchingComparisonResult
                    = new DynamicMatchingComparisonResult(matchings[2].getFindMaxFailed(), scores[0], scores[1], scores[2], scores[3]);

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

    public static float[] evaluateMatchingsAverageIndividualTotalFit(Matching[] matchings) throws MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {

        MatchingEvaluator[] matchingEvaluators = new MatchingEvaluator[matchings.length];
        for (int i = 0; i < matchings.length; i++) {
            matchingEvaluators[i] = new MatchingEvaluator(matchings[i]);
        }

        float[] scores = new float[matchings.length];
        for (int i = 0; i < matchings.length; i++) {
            scores[i] = matchingEvaluators[i].evaluateAverageIndividualTotalFit(false);
        }
        return scores;
    }

    public static void prettyPrintResults(String[] strings, float[] scores) {
        int maxStringLength = 0;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].length() > maxStringLength) {
                maxStringLength = strings[i].length();
            }
        }
        for (int i = 0; i < strings.length; i++) {
            System.out.printf("%" + maxStringLength + "s: %10f%n", strings[i], scores[i]);
        }
        System.out.println();
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