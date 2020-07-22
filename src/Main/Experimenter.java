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
    private float timestepRatio;

    public Experimenter(String outputfolder, long allowedRunningTime, int nTimes,
                        int[] startLines, ArrayList<Double> envRatios, ArrayList<Integer> lineCounts, float timestepRatio) {
        this.outputfolder = outputfolder;
        this.allowedRunningTime = allowedRunningTime;
        this.nTimes = nTimes;
        this.startLines = startLines;
        this.envRatios = envRatios;
        this.lineCounts = lineCounts;
        this.timestepRatio = timestepRatio;
    }

    public void runExperiments() throws Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, HousingMarket.FreeSpaceException, Matching.HouseAlreadyMatchedException, IOException, DynamicMatching.TooManyTimestepsException, InterruptedException {

        // Delete existing files in output folder.
        File folder = new File(outputfolder);
        for(File file: folder.listFiles())
            if (!file.isDirectory())
                file.delete();

        // Start of execution loop.
        for (double envRatio : envRatios) {
            HashMap<GradingStrategy, HashSet<AlgorithmStrategy>> interruptedAlgorithmStrategies = new HashMap();
            for (GradingStrategy gradingStrategy : GradingStrategy.values()) {
                interruptedAlgorithmStrategies.put(gradingStrategy, new HashSet<>());
            }
            for (int lineCount : lineCounts) {

                boolean oneSided = false;


                // Create the dynamic matchings beforehand so all algorithms may run on the same dynamic matchings.
                // Note that these also already will have generated samples for each gradingStrategy to draw from.
                ArrayList<DynamicMatching> dynamicMatchings = new ArrayList<DynamicMatching>(nTimes);
                for (int i = 0; i < nTimes; i++) {
                    Matching matching = setupMatching(1, startLines[i], lineCount, envRatio);
                    int timestepCount = (int) (Math.min(matching.getHouses().size(), matching.getHouseholds().size()) * timestepRatio);
                    DynamicMatching dynamicMatching = new DynamicMatching(matching, timestepCount, oneSided);
                    dynamicMatchings.add(i, dynamicMatching);
                }

                // For each matching size...
                for (GradingStrategy gradingStrategy : GradingStrategy.values()) {
                    // Unless there are no more algorithms left to run...
                    if (interruptedAlgorithmStrategies.get(gradingStrategy).size() == AlgorithmStrategy.values().length) {
                        break;
                    }

                    List<GradingStrategy> gradingStrategies = Arrays.asList(GradingStrategy.values());
                    Calendar cal = calculateRemainingTime(allowedRunningTime, lineCounts.size(),
                            lineCounts.size() - lineCounts.indexOf(lineCount),
                            AlgorithmStrategy.values().length - interruptedAlgorithmStrategies.size(),
                            envRatios.size() - envRatios.indexOf(envRatio),
                            GradingStrategy.values().length - gradingStrategies.indexOf(gradingStrategy));
                    System.out.println("Updated ETA: " + cal.getTime() + ".");




                    // Run and compare all algorithms as necessary, then add newly interrupted algorithms to set.
                    Comparer comparer = new Comparer(dynamicMatchings, allowedRunningTime,
                            lineCount, nTimes, envRatio,
                            gradingStrategy, interruptedAlgorithmStrategies.get(gradingStrategy));
                    HashSet<AlgorithmStrategy> newSet = interruptedAlgorithmStrategies.get(gradingStrategy);
                    newSet.addAll(comparer.run());
                    interruptedAlgorithmStrategies.put(gradingStrategy, newSet);

                }
            }
        }
    }


        private static Matching setupMatching(double connectionProb, int startLine, int lineCount, double envRatio) throws HousingMarket.FreeSpaceException, Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, IOException {
            String inputFileName = "../../Data/Input/test2.csv";
            HousingMarket housingMarket = new HousingMarket(2017, 100);
            DataProcessor dataProcessor = new DataProcessor(housingMarket);
            return dataProcessor.csvToMatching(inputFileName, connectionProb, startLine, lineCount, envRatio);
        }

        private static Calendar calculateRemainingTime(long allowedRunningTime, int linesCount, int linesLeftCount, int algorithmStrategiesLeft, int envRatiosLeft, int gradingStrategiesLeft) {

            // Current envRatio+gradingstrategy combination:
            long eta = allowedRunningTime * linesLeftCount * algorithmStrategiesLeft;
            // Future combinations:
            if (gradingStrategiesLeft > 0) {
                eta += allowedRunningTime * linesCount * AlgorithmStrategy.values().length * gradingStrategiesLeft * envRatiosLeft;
            } else {
                eta += allowedRunningTime * linesCount * AlgorithmStrategy.values().length * GradingStrategy.values().length * (envRatiosLeft - 1);
            }

            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(new Date()); // sets calendar time/date
            cal.add(Calendar.MILLISECOND, (int) eta); // adds time.
            return cal;
        }
}
