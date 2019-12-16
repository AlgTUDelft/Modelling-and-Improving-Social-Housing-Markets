package Algorithms.MinCostPerfectMatchingAlgorithm;

public class MinCostPerfectMatchingResult {

    private float oldOverallScore;
    private float newOverallScore;
    private float overallPercentageImprovement;
    private float oldAverageLocalScore;
    private float newAverageLocalScore;
    private float averageLocalPercentageImprovement;

    public MinCostPerfectMatchingResult(float oldOverallScore, float newOverallScore, float overallPercentageImprovement, float oldAverageLocalScore, float newAverageLocalScore, float averageLocalPercentageImprovement) {
        this.oldOverallScore = oldOverallScore;
        this.newOverallScore = newOverallScore;
        this.overallPercentageImprovement = overallPercentageImprovement;
        this.oldAverageLocalScore = oldAverageLocalScore;
        this.newAverageLocalScore = newAverageLocalScore;
        this.averageLocalPercentageImprovement = averageLocalPercentageImprovement;
    }

    public float getOldOverallScore() {
        return oldOverallScore;
    }

    public float getNewOverallScore() {
        return newOverallScore;
    }

    public float getOverallPercentageImprovement() {
        return overallPercentageImprovement;
    }

    public float getOldAverageLocalScore() {
        return oldAverageLocalScore;
    }

    public float getNewAverageLocalScore() {
        return newAverageLocalScore;
    }

    public float getAverageLocalPercentageImprovement() {
        return averageLocalPercentageImprovement;
    }
}
