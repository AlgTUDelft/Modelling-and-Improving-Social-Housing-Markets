package Main;

import Results.GenericResult;
import Results.GenericResultProcessor;
import Matching.DynamicMatching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Comparer {

    private ArrayList<DynamicMatching> dynamicMatchings;
    private long allowedRunningTime;
    private int lineCount;
    private int nTimes;
    private Double envRatio;
    private GradingStrategy gradingStrategy;
    private HashMap<AlgorithmStrategy, CompletableFuture<ArrayList<GenericResult>>> results;
    private HashSet<AlgorithmStrategy> interruptedAlgorithmStrategies;

    public Comparer(ArrayList<DynamicMatching> dynamicMatchings, long allowedRunningTime, int lineCount, int nTimes, Double envRatio, GradingStrategy gradingStrategy, HashSet<AlgorithmStrategy> interruptedAlgorithmStrategies) {
        this.dynamicMatchings = dynamicMatchings;
        this.allowedRunningTime = allowedRunningTime;
        this.lineCount = lineCount;
        this.nTimes = nTimes;
        this.envRatio = envRatio;
        this.gradingStrategy = gradingStrategy;
        this.interruptedAlgorithmStrategies = interruptedAlgorithmStrategies;

        this.results = new HashMap<>(AlgorithmStrategy.values().length);
        for (AlgorithmStrategy algorithmStrategy : AlgorithmStrategy.values()) {
            results.put(algorithmStrategy, new CompletableFuture<>());
        }
    }

    // Runs algorithms, returns knowledge of newly interrupted algorithms so Experimenter can exclude these in the future.
    public HashSet<AlgorithmStrategy> run() throws InterruptedException {
        HashSet<AlgorithmStrategy> toInterrupt = new HashSet<>();

        // For each algorithm...
        for (AlgorithmStrategy algorithmStrategy : AlgorithmStrategy.values()) {
            if (interruptedAlgorithmStrategies.contains(algorithmStrategy)) {
                // Algorithm took too long in smaller instance, so don't go on.
                System.out.println("Skipping:    " + envRatio + " | " + gradingStrategy + " | " + lineCount + " | " + algorithmStrategy);
            } else {
                // Run it and check if we were interrupted during execution.

                CompletableFuture<ArrayList<GenericResult>> resultsPerAlgorithm = results.get(algorithmStrategy);


                boolean interrupted = this.runAlgorithm(resultsPerAlgorithm, algorithmStrategy, gradingStrategy);

                if (interrupted) {
                    System.out.println("Interrupted: " + envRatio + " | " + gradingStrategy + " | " + lineCount + " | " + algorithmStrategy);
                    toInterrupt.add(algorithmStrategy);
                } else {
                    System.out.println("Finished:    " + envRatio + " | " + gradingStrategy + " | " + lineCount + " | " + algorithmStrategy);
                }
            }
        }

        processAndSaveResults(results, lineCount,envRatio, gradingStrategy);
//        performSanityCheck(results);
        return toInterrupt;
    }

    public Runner createNewRunner(AlgorithmStrategy algorithmStrategy, boolean print) {
        return new Runner(dynamicMatchings, nTimes, algorithmStrategy, print);
    }

    private boolean runAlgorithm(CompletableFuture<ArrayList<GenericResult>> resultsPerAlgorithm, AlgorithmStrategy algorithmStrategy, GradingStrategy gradingStrategy) throws InterruptedException
    {
        boolean tookTooLong = false;
        Thread thread = null;
        Runner runner = createNewRunner(algorithmStrategy, false);

        switch(algorithmStrategy) {
            case WOSMA_REGULAR:
            case WOSMA_FINDMAX:
            case WOSMA_IRCYCLES:
            case IMPROVEMENT_MCPMA:
            case SIMPLE:
                thread = new Thread(runner.runDynamic(resultsPerAlgorithm, gradingStrategy));
                break;
            case MCPMA:
                thread = new Thread(runner.runStaticMCPMA(resultsPerAlgorithm, gradingStrategy));
                break;
        }

        thread.start();
        try {
            thread.join(allowedRunningTime);
        } catch (InterruptedException e) {
            // Needless to say, this shouldn't happen, since we never interrupt threads
            // except through the above thread.join call and the interrupt call just below this.
            System.err.println("Thread got interrupted somehow.");
        }
        if (thread.isAlive()) {
            thread.interrupt();
            thread.join();
            tookTooLong = true;
        }

        return tookTooLong;
    }


    private void performSanityCheck(HashMap<AlgorithmStrategy, CompletableFuture<ArrayList<GenericResult>>> results) {
        try {
            // Compare IR-Cycles with MCPMA.
            if (results.get(AlgorithmStrategy.WOSMA_IRCYCLES).isDone() && results.get(AlgorithmStrategy.MCPMA).isDone()) {
                ArrayList<GenericResult> WOSMA_IRCycles = results.get(AlgorithmStrategy.WOSMA_IRCYCLES).get();
                ArrayList<GenericResult> MCPMA = results.get(AlgorithmStrategy.MCPMA).get();
                for (int i = 0; i < nTimes; i++) {
                    float WOSMA_IRCyclesScore = WOSMA_IRCycles.get(i).getSolvedFinalMatchingAfterwardsScore();
                    float MCPMAScore = MCPMA.get(i).getSolvedFinalMatchingAfterwardsScore();
                    if (WOSMA_IRCyclesScore > MCPMAScore + 0.0001) {
                        System.err.println("Error 1! Got nonsensical results.");
//                        while (true) {
//                            createNewRunner(AlgorithmStrategy.MCPMA, true)
//                                    .individualRunStaticMCPMA(dynamicMatchings.get(i));
//                            createNewRunner(AlgorithmStrategy.WOSMA_IRCYCLES, true)
//                                    .individualRunDynamic(dynamicMatchings.get(i));
//                        }
                    }
                }
            }
            // Compare IR-Cycles with WOSMA_FindMax.
            if (results.get(AlgorithmStrategy.WOSMA_IRCYCLES).isDone() && results.get(AlgorithmStrategy.WOSMA_FINDMAX).isDone()) {
                ArrayList<GenericResult> WOSMA_FindMax = results.get(AlgorithmStrategy.WOSMA_FINDMAX).get();
                ArrayList<GenericResult> WOSMA_IRCycles = results.get(AlgorithmStrategy.WOSMA_IRCYCLES).get();
                for (int i = 0; i < nTimes; i++) {
                    float WOSMA_FindMaxScore = WOSMA_FindMax.get(i).getSolvedFinalMatchingAfterwardsScore();
                    float WOSMA_IRCyclesScore = WOSMA_IRCycles.get(i).getSolvedFinalMatchingAfterwardsScore();
                    if (WOSMA_FindMaxScore > WOSMA_IRCyclesScore + 0.0001) {
                        System.err.println("Error 2! Got nonsensical results.");
//                        while (true) {
//                            createNewRunner(AlgorithmStrategy.WOSMA_IRCYCLES, true)
//                                    .individualRunDynamic(dynamicMatchings.get(i));
////                            createNewRunner(AlgorithmStrategy.WOSMA_FINDMAX, false)
////                                    .individualRunDynamic(dynamicMatchings.get(i));
//                            System.out.println("");
//                            System.out.println("");
//                            System.out.println("Restarting...");
//                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }



    private void processAndSaveResults(HashMap<AlgorithmStrategy, CompletableFuture<ArrayList<GenericResult>>> results,
                                      int lineCount, double envRatio,
                                      GradingStrategy gradingStrategy) {
        for (AlgorithmStrategy algorithmStrategy : AlgorithmStrategy.values()) {
            try {
                if (results.get(algorithmStrategy).isDone()) {
                    ArrayList<GenericResult> algorithmResults = results.get(algorithmStrategy).get();
                    String outputFilename = createFilename(algorithmStrategy, lineCount, envRatio, gradingStrategy);
                    new GenericResultProcessor(algorithmResults).resultsToCSV(outputFilename);
                }
            } catch (InterruptedException e) {
                // Then don't save file.
            } catch (ExecutionException e) {
                // Then don't save file.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String createFilename(AlgorithmStrategy algorithmStrategy, int lineCount, double envRatio, GradingStrategy gradingStrategy) {
        String outputFilename = "../../Data/Output/Scores/";

        switch (algorithmStrategy) {
            case WOSMA_REGULAR:
            case WOSMA_FINDMAX:
            case WOSMA_IRCYCLES:
            case IMPROVEMENT_MCPMA:
            case SIMPLE: outputFilename += "dyn-"; break;
            case MCPMA: outputFilename += "static-"; break;
        }

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
            case SIMPLE:
                outputFilename += "Simple-"; break;
        }
        outputFilename += "50times" + lineCount + "-" + envRatio + "-" + gradingStrategy + "-100prob-twosided.csv";
        return outputFilename;
    }


}

