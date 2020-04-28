package Algorithms.MCPMA;

import HousingMarket.HousingMarketVertex;

public class DummyHouse implements HousingMarketVertex {

    private int ID;

    public DummyHouse(int ID) {
        this.ID = ID;
    }

    public String toString() {
        return "DummyHouse" + this.ID;
    }

    public int getID() {
        return ID;
    }
}
