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
    private float MCPMA_OverallPercentageIncreaseSuperiorityPercentage;
    private float MCPMA_AverageLocalPercentageIncreaseSuperiorityPercentage;

    public float getMCPMA_OverallPercentageIncreaseSuperiorityPercentage() {
        return MCPMA_OverallPercentageIncreaseSuperiorityPercentage;
    }

    public float getMCPMA_AverageLocalPercentageIncreaseSuperiorityPercentage() {
        return MCPMA_AverageLocalPercentageIncreaseSuperiorityPercentage;
    }

    public WMComparisonResult(float oldOverallScore, float WOSMA_OverallScore, float WOSMA_OverallPercentageImprovement,
                              float MCPMA_OverallScore, float MCPMA_OverallPercentageImprovement,
                              float oldAverageLocalScore, float WOSMA_AverageLocalScore,
                              float WOSMA_AverageLocalPercentageImprovement, float MCPMA_AverageLocalScore,
                              float MCPMA_AverageLocalPercentageImprovement, int WOSMA_amtSWIChainsExecuted,
                              int WOSMA_amtSWICyclesExecuted,
                              float MCPMA_OverallPercentageIncreaseSuperiorityPercentage,
                              float MCPMA_AverageLocalPercentageIncreaseSuperiorityPercentage) {
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
        this.MCPMA_OverallPercentageIncreaseSuperiorityPercentage = MCPMA_OverallPercentageIncreaseSuperiorityPercentage;
        this.MCPMA_AverageLocalPercentageIncreaseSuperiorityPercentage = MCPMA_AverageLocalPercentageIncreaseSuperiorityPercentage;
    }

    public int getWOSMA_amtSWIChainsExecuted() {
        return WOSMA_amtSWIChainsExecuted;
    }

    public int getWOSMA_amtSWICyclesExecuted() {
        return WOSMA_amtSWICyclesExecuted;
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
