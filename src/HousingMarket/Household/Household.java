package HousingMarket.Household;

import HousingMarket.HousingMarketVertex;

import java.io.Serializable;

public class Household implements HousingMarketVertex, Serializable {

    private int id;
    private String municipality;
    private String postalCode;
    private String label;
    // _income_: Income per year.
    private int income;
    private int age;
    private HouseholdType householdType;
    private int totalHouseholdCount;
    private boolean priority;
    //private LabelType labelType;
    //private PersonalityType personalityType;
    //private RegistrationTime registrationTime; // In months


    public Household(String municipality, String postalCode, String label,
                     int income, int age, HouseholdType householdType, int totalHouseholdCount,
                     boolean priority) throws InvalidHouseholdException {
        if (age >= 18) {
            this.age = age;
        } else { throw new InvalidHouseholdException("Error: Household age is below legal age.");}

        this.municipality = municipality;
        this.postalCode = postalCode;
        this.label = label;
        this.income = income;
        this.householdType = householdType;
        this.totalHouseholdCount = totalHouseholdCount;
        this.priority = priority;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getMunicipality() {
        return this.municipality;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public String getLabel() {
        return this.label;
    }

    public int getIncome() {
        return this.income;
    }

    public int getAge() {
        return this.age;
    }

    public HouseholdType getHouseholdType() {
        return this.householdType;
    }

    public int getTotalHouseholdCount() {
        return this.totalHouseholdCount;
    }

    public boolean getPriority() {
        return this.priority;
    }

    public class InvalidHouseholdException extends Exception {
        public InvalidHouseholdException(String errorMessage) {
            super(errorMessage);
        }
    }

}
