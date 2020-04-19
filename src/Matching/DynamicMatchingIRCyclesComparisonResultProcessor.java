package Matching;

import Algorithms.Miscellaneous.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicMatchingIRCyclesComparisonResultProcessor {

    private ArrayList<DynamicMatchingIRCyclesComparisonResult> dynamicMatchingIRCyclesComparisonResults;
    private static final char CSV_SEPARATOR = ";".charAt(0);

    public DynamicMatchingIRCyclesComparisonResultProcessor(ArrayList<DynamicMatchingIRCyclesComparisonResult> dynamicMatchingIRCyclesComparisonResults) {
        this.dynamicMatchingIRCyclesComparisonResults = dynamicMatchingIRCyclesComparisonResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList(
                "timeStepCount",
                "solvedFinalMatchingPerStepScore",
                "solvedFinalMatchingAfterwardsScore",
                "superOptimalScore",
                "perStepOptimality",
                "afterwardsOptimality"), CSV_SEPARATOR);

        for (DynamicMatchingIRCyclesComparisonResult dynamicMatchingIRCyclesComparisonResult : dynamicMatchingIRCyclesComparisonResults) {
            List<String> list = new ArrayList<String>();
            list.add(Integer.toString(dynamicMatchingIRCyclesComparisonResult.getTimestepCount()));
            list.add(Float.toString(dynamicMatchingIRCyclesComparisonResult.getSolvedFinalMatchingPerStepScore()));
            list.add(Float.toString(dynamicMatchingIRCyclesComparisonResult.getSolvedFinalMatchingAfterwardsScore()));
            list.add(Float.toString(dynamicMatchingIRCyclesComparisonResult.getSuperOptimalScore()));
            list.add(Float.toString(dynamicMatchingIRCyclesComparisonResult.getPerStepOptimality()));
            list.add(Float.toString(dynamicMatchingIRCyclesComparisonResult.getAfterwardsOptimality()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
