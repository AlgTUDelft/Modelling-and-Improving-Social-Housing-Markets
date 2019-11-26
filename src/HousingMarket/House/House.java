package HousingMarket.House;

import HousingMarket.HousingMarketVertex;

public class House extends HousingMarketVertex {

    //private boolean multiFamilyHome;
    private String municipality;
    private String label;
    private int rent;
    private int roomCount;
    // accessibility <- Groundfloor OR (high floor plus elevator)
    private boolean accessibility;


    public House(String municipality, String label,
                 int rent, int roomCount, boolean accessibility) {

        this.municipality = municipality;
        this.label = label;
        this.rent = rent;
        this.roomCount = roomCount;
        this.accessibility = accessibility;

    }

    public String getMunicipality() { return this.municipality; }

    public String getLabel() { return this.label; }

    public int getMonthlyRent() { return this.rent; }

    public int getYearlyRent() { return this.rent * 12; }

    public int getRoomCount() {
        return this.roomCount;
    }

    public boolean getAccessibility() { return this.accessibility; }

}
