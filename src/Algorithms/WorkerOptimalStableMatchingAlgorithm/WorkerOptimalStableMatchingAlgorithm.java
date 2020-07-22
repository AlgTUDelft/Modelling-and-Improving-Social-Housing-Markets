package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Main.AlgorithmStrategy;
import Main.GradingStrategy;
import Matching.Matching;
import Matching.MatchingEvaluator;

import static Miscellaneous.DeepCloner.deepClone;

import java.util.List;

public class WorkerOptimalStableMatchingAlgorithm {
    private Matching matching;
    private TwoLabeledGraph twoLabeledGraph;

    public WorkerOptimalStableMatchingAlgorithm(Matching matching) {
        this.matching = (Matching) deepClone(matching);
    }

    public Matching findWorkerOptimalStableMatching(AlgorithmStrategy algorithmStrategy, GradingStrategy gradingStrategy, boolean print) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, Matching.PreferredNoHouseholdlessHouseException, CycleFinder.FullyExploredVertexDiscoveredException, InterruptedException {
        twoLabeledGraph = new TwoLabeledGraph(this.matching, algorithmStrategy, gradingStrategy);
        List<Integer> cycle;
        cycle = tryToFindCycle(print);
        while (cycle != null) {
            if(print) { System.out.println("Executing cycle " + cycle); }
            switch (algorithmStrategy) {
                case WOSMA_REGULAR:
                case WOSMA_FINDMAX: this.matching.executeCycle(cycle, twoLabeledGraph.getNil(), print, gradingStrategy); break;
                case WOSMA_IRCYCLES: this.matching.executeCycleIRCycles(cycle, twoLabeledGraph.getNil(), twoLabeledGraph.getHouseholdInitialHouseMap(), print, gradingStrategy); break;
            }
            twoLabeledGraph.updateAfterCycleExecution(this.matching);
            cycle = tryToFindCycle(print);
        }

        this.matching.resetHouseholdsMovedByWOSMA();
        return this.matching;
    }

    private List<Integer> tryToFindCycle(boolean print) throws CycleFinder.FullyExploredVertexDiscoveredException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, InterruptedException {
        List<Integer> cycle;
        try {
            cycle = twoLabeledGraph.findCycle(print);
        } catch (OutOfMemoryError e) {
            this.matching.resetHouseholdsMovedByWOSMA();
            throw new InterruptedException();
        } catch (InterruptedException e) {
            this.matching.resetHouseholdsMovedByWOSMA();
            throw e;
        }
        return cycle;
    }

}
