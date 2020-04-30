import Algorithms.AlgorithmStrategy;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Comparisons.Compare;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.*;
import static Miscellaneous.DeepCloner.deepClone;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, HousingMarket.FreeSpaceException, Matching.HouseAlreadyMatchedException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, DynamicMatching.TooManyTimestepsException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToHouseException {

        long allowedRunningTime = 15_000;
        int maxVal = 150;
        int nTimes = 50;

        int[] startLines = new int[50];
        int counter = 0;
        for (int i = 0; i < 50; i++) {
            startLines[i] = counter;
            counter = counter + (1000-maxVal)/nTimes; // == 17 with maxVal == 150 & nTimes == 50.
        }

        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 25, 30, 35, 40, 45, 50, 75, 100, 125, 150));
//        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12));
//        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(30, 35, 40, 45, 50, 75, 100, 125, 150));

        // Start of execution loop.
        for (MatchingEvaluatorStrategy matchingEvaluatorStrategy : MatchingEvaluatorStrategy.values()) {
            HashSet<AlgorithmStrategy> interruptedAlgorithmStrategies = new HashSet<>();

            // For each matching size...
            for (int lineCount : lineCounts) {
                int matchingEvalStratsLeftCount;
                if (matchingEvaluatorStrategy == MatchingEvaluatorStrategy.values()[0]) {
                    matchingEvalStratsLeftCount = 2;
                } else { matchingEvalStratsLeftCount = 1; }
                // Unless there are no more algorithms left to run...
                if (interruptedAlgorithmStrategies.size() == AlgorithmStrategy.values().length) {
                    break;
                }
                Calendar cal = calculateRemainingTime(allowedRunningTime, lineCounts.size(), lineCounts.size() - lineCounts.indexOf(lineCount), matchingEvalStratsLeftCount, AlgorithmStrategy.values().length - interruptedAlgorithmStrategies.size());
                System.out.println("Updated ETA: " + cal.getTime() + ".");

                int timestepCount = lineCount/2;
                boolean oneSided = false;


                // Create the dynamic matchings beforehand so all algorithms may run on the same dynamic matchings.
                ArrayList<DynamicMatching> dynamicMatchings = new ArrayList<DynamicMatching>(nTimes);
                for (int i = 0; i < nTimes; i++) {
                    Matching matching = setupMatching(1, startLines[i], lineCount, matchingEvaluatorStrategy);
                    DynamicMatching dynamicMatching = new DynamicMatching(matching, timestepCount, oneSided);
                    dynamicMatchings.add(i, dynamicMatching);
                }

                // For each algorithm...
                for (AlgorithmStrategy algorithmStrategy : AlgorithmStrategy.values()) {
                    if (interruptedAlgorithmStrategies.contains(algorithmStrategy)) {
                        // Algorithm took too long in smaller instance, so don't go on.
                        System.out.println("Skipping:    " + matchingEvaluatorStrategy + " | " + lineCount + " | " + algorithmStrategy);
                        continue;
                    } else {
                        // Run it and check if we were interrupted during execution.
                        ArrayList<DynamicMatching> dynamicMatchingsCopy = (ArrayList<DynamicMatching>) deepClone(dynamicMatchings); // Potentially expensive but seemingly necessary...
                        boolean interrupted = runAlgorithm(dynamicMatchingsCopy, allowedRunningTime, algorithmStrategy, lineCount, nTimes, matchingEvaluatorStrategy);
                        if (interrupted) {
                            System.out.println("Interrupted: " + matchingEvaluatorStrategy + " | " + lineCount + " | " + algorithmStrategy);
                            interruptedAlgorithmStrategies.add(algorithmStrategy);
                        } else {
                            System.out.println("Finished:    " + matchingEvaluatorStrategy + " | " + lineCount + " | " + algorithmStrategy);
                        }
                    }
                }
            }
        }
    }

    public static boolean runAlgorithm(ArrayList<DynamicMatching> dynamicmatchings, long allowedRunningTime, AlgorithmStrategy algorithmStrategy, int lineCount, int nTimes, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws InterruptedException, IOException {
        boolean tookTooLong = false;
        Compare compare = new Compare();
        Thread thread = null;
        switch(algorithmStrategy) {
            case WOSMA_REGULAR: thread = new Thread(compare.runDynamicWOSMARegular(dynamicmatchings, lineCount, nTimes,  matchingEvaluatorStrategy)); break;
            case WOSMA_FINDMAX: thread = new Thread(compare.runDynamicWOSMAFindMax(dynamicmatchings, lineCount, nTimes, matchingEvaluatorStrategy)); break;
            case WOSMA_IRCYCLES: thread = new Thread(compare.runDynamicIRCycles(dynamicmatchings, lineCount, nTimes, matchingEvaluatorStrategy)); break;
            case IMPROVEMENT_MCPMA: thread = new Thread(compare.runDynamicImprovementMCPMA(dynamicmatchings, lineCount, nTimes, matchingEvaluatorStrategy)); break;
            case MCPMA: thread = new Thread(compare.runStaticMCPMA(dynamicmatchings, lineCount, nTimes, matchingEvaluatorStrategy)); break;
        }

        thread.start();
        try {
            thread.join(allowedRunningTime);
        } catch (InterruptedException e) {
            tookTooLong = true;
        }
        return tookTooLong;
    }

    public static Matching setupMatching(double connectionProb, int startLine, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, IOException {
        String inputFileName = "../../../Olivier Data [On Laptop]//test2.csv";
        HousingMarket housingMarket = new HousingMarket(2017, 100);
        DataProcessor dataProcessor = new DataProcessor(housingMarket, matchingEvaluatorStrategy);
        return dataProcessor.csvToMatching(inputFileName, connectionProb, startLine, lineCount);
    }

    public static Calendar calculateRemainingTime(long allowedRunningTime, int linesCount, int linesLeftCount, int matchingEvaluatorStrategiesLeftCount, int algorithmStrategiesLeft) {
        long eta = 0;
        if (matchingEvaluatorStrategiesLeftCount == 1) {
             eta = allowedRunningTime * linesLeftCount  * algorithmStrategiesLeft;
        } else {
            eta = allowedRunningTime * (linesCount * AlgorithmStrategy.values().length + linesLeftCount * algorithmStrategiesLeft);
        }
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.MILLISECOND, (int) eta); // adds time.
        return cal;
    }

}