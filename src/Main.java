import Algorithms.Algorithm;
import Matching.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        for (MatchingEvaluatorStrategy matchingEvaluatorStrategy : MatchingEvaluatorStrategy.values()) {
            for (Algorithm algorithm : Algorithm.values()) {
                for (int lineCount : new ArrayList<>(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 25, 30, 35, 40, 45, 50, 75, 100, 125, 150))) {
                    long allowedRunningTime = 2_000;
                    boolean interrupted = runAlgorithm(allowedRunningTime, lineCount, matchingEvaluatorStrategy, algorithm);
                    if (interrupted) {
                        // Larger instances would take too long; therefore, time to break.
                        break;
                    }
                }
            }
        }
    }

    public static boolean runAlgorithm(long allowedRunningTime, int lineCount, MatchingEvaluatorStrategy matchingEvaluatorStrategy, Algorithm algorithm) throws InterruptedException, IOException {
        boolean tookTooLong = false;
        Compare compare = new Compare();
        Thread thread = null;
        switch(algorithm) {
            case IRCYCLES: thread = new Thread(compare.runDynamicIRCycles(lineCount, matchingEvaluatorStrategy)); break;
            case WOSMA: thread = new Thread(compare.runDynamicWOSMAMatching(lineCount, matchingEvaluatorStrategy)); break;
            case IMPROVEMENTMCPMA: thread = new Thread(compare.runImprovement(lineCount, matchingEvaluatorStrategy)); break;
        }

        thread.start();      // requirement 3
        Thread.sleep(allowedRunningTime);     // requirement 4
        if (Thread.activeCount() > 2) {
            System.out.println("Alive");
            tookTooLong = true;
        }
        thread.interrupt();  // requirement 5
        thread.join();  // requirement 6
        System.out.println("Got here");
        return tookTooLong;
    }

}