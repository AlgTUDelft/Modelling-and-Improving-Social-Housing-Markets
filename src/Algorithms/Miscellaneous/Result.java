package Algorithms.Miscellaneous;

public class Result {
    private float oldScore;
    private float newScore;
    private float percentageImprovement;
    private int L;
    private float LPercentage;

    public Result(float oldScore, float newScore, float percentageImprovement, int L, float LPercentage) {
        this.oldScore = oldScore;
        this.newScore = newScore;
        this.percentageImprovement = percentageImprovement;
        this.L = L;
        this.LPercentage = LPercentage;
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

    public int getL() {
        return L;
    }

    public float getLPercentage() {
        return LPercentage;
    }
}
