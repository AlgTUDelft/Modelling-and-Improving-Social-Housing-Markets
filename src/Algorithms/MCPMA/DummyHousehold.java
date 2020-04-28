package Algorithms.MCPMA;

import HousingMarket.HousingMarketVertex;

public class DummyHousehold implements HousingMarketVertex {

    private int ID;

    public DummyHousehold(int ID) {
        this.ID = ID;
    }

    public String toString() {
        return "DummyHousehold" + this.ID;
    }

    public int getID() {
        return ID;
    }
}
