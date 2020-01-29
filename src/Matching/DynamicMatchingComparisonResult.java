package Matching;

public class DynamicMatchingComparisonResult {

    private boolean findMaxFailed;
    private float solvedFinalMatchingPerStepScore;
    private float solvedFinalMatchingAfterwardsScore;
    private float solvedFinalMatchingAfterwardsFindMaxScore;
    private float superOptimalScore;

    public DynamicMatchingComparisonResult(boolean findMaxFailed,
                                           float solvedFinalMatchingPerStepScore,
                                           float solvedFinalMatchingAfterwardsScore, float solvedFinalMatchingAfterwardsFindMaxScore,
                                           float superOptimalScore) {
        this.findMaxFailed = findMaxFailed;
        this.solvedFinalMatchingPerStepScore = solvedFinalMatchingPerStepScore;
        this.solvedFinalMatchingAfterwardsScore = solvedFinalMatchingAfterwardsScore;
        this.solvedFinalMatchingAfterwardsFindMaxScore = solvedFinalMatchingAfterwardsFindMaxScore;
        this.superOptimalScore = superOptimalScore;
    }

    public boolean isFindMaxFailed() {
        return findMaxFailed;
    }

    public float getSolvedFinalMatchingPerStepScore() {
        return solvedFinalMatchingPerStepScore;
    }

    public float getSolvedFinalMatchingAfterwardsScore() {
        return solvedFinalMatchingAfterwardsScore;
    }

    public float getSolvedFinalMatchingAfterwardsFindMaxScore() {
        return solvedFinalMatchingAfterwardsFindMaxScore;
    }

    public float getSuperOptimalScore() {
        return superOptimalScore;
    }
}
