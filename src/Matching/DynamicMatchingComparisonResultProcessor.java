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
        CSVUtils.writeLine(writer, Arrays.asList(
                "timeStepCount",
                "findMaxFailed",
                "solvedFinalMatchingPerStepScore",
                "solvedFinalMatchingAfterwardsScore",
                "solvedFinalMatchingAfterwardsFindMaxScore",
                "superOptimalScore",
                "perStepOptimality",
                "afterwardsOptimality",
                "afterwardsFindMaxOptimality"), CSV_SEPARATOR);

        for (DynamicMatchingComparisonResult dynamicMatchingComparisonResult : dynamicMatchingComparisonResults) {
            List<String> list = new ArrayList<String>();
            list.add(Integer.toString(dynamicMatchingComparisonResult.getTimestepCount()));
            list.add(Boolean.toString(dynamicMatchingComparisonResult.isFindMaxFailed()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getSolvedFinalMatchingPerStepScore()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getSolvedFinalMatchingAfterwardsScore()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getSolvedFinalMatchingAfterwardsFindMaxScore()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getSuperOptimalScore()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getPerStepOptimality()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getAfterwardsOptimality()));
            list.add(Float.toString(dynamicMatchingComparisonResult.getAfterwardsFindMaxOptimality()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
