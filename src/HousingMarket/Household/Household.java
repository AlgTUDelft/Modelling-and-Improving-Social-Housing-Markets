package HousingMarket.Household;

import HousingMarket.House.House;
import HousingMarket.HousingMarketVertex;

public class Household extends HousingMarketVertex {

    private int age;
    private int income;
    private HouseholdType householdType;
    private RegistrationTime registrationTime; // In months

    public Household(int age, int income, HouseholdType householdType, int registrationTime) {
        this.age = age;
        this.income = income;
        this.householdType = householdType;
        this.registrationTime = new RegistrationTime(registrationTime);
    }

    public int getAge() {
        return this.age;
    }

    public int getIncome() {
        return this.income;
    }

    public HouseholdType getHouseholdType() {
        return this.householdType;
    }
    public void readHouseholdType() {this.householdType.readType();}

    public RegistrationTime getRegistrationTime() {
        return this.registrationTime;
    }

    public static void main(String[] args){

    }
}
