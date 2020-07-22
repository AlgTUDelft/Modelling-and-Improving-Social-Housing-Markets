package Main;

import Results.GenericResult;
import Results.GenericResultProcessor;
import Algorithms.MCPMA.*;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Matching.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Runner {

    private static ArrayList<DynamicMatching> dynamicMatchings;
    private static int nTimes;
    private static AlgorithmStrategy algorithmStrategy;
    private boolean print;

    public Runner(ArrayList<DynamicMatching> dynamicMatchings, int nTimes, AlgorithmStrategy algorithmStrategy, boolean print) {
        this.dynamicMatchings = dynamicMatchings;
        this.nTimes = nTimes;
        this.algorithmStrategy = algorithmStrategy;
        this.print = print;
    }

    public Runnable runDynamic(CompletableFuture<ArrayList<GenericResult>> resultsForAlgorithm, GradingStrategy gradingStrategy) {
        return () -> {


            ArrayList<GenericResult> results = new ArrayList<>(nTimes);
            boolean interrupted = false;

            for (int i = 0; i < nTimes; i++) {
                try {
                    DynamicMatching dynamicMatching = dynamicMatchings.get(i);
                    results.add(i, individualRunDynamic(dynamicMatching, gradingStrategy));
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

    private GenericResult individualRunDynamic(DynamicMatching dynamicMatching, GradingStrategy gradingStrategy) throws InterruptedException {
        GenericResult genericResult = null;
        try {

            if (print) {
                System.out.println("Running " + algorithmStrategy);
            }
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStepAndReset(algorithmStrategy, gradingStrategy, print);
            if (print) {
                System.out.println("");
            }
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolveAndReset(algorithmStrategy, gradingStrategy, print);

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings, gradingStrategy);
            genericResult = new GenericResult(scores[0], scores[1]);

        } catch (MCPMAPrices.AlreadyInitiatedException | Matching.HouseLinkedToMultipleException | Matching.HouseIDAlreadyPresentException | Matching.HouseholdLinkedToHouseholdException | Matching.HouseLinkedToHouseException | Matching.PreferredNoHouseholdlessHouseException | ResidualGraph.MatchGraphNotEmptyException | CycleFinder.FullyExploredVertexDiscoveredException | Matching.HouseholdIDAlreadyPresentException | ResidualGraph.PathEdgeNotInResidualGraphException | MCPMA.UnequalSidesException | MatchingEvaluator.HouseholdIncomeTooHighException | Matching.HouseAlreadyMatchedException | Matching.HouseholdLinkedToMultipleException | Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        }

        return genericResult;
    }

    public Runnable runStaticMCPMA(CompletableFuture<ArrayList<GenericResult>> resultsForAlgorithm, GradingStrategy gradingStrategy) {
        return () -> {

            ArrayList<GenericResult> mcpmaResults
                    = new ArrayList(nTimes);
            boolean interrupted = false;
            for (int i = 0; i < nTimes; i++) {
                try {
                    DynamicMatching dynamicMatching = dynamicMatchings.get(i);
                    mcpmaResults.add(i, individualRunStaticMCPMA(dynamicMatching, gradingStrategy));
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

    private GenericResult individualRunStaticMCPMA(DynamicMatching dynamicMatching, GradingStrategy gradingStrategy) throws InterruptedException {
        GenericResult mcpmaResult = null;
        try {
            Matching matching = dynamicMatching.getInputMatching();
            MCPMAOnMatchingRunner mcpmaOnMatchingRunner = new MCPMAOnMatchingRunner(matching, MCPMAStrategy.REGULAR, gradingStrategy);
            Matching result = mcpmaOnMatchingRunner.optimizeMatching(print);
            Matching[] matchings = new Matching[1];
            matchings[0] = result;

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings, gradingStrategy);

            mcpmaResult = new GenericResult(scores[0], scores[0]);

        } catch (Matching.HouseholdLinkedToMultipleException | ResidualGraph.MatchGraphNotEmptyException | Matching.HouseLinkedToMultipleException | Matching.HouseLinkedToHouseException | Matching.HouseAlreadyMatchedException | Matching.HouseholdAlreadyMatchedException | MCPMAPrices.AlreadyInitiatedException | ResidualGraph.PathEdgeNotInResidualGraphException | Matching.HouseholdLinkedToHouseholdException | MCPMA.UnequalSidesException | MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        }
        return mcpmaResult;
    }






    private static float[] evaluateMatchingsAverageIndividualTotalFit(Matching[] matchings, GradingStrategy gradingStrategy) throws MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {
        float[] scores = new float[matchings.length];
        for (int i = 0; i < matchings.length; i++) {
            scores[i] = matchings[i].gradeAverage(gradingStrategy);
        }
        return scores;
    }

}
