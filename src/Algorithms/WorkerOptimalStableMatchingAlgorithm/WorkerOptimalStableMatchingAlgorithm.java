package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Matching.Matching;
import Matching.MatchingEvaluator;

import java.util.List;

public class WorkerOptimalStableMatchingAlgorithm {
    private Matching matching;

    public WorkerOptimalStableMatchingAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public Matching findWorkerOptimalStableMatching(boolean findMax, boolean print) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, Matching.PreferredNoHouseholdlessHouseException, CycleFinder.FullyExploredVertexDiscoveredException {
        int i = 1;
        TwoLabeledGraph twoLabeledGraph = new TwoLabeledGraph(this.matching, findMax);
        List<Integer> cycle;
        try {
            cycle = twoLabeledGraph.findCycle(findMax, print);
        } catch (OutOfMemoryError e) {
            System.err.println("Tarjan found more cycles than can fit in this computer's memory.");
            System.err.println("Continuing as though _findMax_ is false...");
            findMax = false;
            this.matching.setFindMaxFailed();
            cycle = twoLabeledGraph.findCycle(findMax, print);
        }
        while (cycle != null) {
            if(print) { System.out.println("Executing cycle " + i); }
            this.matching.executeCycle(cycle, twoLabeledGraph.getNil(), print);
            twoLabeledGraph.updateAfterCycleExecution(this.matching, findMax);
            cycle = twoLabeledGraph.findCycle(findMax, print);
            i++;
        }

        this.matching.resetHouseholdsMovedByWOSMA();
        return this.matching;
    }


}
