package Comparisons;

import Miscellaneous.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WOSMALikeResultProcessor {


    protected ArrayList<WOSMALikeResult> wosmaLikeResults;
    protected static final char CSV_SEPARATOR = ";".charAt(0);

    public WOSMALikeResultProcessor(ArrayList<WOSMALikeResult> wosmaLikeResults) {
        this.wosmaLikeResults = wosmaLikeResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList(
                "strategyFailed",
                "solvedFinalMatchingPerStepScore",
                "solvedFinalMatchingAfterwardsScore"), CSV_SEPARATOR);

        for (WOSMALikeResult wosmaLikeResult : wosmaLikeResults) {
            List<String> list = new ArrayList<String>();
            list.add(Boolean.toString(wosmaLikeResult.isStrategyFailed()));
            list.add(Float.toString(wosmaLikeResult.getSolvedFinalMatchingPerStepScore()));
            list.add(Float.toString(wosmaLikeResult.getSolvedFinalMatchingAfterwardsScore()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }

}
