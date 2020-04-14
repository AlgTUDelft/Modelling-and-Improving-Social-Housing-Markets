package Algorithms.SimpleImprovement;

import HousingMarket.HousingMarketVertex;

public class DummyHouse implements HousingMarketVertex {

    private int ID;

    public DummyHouse(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
}
