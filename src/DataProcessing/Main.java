package DataProcessing;

import java.io.*;

// Adapted from: https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/


public class Main {

    public static void main(String[] args) {
        String csvFileName = "../../../Olivier Data [On Laptop]//dbo_V_verhuringen dd 11 jan 17.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(csvFileName));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] row = line.split(cvsSplitBy);

                System.out.println(line);

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
    }
}

