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


    public House(int rent, int roomCount, boolean accessibility,
                 String municipality, String label) {
        this.rent = rent;
        this.roomCount = roomCount;
        this.accessibility = accessibility;
        this.municipality = municipality;
        this.label = label;

    }

    public String getMunicipality() { return this.municipality; }

    public String getLabel() { return this.label; }

    public int getRent() { return this.rent; }

    public int getRoomCount() {
        return this.roomCount;
    }

    public boolean getAccessibility() { return this.accessibility; }

}
