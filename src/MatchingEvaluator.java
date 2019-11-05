public class MatchingEvaluator {

    private Matching matching;
//    Takes Matching and evaluates based on several criteria.
    public MatchingEvaluator(Matching matching) {
        this.matching = matching;
    }

    public void evaluateHouseless() {
        this.matching.getHouses();
        // TODO: finish
    }

    public void evaluateHouseholdless() {
        // TODO: finish
    }

    public void evaluateFinancialFit() {
        // TODO: finish
    }



    public static void test() {System.out.println("Test executed!");}
}
