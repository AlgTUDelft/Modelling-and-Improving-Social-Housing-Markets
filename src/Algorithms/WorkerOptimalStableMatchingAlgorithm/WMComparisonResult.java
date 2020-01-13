package Algorithms.WorkerOptimalStableMatchingAlgorithm;

public class WMComparisonResult {

    private float oldOverallScore;
    private float WOSMA_OverallScore;
    private float WOSMA_OverallPercentageImprovement;
    private float MCPMA_OverallScore;
    private float MCPMA_OverallPercentageImprovement;
    private float oldAverageLocalScore;
    private float WOSMA_AverageLocalScore;
    private float WOSMA_AverageLocalPercentageImprovement;
    private float MCPMA_AverageLocalScore;
    private float MCPMA_AverageLocalPercentageImprovement;
    private int WOSMA_amtSWIChainsExecuted;
    private int WOSMA_amtSWICyclesExecuted;
    private float WOSMA_averageSWIChainLength;
    private float WOSMA_averageSWICycleLength;
    private float WOSMA_OverallScoreOptimality;
    private float WOSMA_AverageLocalScoreOptimality;

    public float getWOSMA_OverallScoreOptimality() {
        return WOSMA_OverallScoreOptimality;
    }

    public float getWOSMA_AverageLocalScoreOptimality() {
        return WOSMA_AverageLocalScoreOptimality;
    }

    public WMComparisonResult(float oldOverallScore, float WOSMA_OverallScore, float WOSMA_OverallPercentageImprovement,
                              float MCPMA_OverallScore, float MCPMA_OverallPercentageImprovement,
                              float oldAverageLocalScore, float WOSMA_AverageLocalScore,
                              float WOSMA_AverageLocalPercentageImprovement, float MCPMA_AverageLocalScore,
                              float MCPMA_AverageLocalPercentageImprovement, int WOSMA_amtSWIChainsExecuted,
                              int WOSMA_amtSWICyclesExecuted,
                              float WOSMA_averageSWIChainLength, float WOSMA_averageSWICycleLength,
                              float WOSMA_OverallScoreOptimality,
                              float WOSMA_AverageLocalScoreOptimality) {
        this.oldOverallScore = oldOverallScore;
        this.WOSMA_OverallScore = WOSMA_OverallScore;
        this.WOSMA_OverallPercentageImprovement = WOSMA_OverallPercentageImprovement;
        this.MCPMA_OverallScore = MCPMA_OverallScore;
        this.MCPMA_OverallPercentageImprovement = MCPMA_OverallPercentageImprovement;
        this.oldAverageLocalScore = oldAverageLocalScore;
        this.WOSMA_AverageLocalScore = WOSMA_AverageLocalScore;
        this.WOSMA_AverageLocalPercentageImprovement = WOSMA_AverageLocalPercentageImprovement;
        this.MCPMA_AverageLocalScore = MCPMA_AverageLocalScore;
        this.MCPMA_AverageLocalPercentageImprovement = MCPMA_AverageLocalPercentageImprovement;
        this.WOSMA_amtSWIChainsExecuted = WOSMA_amtSWIChainsExecuted;
        this.WOSMA_amtSWICyclesExecuted = WOSMA_amtSWICyclesExecuted;
        this.WOSMA_averageSWIChainLength = WOSMA_averageSWIChainLength;
        this.WOSMA_averageSWICycleLength = WOSMA_averageSWICycleLength;
        this.WOSMA_OverallScoreOptimality = WOSMA_OverallScoreOptimality;
        this.WOSMA_AverageLocalScoreOptimality = WOSMA_AverageLocalScoreOptimality;
    }

    public int getWOSMA_amtSWIChainsExecuted() {
        return WOSMA_amtSWIChainsExecuted;
    }

    public int getWOSMA_amtSWICyclesExecuted() {
        return WOSMA_amtSWICyclesExecuted;
    }

    public float getWOSMA_averageSWIChainLength() {
        return WOSMA_averageSWIChainLength;
    }

    public float getWOSMA_averageSWICycleLength() {
        return WOSMA_averageSWICycleLength;
    }

    public float getOldOverallScore() {
        return oldOverallScore;
    }

    public float getWOSMA_OverallScore() {
        return WOSMA_OverallScore;
    }

    public float getWOSMA_OverallPercentageImprovement() {
        return WOSMA_OverallPercentageImprovement;
    }

    public float getMCPMA_OverallScore() {
        return MCPMA_OverallScore;
    }

    public float getMCPMA_OverallPercentageImprovement() {
        return MCPMA_OverallPercentageImprovement;
    }

    public float getOldAverageLocalScore() {
        return oldAverageLocalScore;
    }

    public float getWOSMA_AverageLocalScore() {
        return WOSMA_AverageLocalScore;
    }

    public float getWOSMA_AverageLocalPercentageImprovement() {
        return WOSMA_AverageLocalPercentageImprovement;
    }

    public float getMCPMA_AverageLocalScore() {
        return MCPMA_AverageLocalScore;
    }

    public float getMCPMA_AverageLocalPercentageImprovement() {
        return MCPMA_AverageLocalPercentageImprovement;
    }
}
