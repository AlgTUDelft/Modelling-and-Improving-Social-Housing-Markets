package HousingMarket.Household;

import HousingMarket.House.House;

public class Household {

    private int age;
    private int income;
    private HouseholdType householdType;
    private RegistrationTime registrationTime; // In months


    private House house;

    public Household Household(int age, int income, int householdType, int registrationTime)  throws InvalidHousehold{
        this.age = age;
        this.income = income;
        try {
            this.householdType = new HouseholdType(householdType);
        } catch (HouseholdType.NoHouseholdTypeFoundException e) {
            throw new InvalidHousehold(e.getMessage());
        }
        this.registrationTime = new RegistrationTime(registrationTime);

        return this;
    }

    public House getHouse(){
        return this.house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public HouseholdType getHouseholdType() {
        return this.householdType;
    }
    public void readHouseholdType() {this.householdType.readType();}



    public static void main(String[] args){

    }

    public class InvalidHousehold extends Exception {
        public InvalidHousehold(String errorMessage) {
            super(errorMessage);
        }
    }
}
