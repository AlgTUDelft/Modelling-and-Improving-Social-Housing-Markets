package Main;

import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.DynamicMatching;
import Matching.Matching;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Experimenter {

    private String outputfolder;
    private long allowedRunningTime;
    private int nTimes;
    private int[] startLines;
    private ArrayList<Double> envRatios;
    private ArrayList<Integer> lineCounts;

    public Experimenter(String outputfolder, long allowedRunningTime, int nTimes,
                        int[] startLines, ArrayList<Double> envRatios, ArrayList<Integer> lineCounts) {
        this.outputfolder = outputfolder;
        this.allowedRunningTime = allowedRunningTime;
        this.nTimes = nTimes;
        this.startLines = startLines;
        this.envRatios = envRatios;
        this.lineCounts = lineCounts;
    }

    public void runExperiments() throws Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, HousingMarket.FreeSpaceException, Matching.HouseAlreadyMatchedException, IOException, DynamicMatching.TooManyTimestepsException, InterruptedException {

        // Delete existing files in output folder.
        File folder = new File(outputfolder);
        for(File file: folder.listFiles())
            if (!file.isDirectory())
                file.delete();

        // Start of execution loop.
        for (double envRatio : envRatios) {
            envRatio = 1.25;
            for (GradingStrategy gradingStrategy : GradingStrategy.values()) {
                HashSet<AlgorithmStrategy> interruptedAlgorithmStrategies = new HashSet<>();

                // For each matching size...
                for (int lineCount : lineCounts) {
                    // Unless there are no more algorithms left to run...
                    if (interruptedAlgorithmStrategies.size() == AlgorithmStrategy.values().length) {
                        break;
                    }

                    Calendar cal = calculateRemainingTime(allowedRunningTime, lineCounts.size(), lineCounts.size() - lineCounts.indexOf(lineCount), AlgorithmStrategy.values().length - interruptedAlgorithmStrategies.size(), envRatios.size() - envRatios.indexOf(envRatio));
                    System.out.println("Updated ETA: " + cal.getTime() + ".");

                    boolean oneSided = false;


                    // Create the dynamic matchings beforehand so all algorithms may run on the same dynamic matchings.
                    ArrayList<DynamicMatching> dynamicMatchings = new ArrayList<DynamicMatching>(nTimes);
                    for (int i = 0; i < nTimes; i++) {
                        Matching matching = setupMatching(1, startLines[i], lineCount, envRatio, gradingStrategy);
                        int timestepCount = (int) (Math.min(matching.getHouses().size(), matching.getHouseholds().size()) / 1.5);
                        DynamicMatching dynamicMatching = new DynamicMatching(matching, timestepCount, oneSided);
                        dynamicMatchings.add(i, dynamicMatching);
                    }


                    // Run and compare all algorithms as necessary, then add newly interrupted algorithms to set.
                    Comparer comparer = new Comparer(dynamicMatchings, allowedRunningTime,
                            lineCount, nTimes, envRatio,
                            gradingStrategy, interruptedAlgorithmStrategies);
                    interruptedAlgorithmStrategies.addAll(comparer.run());

                }
            }
        }
    }


        public static Matching setupMatching(double connectionProb, int startLine, int lineCount, double envRatio, GradingStrategy gradingStrategy) throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, IOException {
            String inputFileName = "../../Data/Input/test2.csv";
            HousingMarket housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            return dataProcessor.csvToMatching(inputFileName, connectionProb, startLine, lineCount, envRatio, gradingStrategy);
        }

        public static Calendar calculateRemainingTime(long allowedRunningTime, int linesCount, int linesLeftCount, int algorithmStrategiesLeft, int envRatiosLeft) {
            long eta = 0;
//            if (matchingEvaluatorStrategiesLeftCount == 1) {
//                eta = allowedRunningTime * linesLeftCount  * algorithmStrategiesLeft;
//            } else {
//                eta = allowedRunningTime * (linesCount * AlgorithmStrategy.values().length + linesLeftCount * algorithmStrategiesLeft);
//            }
            eta = eta * envRatiosLeft;
            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(new Date()); // sets calendar time/date
            cal.add(Calendar.MILLISECOND, (int) eta); // adds time.
            return cal;
        }
}
