package Algorithms.MinCostPerfectMatchingAlgorithm;

public class MinCostPerfectMatchingResult {

    private float oldScore;
    private float newScore;
    private float percentageImprovement;

    public MinCostPerfectMatchingResult(float oldScore, float newScore, float percentageImprovement) {
        this.oldScore = oldScore;
        this.newScore = newScore;
        this.percentageImprovement = percentageImprovement;
    }

    public float getOldScore() {
        return oldScore;
    }

    public float getNewScore() {
        return newScore;
    }

    public float getPercentageImprovement() {
        return percentageImprovement;
    }
}
