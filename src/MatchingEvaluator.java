import HousingMarket.House.House;
import HousingMarket.Household.Household;

import java.util.HashSet;

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

    public float evaluateAccessibilityFit()
            throws Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseholdLinkedToMultipleException {
        float householdsAbove65WithHouses = 0;
        float householdsAbove65WithHousesAndAccessibility = 0;
        for (Household household : this.matching.getHouseholds()
             ) {
                if (household.getAge() >= 65) {
                    House house = matching.getHouseFromHousehold(household);
                    if (house != null) {
                        householdsAbove65WithHouses++;
                        if (house.getAccessibility()) {
                            householdsAbove65WithHousesAndAccessibility++;
                        }
                    }
                }
        }
        float result = 0;
        if (householdsAbove65WithHouses > 0) {
            result = householdsAbove65WithHousesAndAccessibility / householdsAbove65WithHouses;
            System.out.println(result * 100 + "% of households above the age of 65 that own houses, own houses that are accessible.");
        } else {
            System.out.println("Matching does not contain households above the age of 65 that own houses.");
        }
        return result;
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

    public void evaluateTotal() {
        // TODO: finish last.
    }

    public class InvalidMatchingException extends Exception {
        public InvalidMatchingException(String errorMessage) {
            super(errorMessage);
        }
    }
}
