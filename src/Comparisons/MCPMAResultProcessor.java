package Comparisons;

import Miscellaneous.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCPMAResultProcessor {

    private ArrayList<MCPMAResult> mcpmaResults;
    private static final char CSV_SEPARATOR = ";".charAt(0);

    public MCPMAResultProcessor(ArrayList<MCPMAResult> mcpmaResults) {
        this.mcpmaResults = mcpmaResults;
    }

    public void resultsToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        CSVUtils.writeLine(writer, Arrays.asList(
                "MCPMAScore"), CSV_SEPARATOR);

        for (MCPMAResult mcpmaResult : mcpmaResults) {
            List<String> list = new ArrayList<String>(1);
            list.add(Float.toString(mcpmaResult.getMCPMAScore()));

            CSVUtils.writeLine(writer, list, CSV_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
}
