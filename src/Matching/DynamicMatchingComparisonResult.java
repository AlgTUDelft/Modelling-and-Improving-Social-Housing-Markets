package Matching;

public class DynamicMatchingComparisonResult {

    private float solvedInitialMatchingScore;
    private float solvedFinalMatchingPerStepScore;
    private float solvedFinalMatchingAfterwardsScore;
    private float superOptimalScore;

    public DynamicMatchingComparisonResult(float solvedInitialMatchingScore, float solvedFinalMatchingPerStepScore,
                                           float solvedFinalMatchingAfterwardsScore, float superOptimalScore) {
        this.solvedInitialMatchingScore = solvedInitialMatchingScore;
        this.solvedFinalMatchingPerStepScore = solvedFinalMatchingPerStepScore;
        this.solvedFinalMatchingAfterwardsScore = solvedFinalMatchingAfterwardsScore;
        this.superOptimalScore = superOptimalScore;
    }

    public float getSolvedInitialMatchingScore() {
        return solvedInitialMatchingScore;
    }

    public float getSolvedFinalMatchingPerStepScore() {
        return solvedFinalMatchingPerStepScore;
    }

    public float getSolvedFinalMatchingAfterwardsScore() {
        return solvedFinalMatchingAfterwardsScore;
    }

    public float getSuperOptimalScore() {
        return superOptimalScore;
    }
}
