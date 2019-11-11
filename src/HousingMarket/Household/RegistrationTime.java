package HousingMarket.Household;

public class RegistrationTime {

    private Integer months;
    private RegistrationTimeType registrationTimeType;

    public RegistrationTime(Integer months) {
        this.months = months;

        if (months == null) {
            this.registrationTimeType = RegistrationTimeType.UNKNOWN;
        }
        else if(months < 6) {
            this.registrationTimeType = RegistrationTimeType.SIX_MONTHS_OR_LESS;
        }
        else if(months < 12) {
            this.registrationTimeType = RegistrationTimeType.SIX_TO_TWELVE_MONTHS;
        }
        else if(months < 60) {
            this.registrationTimeType = RegistrationTimeType.ONE_TO_FIVE_YEARS;
        }
        else if(months < 120) {
            this.registrationTimeType = RegistrationTimeType.FIVE_TO_TEN_YEARS;
        }
        else if(months < 180) {
            this.registrationTimeType = RegistrationTimeType.TEN_TO_FIFTEEN_YEARS;
        }
        else {
            this.registrationTimeType = RegistrationTimeType.FIFTEEN_YEARS_OR_MORE;
        }
    }

    public Integer getMonths(){
        return this.months;
    }

    public RegistrationTimeType getRegistrationTimeType() { return this.registrationTimeType; }

    public void readRegistrationTimeType() {
        if (this.registrationTimeType == RegistrationTimeType.UNKNOWN) {
            System.out.println("Registration time: unknown.");
        }
        else if(this.registrationTimeType == RegistrationTimeType.SIX_MONTHS_OR_LESS) {
            System.out.println("Registration time: less than 6 months.");
        }
        else if(this.registrationTimeType == RegistrationTimeType.SIX_TO_TWELVE_MONTHS) {
            System.out.println("Registration time: between 0.5-1 years.");
        }
        else if(this.registrationTimeType == RegistrationTimeType.ONE_TO_FIVE_YEARS) {
            System.out.println("Registration time: between 1-5 years.");
        }
        else if(this.registrationTimeType == RegistrationTimeType.FIVE_TO_TEN_YEARS) {
            System.out.println("Registration time: between 5-10 years.");
        }
        else if(this.registrationTimeType == RegistrationTimeType.TEN_TO_FIFTEEN_YEARS) {
            System.out.println("Registration time: between 10-15 years.");
        }
        else {
            System.out.println("Registration time: more than 15 years.");
        }
    }
}
