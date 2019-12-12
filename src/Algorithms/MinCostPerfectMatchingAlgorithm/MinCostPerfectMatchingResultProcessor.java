package Algorithms.MinCostPerfectMatchingAlgorithm;

import Algorithms.Miscellaneous.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinCostPerfectMatchingResultProcessor {

    private ArrayList<MinCostPerfectMatchingResult> minCostPerfectMatchingAlgorithmResults;
    private static final char CSV_SEPARATOR = ";".charAt(0);

    public MinCostPerfectMatchingResultProcessor(ArrayList<MinCostPerfectMatchingResult> minCostPerfectMatchingAlgorithmResults) {
        this.minCostPerfectMatchingAlgorithmResults = minCostPerfectMatchingAlgorithmResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList("oldScore", "newScore", "percentageImprovement"), CSV_SEPARATOR);

        for (MinCostPerfectMatchingResult minCostPerfectMatchingResult : minCostPerfectMatchingAlgorithmResults) {
            List<String> list = new ArrayList<String>();
            list.add(Float.toString(minCostPerfectMatchingResult.getOldScore()));
            list.add(Float.toString(minCostPerfectMatchingResult.getNewScore()));
            list.add(Float.toString(minCostPerfectMatchingResult.getPercentageImprovement()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
