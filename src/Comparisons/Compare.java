package Comparisons;

import Algorithms.AlgorithmStrategy;
import Algorithms.MCPMA.*;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Matching.*;

import java.io.*;
import java.util.ArrayList;

public class Compare {

    public Compare() {}

    public static Runnable runDynamicIRCycles(ArrayList<DynamicMatching> dynamicMatchings, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws IOException {
        return () -> {

            String outputFilename = createFilename(AlgorithmStrategy.WOSMA_IRCYCLES, lineCount, matchingEvaluatorStrategy);

            ArrayList<WOSMALikeResult> wosmaLikeResults
                    = new ArrayList();
            boolean interrupted = false;
            for (DynamicMatching dynamicMatching : dynamicMatchings) {
                try {
                    wosmaLikeResults.add(individualRunDynamicIRCycles(dynamicMatching));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
            WOSMALikeResultProcessor wosmaLikeResultProcessor
                    = new WOSMALikeResultProcessor(wosmaLikeResults);
            try {
                if(!interrupted) {
                    wosmaLikeResultProcessor.resultsToCSV(outputFilename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static WOSMALikeResult individualRunDynamicIRCycles(DynamicMatching dynamicMatching) throws InterruptedException {
        WOSMALikeResult wosmaLikeResult = null;
        try {
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(DynamicStrategy.WOSMA_IR_CYCLES, false);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(DynamicStrategy.WOSMA_IR_CYCLES, false);
            dynamicMatching.resetState();

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            wosmaLikeResult = new WOSMALikeResult(matchings[0].getStrategyDowngraded() || matchings[1].getStrategyDowngraded(), scores[0], scores[1]);

        } catch (Matching.HouseLinkedToHouseException | MCPMA.UnequalSidesException | ResidualGraph.MatchGraphNotEmptyException | MCPMAPrices.AlreadyInitiatedException | Matching.HouseLinkedToMultipleException | CycleFinder.FullyExploredVertexDiscoveredException | ResidualGraph.PathEdgeNotInResidualGraphException | Matching.HouseholdIDAlreadyPresentException | Matching.HouseholdLinkedToMultipleException | Matching.HouseholdLinkedToHouseholdException | Matching.HouseholdAlreadyMatchedException | Matching.HouseAlreadyMatchedException | Matching.HouseIDAlreadyPresentException | MatchingEvaluator.HouseholdIncomeTooHighException | Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        }

        return wosmaLikeResult;
    }


    public static Runnable runDynamicImprovementMCPMA(ArrayList<DynamicMatching> dynamicMatchings, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) {
        return () -> {

            String outputFilename = createFilename(AlgorithmStrategy.IMPROVEMENT_MCPMA, lineCount, matchingEvaluatorStrategy);

            ArrayList<GenericResult> genericResults
                    = new ArrayList();
            boolean interrupted = false;
            for (DynamicMatching dynamicMatching : dynamicMatchings) {
                try {
                    genericResults.add(individualRunDynamicImprovementMCPMA(dynamicMatching));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
            GenericResultProcessor genericResultProcessor
                    = new GenericResultProcessor(genericResults);
            try {
                if(!interrupted) {
                    genericResultProcessor.resultsToCSV(outputFilename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static GenericResult individualRunDynamicImprovementMCPMA(DynamicMatching dynamicMatching) throws InterruptedException {
        GenericResult genericResult = null;
        try {
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(DynamicStrategy.MCPMA_IMPROVEMENT, false);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(DynamicStrategy.MCPMA_IMPROVEMENT, false);
            dynamicMatching.resetState();

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            genericResult = new GenericResult(scores[0], scores[1]);

        } catch (MCPMAPrices.AlreadyInitiatedException | Matching.HouseIDAlreadyPresentException | Matching.HouseholdLinkedToHouseholdException | Matching.HouseLinkedToHouseException | Matching.PreferredNoHouseholdlessHouseException | ResidualGraph.MatchGraphNotEmptyException | CycleFinder.FullyExploredVertexDiscoveredException | Matching.HouseholdIDAlreadyPresentException | ResidualGraph.PathEdgeNotInResidualGraphException | MCPMA.UnequalSidesException | MatchingEvaluator.HouseholdIncomeTooHighException | Matching.HouseAlreadyMatchedException | Matching.HouseholdLinkedToMultipleException | Matching.HouseholdAlreadyMatchedException | Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        }

        return genericResult;
    }


    public static Runnable runDynamicWOSMAFindMax(ArrayList<DynamicMatching> dynamicMatchings, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) {
        return () -> {

            String outputFilename = createFilename(AlgorithmStrategy.WOSMA_FINDMAX, lineCount, matchingEvaluatorStrategy);

            ArrayList<WOSMALikeResult> wosmaLikeResults
                    = new ArrayList();
            boolean interrupted = false;
            for (DynamicMatching dynamicMatching : dynamicMatchings) {
                try {
                    wosmaLikeResults.add(individualRunDynamicWOSMAFindMax(dynamicMatching));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
            WOSMALikeResultProcessor wosmaLikeResultProcessor
                    = new WOSMALikeResultProcessor(wosmaLikeResults);
            try {
                if(!interrupted) {
                    wosmaLikeResultProcessor.resultsToCSV(outputFilename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static WOSMALikeResult individualRunDynamicWOSMAFindMax(DynamicMatching dynamicMatching) throws InterruptedException {
        WOSMALikeResult wosmaLikeResult = null;
        try {
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(DynamicStrategy.WOSMA_FINDMAX, false);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(DynamicStrategy.WOSMA_FINDMAX, false);
            dynamicMatching.resetState();

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            wosmaLikeResult = new WOSMALikeResult(matchings[0].getStrategyDowngraded() || matchings[1].getStrategyDowngraded(), scores[0], scores[1]);

        } catch (Matching.HouseLinkedToHouseException | MCPMA.UnequalSidesException | ResidualGraph.MatchGraphNotEmptyException | MCPMAPrices.AlreadyInitiatedException | Matching.HouseLinkedToMultipleException | CycleFinder.FullyExploredVertexDiscoveredException | ResidualGraph.PathEdgeNotInResidualGraphException | Matching.HouseholdIDAlreadyPresentException | Matching.HouseholdLinkedToMultipleException | Matching.HouseholdLinkedToHouseholdException | Matching.HouseholdAlreadyMatchedException | Matching.HouseAlreadyMatchedException | Matching.HouseIDAlreadyPresentException | MatchingEvaluator.HouseholdIncomeTooHighException | Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        }

        return wosmaLikeResult;
    }


    public static Runnable runDynamicWOSMARegular(ArrayList<DynamicMatching> dynamicMatchings, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) {
        return () -> {

            String outputFilename = createFilename(AlgorithmStrategy.WOSMA_REGULAR, lineCount, matchingEvaluatorStrategy);

            ArrayList<GenericResult> genericResults
                    = new ArrayList();
            boolean interrupted = false;
            for (DynamicMatching dynamicMatching : dynamicMatchings) {
                try {
                    genericResults.add(individualRunDynamicWOSMARegular(dynamicMatching));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
            GenericResultProcessor genericResultProcessor
                    = new GenericResultProcessor(genericResults);
            try {
                if(!interrupted) {
                    genericResultProcessor.resultsToCSV(outputFilename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static GenericResult individualRunDynamicWOSMARegular(DynamicMatching dynamicMatching) throws InterruptedException {
        GenericResult genericResult = null;
        try {
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(DynamicStrategy.WOSMA_REGULAR, false);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(DynamicStrategy.WOSMA_REGULAR, false);
            dynamicMatching.resetState();

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            genericResult = new GenericResult(scores[0], scores[1]);

        } catch (MCPMAPrices.AlreadyInitiatedException | Matching.HouseLinkedToMultipleException | Matching.HouseIDAlreadyPresentException | Matching.HouseholdLinkedToHouseholdException | Matching.HouseLinkedToHouseException | Matching.PreferredNoHouseholdlessHouseException | ResidualGraph.MatchGraphNotEmptyException | CycleFinder.FullyExploredVertexDiscoveredException | Matching.HouseholdIDAlreadyPresentException | ResidualGraph.PathEdgeNotInResidualGraphException | MCPMA.UnequalSidesException | MatchingEvaluator.HouseholdIncomeTooHighException | Matching.HouseAlreadyMatchedException | Matching.HouseholdLinkedToMultipleException | Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        }

        return genericResult;
    }


    public static Runnable runStaticMCPMA(ArrayList<DynamicMatching> dynamicMatchings, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) {
        return () -> {

            String outputFilename = createFilename(AlgorithmStrategy.MCPMA, lineCount, matchingEvaluatorStrategy);

            ArrayList<MCPMAResult> mcpmaResults
                    = new ArrayList();
            boolean interrupted = false;
            for (DynamicMatching dynamicMatching : dynamicMatchings) {
                try {
                    Matching matching = dynamicMatching.getInputMatching();
                    mcpmaResults.add(individualRunStaticMCPMA(matching));
                    dynamicMatching.resetState();
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
            matching = (Matching) deepClone(matching);
            MCPMAOnMatchingRunner mcpmaOnMatchingRunner = new MCPMAOnMatchingRunner(matching, MCPMAStrategy.REGULAR);
            matching = mcpmaOnMatchingRunner.optimizeMatching(false);
            Matching[] matchings = new Matching[1];
            matchings[0] = matching;

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


    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
