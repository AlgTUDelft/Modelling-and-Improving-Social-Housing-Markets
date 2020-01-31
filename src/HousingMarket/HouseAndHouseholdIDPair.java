package HousingMarket;

import java.io.Serializable;

public class HouseAndHouseholdIDPair implements Serializable {
    private int house;
    private int household;

    public HouseAndHouseholdIDPair(int house, int household) {
        this.house = house;
        this.household = household;
    }

    @Override
    public boolean equals(Object obj) {
        HouseAndHouseholdIDPair input = (HouseAndHouseholdIDPair) obj;
        if (this.house == input.getHouseID() && this.household == input.getHouseholdID()) {
            return true;
        }
        else { return false; }
    }

    // Uses Cantor's pairing function, which uniquely maps two integers to some outcome such that no other
    // pair of integers is mapped by this function to that same outcome.
    // Cantor's function is multiplied by 2, which doesn't invalidate the outcome's uniqueness,
    // but which does simplify things for IntelliJ.
    @Override
    public int hashCode() {
        int result = (house + household) * (house + household + 1) + 2 * household;
        return result;
    }

    public int getHouseID() {
        return this.house;
    }

    public int getHouseholdID() {
        return this.household;
    }

}
