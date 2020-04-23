package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Matching.Matching;
import Matching.MatchingEvaluator;
import Matching.Strategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.module.FindException;
import java.util.List;

public class WorkerOptimalStableMatchingAlgorithm {
    private Matching matching;

    public WorkerOptimalStableMatchingAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public Matching findWorkerOptimalStableMatching(Strategy strategy, boolean print) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, Matching.PreferredNoHouseholdlessHouseException, CycleFinder.FullyExploredVertexDiscoveredException {
        Strategy inputStrategy = strategy;
        switch (strategy) {
            case WOSMA_REGULAR: inputStrategy = Strategy.WOSMA_REGULAR; break;
            case WOSMA_FINDMAX: inputStrategy = Strategy.WOSMA_FINDMAX; break;
            case WOSMA_IR_CYCLES: inputStrategy = Strategy.WOSMA_IR_CYCLES; break;
            case MCPMA_IMPROVEMENT: inputStrategy = Strategy.MCPMA_IMPROVEMENT; break;
        }
        int i = 1;
        TwoLabeledGraph twoLabeledGraph = new TwoLabeledGraph(this.matching, strategy);
        List<Integer> cycle;
        boolean justFailed = false;
        try {
            cycle = twoLabeledGraph.findCycle(print);
        } catch (OutOfMemoryError e) {
            System.err.println("Error: Szwarcfiter-Lauer found more cycles than can fit in this computer's memory.");
            System.err.println("Downgrading strategy...");
            if (strategy == Strategy.WOSMA_IR_CYCLES) {
                strategy = Strategy.WOSMA_FINDMAX;
            } else if (strategy == Strategy.WOSMA_FINDMAX) {
                strategy = Strategy.WOSMA_REGULAR;
            }
            this.matching.setFindMaxFailed();
            twoLabeledGraph = new TwoLabeledGraph(this.matching, strategy);
            cycle = twoLabeledGraph.findCycle(print);
            justFailed = true;
        }
        while (cycle != null) {
            if(print) { System.out.println("Executing cycle " + i); }
            switch (strategy) {
                case WOSMA_REGULAR:
                case WOSMA_FINDMAX: this.matching.executeCycle(cycle, twoLabeledGraph.getNil(), print); break;
                case WOSMA_IR_CYCLES: this.matching.executeCycleIRCycles(cycle, twoLabeledGraph.getNil(), twoLabeledGraph.getHouseholdInitialHouseMap(), print); break;
            }
            if (justFailed) {
                strategy = inputStrategy;
            }
            twoLabeledGraph.updateAfterCycleExecution(this.matching);
            cycle = twoLabeledGraph.findCycle(print);
            i++;
        }

        this.matching.resetHouseholdsMovedByWOSMA();
        return this.matching;
    }

}
