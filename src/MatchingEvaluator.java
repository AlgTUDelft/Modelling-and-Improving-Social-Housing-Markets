import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.Household.RegistrationTimeType;

import java.lang.reflect.Array;
import java.util.ArrayList;


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
            if (income <= 22700) {
                if (monthlyRent <= 607.46) {
                    fit = 1;
                } else { fit = 0; }
            }
            else if (income <= 42436) {
                if (monthlyRent <= 720.42) {
                    fit = 1;
                } else { fit = 0; }
            }
            else {
                throw new HouseholdIncomeTooHighException("Maximum income is 42436, household's income is " + income);
            }
        } else if (hhCount == 2) {
            if (income <= 30825) {
                if (monthlyRent <= 607.46) {
                    fit = 1;
                } else { fit = 0; }
            }
            else if (income <= 42436) {
                if (monthlyRent <= 720.42) {
                    fit = 1;
                } else { fit = 0; }
            } else {
                throw new HouseholdIncomeTooHighException("Maximum income is 42436, household's income is " + income);
            }
        } else {
            if (income <= 30825) {
                if (monthlyRent <= 651.03) {
                    fit = 1;
                } else { fit = 0; }
            }
            else if (income <= 42436) {
                if (monthlyRent <= 720.42) {
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

    public float evaluateIndividualAccessibilityFit(House house, Household household) {
        float fit = 0;
        if (household.getAge() >= 65) {
            if (house.getAccessibility()) {
                fit = 1;
            }
        } else { fit = 1; }
        return fit;
    }

    public float evaluateIndividualTotalFit(House house, Household household)
    throws HouseholdIncomeTooHighException {
        float financialIndividualFit = evaluateIndividualFinancialFit(house, household);
        float roomIndividualFit = evaluateIndividualRoomFit(house, household);
        float accessibilityIndividualFit = evaluateIndividualAccessibilityFit(house, household);

        // TODO: _individualTotalFit_ calculation method open to revision and addition; currently based on nothing.
        float individualTotalFit = Math.min(Math.min(
                financialIndividualFit, roomIndividualFit),
                accessibilityIndividualFit);
        return individualTotalFit;
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

    public float evaluateOverallAccessibilityFit()
            throws Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseholdLinkedToMultipleException {
        float householdsAbove65WithHouses = 0;
        float householdsAbove65WithHousesAndAccessibility = 0;
        for (Household household : this.matching.getHouseholds()) {
            House house = matching.getHouseFromHousehold(household);
            if (house != null) {
                if (household.getAge() >= 65) {
                    float individualFit = evaluateIndividualAccessibilityFit(house, household);
                    householdsAbove65WithHouses++;
                    if (Float.compare(individualFit, 1) == 0) {
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

    public float evaluateAverageIndividualTotalFit()
            throws Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException,
            HouseholdIncomeTooHighException {
        float sum = 0;
        float amt = 0;
        for (Household household : matching.getHouseholds()) {
            House house = matching.getHouseFromHousehold(household);
            float fit = 0;
            if (house != null) {
                fit = evaluateIndividualTotalFit(house, household);
            }
            sum+= fit;
            amt++;
        }
        float result = sum/amt;
        System.out.println("Average individual total fit is: " + result);
        return result;
    }

    public float evaluateAveragePriority()
            throws Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException,
            HouseholdIncomeTooHighException {

        float sum = 0;
        float amt = 0;
        for (Household household : matching.getHouseholds()) {
            if (household.getPriority()) {
                House house = matching.getHouseFromHousehold(household);
                if (house != null) {
                    sum+= evaluateIndividualTotalFit(house, household);
                    amt++;
                }
                else {
                    // Fit-value of houseless household is 0,
                    // so 0 is implicitly 'added' to _sum_.
                    amt++;
                }
            }
        }
        float result = 0;
        if (amt > 0) {
            result = sum / amt;
            System.out.println("Average total fit of households with priority is: " + result);
        } else { System.out.println("No households with priority present."); }
        return result;
    }

    public void evaluateOverallHouseholdType() {
        // TODO: finish evaluator.
    }

    public void evaluateOverallHouseholdIncome() {
        // TODO: finish evaluator. (Measure mixing in different neighborhoods.)
    }

    public float evaluateTotal() throws HouseholdIncomeTooHighException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {
        // TODO: _overallTotalFit_ calculation method open to revision and addition; currently based on nothing.
        float averageIndividualTotalFit = evaluateAverageIndividualTotalFit();
        float averagePriorityFit = evaluateAveragePriority();
        float overallAccessibilityFit = evaluateOverallAccessibilityFit();

        // Describes the marginal extra importance we allot to the fit of households with priority.
        // TODO: _marginalPriorityBonus_ and _marginalElderlyBonus_ values open to revision;
        //  currently based on nothing.
        double marginalPriorityBonus = 0.3;
        double marginalElderlyBonus = 0.1;
        // The following calculation
        float weightedTotalFit = (float) (averageIndividualTotalFit * matching.getHouseholds().size()
                    + averagePriorityFit * matching.getHouseholdsWithPriority().size() * marginalPriorityBonus
                    + overallAccessibilityFit * matching.getElderlyHouseholds().size() * marginalElderlyBonus)
                / (float) (matching.getHouseholds().size()
                    + matching.getHouseholdsWithPriority().size() * marginalPriorityBonus
                    + matching.getElderlyHouseholds().size() * marginalElderlyBonus);

        // TODO: Integrate houseless households and householdless houses metrics into total.

        System.out.println("Weighted total matching quality is: " + weightedTotalFit);
        return weightedTotalFit;
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
