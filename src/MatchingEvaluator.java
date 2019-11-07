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

        float result = houselessHouseholdsCount/householdsCount;
        System.out.println(result * 100 + "% of households have no house.");
        return result;
    }

    public float evaluateHouseholdlessHouses() throws InvalidMatchingException {
        float housesCount;
        try {
            housesCount = this.matching.getHouses().size();
        } catch (NullPointerException e) {
            throw new InvalidMatchingException("Error: Matching is null.");
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

        float result = householdlessHousesCount/housesCount;
        System.out.println(result * 100 + "% of houses have no household.");
        return result;
    }

    public void evaluateFinancialFit() {
        // TODO: finish
    }

    public void evaluateSizeFit() {
        // TODO: finish; rooms and meters.
    }

    public void evaluateAccessibilityFit() {
        // TODO: finish
    }

    public void evaluateDistributionOverHouseSize() {
        // TODO: finish
    }

    public void evaluateDistributionOverHouseRoomCount() {
        // TODO: finish
    }

    public void evaluateDistributionOverHouseRent() {
        // TODO: finish
    }

    public void evaluateDistributionOverHouseholdType() {
        // TODO: finish
    }

    public void evaluateDistributionOverHouseholdAge() {
        // TODO: finish
    }

    public void evaluateDistributionOverHouseholdIncome() {
        // TODO: finish
    }

    public void evaluateDistributionOverPriority() {
        // TODO: finish
    }

    public void evaluateDistributionOverRegistrationTime() {
        // TODO: finish
    }


    public class InvalidMatchingException extends Exception {
        public InvalidMatchingException(String errorMessage) {
            super(errorMessage);
        }
    }
}
