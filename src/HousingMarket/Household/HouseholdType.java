package HousingMarket.Household;

public class HouseholdType {

    private int type;

    public HouseholdType(int type) throws NoHouseholdTypeFoundException {
        if(type < 0 || type > 5) {
            throw new NoHouseholdTypeFoundException("Type not found!");
        }
        else {
            this.type = type;
        }
    }

    public int getType(){
        return this.type;
    }

    public void readType() {
        switch(type) {
            case 0:
                System.out.println("1-persoons");
                break;
            case 1:
                System.out.println("2-persoons");
                break;
            case 2:
                System.out.println("hh-1 kind");
                break;
            case 3:
                System.out.println("hh-2 kind");
                break;
            case 4:
                System.out.println("hh-3 of meer kind");
                break;
            case 5:
                System.out.println("overig");
                break;
        }
    }

    public class NoHouseholdTypeFoundException extends Exception {
        public NoHouseholdTypeFoundException(String errorMessage) {
            super(errorMessage);
        }
    }
}
