package HousingMarket.Household;

import HousingMarket.House.House;
import HousingMarket.HousingMarketVertex;

public class Household extends HousingMarketVertex {

    private int age;
    private int income;
    private HouseholdType householdType;
    private RegistrationTime registrationTime; // In months

    public Household Household(int age, int income, int householdType, int registrationTime)  throws InvalidHouseholdException{
        this.age = age;
        this.income = income;
        try {
            this.householdType = new HouseholdType(householdType);
        } catch (HouseholdType.NoHouseholdTypeFoundException e) {
            throw new InvalidHouseholdException(e.getMessage());
        }
        this.registrationTime = new RegistrationTime(registrationTime);

        return this;
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

    public class InvalidHouseholdException extends Exception {
        public InvalidHouseholdException(String errorMessage) {
            super(errorMessage);
        }
    }
}
