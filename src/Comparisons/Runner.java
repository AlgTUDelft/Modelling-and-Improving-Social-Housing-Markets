package Comparisons;

import Main.AlgorithmStrategy;
import Algorithms.MCPMA.*;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Main.GradingStrategy;
import Main.MatchingEvaluatorStrategy;
import Matching.*;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Runner {

    private static ArrayList<DynamicMatching> dynamicMatchings;
    private static int lineCount;
    private static int nTimes;
    private static MatchingEvaluatorStrategy matchingEvaluatorStrategy;
    private static AlgorithmStrategy algorithmStrategy;
    private static double envRatio;
    private static GradingStrategy gradingStrategy;

    public Runner(ArrayList<DynamicMatching> dynamicMatchings, int lineCount, int nTimes, MatchingEvaluatorStrategy matchingEvaluatorStrategy, AlgorithmStrategy algorithmStrategy, double envRatio, GradingStrategy gradingStrategy) {
        this.dynamicMatchings = dynamicMatchings;
        this.lineCount = lineCount;
        this.nTimes = nTimes;
        this.matchingEvaluatorStrategy = matchingEvaluatorStrategy;
        this.algorithmStrategy = algorithmStrategy;
        this.envRatio = envRatio;
        this.gradingStrategy = gradingStrategy;
    }

    public static Runnable runDynamic(CompletableFuture<ArrayList<GenericResult>> resultsForAlgorithm) {
        return () -> {


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

            if(!interrupted) {
                resultsForAlgorithm.complete(results);
            }
        };
    }

    public static GenericResult individualRunDynamic(DynamicMatching dynamicMatching) throws InterruptedException {
        GenericResult genericResult = null;
        try {

            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStepAndReset(algorithmStrategy, false);
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolveAndReset(algorithmStrategy, false);

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);
            genericResult = new GenericResult(scores[0], scores[1]);

        } catch (MCPMAPrices.AlreadyInitiatedException | Matching.HouseLinkedToMultipleException | Matching.HouseIDAlreadyPresentException | Matching.HouseholdLinkedToHouseholdException | Matching.HouseLinkedToHouseException | Matching.PreferredNoHouseholdlessHouseException | ResidualGraph.MatchGraphNotEmptyException | CycleFinder.FullyExploredVertexDiscoveredException | Matching.HouseholdIDAlreadyPresentException | ResidualGraph.PathEdgeNotInResidualGraphException | MCPMA.UnequalSidesException | MatchingEvaluator.HouseholdIncomeTooHighException | Matching.HouseAlreadyMatchedException | Matching.HouseholdLinkedToMultipleException | Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        }

        return genericResult;
    }

    public static Runnable runStaticMCPMA(CompletableFuture<ArrayList<GenericResult>> resultsForAlgorithm) {
        return () -> {

            ArrayList<GenericResult> mcpmaResults
                    = new ArrayList(nTimes);
            boolean interrupted = false;
            for (int i = 0; i < nTimes; i++) {
                try {
                    DynamicMatching dynamicMatching = dynamicMatchings.get(i);
                    mcpmaResults.add(i, individualRunStaticMCPMA(dynamicMatching));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
            GenericResultProcessor genericResultProcessor
                    = new GenericResultProcessor(mcpmaResults);
            if(!interrupted) {
                resultsForAlgorithm.complete(mcpmaResults);
            }
        };
    }

    public static GenericResult individualRunStaticMCPMA(DynamicMatching dynamicMatching) throws InterruptedException {
        GenericResult mcpmaResult = null;
        try {
            Matching matching = dynamicMatching.getInputMatching();
            MCPMAOnMatchingRunner mcpmaOnMatchingRunner = new MCPMAOnMatchingRunner(matching, MCPMAStrategy.REGULAR);
            Matching result = mcpmaOnMatchingRunner.optimizeMatching(false);
            Matching[] matchings = new Matching[1];
            matchings[0] = result;

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            mcpmaResult = new GenericResult(scores[0], scores[0]);

        } catch (Matching.HouseholdLinkedToMultipleException | ResidualGraph.MatchGraphNotEmptyException | Matching.HouseLinkedToMultipleException | Matching.HouseLinkedToHouseException | Matching.HouseAlreadyMatchedException | Matching.HouseholdAlreadyMatchedException | MCPMAPrices.AlreadyInitiatedException | ResidualGraph.PathEdgeNotInResidualGraphException | Matching.HouseholdLinkedToHouseholdException | MCPMA.UnequalSidesException | MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        }
        return mcpmaResult;
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
