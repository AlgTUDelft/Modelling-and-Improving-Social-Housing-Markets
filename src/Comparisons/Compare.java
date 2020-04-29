package Comparisons;

import Algorithms.AlgorithmStrategy;
import Algorithms.MCPMA.*;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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

            wosmaLikeResult = new WOSMALikeResult(matchings[0].getFindMaxFailed(), scores[0], scores[1]);

        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (Matching.HouseIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (CycleFinder.FullyExploredVertexDiscoveredException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (MCPMAPrices.AlreadyInitiatedException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchGraphNotEmptyException e) {
            e.printStackTrace();
        } catch (MCPMA.UnequalSidesException e) {
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

    public static GenericResult individualRunDynamicImprovementMCPMA(DynamicMatching dynamicMatching) {
        GenericResult genericResult = null;
        try {
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(DynamicStrategy.MCPMA_IMPROVEMENT, false);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(DynamicStrategy.MCPMA_IMPROVEMENT, false);
            dynamicMatching.resetState();

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            genericResult = new GenericResult(scores[0], scores[1]);

        } catch (MCPMAPrices.AlreadyInitiatedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (MCPMA.UnequalSidesException e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (CycleFinder.FullyExploredVertexDiscoveredException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchGraphNotEmptyException e) {
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseIDAlreadyPresentException e) {
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

    public static WOSMALikeResult individualRunDynamicWOSMAFindMax(DynamicMatching dynamicMatching) {
        WOSMALikeResult wosmaLikeResult = null;
        try {
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(DynamicStrategy.WOSMA_FINDMAX, false);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(DynamicStrategy.WOSMA_FINDMAX, false);
            dynamicMatching.resetState();

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            wosmaLikeResult = new WOSMALikeResult(matchings[0].getFindMaxFailed(), scores[0], scores[1]);

        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (Matching.HouseIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (CycleFinder.FullyExploredVertexDiscoveredException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (MCPMAPrices.AlreadyInitiatedException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchGraphNotEmptyException e) {
            e.printStackTrace();
        } catch (MCPMA.UnequalSidesException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    public static GenericResult individualRunDynamicWOSMARegular(DynamicMatching dynamicMatching) {
        GenericResult genericResult = null;
        try {
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(DynamicStrategy.WOSMA_REGULAR, false);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(DynamicStrategy.WOSMA_REGULAR, false);
            dynamicMatching.resetState();

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            genericResult = new GenericResult(scores[0], scores[1]);

        } catch (MCPMAPrices.AlreadyInitiatedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (MCPMA.UnequalSidesException e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (CycleFinder.FullyExploredVertexDiscoveredException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchGraphNotEmptyException e) {
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseIDAlreadyPresentException e) {
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

    public static MCPMAResult individualRunStaticMCPMA(Matching matching) {
        MCPMAResult mcpmaResult = null;
        try {
            Matching[] matchings = new Matching[1];
            matchings[0] = matching;
            MCPMAOnMatchingRunner mcpmaOnMatchingRunner = new MCPMAOnMatchingRunner(matchings[0], MCPMAStrategy.REGULAR);
            mcpmaOnMatchingRunner.optimizeMatching(false);

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            mcpmaResult = new MCPMAResult(scores[0]);

        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (MCPMA.UnequalSidesException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchGraphNotEmptyException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (MCPMAPrices.AlreadyInitiatedException e) {
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
