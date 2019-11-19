import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.Household.RegistrationTimeType;


//    Takes Matching and evaluates based on several criteria.
public class MatchingEvaluator {

    private Matching matching;

    public MatchingEvaluator(Matching matching) {
        this.matching = matching;
    }


    public void evaluateIndividualFinancialFit() {
        // TODO: finish individual
    }

    public float evaluateIndividualRoomFit(House house, Household household) {
        float fit;
        if (house.getRoomCount() <= household.getTotalHouseholdCount() &&
                household.getTotalHouseholdCount() <= house.getRoomCount() + 1) {
            fit = 100;
        }
        else if (house.getRoomCount() < household.getTotalHouseholdCount()) {
            fit = 0;}
        else { fit = (house.getRoomCount() + 1)/household.getTotalHouseholdCount() * 100; }
        return fit;
    }

    public void evaluateIndividualSizeFit(House house, Household household) {

    }

    public float evaluateOverallHouselessHouseholds() throws InvalidMatchingException {
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

    public float evaluateOverallHouseholdlessHouses() throws InvalidMatchingException {
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

    public void evaluateOverallSizeFit() throws Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseholdLinkedToMultipleException {

//        for (Household household : this.matching.getHouseholds()) {
//            House house = matching.getHouseFromHousehold(household);
//            float fit = evaluateIndividualSizeFit(house, household);
//
//            // TODO: finish overall
//        }

    }

    public void evaluateOverallFinancialFit() {
        // TODO: finish overall
    }

    public float evaluateOverallAccessibilityFit()
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

    public void evaluateOverallRegistrationTime() {
//        Hashmap<RegistrationTimeType, ArrayList<>>
//        for (RegistrationTimeType type:
//             RegistrationTimeType.values()
//             ) {
//
//        }
        // TODO: finish.
        //  * (Run, for each RegistrationTimeType, all other base-level evaluators.)
    }

    public void evaluateOverallHouseSize() {
        // TODO: finish
    }

    public void evaluateOverallHouseRoomCount() {
        // TODO: finish
    }

    public void evaluateOverallHouseRent() {
        // TODO: finish
    }

    public void evaluateOverallHouseholdType() {
        // TODO: finish
    }

    public void evaluateOverallHouseholdAge() {
        // TODO: finish
    }

    public void evaluateOverallHouseholdIncome() {
        // TODO: finish
    }

    public void evaluateOverallPriority() {
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
