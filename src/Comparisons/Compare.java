package Comparisons;

import Algorithms.AlgorithmStrategy;
import Algorithms.MCPMA.*;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Matching.*;
import static Miscellaneous.DeepCloner.deepClone;

import java.io.*;
import java.util.ArrayList;

public class Compare {

    private static ArrayList<DynamicMatching> dynamicMatchings;
    private static int lineCount;
    private static int nTimes;
    private static MatchingEvaluatorStrategy matchingEvaluatorStrategy;
    private static AlgorithmStrategy algorithmStrategy;

    public Compare(ArrayList<DynamicMatching> dynamicMatchings, int lineCount, int nTimes, MatchingEvaluatorStrategy matchingEvaluatorStrategy, AlgorithmStrategy algorithmStrategy) {
        this.dynamicMatchings = dynamicMatchings;
        this.lineCount = lineCount;
        this.nTimes = nTimes;
        this.matchingEvaluatorStrategy = matchingEvaluatorStrategy;
        this.algorithmStrategy = algorithmStrategy;
    }

    public static Runnable runDynamic() {
        return () -> {

            String outputFilename = createFilename(algorithmStrategy, lineCount, matchingEvaluatorStrategy);

            ArrayList<GenericResult> results = new ArrayList<>(nTimes);
            boolean interrupted = false;

            for (int i = 0; i < nTimes; i++) {
                try {
                    DynamicMatching dynamicMatching = dynamicMatchings.get(i);
                    results.add(i, individualRunDynamic(dynamicMatching));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }

            try {
                if(!interrupted) {
                    switch (algorithmStrategy) {
                        case WOSMA_REGULAR:
                        case IMPROVEMENT_MCPMA:
                            new GenericResultProcessor(results).resultsToCSV(outputFilename);
                            break;
                        case WOSMA_FINDMAX:
                        case WOSMA_IRCYCLES:
                            new GenericResultProcessor(results).resultsToCSV(outputFilename);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static GenericResult individualRunDynamic(DynamicMatching dynamicMatching) throws InterruptedException {
        GenericResult genericResult = null;
        try {
            DynamicStrategy dynamicStrategy = null;
            switch (algorithmStrategy) {
                case WOSMA_REGULAR: dynamicStrategy = DynamicStrategy.WOSMA_REGULAR; break;
                case WOSMA_FINDMAX: dynamicStrategy = DynamicStrategy.WOSMA_FINDMAX; break;
                case WOSMA_IRCYCLES: dynamicStrategy = DynamicStrategy.WOSMA_IR_CYCLES; break;
                case IMPROVEMENT_MCPMA: dynamicStrategy = DynamicStrategy.MCPMA_IMPROVEMENT; break;
            }

            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStepAndReset(dynamicStrategy, false);
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolveAndReset(dynamicStrategy, false);

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);
            genericResult = new GenericResult(scores[0], scores[1]);

        } catch (MCPMAPrices.AlreadyInitiatedException | Matching.HouseLinkedToMultipleException | Matching.HouseIDAlreadyPresentException | Matching.HouseholdLinkedToHouseholdException | Matching.HouseLinkedToHouseException | Matching.PreferredNoHouseholdlessHouseException | ResidualGraph.MatchGraphNotEmptyException | CycleFinder.FullyExploredVertexDiscoveredException | Matching.HouseholdIDAlreadyPresentException | ResidualGraph.PathEdgeNotInResidualGraphException | MCPMA.UnequalSidesException | MatchingEvaluator.HouseholdIncomeTooHighException | Matching.HouseAlreadyMatchedException | Matching.HouseholdLinkedToMultipleException | Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        }

        return genericResult;
    }

    public static Runnable runStaticMCPMA() {
        return () -> {

            String outputFilename = createFilename(AlgorithmStrategy.MCPMA, lineCount, matchingEvaluatorStrategy);

            ArrayList<MCPMAResult> mcpmaResults
                    = new ArrayList(nTimes);
            boolean interrupted = false;
            for (int i = 0; i < nTimes; i++) {
                try {
                    DynamicMatching dynamicMatching = dynamicMatchings.get(i);
                    Matching matching = dynamicMatching.getInputMatching();
                    mcpmaResults.add(i, individualRunStaticMCPMA(matching));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
            MCPMAResultProcessor mcpmaResultProcessor
                    = new MCPMAResultProcessor(mcpmaResults);
            try {
                if(!interrupted) {
                    mcpmaResultProcessor.resultsToCSV(outputFilename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static MCPMAResult individualRunStaticMCPMA(Matching matching) throws InterruptedException {
        MCPMAResult mcpmaResult = null;
        try {
            MCPMAOnMatchingRunner mcpmaOnMatchingRunner = new MCPMAOnMatchingRunner(matching, MCPMAStrategy.REGULAR);
            Matching result = mcpmaOnMatchingRunner.optimizeMatching(false);
            Matching[] matchings = new Matching[1];
            matchings[0] = result;

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            mcpmaResult = new MCPMAResult(scores[0]);

        } catch (Matching.HouseholdLinkedToMultipleException | ResidualGraph.MatchGraphNotEmptyException | Matching.HouseLinkedToMultipleException | Matching.HouseLinkedToHouseException | Matching.HouseAlreadyMatchedException | Matching.HouseholdAlreadyMatchedException | MCPMAPrices.AlreadyInitiatedException | ResidualGraph.PathEdgeNotInResidualGraphException | Matching.HouseholdLinkedToHouseholdException | MCPMA.UnequalSidesException | MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        }
        return mcpmaResult;
    }




    public static String createFilename(AlgorithmStrategy algorithmStrategy, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) {
        String outputFilename = "../dyn-";
        switch (algorithmStrategy) {
            case MCPMA:
                outputFilename += "MCPMA-"; break;
            case WOSMA_REGULAR:
                outputFilename += "WOSMARegular-"; break;
            case WOSMA_FINDMAX:
                outputFilename += "WOSMAFindMax-"; break;
            case WOSMA_IRCYCLES:
                outputFilename += "WOSMAIRCycles-"; break;
            case IMPROVEMENT_MCPMA:
                outputFilename += "ImprovementMCPMA-"; break;
        }
        outputFilename += "50times" + lineCount + "-";
        switch (matchingEvaluatorStrategy) {
            case AVG:
                outputFilename += "avgME-";
                break;
            case MIN:
                outputFilename += "minME-";
                break;
        }
        outputFilename += "100prob-twosided.csv";
        return outputFilename;
    }


    public static float[] evaluateMatchingsAverageIndividualTotalFit(Matching[] matchings) throws MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {

        MatchingEvaluator[] matchingEvaluators = new MatchingEvaluator[matchings.length];
        for (int i = 0; i < matchings.length; i++) {
            matchingEvaluators[i] = new MatchingEvaluator(matchings[i]);
        }

        float[] scores = new float[matchings.length];
        for (int i = 0; i < matchings.length; i++) {
            scores[i] = matchingEvaluators[i].evaluateAverageIndividualTotalFit(false);
        }
        return scores;
    }

}
