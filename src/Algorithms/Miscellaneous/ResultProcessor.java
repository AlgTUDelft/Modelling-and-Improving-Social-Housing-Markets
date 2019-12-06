package Algorithms.Miscellaneous;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultProcessor {

    private ArrayList<Result> results;
    private static final char CSV_SEPARATOR = ";".charAt(0);

    public ResultProcessor(ArrayList<Result> results) {
        this.results = results;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList("oldScore", "newScore", "percentageImprovement", "L", "LPercentage"), CSV_SEPARATOR);

        for (Result result : results) {
            List<String> list = new ArrayList<String>();
            list.add(Float.toString(result.getOldScore()));
            list.add(Float.toString(result.getNewScore()));
            list.add(Float.toString(result.getPercentageImprovement()));
            list.add(Integer.toString(result.getL()));
            list.add(Float.toString(result.getLPercentage()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
