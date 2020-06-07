
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.*;
import Main.Experimenter;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, Household.InvalidHouseholdException, Matching.HouseholdAlreadyMatchedException, HousingMarket.FreeSpaceException, Matching.HouseAlreadyMatchedException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, DynamicMatching.TooManyTimestepsException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToHouseException {

        String outputfolder = "../../Data/Output/Scores/";

        long allowedRunningTime =3600_000;
        int maxVal = 150;
        int nTimes = 1;
        float timestepRatio = 2/3f;

        int[] startLines = new int[nTimes];
        int counter = 0;
        for (int i = 0; i < nTimes; i++) {
            startLines[i] = counter;
            counter = counter + (1000 - maxVal) / nTimes; // == 17 with maxVal == 150 & nTimes == 50.
        }

//        ArrayList<Double> envRatios = new ArrayList<>(Arrays.asList(0.5, 0.75, 1.0, 1.25, 1.5));
        ArrayList<Double> envRatios = new ArrayList<>(Arrays.asList(0.5));

        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(100, 125, 150, 175, 200, 225, 250, 300, 350, 400, 450, 500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 12500, 15000, 17500, 20000));
//        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12));
//        ArrayList<Integer> lineCounts = new ArrayList<>(Arrays.asList(20, 25, 30, 35, 40, 45, 50, 75, 100, 125, 150));

        Experimenter experimenter = new Experimenter(outputfolder, allowedRunningTime, nTimes, startLines, envRatios, lineCounts, timestepRatio);
        experimenter.runExperiments();
    }
}