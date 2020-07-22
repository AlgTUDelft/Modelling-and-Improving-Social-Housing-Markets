package Matching;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Main.GradingStrategy;

import java.io.Serializable;


//    Takes Matching and evaluates based on several criteria.
public class MatchingEvaluator implements Serializable {

    private Matching matching;
    private GradingStrategy gradingStrategy;

    public MatchingEvaluator(Matching matching, GradingStrategy gradingStrategy) {
        this.matching = matching;
        this.gradingStrategy = gradingStrategy;
    }


    public float evaluateIndividualFinancialFit(int houseID, int householdID)
    throws HouseholdIncomeTooHighException {
        House house = this.matching.getHouse(houseID);
        Household household = this.matching.getHousehold(householdID);
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

    public float evaluateIndividualRoomFit(int houseID, int householdID) {
        House house = this.matching.getHouse(houseID);
        Household household = this.matching.getHousehold(householdID);
        float fit;
        if (house.getRoomCount() <= household.getTotalHouseholdCount() &&
                household.getTotalHouseholdCount() <= house.getRoomCount() + 1) {
            fit = 1;
        }
        else if (house.getRoomCount() < household.getTotalHouseholdCount()) {
            fit = 0;}
        else { fit = 0; }
        return fit;
    }

    public float evaluateIndividualAccessibilityFit(int houseID, int householdID) {
        House house = this.matching.getHouse(houseID);
        Household household = this.matching.getHousehold(householdID);
        float fit = 0;
        if (household.getAge() >= 65) {
            if (house.getAccessibility()) {
                fit = 1;
            }
        } else { fit = 1; }
        return fit;
    }

    public float evaluateIndividualTotalFit(int houseID, int householdID)
    throws HouseholdIncomeTooHighException {
        float financialIndividualFit = evaluateIndividualFinancialFit(houseID, householdID);
        float roomIndividualFit = evaluateIndividualRoomFit(houseID, householdID);
        float accessibilityIndividualFit = evaluateIndividualAccessibilityFit(houseID, householdID);

//         TODO: _individualTotalFit_ calculation method open to revision and addition; currently based on nothing.
        float individualTotalFit = 0;
        switch (this.gradingStrategy) {
            case MatchingEvaluatorMIN: individualTotalFit = Math.min(Math.min(
                    financialIndividualFit, roomIndividualFit),
                    accessibilityIndividualFit); break;
            case MatchingEvaluatorAVG: individualTotalFit = (financialIndividualFit + roomIndividualFit + accessibilityIndividualFit) / 3; break;
        }
        return individualTotalFit;
    }

    public float evaluateOverallHouselessHouseholds() throws InvalidMatchingException {
        float householdsCount = this.matching.getHouseholds().size();

        int houselessHouseholdsCount = 0;
        try {
            for (Household household: this.matching.getHouseholds()
            ) {
                if (this.matching.getHouseFromHousehold(household.getID()) == null) {
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
                if (this.matching.getHouseholdFromHouse(house.getID()) == null) {
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

    public float evaluateOverallAccessibilityFit(boolean printOutput)
            throws Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseholdLinkedToMultipleException {
        float householdsAbove65WithHouses = 0;
        float householdsAbove65WithHousesAndAccessibility = 0;
        for (Household household : this.matching.getHouseholds()) {
            House house = matching.getHouseFromHousehold(household.getID());
            if (house != null) {
                if (household.getAge() >= 65) {
                    float individualFit = evaluateIndividualAccessibilityFit(house.getID(), household.getID());
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
            if(printOutput) {
                System.out.println(result * 100 + "% of households above the age of 65 that own houses, own houses that are accessible.");
            }
        } else if(printOutput) {
            System.out.println("Matching does not contain households above the age of 65 that own houses.");
        }
        return result;
    }

    public float evaluateAverageIndividualTotalFit(boolean printOutput)
            throws Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException,
            HouseholdIncomeTooHighException {
        float sum = 0;
        float amt = 0;
        for (Household household : matching.getHouseholds()) {
            House house = matching.getHouseFromHousehold(household.getID());
            float fit = 0;
            if (house != null) {
                fit = evaluateIndividualTotalFit(house.getID(), household.getID());
            }
            sum+= fit;
            amt++;
        }
        float result = sum/amt;
        if(printOutput) {
            System.out.println("Average individual total fit is: " + result);
        }
        return result;
    }

    public float evaluateAveragePriority(boolean printOutput)
            throws Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException,
            HouseholdIncomeTooHighException {

        float sum = 0;
        float amt = 0;
        for (Household household : matching.getHouseholds()) {
            if (household.getPriority()) {
                House house = matching.getHouseFromHousehold(household.getID());
                if (house != null) {
                    sum+= evaluateIndividualTotalFit(house.getID(), household.getID());
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
            if(printOutput) {
                System.out.println("Average total fit of households with priority is: " + result);
            }
        } else if(printOutput) { System.out.println("No households with priority present."); }
        return result;
    }

    public float evaluateTotal(boolean printOutput) throws HouseholdIncomeTooHighException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {
        // TODO: _overallTotalFit_ calculation method open to revision and addition; currently based on nothing.
        float averageIndividualTotalFit = evaluateAverageIndividualTotalFit(printOutput);
        float averagePriorityFit = evaluateAveragePriority(printOutput);
        float overallAccessibilityFit = evaluateOverallAccessibilityFit(printOutput);

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

        // TODO: Integrate houseless households and householdless houses metrics into total?

        if(printOutput) {
            System.out.println("Weighted total matching quality is: " + weightedTotalFit);
            System.out.println("");
        }
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
