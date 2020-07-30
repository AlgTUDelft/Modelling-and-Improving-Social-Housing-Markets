
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.*;
import Main.Experimenter;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, HousingMarket.FreeSpaceException, Matching.HouseAlreadyMatchedException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, DynamicMatching.TooManyTimestepsException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToHouseException {
        /*
        MAIN FUNCTION
        -------------

        This set-up runs the default experiments (as described in my thesis).

        The structure of this project is as follows.

        Main calls Experimenter, which sets up various DynamicMatching instances using DataProcessor,
        which assigns each dynamic matching a Grader function,
        which is generated in GraderCreator and may make use of MatchingEvaluator, depending on the GradingStrategy used.
        Experimenter then runs experiments using Comparer.
        Comparer lets each algorithm run on the given experiment set-up using Runner
        (though will try to interrupt the algorithm at various points after _allowedRunningTime_ has passed),
        evaluates their performance by applying the matchings' pre-assigned Grader function
        on the algorithms' output matchings,
        and saves the result in a simple csv-file in _outputfolder_ (which is defined below).
        WARNING: _outputfolder_ is emptied at the start of every run of Experimenter. Be careful!

        DataProcessor is the only class in this project that takes external data. See its _csvToMatching_-function.

        A DynamicMatching, when given an AlgorithmStrategy and some GradingStrategy,
        runs said algorithm on the matching, returns a copy of the (non-dynamic) output matching,
        and then resets itself.
         */

        // Customize these:
        String inputFileName = "../../Data/Input/test2.csv";
        String outputfolder = "../../Data/Output/Scores/";

        int nTimes = 50; // Amount of matchings generated per configuration.
        long allowedRunningTime = 1_000; // Time an experiment for _nTimes_ matchings is allowed to run.
        int maxVal = 150; // Maximum lineCount.
        float timestepRatio = 2/3f; // Ratio of time-steps in a dynamic matching that have not yet been taken.
                                    // Uses H or F, whichever is lowest.
        // ---------------------------------------------------------------------------------------------------


        int[] startLines = new int[50];
        int counter = 0;
        for (int i = 0; i < 50; i++) {
            startLines[i] = counter;
            counter = counter + (1000 - maxVal) / nTimes; // == 17 with maxVal == 150 & nTimes == 50.
        }

        ArrayList<Double> envRatios = new ArrayList<>(Arrays.asList(0.5, 0.75, 1.0, 1.25, 1.5));

        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 25, 30, 35, 40, 45, 50, 75, 100, 125, 150));
//        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12));
//        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(20, 25, 30, 35, 40, 45, 50, 75, 100, 125, 150));

        Experimenter experimenter = new Experimenter(inputFileName, outputfolder, allowedRunningTime, nTimes, startLines, envRatios, lineCounts, timestepRatio);
        experimenter.runExperiments();
    }
}