package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Algorithms.AlgorithmStrategy;
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

    public Matching findWorkerOptimalStableMatching(AlgorithmStrategy algorithmStrategy, boolean print) throws Matching.HouseholdLinkedToHouseholdException, Matching.HouseLinkedToMultipleException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, Matching.PreferredNoHouseholdlessHouseException, CycleFinder.FullyExploredVertexDiscoveredException, InterruptedException {
        int i = 1;
        twoLabeledGraph = new TwoLabeledGraph(this.matching, algorithmStrategy);
        List<Integer> cycle;
        cycle = tryToFindCycle(print);
        while (cycle != null) {
            if(print) { System.out.println("Executing cycle " + i); }
            switch (algorithmStrategy) {
                case WOSMA_REGULAR:
                case WOSMA_FINDMAX: this.matching.executeCycle(cycle, twoLabeledGraph.getNil(), print); break;
                case WOSMA_IRCYCLES: this.matching.executeCycleIRCycles(cycle, twoLabeledGraph.getNil(), twoLabeledGraph.getHouseholdInitialHouseMap(), print); break;
            }
            twoLabeledGraph.updateAfterCycleExecution(this.matching);
            cycle = tryToFindCycle(print);
            i++;
        }

        this.matching.resetHouseholdsMovedByWOSMA();
        return this.matching;
    }

    public List<Integer> tryToFindCycle(boolean print) throws CycleFinder.FullyExploredVertexDiscoveredException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseholdLinkedToMultipleException, Matching.HouseLinkedToHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, InterruptedException {
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
