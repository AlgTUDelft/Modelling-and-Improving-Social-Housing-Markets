import Algorithms.AlgorithmStrategy;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Comparisons.Compare;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, Household.InvalidHouseholdException, Matching.Matching.HouseholdAlreadyMatchedException, HousingMarket.FreeSpaceException, Matching.Matching.HouseAlreadyMatchedException, Matching.Matching.HouseholdLinkedToMultipleException, Matching.Matching.HouseholdLinkedToHouseholdException, Matching.DynamicMatching.TooManyTimestepsException, Matching.Matching.HouseLinkedToMultipleException, Matching.MatchingEvaluator.HouseholdIncomeTooHighException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.Matching.PreferredNoHouseholdlessHouseException, Matching.Matching.HouseLinkedToHouseException {

        long allowedRunningTime = 2_000;
        int maxVal = 150;
        int nTimes = 50;

        int[] startLines = new int[50];
        int counter = 0;
        for (int i = 0; i < 50; i++) {
            startLines[i] = counter;
            counter = counter + (1000-maxVal)/nTimes; // == 17 with maxVal == 150 & nTimes == 50.
        }

        // Start of execution loop.
        for (MatchingEvaluatorStrategy matchingEvaluatorStrategy : MatchingEvaluatorStrategy.values()) {
            HashSet<AlgorithmStrategy> interruptedAlgorithmStrategies = new HashSet<>();

            // For each matching size...
            for (int lineCount : new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 25, 30, 35, 40, 45, 50, 75, 100, 125, 150))) {
                int timestepCount = lineCount/2;
                boolean oneSided = false;
                ArrayList<DynamicMatching> dynamicMatchings = new ArrayList<DynamicMatching>();

                // Create the dynamic matchings beforehand so all algorithms may run on the same dynamic matchings.
                for (int i = 0; i < nTimes; i++) {
                    Matching matching = setupMatching(1, startLines[i], lineCount, matchingEvaluatorStrategy);
                    DynamicMatching dynamicMatching = new DynamicMatching(matching, timestepCount, oneSided);
                    dynamicMatchings.add(dynamicMatching);
                }

                // For each algorithm...
                for (AlgorithmStrategy algorithmStrategy : AlgorithmStrategy.values()) {
                    if (interruptedAlgorithmStrategies.contains(algorithmStrategy)) {
                        // Algorithm took too long in smaller instance, so don't go on.
                        break;
                    } else {
                        // Run it and check if we were interrupted during execution.
                        boolean interrupted = runAlgorithm(dynamicMatchings, allowedRunningTime, algorithmStrategy, lineCount, matchingEvaluatorStrategy);
                        if (interrupted) {
                            interruptedAlgorithmStrategies.add(algorithmStrategy);
                        }
                    }
                }
            }
        }
    }

    public static boolean runAlgorithm(ArrayList<DynamicMatching> dynamicmatchings, long allowedRunningTime, AlgorithmStrategy algorithmStrategy, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws InterruptedException, IOException {
        boolean tookTooLong = false;
        Compare compare = new Compare();
        Thread thread = null;
        switch(algorithmStrategy) {
            case WOSMA_REGULAR: thread = new Thread(compare.runDynamicWOSMARegular(dynamicmatchings, lineCount, matchingEvaluatorStrategy)); break;
            case WOSMA_FINDMAX: thread = new Thread(compare.runDynamicWOSMAFindMax(dynamicmatchings, lineCount, matchingEvaluatorStrategy)); break;
            case WOSMA_IRCYCLES: thread = new Thread(compare.runDynamicIRCycles(dynamicmatchings, lineCount, matchingEvaluatorStrategy)); break;
            case IMPROVEMENT_MCPMA: thread = new Thread(compare.runDynamicImprovementMCPMA(dynamicmatchings, lineCount, matchingEvaluatorStrategy)); break;
            case MCPMA: thread = new Thread(compare.runStaticMCPMA(dynamicmatchings, lineCount, matchingEvaluatorStrategy)); break;
        }

        thread.start();      // requirement 3
        Thread.sleep(allowedRunningTime);     // requirement 4
        if (Thread.activeCount() > 2) {
            System.out.println("Alive");
            tookTooLong = true;
        }
        thread.interrupt();  // requirement 5
        thread.join();  // requirement 6
        System.out.println("Got here");
        return tookTooLong;
    }

    public static Matching setupMatching(double connectionProb, int startLine, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, IOException {
        String inputFileName = "../../../Olivier Data [On Laptop]//test2.csv";
        HousingMarket housingMarket = new HousingMarket(2017, 100);
        DataProcessor dataProcessor = new DataProcessor(housingMarket, matchingEvaluatorStrategy);
        return dataProcessor.csvToMatching(inputFileName, connectionProb, startLine, lineCount);
    }

}