package Matching;

public class DynamicMatchingImprovementMCPMAComparisonResult {

    private int timestepCount;
    private float solvedFinalMatchingPerStepScore;
    private float solvedFinalMatchingAfterwardsScore;
    private float superOptimalScore;
    private float perStepOptimality;
    private float afterwardsOptimality;

    public DynamicMatchingImprovementMCPMAComparisonResult(
            int timestepCount,
            float solvedFinalMatchingPerStepScore,
            float solvedFinalMatchingAfterwardsScore,
            float superOptimalScore,
            float perStepOptimality,
            float afterwardsOptimality) {
        this.timestepCount = timestepCount;
        this.solvedFinalMatchingPerStepScore = solvedFinalMatchingPerStepScore;
        this.solvedFinalMatchingAfterwardsScore = solvedFinalMatchingAfterwardsScore;
        this.superOptimalScore = superOptimalScore;
        this.perStepOptimality = perStepOptimality;
        this.afterwardsOptimality = afterwardsOptimality;
    }

    public int getTimestepCount() {
        return timestepCount;
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

    public float getPerStepOptimality() {
        return perStepOptimality;
    }

    public float getAfterwardsOptimality() {
        return afterwardsOptimality;
    }
}
