package Comparisons;

import Algorithms.MCPMA.*;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import Matching.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Compare {

    public Compare() {

    }

    public static Runnable runDynamicWOSMAMatching(int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws IOException {
        return () -> {
            String outputFilename = "../dyn-50times" + lineCount + "-";
            switch (matchingEvaluatorStrategy) {
                case AVG:
                    outputFilename += "avgME-";
                    break;
                case MIN:
                    outputFilename += "minME-";
                    break;
            }
            outputFilename += "100prob-twosided.csv";
            boolean oneSided = false;

            ArrayList<DynamicMatchingComparisonResult> dynamicMatchingComparisonResults
                    = new ArrayList<DynamicMatchingComparisonResult>();
            ArrayList<Integer> startLines = new ArrayList<Integer>(Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300, 310, 320, 330, 340, 350, 360, 370, 380, 390, 400, 410, 420, 430, 440, 450, 460, 470, 480, 490, 500));
//        ArrayList<Integer> startLines = new ArrayList<Integer>(Arrays.asList(0, 125, 250, 375, 500, 625, 750, 875));
//        ArrayList<Integer> startLines = new ArrayList<Integer>(Arrays.asList(0, 250, 500, 750));
//        ArrayList<Integer> startLines = new ArrayList<Integer>(Arrays.asList(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40,
//                44, 48, 52, 56, 60, 64, 68, 72, 76, 80, 84, 88, 92, 96, 100, 104, 108, 112, 116, 120, 124, 128, 132,
//                136, 140, 144, 148, 152, 156, 160, 164, 168, 172, 176, 180, 184, 188, 192, 196, 200, 204, 208, 212, 216,
//                220, 224, 228, 232, 236, 240, 244, 248, 252, 256, 260, 264, 268, 272, 276, 280, 284, 288, 292, 296, 300,
//                304, 308, 312, 316, 320, 324, 328, 332, 336, 340,344, 348, 352, 356, 360, 364, 368, 372, 376, 380, 384,
//                388, 392, 396, 400, 404, 408, 412, 416, 420, 424, 428, 432, 436, 440, 444, 448, 452, 456, 460, 464, 468,
//                472, 476, 480, 484, 488, 492, 496, 500, 504, 508, 512, 516, 520, 524, 528, 532, 536, 540, 544, 548, 552,
//                556, 560, 564, 568, 572, 576, 580, 584, 588, 592, 596, 600, 604, 608,612, 616, 620, 624, 628, 632, 636,
//                640, 644, 648, 652, 656, 660, 664, 668, 672, 676, 680, 684, 688, 692, 696, 700, 704, 708, 712, 716, 720,
//                724, 728, 732, 736, 740, 744, 748, 752, 756, 760, 764, 768, 772, 776, 780, 784, 788, 792, 796, 800, 804,
//                808, 812, 816, 820, 824, 828, 832, 836, 840, 844));
            boolean interrupted = false;
            for (int startLine : startLines) {
                try {
                    dynamicMatchingComparisonResults.add(individualRunDynamicWOSMAMatching(startLine, lineCount, oneSided, matchingEvaluatorStrategy));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
            DynamicMatchingComparisonResultProcessor dynamicMatchingComparisonResultProcessor
                    = new DynamicMatchingComparisonResultProcessor(dynamicMatchingComparisonResults);
            if (!interrupted) {
                try {
                    dynamicMatchingComparisonResultProcessor.resultsToCSV(outputFilename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static DynamicMatchingComparisonResult individualRunDynamicWOSMAMatching(DynamicMatching dynamicMatching, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws InterruptedException {
        int timestepCount = lineCount/2;
        DynamicMatchingComparisonResult dynamicMatchingComparisonResult = null;
        try {
            double connectionProb = 1.0;
            Matching matching = setupMatching(connectionProb, startLine, lineCount, matchingEvaluatorStrategy);
            DynamicMatching dynamicMatching = new DynamicMatching(matching, timestepCount, oneSided);

            Matching[] matchings = new Matching[5];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(timestepCount, DynamicStrategy.WOSMA_REGULAR, false);
            System.out.println("Got here! 0 " + matchings[0].getFindMaxFailed());
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeAndSolvePerStep(timestepCount, DynamicStrategy.WOSMA_FINDMAX, false);
            System.out.println("Got here! 1 " + matchings[1].getFindMaxFailed());
            dynamicMatching.resetState();
            matchings[2] = dynamicMatching.advanceTimeFullyThenSolve(timestepCount, DynamicStrategy.WOSMA_REGULAR,false);
            System.out.println("Got here! 2 " + matchings[2].getFindMaxFailed());
            dynamicMatching.resetState();
            matchings[3] = dynamicMatching.advanceTimeFullyThenSolve(timestepCount, DynamicStrategy.WOSMA_FINDMAX,false);
            System.out.println("Got here! 3 " + matchings[3].getFindMaxFailed());
            matchings[4] = new MCPMAOnMatchingRunner((Matching) deepClone(dynamicMatching.getInputMatching()), MCPMAStrategy.REGULAR)
                    .optimizeMatching(false);
            System.out.println("Got here! 4 " + matchings[4].getFindMaxFailed());

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);
            String[] strings = {
                    "Final per step score",
                    "Final per step + findMax score",
                    "Final afterwards score",
                    "Final afterwards + findMax score",
                    "Optimal score" };

            prettyPrintResults(strings, scores);

//            if (scores[2] < scores[1] - 0.0000001) {
//                System.err.println("Found one!");
//                Matching inputMatching = dynamicMatching.getInputMatching();
//                ArrayList<String> houseIDs = new ArrayList<String>();
//                for (int i = 1; i < inputMatching.getHouses().size() + 1; i++) {
//                    houseIDs.add("h" + i + "ID");
//                }
//                ArrayList<String> householdIDs = new ArrayList<String>();
//                for (int i = 1; i < inputMatching.getHouseholds().size() + 1; i++) {
//                    householdIDs.add("f" + i + "ID");
//                }
//                MatchingEvaluator matchingEvaluator = new MatchingEvaluator(inputMatching);
//                PrimitiveIterator.OfInt houseIterator = IntStream.range(0,inputMatching.getHouses().size()).iterator();
//                while (houseIterator.hasNext()) {
//                    int houseIndex = houseIterator.next();
//                    PrimitiveIterator.OfInt householdIterator = IntStream.range(0,inputMatching.getHouseholds().size()).iterator();
//                    while (householdIterator.hasNext()) {
//                        int householdIndex = householdIterator.next();
//                        System.out.println("scores.put(new HouseAndHouseholdIDPair(" + houseIDs.get(houseIndex) + ", "
//                                + householdIDs.get(householdIndex) + "), "
//                                + matchingEvaluator.evaluateIndividualTotalFit(inputMatching.getHouses().get(houseIndex).getID(),
//                                inputMatching.getHouseholds().get(householdIndex).getID()) + ");");
//                    }
//                }
//
//                System.out.println();
//
//                PrimitiveIterator.OfInt newHouseIterator = IntStream.range(0,inputMatching.getHouses().size()).iterator();
//                while (newHouseIterator.hasNext()) {
//                    int houseIndex = newHouseIterator.next();
//                    Object[] edges = inputMatching.getMatchingGraph().edgesOf(inputMatching.getHouses().get(houseIndex)).toArray(); // only contains a single edge at most
//                    if (edges.length > 0) {
//                        int householdID = inputMatching.getMatchingGraph().getEdgeTarget((DefaultEdge) edges[0]).getID();
//                        int householdIndex = -1;
//                        for (int i = 0; i < inputMatching.getHouseholds().size(); i++) {
//                            if (inputMatching.getHouseholds().get(i).getID() == householdID) {
//                                householdIndex = i;
//                                break;
//                            }
//                        }
//                        System.out.println("artificialMatching.connect(" + houseIDs.get(houseIndex) + ", " + householdIDs.get(householdIndex) + ");");
//                    }
//                }
//
//            }

            float perStepOptimality = scores[0]/scores[4];
            float perStepFindMaxOptimality = scores[1]/scores[4];
            float afterwardsOptimality = scores[2]/scores[4];
            float afterwardsFindMaxOptimality = scores[3]/scores[4];
            dynamicMatchingComparisonResult
                    = new DynamicMatchingComparisonResult(timestepCount, matchings[1].getFindMaxFailed() || matchings[3].getFindMaxFailed(),
                    scores[0], scores[1], scores[2], scores[3], scores[4],
                    perStepOptimality, perStepFindMaxOptimality, afterwardsOptimality, afterwardsFindMaxOptimality);

        } catch (HousingMarket.FreeSpaceException e) {
            e.printStackTrace();
        } catch (Household.InvalidHouseholdException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (DynamicMatching.TooManyTimestepsException e) {
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToMultipleException e) {
            e.printStackTrace();
        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdLinkedToHouseholdException e) {
            e.printStackTrace();
        } catch (CycleFinder.FullyExploredVertexDiscoveredException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (ResidualGraph.PathEdgeNotInResidualGraphException e) {
            e.printStackTrace();
        } catch (Matching.HouseholdIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (Matching.HouseIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (MCPMAPrices.AlreadyInitiatedException e) {
            e.printStackTrace();
        } catch (ResidualGraph.MatchGraphNotEmptyException e) {
            e.printStackTrace();
        } catch (MCPMA.UnequalSidesException e) {
            e.printStackTrace();
        }


        return dynamicMatchingComparisonResult;
    }

    // The fact that AfterSteps sometimes (albeit rarely) performs worse than PerStep, is due to the following.
    // PerStep allows moves to houses that are empty at any timestep.
    // AfterSteps only allows moves houses that are empty at the end of all environmental timesteps.
    // This means that by the time all new families and houses have been added,
    // PerStep will have a different matching and a different set of empty houses to move to, than AfterSteps.
    // Hence we get different solutions. The problem spaces simply aren't equal, nor is either a subset of the other.
    //
    // What we can also do is to let AfterSteps run the improvement-MCPMA as many times as there are timesteps.
    // But then we also have a slightly different problem space.
    public static Runnable runImprovement(int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws IOException {
        return () -> {
            String outputFilename = "../dyn-improvement-50times" + lineCount + "-";
            switch (matchingEvaluatorStrategy) {
                case AVG:
                    outputFilename += "avgME-";
                    break;
                case MIN:
                    outputFilename += "minME-";
                    break;
            }
            outputFilename += "100prob-twosided.csv";
            boolean oneSided = false;

            ArrayList<DynamicMatchingImprovementMCPMAComparisonResult> dynamicMatchingImprovementMCPMAComparisonResults
                    = new ArrayList<DynamicMatchingImprovementMCPMAComparisonResult>();
            ArrayList<Integer> startLines = new ArrayList<Integer>(Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300, 310, 320, 330, 340, 350, 360, 370, 380, 390, 400, 410, 420, 430, 440, 450, 460, 470, 480, 490, 500));
            boolean interrupted = false;
            for (int startLine : startLines) {
                System.out.println("Startline: " + startLine);
                try {
                    dynamicMatchingImprovementMCPMAComparisonResults.add(individualRunDynamicImprovementMatching(startLine, lineCount, oneSided, matchingEvaluatorStrategy));
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
            DynamicMatchingImprovementMCPMAComparisonResultProcessor dynamicMatchingImprovementMCPMAComparisonResultProcessor
                    = new DynamicMatchingImprovementMCPMAComparisonResultProcessor(dynamicMatchingImprovementMCPMAComparisonResults);
            if (!interrupted) {
                try {
                    dynamicMatchingImprovementMCPMAComparisonResultProcessor.resultsToCSV(outputFilename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static DynamicMatchingImprovementMCPMAComparisonResult individualRunDynamicImprovementMatching(int startLine, int lineCount, boolean oneSided, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws InterruptedException {
        int timestepCount = lineCount/2;
        DynamicMatchingImprovementMCPMAComparisonResult dynamicMatchingImprovementMCPMAComparisonResult = null;
        try {
            double connectionProb = 1.0;
            Matching matching = setupMatching(connectionProb, startLine, lineCount, matchingEvaluatorStrategy);
            DynamicMatching dynamicMatching = new DynamicMatching(matching, timestepCount, oneSided);

            Matching[] matchings = new Matching[3];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(timestepCount, DynamicStrategy.MCPMA_IMPROVEMENT, false);
            System.out.println("Got here! 0");
            DynamicMatching dynamicMatching0 = (DynamicMatching) deepClone(dynamicMatching);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(timestepCount, DynamicStrategy.MCPMA_IMPROVEMENT, false);
            System.out.println("Got here! 1");
            DynamicMatching dynamicMatching1 = (DynamicMatching) deepClone(dynamicMatching);
            dynamicMatching.resetState(); // Unnecessary but eh.
            matchings[2] = new MCPMAOnMatchingRunner((Matching) deepClone(dynamicMatching.getInputMatching()), MCPMAStrategy.REGULAR)
                    .optimizeMatching(false);
            System.out.println("Got here! 2");


            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            String[] strings = {
                    "Final per step score",
                    "Final afterwards score",
                    "Optimal (but non-IR) score"};

            prettyPrintResults(strings, scores);

            float perStepOptimality = scores[0]/scores[2];
            float afterwardsOptimality = scores[1]/scores[2];
            dynamicMatchingImprovementMCPMAComparisonResult
                    = new DynamicMatchingImprovementMCPMAComparisonResult(timestepCount,
                    scores[0], scores[1], scores[2],
                    perStepOptimality, afterwardsOptimality);

        } catch (Matching.HouseLinkedToHouseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DynamicMatching.TooManyTimestepsException e) {
            e.printStackTrace();
        } catch (Matching.PreferredNoHouseholdlessHouseException e) {
            e.printStackTrace();
        } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
            e.printStackTrace();
        } catch (Matching.HouseIDAlreadyPresentException e) {
            e.printStackTrace();
        } catch (HousingMarket.FreeSpaceException e) {
            e.printStackTrace();
        } catch (Matching.HouseAlreadyMatchedException e) {
            e.printStackTrace();
        } catch (Household.InvalidHouseholdException e) {
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

        return dynamicMatchingImprovementMCPMAComparisonResult;
    }

    public static Runnable runDynamicIRCycles(ArrayList<DynamicMatching> dynamicMatchings, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy) throws IOException {
        return () -> {

            String outputFilename = "../dyn-IRCycles-50times" + lineCount + "-";
            switch (matchingEvaluatorStrategy) {
                case AVG:
                    outputFilename += "avgME-";
                    break;
                case MIN:
                    outputFilename += "minME-";
                    break;
            }
            outputFilename += "100prob-twosided.csv";

            ArrayList<GenericResult> genericResults
                    = new ArrayList();
            boolean interrupted = false;
            for (DynamicMatching dynamicMatching : dynamicMatchings) {
                try {
                    genericResults.add(individualRunDynamicIRCyclesMatching(dynamicMatching));
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

    public static GenericResult individualRunDynamicIRCyclesMatching(DynamicMatching dynamicMatching) throws InterruptedException {
        GenericResult genericResult = null;
        try {
            Matching[] matchings = new Matching[2];
            matchings[0] = dynamicMatching.advanceTimeAndSolvePerStep(DynamicStrategy.WOSMA_IR_CYCLES, false);
            dynamicMatching.resetState();
            matchings[1] = dynamicMatching.advanceTimeFullyThenSolve(DynamicStrategy.WOSMA_IR_CYCLES, false);
            dynamicMatching.resetState();

            float[] scores = evaluateMatchingsAverageIndividualTotalFit(matchings);

            genericResult = new GenericResult(scores[0], scores[1]);

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
        }

        return genericResult;
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
