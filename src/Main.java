import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import HousingMarket.Household.HouseholdType;

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
        House house1 = new House(false, 500,3,50, false, 2011);
        House house2 = new House(false, 600, 4, 60, true, 2016);
        Household household1 = new Household(43, 21000, HouseholdType.TWO, 12 * 5);
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
    //  * Check which data you might want to include [See Evernote] [See HTML]
    //  * Create new possible fits
    //  * Create new goals to test for
    //  * Create test example
    //  * Evaluate test example
    //  * Finish MatchingEvaluator class
}
