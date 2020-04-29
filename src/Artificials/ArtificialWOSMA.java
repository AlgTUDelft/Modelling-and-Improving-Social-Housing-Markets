package Artificials;

import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Matching.Matching;
import Matching.MatchingEvaluator;

import java.util.List;

public class ArtificialWOSMA {
    private ArtificialMatching artificialMatching;

    public ArtificialWOSMA(ArtificialMatching artificialMatching) {
        this.artificialMatching = artificialMatching;
    }

    public ArtificialMatching findWorkerOptimalStableMatching(boolean findMax, boolean print) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, Matching.PreferredNoHouseholdlessHouseException, CycleFinder.FullyExploredVertexDiscoveredException {
        int i = 1;
        ArtificialTwoLabeledGraph artificialTwoLabeledGraph = new ArtificialTwoLabeledGraph(this.artificialMatching, findMax);
        List<Integer> cycle;
        try {
            cycle = artificialTwoLabeledGraph.findCycle(findMax, print);
        } catch (OutOfMemoryError e) {
            System.err.println("Tarjan found more cycles than can fit in this computer's memory.");
            System.err.println("Continuing as though _findMax_ is false...");
            findMax = false;
            this.artificialMatching.setStrategyDowngraded();
            cycle = artificialTwoLabeledGraph.findCycle(findMax, print);
        }
        while (cycle != null) {
//            if(print) { System.out.println("Executing cycle " + i); }
            System.out.println("Executing cycle " + i);
            this.artificialMatching.executeCycle(cycle, artificialTwoLabeledGraph.getNil(), print);
            artificialTwoLabeledGraph.updateAfterCycleExecution(this.artificialMatching, findMax);
            cycle = artificialTwoLabeledGraph.findCycle(findMax, print);
            i++;
        }

        this.artificialMatching.resetHouseholdsMovedByWOSMA();
        return this.artificialMatching;
    }


}
