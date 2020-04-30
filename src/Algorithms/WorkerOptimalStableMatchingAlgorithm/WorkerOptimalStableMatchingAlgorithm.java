package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Matching.Matching;
import Matching.MatchingEvaluator;
import Matching.DynamicStrategy;

import static Miscellaneous.DeepCloner.deepClone;

import java.util.List;
import java.util.Random;

public class WorkerOptimalStableMatchingAlgorithm {
    private Matching matching;
    private TwoLabeledGraph twoLabeledGraph;

    public WorkerOptimalStableMatchingAlgorithm(Matching matching) {
        this.matching = (Matching) deepClone(matching);
    }

    public Matching findWorkerOptimalStableMatching(DynamicStrategy dynamicStrategy, boolean print) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, Matching.PreferredNoHouseholdlessHouseException, CycleFinder.FullyExploredVertexDiscoveredException, InterruptedException {
        DynamicStrategy inputDynamicStrategy = (DynamicStrategy) deepClone(dynamicStrategy);
        int i = 1;
        twoLabeledGraph = new TwoLabeledGraph(this.matching, dynamicStrategy);
        List<Integer> cycle;
        cycle = tryToFindCycle(dynamicStrategy, inputDynamicStrategy, print);
        while (cycle != null) {
            if (Thread.interrupted()) {
//                System.out.println("Interrupted here");
                dynamicStrategy = inputDynamicStrategy;
                this.matching.resetHouseholdsMovedByWOSMA();
                throw new InterruptedException();
            }

            if(print) { System.out.println("Executing cycle " + i); }
            switch (dynamicStrategy) {
                case WOSMA_REGULAR:
                case WOSMA_FINDMAX: this.matching.executeCycle(cycle, twoLabeledGraph.getNil(), print); break;
                case WOSMA_IR_CYCLES: this.matching.executeCycleIRCycles(cycle, twoLabeledGraph.getNil(), twoLabeledGraph.getHouseholdInitialHouseMap(), print); break;
            }
            twoLabeledGraph.updateAfterCycleExecution(this.matching);
            cycle = tryToFindCycle(dynamicStrategy, inputDynamicStrategy, print);
            i++;
        }

        dynamicStrategy = inputDynamicStrategy;
        this.matching.resetHouseholdsMovedByWOSMA();
        return this.matching;
    }

    public List<Integer> tryToFindCycle(DynamicStrategy dynamicStrategy, DynamicStrategy inputDynamicStrategy, boolean print) throws CycleFinder.FullyExploredVertexDiscoveredException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, InterruptedException {
        List<Integer> cycle;
        try {
            cycle = twoLabeledGraph.findCycle(print);
        } catch (OutOfMemoryError e) {
            if (print) {
                System.err.println("Error: Szwarcfiter-Lauer found more cycles than can fit in this computer's memory.");
                System.err.println("Downgrading strategy...");
            }
            if (dynamicStrategy == DynamicStrategy.WOSMA_IR_CYCLES) {
                dynamicStrategy = DynamicStrategy.WOSMA_FINDMAX;
            } else if (dynamicStrategy == DynamicStrategy.WOSMA_FINDMAX) {
                dynamicStrategy = DynamicStrategy.WOSMA_REGULAR;
            } else if (dynamicStrategy == DynamicStrategy.WOSMA_REGULAR) {
                if (print) {
                    System.err.println("WOSMA_REGULAR ran out of memory somehow. Interrupting thread...");
                }
                this.matching.resetHouseholdsMovedByWOSMA();
                dynamicStrategy = inputDynamicStrategy;
                throw new InterruptedException();
            }
            this.matching.setStrategyDowngraded();
            twoLabeledGraph = new TwoLabeledGraph(matching, dynamicStrategy);
            cycle = tryToFindCycle(dynamicStrategy, inputDynamicStrategy, print);
        }
        return cycle;
    }

}
