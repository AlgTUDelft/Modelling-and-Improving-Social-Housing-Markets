package HousingMarket;

import HousingMarket.House.House;
import HousingMarket.Household.Household;

public class HouseAndHouseholdPair {

    private House house;
    private Household household;

    public HouseAndHouseholdPair(House house, Household household) {
        this.house = house;
        this.household = household;
    }

    public House getHouse() {
        return this.house;
    }

    public Household getHousehold() {
        return this.household;
    }
}
