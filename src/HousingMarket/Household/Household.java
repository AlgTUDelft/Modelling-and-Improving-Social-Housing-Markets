package HousingMarket.Household;

import HousingMarket.HousingMarketVertex;

public class Household extends HousingMarketVertex {

    private int age;
    private int income;
    private HouseholdType householdType;
    private int totalHouseholdCount;
    private int childrenCount;
    private LabelType labelType;
    private PersonalityType personalityType;
    private RegistrationTime registrationTime; // In months
    private boolean priority;
    // TODO:
    //  * Add voorrang? [See Interactive_Report.html]
    //  * Hoe meten wij urgentie? -> ("voorrang wz" == "sociaal-medisch urgent") || ("voorrangskenmerk" == "Voorrang")?

    public Household(int age, int income, HouseholdType householdType, int totalHouseholdCount, int childrenCount,
                     LabelType labelType, PersonalityType personalityType, int registrationTime,
                     boolean priority) throws InvalidHouseholdException {
        if (age >= 18) {
            this.age = age;
        } else { throw new InvalidHouseholdException("Error: Household age is below legal age.");}

        this.income = income;
        this.householdType = householdType;
        if (totalHouseholdCount > childrenCount) {
            this.totalHouseholdCount = totalHouseholdCount;
            this.childrenCount = childrenCount;
        } else { throw new InvalidHouseholdException("Error: Household contains no adults.");}

        this.labelType = labelType;
        this.personalityType = personalityType;
        this.registrationTime = new RegistrationTime(registrationTime);
        this.priority = priority;
    }

    public int getAge() {
        return this.age;
    }

    public int getIncome() {
        return this.income;
    }

    public int getTotalHouseholdCount() {
        return this.totalHouseholdCount;
    }

    public RegistrationTime getRegistrationTime() {
        return this.registrationTime;
    }

    public RegistrationTimeType getRegistrationTimeType() {
        return this.registrationTime.getRegistrationTimeType();
    }

    public class InvalidHouseholdException extends Exception {
        public InvalidHouseholdException(String errorMessage) {
            super(errorMessage);
        }
    }

}
