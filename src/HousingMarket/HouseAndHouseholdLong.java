package HousingMarket;

import java.util.concurrent.atomic.AtomicLong;

public class HouseAndHouseholdLong {
    AtomicLong house;
    AtomicLong household;

    public HouseAndHouseholdLong(AtomicLong house, AtomicLong household) {
        this.house = house;
        this.household = household;
    }
}
