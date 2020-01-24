package Matching;

import Algorithms.Miscellaneous.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicMatchingComparisonResultProcessor {

    private ArrayList<DynamicMatchingComparisonResult> dynamicMatchingComparisonResults;
    private static final char CSV_SEPARATOR = ";".charAt(0);

    public DynamicMatchingComparisonResultProcessor(ArrayList<DynamicMatchingComparisonResult> dynamicMatchingComparisonResults) {
        this.dynamicMatchingComparisonResults = dynamicMatchingComparisonResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList("solvedInitialMatchingScore", "solvedFinalMatchingPerStepScore",
                "solvedFinalMatchingAfterwardsScore", "superOptimalScore"), CSV_SEPARATOR);

        for (DynamicMatchingComparisonResult dynamicMatchingComparisonResult : dynamicMatchingComparisonResults) {
            List<String> list = new ArrayList<String>();
            list.add(Float.toString(dynamicMatchingComparisonResult.getSolvedInitialMatchingScore()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getSolvedFinalMatchingPerStepScore()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getSolvedFinalMatchingAfterwardsScore()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getSuperOptimalScore()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
