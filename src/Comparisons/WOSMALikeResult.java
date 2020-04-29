package Comparisons;

public class WOSMALikeResult extends GenericResult {

    private boolean strategyFailed;

    public WOSMALikeResult(boolean strategyFailed, float solvedFinalMatchingPerStepScore, float solvedFinalMatchingAfterwardsScore) {
        super(solvedFinalMatchingPerStepScore, solvedFinalMatchingAfterwardsScore);
        this.strategyFailed = strategyFailed;
    }

    public boolean isStrategyFailed() {
        return strategyFailed;
    }
}
