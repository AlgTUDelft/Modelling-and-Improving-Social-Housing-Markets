import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;

public class Main {

    public static void main(String[] args){
        HousingMarket housingMarket = new HousingMarket(2018);
        Matching matching = new Matching(housingMarket);
        seedTestMatching(matching);
        MatchingEvaluator matchingEvaluator= new MatchingEvaluator(matching);
        try {
            System.out.println(matchingEvaluator.evaluateHouseholdlessHouses());
            System.out.println(matchingEvaluator.evaluateHouselessHouseholds());
        } catch (MatchingEvaluator.InvalidMatchingException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void seedTestMatching(Matching matching) {
        House house1 = new House();
        House house2 = new House();
        Household household1 = new Household();
        matching.addHouse(house1);
        matching.addHouse(house2);
        matching.addHousehold(household1);
        try {
            matching.connect(house1, household1);
        } catch (Matching.HouseOrHouseholdAlreadyMatchedException e) {
            System.err.println(e.getMessage());
        }
    }

    // TODO:
    //  * Finish MatchingEvaluator class
    //  * Create test example
    //  * Evaluate test example
}
