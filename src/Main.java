import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.Household.LabelType;
import HousingMarket.Household.PersonalityType;
import HousingMarket.Household.HouseholdType;
import HousingMarket.HousingMarket;

public class Main {

    public static void main(String[] args){
        try {
            HousingMarket housingMarket = new HousingMarket(2018, 25.0);
            Matching matching = new Matching(housingMarket);
            seedTestMatching(matching);
            MatchingEvaluator matchingEvaluator= new MatchingEvaluator(matching);
            matchingEvaluator.evaluateHouseholdlessHouses();
            matchingEvaluator.evaluateHouselessHouseholds();
        } catch (MatchingEvaluator.InvalidMatchingException
                | HousingMarket.FreeSpaceException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void seedTestMatching(Matching matching) {
        House house1 = new House(false, 500,3,50, false, 2011);
        House house2 = new House(false, 600, 4, 60, true, 2016);
        Household household1 = new Household(43, 25000, 4, 2, LabelType.DOORSTROMER , PersonalityType.RED, 12 * 8, false);
        Household household2 = new Household(20, 20000, 1, 0, LabelType.STARTER, PersonalityType.GREEN, 12 * 1, false);
        Household household3 = new Household(36, 35000, 2, 0, LabelType.DOORSTROMER, PersonalityType.BLUE, 12 * 4, true);
        matching.addHouse(house1);
        matching.addHouse(house2);
        matching.addHousehold(household1);
        matching.addHousehold(household2);
        matching.addHousehold(household3);
        try {
            matching.connect(house1, household1);
        } catch (Matching.HouseAlreadyMatchedException
                | Matching.HouseholdAlreadyMatchedException e) {
            System.err.println(e.getMessage());
        }

    }

    // TODO:
    //  * Check which data you might want to include [See Evernote] [HTML -- DONE]
    //  * Create new possible fits
    //  * Create new goals to test for
    //  * Create test example
    //  * Evaluate test example
    //  * Finish MatchingEvaluator class
}
