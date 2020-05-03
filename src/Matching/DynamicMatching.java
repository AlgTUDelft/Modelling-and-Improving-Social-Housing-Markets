package Matching;

import Main.AlgorithmStrategy;
import Algorithms.MCPMA.*;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.WorkerOptimalStableMatchingAlgorithm;
import HousingMarket.House.House;
import HousingMarket.Household.Household;
import static Miscellaneous.DeepCloner.deepClone;

import java.io.*;
import java.util.ArrayList;

public class DynamicMatching implements Serializable {

    protected Matching inputMatching;

    private final Matching initialMatching;
    private final int initialTimestepsLeft;
    private final ArrayList<House> initialHousesToArrive;
    private final ArrayList<Household> initialHouseholdsToArrive;

    private Matching currentMatching;
    private int currentTimestepsLeft;
    private ArrayList<House> currentHousesToArrive;
    private ArrayList<Household> currentHouseholdsToArrive;
    private int timestepCount;

    protected boolean oneSided; // false means two-sided arrival. One-sided means houses are set and households arrive.

    public DynamicMatching(Matching matching, int timestepCount, boolean oneSided) throws TooManyTimestepsException {
        inputMatching = (Matching) deepClone(matching);
        this.oneSided = oneSided;
        this.timestepCount = (Integer) deepClone(timestepCount);
        initialHousesToArrive = new ArrayList<House>(this.timestepCount);
        initialHouseholdsToArrive = new ArrayList<Household>(this.timestepCount);
        if (this.timestepCount > inputMatching.getHouseholds().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of households.");
        }
        if (!oneSided && this.timestepCount > inputMatching.getHouses().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of houses.");
        }
        Matching initialMatching = (Matching) deepClone(inputMatching);
        for (int step = 0; step < this.timestepCount; step++) {
            Household lastHousehold = initialMatching.getHouseholds().get(initialMatching.getHouseholds().size()-1);
            initialMatching.removeHousehold(lastHousehold.getID());
            this.initialHouseholdsToArrive.add(step, lastHousehold);
            if (!oneSided) {
                House lastHouse = initialMatching.getHouses().get(initialMatching.getHouses().size()-1);
                initialMatching.removeHouse(lastHouse.getID());
                this.initialHousesToArrive.add(step, lastHouse);
            }
        }
        // TODO: Include initial WOSMA call?
        //WorkerOptimalStableMatchingAlgorithm wosma
//                = new WorkerOptimalStableMatchingAlgorithm(initialMatching);
        this.initialMatching = initialMatching;//wosma.findWorkerOptimalStableMatching(false,false);
        this.currentMatching = (Matching) deepClone(initialMatching);
        this.currentHousesToArrive = (ArrayList<House>) deepClone(initialHousesToArrive);
        this.currentHouseholdsToArrive = (ArrayList<Household>) deepClone(initialHouseholdsToArrive);
        this.initialTimestepsLeft = (Integer) deepClone(this.timestepCount);
        this.currentTimestepsLeft = (Integer) deepClone(this.timestepCount);
    }


    public Matching advanceTimeAndSolvePerStepAndReset(AlgorithmStrategy algorithmStrategy, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, ResidualGraph.MatchGraphNotEmptyException, MCPMAPrices.AlreadyInitiatedException, MCPMA.UnequalSidesException, ResidualGraph.PathEdgeNotInResidualGraphException, InterruptedException {
        if (print) {
            System.out.println("Running PerStep");
        }
        try {
            for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
            runAlgorithm(algorithmStrategy, print);
            }
        } catch (InterruptedException e) {
            this.resetState();
            throw e;
        }
        Matching resultingMatching = (Matching) deepClone(currentMatching);
        this.resetState();
        return resultingMatching;
    }

    public Matching advanceTimeFullyThenSolveAndReset(AlgorithmStrategy algorithmStrategy, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, ResidualGraph.MatchGraphNotEmptyException, MCPMAPrices.AlreadyInitiatedException, MCPMA.UnequalSidesException, ResidualGraph.PathEdgeNotInResidualGraphException, InterruptedException {
        if (print) {
            System.out.println("Running AfterSteps");
        }
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
        }
////        Note: This isn't regular behaviour.
//        for (int i = 0; i < timestepCount * 2; i++) {
//            runAlgorithm(findMax, print);
//        }
        try {
            runAlgorithm(algorithmStrategy, print);
        } catch (InterruptedException e) {
            this.resetState();
            throw e;
        }
        Matching resultingMatching = (Matching) deepClone(currentMatching);
        this.resetState();
        return resultingMatching;
    }

    private void simulateEnvironmentTimestep() throws Matching.HouseholdIDAlreadyPresentException, Matching.HouseIDAlreadyPresentException {
        if (currentTimestepsLeft == 0) {
            System.err.println("Simulation has ended; cannot advance time further.");
        } else {
            Household household = currentHouseholdsToArrive.get(0);
            currentHouseholdsToArrive.remove(household);
            currentMatching.addHousehold(household);
            if (!oneSided) {
                House house = currentHousesToArrive.get(0);
                currentHousesToArrive.remove(house);
                currentMatching.addHouse(house);
            }
            currentTimestepsLeft--;
        }
    }

    protected void runAlgorithm(AlgorithmStrategy algorithmStrategy, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, MCPMA.UnequalSidesException, MCPMAPrices.AlreadyInitiatedException, ResidualGraph.PathEdgeNotInResidualGraphException, ResidualGraph.MatchGraphNotEmptyException, InterruptedException {
        switch (algorithmStrategy) {
            case WOSMA_REGULAR:
            case WOSMA_FINDMAX:
            case WOSMA_IRCYCLES:
                WorkerOptimalStableMatchingAlgorithm wosma
                    = new WorkerOptimalStableMatchingAlgorithm(currentMatching);
                currentMatching = wosma.findWorkerOptimalStableMatching(algorithmStrategy, print);
                break;
            case IMPROVEMENT_MCPMA:
                MCPMAOnMatchingRunner mcpmaOnMatchingRunner
                    = new MCPMAOnMatchingRunner(currentMatching, MCPMAStrategy.IMPROVEMENT);
                currentMatching = mcpmaOnMatchingRunner.optimizeMatching(print);
                break;
        }
    }

    private void resetState() {
        this.currentMatching = (Matching) deepClone(this.initialMatching);
        this.currentHousesToArrive = (ArrayList<House>) deepClone(this.initialHousesToArrive);
        this.currentHouseholdsToArrive = (ArrayList<Household>) deepClone(this.initialHouseholdsToArrive);
        this.currentTimestepsLeft = (Integer) deepClone(initialTimestepsLeft);
    }

    public Matching getInitialMatching() {
        return initialMatching;
    }

    public Matching getInputMatching() {
        return inputMatching;
    }

    public String toString() {
        return this.currentMatching.toString();
    }

    public class TooManyTimestepsException extends Exception {
        public TooManyTimestepsException(String errorMessage) { super(errorMessage); }
    }
}
