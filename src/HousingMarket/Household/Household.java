package HousingMarket.Household;

import HousingMarket.HousingMarketVertex;

public class Household extends HousingMarketVertex {

    private int age;
    private int income;
//    private HouseholdType householdType;
    private int totalHouseholdCount;
    private int childrenCount;
    private LabelType labelType;
    private PersonalityType personalityType;
    private RegistrationTime registrationTime; // In months
    private boolean priority;
    // TODO:
    //  * Add voorrang? [See Interactive_Report.html]
    //  * Hoe meten wij urgentie? -> ("voorrang wz" == "sociaal-medisch urgent") || ("voorrangskenmerk" == "Voorrang")?

    public Household(int age, int income, int totalHouseholdCount, int childrenCount, LabelType labelType, PersonalityType personalityType, int registrationTime, boolean priority) {
        this.age = age;
        this.income = income;
        this.totalHouseholdCount = totalHouseholdCount;
        this.childrenCount = childrenCount;
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


    public RegistrationTime getRegistrationTime() {
        return this.registrationTime;
    }
}
