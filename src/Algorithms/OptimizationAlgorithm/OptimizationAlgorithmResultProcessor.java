package Algorithms.OptimizationAlgorithm;

import Algorithms.Miscellaneous.CSVUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptimizationAlgorithmResultProcessor {

    private ArrayList<OptimizationAlgorithmResult> optimizationAlgorithmResults;
    private static final char CSV_SEPARATOR = ";".charAt(0);

    public OptimizationAlgorithmResultProcessor(ArrayList<OptimizationAlgorithmResult> optimizationAlgorithmResults) {
        this.optimizationAlgorithmResults = optimizationAlgorithmResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList("oldScore", "newScore", "percentageImprovement", "L", "LPercentage"), CSV_SEPARATOR);

        for (OptimizationAlgorithmResult optimizationAlgorithmResult : optimizationAlgorithmResults) {
            List<String> list = new ArrayList<String>();
            list.add(Float.toString(optimizationAlgorithmResult.getOldScore()));
            list.add(Float.toString(optimizationAlgorithmResult.getNewScore()));
            list.add(Float.toString(optimizationAlgorithmResult.getPercentageImprovement()));
            list.add(Integer.toString(optimizationAlgorithmResult.getL()));
            list.add(Float.toString(optimizationAlgorithmResult.getLPercentage()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
