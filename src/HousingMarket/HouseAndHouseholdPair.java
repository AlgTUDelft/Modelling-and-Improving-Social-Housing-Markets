package HousingMarket;

import java.util.concurrent.atomic.AtomicLong;

public class HouseAndHouseholdPair {
    private int house;
    private int household;

    public HouseAndHouseholdPair(int house, int household) {
        this.house = house;
        this.household = household;
    }

    @Override
    public boolean equals(Object obj) {
        HouseAndHouseholdPair input = (HouseAndHouseholdPair) obj;
        if (this.house == input.getHouseID() && this.household == input.getHouseholdID()) {
            return true;
        }
        else { return false; }
    }

    // Using Cantor's pairing function.
    @Override
    public int hashCode() {
        int result = 1/2 * (house + household) * (house + household + 1) + household;
        return result;
    }

    public int getHouseID() {
        return this.house;
    }

    public int getHouseholdID() {
        return this.household;
    }

}
