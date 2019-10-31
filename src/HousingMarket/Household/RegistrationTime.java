package HousingMarket.Household;

public class RegistrationTime {

    private Integer months;

    public RegistrationTime(Integer months) {
        this.months = months;
    }

    public Integer getMonths(){
        return this.months;
    }

    public void readRegistrationType() {
        if (months == null) {
            System.out.println("Registration time: unknown.");

        }
        else if(months < 6) {
            System.out.println("Registration time: less than 6 months.");
        }
        else if(months < 12) {
            System.out.println("Registration time: between 0.5-1 years.");
        }
        else if(months < 60) {
            System.out.println("Registration time: between 1-5 years.");
        }
        else if(months < 120) {
            System.out.println("Registration time: between 5-10 years.");
        }
        else if(months < 180) {
            System.out.println("Registration time: between 10-15 years.");
        }
        else {
            System.out.println("Registration time: more than 15 years.");
        }
    }
}
