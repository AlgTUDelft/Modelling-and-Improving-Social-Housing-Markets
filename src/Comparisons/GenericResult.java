package Comparisons;

public class GenericResult {
    private float solvedFinalMatchingPerStepScore;
    private float solvedFinalMatchingAfterwardsScore;

    public GenericResult(float solvedFinalMatchingPerStepScore,
                         float solvedFinalMatchingAfterwardsScore) {
        this.solvedFinalMatchingPerStepScore = solvedFinalMatchingPerStepScore;
        this.solvedFinalMatchingAfterwardsScore = solvedFinalMatchingAfterwardsScore;
    }

    public float getSolvedFinalMatchingPerStepScore() {
        return solvedFinalMatchingPerStepScore;
    }

    public float getSolvedFinalMatchingAfterwardsScore() {
        return solvedFinalMatchingAfterwardsScore;
    }
}
