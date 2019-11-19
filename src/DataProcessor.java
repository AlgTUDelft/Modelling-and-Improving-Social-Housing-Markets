import HousingMarket.HousingMarket;

import java.io.*;

// Adapted from: https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/


public class DataProcessor {

    private Matching matching;

    public DataProcessor(HousingMarket housingMarket) {
        this.matching = new Matching(housingMarket);
    }

    public Matching csv_to_data(String csvFileName) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        String[] columnNames;

        boolean firstLine = true;

        try {

            br = new BufferedReader(new FileReader(csvFileName));
            while ((line = br.readLine()) != null) {

                if (firstLine) {
                    columnNames = line.split(cvsSplitBy);
                    firstLine = false;
                }
                else {
                    // use comma as separator
                    String[] row = line.split(cvsSplitBy);

                }


            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.matching;
    }
}

