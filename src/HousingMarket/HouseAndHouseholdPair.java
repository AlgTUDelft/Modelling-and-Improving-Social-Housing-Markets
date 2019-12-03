package HousingMarket;

import java.util.concurrent.atomic.AtomicLong;

public class HouseAndHouseholdPair {
    private int house;
    private int household;

    public HouseAndHouseholdPair(int house, int household) {
        this.house = house;
        this.household = household;
    }

    public int getHouseID() {
        return this.house;
    }

    public int getHouseholdID() {
        return this.household;
    }

}
