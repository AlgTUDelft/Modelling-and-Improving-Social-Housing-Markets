package Comparisons;

import Miscellaneous.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenericResultProcessor {


    protected ArrayList<GenericResult> genericResults;
    protected static final char CSV_SEPARATOR = ";".charAt(0);

    public GenericResultProcessor(ArrayList<GenericResult> genericResults) {
        this.genericResults = genericResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList(
                "solvedFinalMatchingPerStepScore",
                "solvedFinalMatchingAfterwardsScore"), CSV_SEPARATOR);

        for (GenericResult genericResult : genericResults) {
            List<String> list = new ArrayList<String>(2);
            list.add(Float.toString(genericResult.getSolvedFinalMatchingPerStepScore()));
            list.add(Float.toString(genericResult.getSolvedFinalMatchingAfterwardsScore()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
