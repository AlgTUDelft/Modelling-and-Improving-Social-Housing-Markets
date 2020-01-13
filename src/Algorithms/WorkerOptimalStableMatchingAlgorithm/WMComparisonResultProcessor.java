package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Algorithms.Miscellaneous.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WMComparisonResultProcessor {

    private ArrayList<WMComparisonResult> WMComparisonResults;
    private static final char CSV_SEPARATOR = ";".charAt(0);

    public WMComparisonResultProcessor(ArrayList<WMComparisonResult> WMComparisonResults) {
        this.WMComparisonResults = WMComparisonResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList("oldOverallScore", "WOSMA_OverallScore", "WOSMA_OverallPercentageImprovement", "MCPMA_OverallScore", "MCPMA_OverallPercentageImprovement", "oldAverageLocalScore", "WOSMA_AverageLocalScore", "WOSMA_AverageLocalPercentageImprovement", "MCPMA_AverageLocalScore", "MCPMA_AverageLocalPercentageImprovement"), CSV_SEPARATOR);

        for (WMComparisonResult wmComparisonResult : WMComparisonResults) {
            List<String> list = new ArrayList<String>();
            list.add(Float.toString(wmComparisonResult.getOldOverallScore()));
            list.add(Float.toString(wmComparisonResult.getWOSMA_OverallScore()));
            list.add(Float.toString(wmComparisonResult.getWOSMA_OverallPercentageImprovement()));
            list.add(Float.toString(wmComparisonResult.getMCPMA_OverallScore()));
            list.add(Float.toString(wmComparisonResult.getMCPMA_OverallPercentageImprovement()));
            list.add(Float.toString(wmComparisonResult.getOldAverageLocalScore()));
            list.add(Float.toString(wmComparisonResult.getWOSMA_AverageLocalScore()));
            list.add(Float.toString(wmComparisonResult.getWOSMA_AverageLocalPercentageImprovement()));
            list.add(Float.toString(wmComparisonResult.getMCPMA_AverageLocalScore()));
            list.add(Float.toString(wmComparisonResult.getMCPMA_AverageLocalPercentageImprovement()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
