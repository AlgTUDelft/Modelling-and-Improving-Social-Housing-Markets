package Algorithms.SimpleImprovement;

import HousingMarket.HousingMarketVertex;

public class DummyHousehold implements HousingMarketVertex {

    private int ID;

    public DummyHousehold(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
}
