package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Matching.Matching;
import Matching.MatchingEvaluator;
import DynamicStrategy;

import java.util.List;

public class WorkerOptimalStableMatchingAlgorithm {
    private Matching matching;
    private TwoLabeledGraph twoLabeledGraph;

    public WorkerOptimalStableMatchingAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public Matching findWorkerOptimalStableMatching(DynamicStrategy dynamicStrategy, boolean print) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, Matching.PreferredNoHouseholdlessHouseException, CycleFinder.FullyExploredVertexDiscoveredException, InterruptedException {
        DynamicStrategy inputDynamicStrategy = dynamicStrategy;
        switch (dynamicStrategy) {
            case WOSMA_REGULAR: inputDynamicStrategy = DynamicStrategy.WOSMA_REGULAR; break;
            case WOSMA_FINDMAX: inputDynamicStrategy = DynamicStrategy.WOSMA_FINDMAX; break;
            case WOSMA_IR_CYCLES: inputDynamicStrategy = DynamicStrategy.WOSMA_IR_CYCLES; break;
            case MCPMA_IMPROVEMENT: inputDynamicStrategy = DynamicStrategy.MCPMA_IMPROVEMENT; break;
        }
        int i = 1;
        twoLabeledGraph = new TwoLabeledGraph(this.matching, dynamicStrategy);
        List<Integer> cycle;
        cycle = tryToFindCycle(dynamicStrategy, print);
        while (cycle != null) {
            if (Thread.interrupted()) {
                System.out.println("Interrupted here");
                throw new InterruptedException();
            }

            if(print) { System.out.println("Executing cycle " + i); }
            switch (dynamicStrategy) {
                case WOSMA_REGULAR:
                case WOSMA_FINDMAX: this.matching.executeCycle(cycle, twoLabeledGraph.getNil(), print); break;
                case WOSMA_IR_CYCLES: this.matching.executeCycleIRCycles(cycle, twoLabeledGraph.getNil(), twoLabeledGraph.getHouseholdInitialHouseMap(), print); break;
            }
            twoLabeledGraph.updateAfterCycleExecution(this.matching);
            cycle = tryToFindCycle(dynamicStrategy, print);
            i++;
        }

        this.matching.resetHouseholdsMovedByWOSMA();
        return this.matching;
    }

    public List<Integer> tryToFindCycle(DynamicStrategy dynamicStrategy, boolean print) throws CycleFinder.FullyExploredVertexDiscoveredException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException {
        List<Integer> cycle;
        try {
            cycle = twoLabeledGraph.findCycle(print);
        } catch (OutOfMemoryError e) {
            System.err.println("Error: Szwarcfiter-Lauer found more cycles than can fit in this computer's memory.");
            System.err.println("Downgrading strategy...");
            if (dynamicStrategy == DynamicStrategy.WOSMA_IR_CYCLES) {
                dynamicStrategy = DynamicStrategy.WOSMA_FINDMAX;
            } else if (dynamicStrategy == DynamicStrategy.WOSMA_FINDMAX) {
                dynamicStrategy = DynamicStrategy.WOSMA_REGULAR;
            }
            this.matching.setFindMaxFailed();
            twoLabeledGraph = new TwoLabeledGraph(matching, dynamicStrategy);
            cycle = tryToFindCycle(dynamicStrategy, print);
        }
        return cycle;
    }

}
