package HousingMarket.Household;

import HousingMarket.House.House;
import HousingMarket.HousingMarketVertex;

public class Household extends HousingMarketVertex {

    private int age;
    private int income;
    private HouseholdType householdType;
    private LabelType labelType;
    private PersonalityType personalityType;
    private RegistrationTime registrationTime; // In months
    // TODO: Add voorrang [See Interactive_Report.html]

    public Household(int age, int income, HouseholdType householdType, LabelType labelType, PersonalityType personalityType, int registrationTime) {
        this.age = age;
        this.income = income;
        this.householdType = householdType;
        this.labelType = labelType;
        this.personalityType = personalityType;
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
