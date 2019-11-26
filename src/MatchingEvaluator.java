import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.Household.RegistrationTimeType;


//    Takes Matching and evaluates based on several criteria.
public class MatchingEvaluator {

    private Matching matching;

    public MatchingEvaluator(Matching matching) {
        this.matching = matching;
    }


    public float evaluateIndividualFinancialFit(House house, Household household)
    throws HouseholdIncomeTooHighException {
        // TODO: Incorporate year.
        int year = this.matching.getHousingMarket().getYear(); // Unused
        float fit = 0;

        // Below numbers are valid for 2019. Taken from: "Inkomensgrenzen per 1-1-2019".
        int hhCount = household.getTotalHouseholdCount();
        int income = household.getIncome();
        float monthlyRent = house.getMonthlyRent();
        if (hhCount == 1) {
            if (income < 22700) {
                if (monthlyRent < 607.46) {
                    fit = 1;
                } else { fit = 0; }
            }
            else if (income < 42436) {
                if (monthlyRent < 720.42) {
                    fit = 1;
                } else { fit = 0; }
            }
            else {
                throw new HouseholdIncomeTooHighException("Maximum income is 42436, household's income is " + income);
            }
        } else if (hhCount == 2) {
            if (income < 30825) {
                if (monthlyRent < 607.46) {
                    fit = 1;
                } else { fit = 0; }
            }
            else if (income < 42436) {
                if (monthlyRent < 720.42) {
                    fit = 1;
                } else { fit = 0; }
            } else {
                throw new HouseholdIncomeTooHighException("Maximum income is 42436, household's income is " + income);
            }
        } else {
            if (income < 30825) {
                if (monthlyRent < 651.03) {
                    fit = 1;
                } else { fit = 0; }
            }
            else if (income < 42436) {
                if (monthlyRent < 720.42) {
                    fit = 1;
                } else { fit = 0; }
            } else {
                throw new HouseholdIncomeTooHighException("Maximum income is 42436, household's income is " + income);
            }
        }
        return fit;
    }

    public float evaluateIndividualRoomFit(House house, Household household) {
        float fit;
        if (house.getRoomCount() <= household.getTotalHouseholdCount() &&
                household.getTotalHouseholdCount() <= house.getRoomCount() + 1) {
            fit = 1;
        }
        else if (house.getRoomCount() < household.getTotalHouseholdCount()) {
            fit = 0;}
        else { fit = (house.getRoomCount() + 1)/household.getTotalHouseholdCount() * 100; }
        return fit;
    }


    public float evaluateTotalIndividualFit(House house, Household household)
    throws HouseholdIncomeTooHighException {
        float financialIndividualFit = evaluateIndividualFinancialFit(house, household);
        float roomIndividualFit = evaluateIndividualRoomFit(house, household);

        // TODO: _totalFit_ calculation method open to revision.
        float totalIndividualFit = Math.min(financialIndividualFit, roomIndividualFit);
        return totalIndividualFit;
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
    }

    public void evaluateOverallHouseholdType() {
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

    public class HouseholdIncomeTooHighException extends Exception {
        public HouseholdIncomeTooHighException(String errorMessage) { super(errorMessage); }
    }
}
