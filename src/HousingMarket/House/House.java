package HousingMarket.House;

import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;

public class House extends HousingMarketVertex {

    private boolean multiFamilyHome;
    private int rent;
    private int rooms;
    private int size;
    private boolean accessibility;
    private int constructionYear;


    public House House(boolean multiFamilyHome, int rent, int rooms,
                       int size, boolean accessibility, int constructionYear) {
        this.multiFamilyHome = multiFamilyHome;
        this.rent = rent;
        this.rooms = rooms;
        this.size = size;
        this.accessibility = accessibility;
        this.constructionYear = constructionYear;

        return this;
    }

    public boolean getMultiFamilyHome() {
        return this.multiFamilyHome;
    }

    public int getRent() { return this.rent; }

    public int getSize() { return this.size; }

    public boolean getAccessibility() { return this.accessibility; }

    public int getConstructionYear() { return this.constructionYear; }

}
