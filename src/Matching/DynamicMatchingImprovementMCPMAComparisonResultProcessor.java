package Matching;

import Algorithms.Miscellaneous.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicMatchingImprovementMCPMAComparisonResultProcessor {

    private ArrayList<DynamicMatchingImprovementMCPMAComparisonResult> dynamicMatchingImprovementMCPMAComparisonResults;
    private static final char CSV_SEPARATOR = ";".charAt(0);

    public DynamicMatchingImprovementMCPMAComparisonResultProcessor(ArrayList<DynamicMatchingImprovementMCPMAComparisonResult> dynamicMatchingImprovementMCPMAComparisonResults) {
        this.dynamicMatchingImprovementMCPMAComparisonResults = dynamicMatchingImprovementMCPMAComparisonResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList(
                "timeStepCount",
                "findMaxFailed",
                "solvedFinalMatchingPerStepScore",
                "solvedFinalMatchingAfterwardsScore",
                "superOptimalScore",
                "perStepOptimality",
                "afterwardsOptimality"), CSV_SEPARATOR);

        for (DynamicMatchingImprovementMCPMAComparisonResult dynamicMatchingImprovementMCPMAComparisonResult : dynamicMatchingImprovementMCPMAComparisonResults) {
            List<String> list = new ArrayList<String>();
            list.add(Integer.toString(dynamicMatchingImprovementMCPMAComparisonResult.getTimestepCount()));
            list.add(Float.toString(dynamicMatchingImprovementMCPMAComparisonResult.getSolvedFinalMatchingPerStepScore()));
            list.add(Float.toString(dynamicMatchingImprovementMCPMAComparisonResult.getSolvedFinalMatchingAfterwardsScore()));
            list.add(Float.toString(dynamicMatchingImprovementMCPMAComparisonResult.getSuperOptimalScore()));
            list.add(Float.toString(dynamicMatchingImprovementMCPMAComparisonResult.getPerStepOptimality()));
            list.add(Float.toString(dynamicMatchingImprovementMCPMAComparisonResult.getAfterwardsOptimality()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }

}
