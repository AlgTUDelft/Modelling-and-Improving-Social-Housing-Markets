package HousingMarket.Household;

public enum HouseholdType {
    ONE, TWO, HH1, HH2, HH3PLUS, OTHER;

    public void readType() {
        switch(this) {
            case ONE:
                System.out.println("1-persoons");
                break;
            case TWO:
                System.out.println("2-persoons");
                break;
            case HH1:
                System.out.println("hh-1 kind");
                break;
            case HH2:
                System.out.println("hh-2 kind");
                break;
            case HH3PLUS:
                System.out.println("hh-3 of meer kind");
                break;
            case OTHER:
                System.out.println("overig");
                break;
        }
    }

    public HouseholdType getType() {
        return this;
    }
}