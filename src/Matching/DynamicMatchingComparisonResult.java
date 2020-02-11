package Matching;

public class DynamicMatchingComparisonResult {

    private int timestepCount;
    private boolean findMaxFailed;
    private float solvedFinalMatchingPerStepScore;
    private float solvedFinalMatchingPerStepFindMaxScore;
    private float solvedFinalMatchingAfterwardsScore;
    private float solvedFinalMatchingAfterwardsFindMaxScore;
    private float superOptimalScore;
    private float perStepOptimality;
    private float perStepFindMaxOptimality;
    private float afterwardsOptimality;
    private float afterwardsFindMaxOptimality;

    public DynamicMatchingComparisonResult(int timestepCount,
                                           boolean findMaxFailed,
                                           float solvedFinalMatchingPerStepScore,
                                           float solvedFinalMatchingPerStepFindMaxScore,
                                           float solvedFinalMatchingAfterwardsScore,
                                           float solvedFinalMatchingAfterwardsFindMaxScore,
                                           float superOptimalScore,
                                           float perStepOptimality,
                                           float perStepFindMaxOptimality,
                                           float afterwardsOptimality,
                                           float afterwardsFindMaxOptimality) {
        this.timestepCount = timestepCount;
        this.findMaxFailed = findMaxFailed;
        this.solvedFinalMatchingPerStepScore = solvedFinalMatchingPerStepScore;
        this.solvedFinalMatchingPerStepFindMaxScore = solvedFinalMatchingPerStepFindMaxScore;
        this.solvedFinalMatchingAfterwardsScore = solvedFinalMatchingAfterwardsScore;
        this.solvedFinalMatchingAfterwardsFindMaxScore = solvedFinalMatchingAfterwardsFindMaxScore;
        this.superOptimalScore = superOptimalScore;
        this.perStepOptimality = perStepOptimality;
        this.perStepFindMaxOptimality = perStepFindMaxOptimality;
        this.afterwardsOptimality = afterwardsOptimality;
        this.afterwardsFindMaxOptimality = afterwardsFindMaxOptimality;
    }

    public int getTimestepCount() {
        return timestepCount;
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

    public float getPerStepOptimality() {
        return perStepOptimality;
    }

    public float getAfterwardsOptimality() {
        return afterwardsOptimality;
    }

    public float getAfterwardsFindMaxOptimality() {
        return afterwardsFindMaxOptimality;
    }

    public float getSolvedFinalMatchingPerStepFindMaxScore() {
        return solvedFinalMatchingPerStepFindMaxScore;
    }

    public float getPerStepFindMaxOptimality() {
        return perStepFindMaxOptimality;
    }
}
