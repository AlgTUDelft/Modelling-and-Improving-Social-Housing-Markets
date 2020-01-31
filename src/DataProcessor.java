import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.Household.HouseholdType;
import HousingMarket.HousingMarket;
import HousingMarket.HouseAndHouseholdPair;
import Matching.Matching;

import java.io.*;
import java.util.Random;

// Adapted from: https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/


public class DataProcessor {

    private Matching matching;

    public DataProcessor(HousingMarket housingMarket) {
        this.matching = new Matching(housingMarket);
    }

    public Matching csvToMatching(String csvFileName, double connectionProb, int startLine, int linesToParse)
            throws Household.InvalidHouseholdException,
            Matching.HouseAlreadyMatchedException,
            Matching.HouseholdAlreadyMatchedException, IOException {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        String[] columnNames;

        boolean firstLine = false;
        if (startLine == 0) {
            firstLine = true;
        }

        int currentLine = 0;
        br = new BufferedReader(new FileReader(csvFileName));
        while (currentLine < startLine) {
            br.readLine();
            currentLine++;
        }

        try {
            while (linesToParse > 0) {
                if ((line = br.readLine()) != null) {
                    if (firstLine) {
                        columnNames = line.split(cvsSplitBy);
                        firstLine = false;
                    } else {
                        HouseAndHouseholdPair houseAndHouseholdPair = readSingleLine(line, cvsSplitBy);
                        if (houseAndHouseholdPair != null) {
                            House house = houseAndHouseholdPair.getHouse();
                            Household household = houseAndHouseholdPair.getHousehold();
                            this.matching.addHouse(house);
                            this.matching.addHousehold(household);
                            // With _connectionProb_ chance, connect these houses.
                            if (new Random().nextDouble() <= connectionProb) {
                                this.matching.connect(house.getID(), household.getID());
                            }
                        }
                    }
                }
                linesToParse--;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Matching.HouseIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdIDAlreadyPresentException e) {
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

    public HouseAndHouseholdPair readSingleLine(String line, String cvsSplitBy) throws Household.InvalidHouseholdException {
        String[] row = line.split(cvsSplitBy, -1);

        String houseMunicipality = row[0];
        String houseLabel = row[1];
        int rent = Integer.parseInt(row[2]);
        int roomCount = Integer.parseInt(row[4].substring(0, 1));
        boolean accessibility;
        if (row[1].equals("senioren woning") || row[1].equals("seniorenwoning")
                || row[1].equals("Miva-woning") || row[5].equals("Begane grond") || row[6].equals("Ja")) {
            accessibility = true;
        } else {
            accessibility = false;
        }

        House house = new House(houseMunicipality, houseLabel, rent, roomCount, accessibility);

        String householdMunicipality = row[7];
        String postalCode = row[8];
        String householdLabel = row[9];
        int income = Integer.parseInt(row[10]);
        // TODO: Incorporate year. (Probably move this functionality elsewhere.)
        if (income > 42436) {
            return null;
        }
        int age = Integer.parseInt(row[12]);
        HouseholdType householdType;
        if (row[14].equals("1-persoons")) {
            householdType = HouseholdType.ONE;
        } else if (row[14].equals("2-persoons")) {
            householdType = HouseholdType.TWO;
        } else if (row[14].equals("hh-1 kind")) {
            householdType = HouseholdType.HH1;
        } else if (row[14].equals("hh-2 kind")) {
            householdType = HouseholdType.HH2;
        } else if (row[14].equals("hh-3 of meer kind")) {
            householdType = HouseholdType.HH3PLUS;
        } else {
            householdType = HouseholdType.OTHER;
        }
        int totalHouseholdCount = Integer.parseInt(row[15]);
        boolean priority = false;
        if (row[16].equals("sociaal-medisch urgent")) {
            priority = true;
        }

        Household household = new Household(householdMunicipality, postalCode, householdLabel,
                income, age, householdType, totalHouseholdCount, priority);

        HouseAndHouseholdPair houseAndHouseholdPair = new HouseAndHouseholdPair(house, household);
        return houseAndHouseholdPair;
    }
}

