import HousingMarket.House.House;
import HousingMarket.Household.Household;

public class MatchingEvaluator {

    private Matching matching;
//    Takes Matching and evaluates based on several criteria.
    public MatchingEvaluator(Matching matching) {
        this.matching = matching;
    }

    public float evaluateHouselessHouseholds() throws InvalidMatchingException {
        float householdsCount = this.matching.getHouseholds().size();

        int houselessHouseholdsCount = 0;
        try {
            for (Household household: this.matching.getHouseholds()
                 ) {
                    if (this.matching.getHouseFromHousehold(household) == null) {
                        houselessHouseholdsCount++;
                    }
                    else continue;
                }
            } catch (Matching.HouseholdLinkedToHouseholdException
                | Matching.HouseholdLinkedToMultipleException e) {
            throw new InvalidMatchingException(e.getMessage());
        }

        return houselessHouseholdsCount/householdsCount;
    }

    public float evaluateHouseholdlessHouses() throws InvalidMatchingException {
        float housesCount;
        try {
            housesCount = this.matching.getHouses().size();
        } catch (NullPointerException e) {
            throw new InvalidMatchingException("Matching is null.");
        }

        int householdlessHousesCount = 0;
        try {
            for (House house: this.matching.getHouses()
            ) {
                if (this.matching.getHouseholdFromHouse(house) == null) {
                    householdlessHousesCount++;
                }
                else continue;
            }
        } catch (Matching.HouseLinkedToHouseException
                | Matching.HouseLinkedToMultipleException e) {
            throw new InvalidMatchingException(e.getMessage());
        }

        return householdlessHousesCount/housesCount;
    }

    public void evaluateFinancialFit() {
        // TODO: finish
    }



    public class InvalidMatchingException extends Exception {
        public InvalidMatchingException(String errorMessage) {
            super(errorMessage);
        }
    }
}
