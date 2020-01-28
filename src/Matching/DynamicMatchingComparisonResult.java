package Matching;

public class DynamicMatchingComparisonResult {

    private float solvedInitialMatchingScore;
    private float solvedFinalMatchingPerStepScore;
    private float solvedFinalMatchingAfterwardsScore;
    private float solvedFinalMatchingAfterwardsFindMaxScore;
    private float superOptimalScore;

    public DynamicMatchingComparisonResult(float solvedInitialMatchingScore, float solvedFinalMatchingPerStepScore,
                                           float solvedFinalMatchingAfterwardsScore, float solvedFinalMatchingAfterwardsFindMaxScore,
                                           float superOptimalScore) {
        this.solvedInitialMatchingScore = solvedInitialMatchingScore;
        this.solvedFinalMatchingPerStepScore = solvedFinalMatchingPerStepScore;
        this.solvedFinalMatchingAfterwardsScore = solvedFinalMatchingAfterwardsScore;
        this.solvedFinalMatchingAfterwardsFindMaxScore = solvedFinalMatchingAfterwardsFindMaxScore;
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

    public float getSolvedFinalMatchingAfterwardsFindMaxScore() {
        return solvedFinalMatchingAfterwardsFindMaxScore;
    }

    public float getSuperOptimalScore() {
        return superOptimalScore;
    }
}
