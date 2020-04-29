package Matching;

import Algorithms.MCPMA.*;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.WorkerOptimalStableMatchingAlgorithm;
import HousingMarket.House.House;
import HousingMarket.Household.Household;

import java.io.*;
import java.util.ArrayList;

public class DynamicMatching implements Serializable {

    protected Matching inputMatching;

    private final Matching initialMatching;
    private final int initialTimestepsLeft;
    private final ArrayList<House> initialHousesToArrive = new ArrayList<House>();
    private final ArrayList<Household> initialHouseholdsToArrive = new ArrayList<Household>();

    private Matching currentMatching;
    private int currentTimestepsLeft;
    private ArrayList<House> currentHousesToArrive;
    private ArrayList<Household> currentHouseholdsToArrive;
    private int timestepCount;

    protected boolean oneSided; // false means two-sided arrival. One-sided means houses are set and households arrive.

    public DynamicMatching(Matching matching, int timestepCount, boolean oneSided) throws TooManyTimestepsException, Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        inputMatching = (Matching) deepClone(matching);
        this.oneSided = oneSided;
        this.timestepCount = timestepCount;
        if (timestepCount > inputMatching.getHouseholds().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of households.");
        }
        if (!oneSided && timestepCount > inputMatching.getHouses().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of houses.");
        }
        Matching initialMatching = (Matching) deepClone(inputMatching);
        for (int step = 0; step < timestepCount; step++) {
            Household lastHousehold = initialMatching.getHouseholds().get(initialMatching.getHouseholds().size()-1);
            initialMatching.removeHousehold(lastHousehold.getID());
            this.initialHouseholdsToArrive.add(lastHousehold);
            if (!oneSided) {
                House lastHouse = initialMatching.getHouses().get(initialMatching.getHouses().size()-1);
                initialMatching.removeHouse(lastHouse.getID());
                this.initialHousesToArrive.add(lastHouse);
            }
        }
        // TODO: Include initial WOSMA call?
        //WorkerOptimalStableMatchingAlgorithm wosma
//                = new WorkerOptimalStableMatchingAlgorithm(initialMatching);
        this.initialMatching = initialMatching;//wosma.findWorkerOptimalStableMatching(false,false);
        this.currentMatching = (Matching) deepClone(initialMatching);
        this.currentHousesToArrive = (ArrayList<House>) deepClone(initialHousesToArrive);
        this.currentHouseholdsToArrive = (ArrayList<Household>) deepClone(initialHouseholdsToArrive);
        this.initialTimestepsLeft = (Integer) deepClone(timestepCount);
        this.currentTimestepsLeft = (Integer) deepClone(timestepCount);
    }


    public Matching advanceTimeAndSolvePerStep(DynamicStrategy dynamicStrategy, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, ResidualGraph.MatchGraphNotEmptyException, MCPMAPrices.AlreadyInitiatedException, MCPMA.UnequalSidesException, ResidualGraph.PathEdgeNotInResidualGraphException, InterruptedException {
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
            runAlgorithm(dynamicStrategy, print);
        }
        Matching resultingMatching = (Matching) deepClone(currentMatching);
//        checkIfHouselessHouseholdsHaveNoPreferredEmptyHouse();
        return resultingMatching;
    }

    public Matching advanceTimeFullyThenSolve(DynamicStrategy dynamicStrategy, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException, ResidualGraph.MatchGraphNotEmptyException, MCPMAPrices.AlreadyInitiatedException, MCPMA.UnequalSidesException, ResidualGraph.PathEdgeNotInResidualGraphException, InterruptedException {
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
        }
////        Note: This isn't regular behaviour.
//        for (int i = 0; i < timestepCount * 2; i++) {
//            runAlgorithm(findMax, print);
//        }
        runAlgorithm(dynamicStrategy, print);
        Matching resultingMatching = (Matching) deepClone(currentMatching);
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

    protected void runAlgorithm(DynamicStrategy dynamicStrategy, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, MCPMA.UnequalSidesException, MCPMAPrices.AlreadyInitiatedException, ResidualGraph.PathEdgeNotInResidualGraphException, ResidualGraph.MatchGraphNotEmptyException, InterruptedException {
        switch (dynamicStrategy) {
            case WOSMA_REGULAR:
            case WOSMA_FINDMAX:
            case WOSMA_IR_CYCLES:
                WorkerOptimalStableMatchingAlgorithm wosma
                    = new WorkerOptimalStableMatchingAlgorithm(currentMatching);
                try {
                    currentMatching = wosma.findWorkerOptimalStableMatching(dynamicStrategy, print);
                } catch (InterruptedException e) {
                    throw e;
                }
                break;
            case MCPMA_IMPROVEMENT:
                MCPMAOnMatchingRunner mcpmaOnMatchingRunner
                    = new MCPMAOnMatchingRunner(currentMatching, MCPMAStrategy.IMPROVEMENT);
                try {
                    currentMatching = mcpmaOnMatchingRunner.optimizeMatching(print);
                } catch (InterruptedException e) {
                    throw e;
                }
                break;
        }
    }

    public void resetState() {
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

    private static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Matching getCurrentMatching() {
        return currentMatching;
    }

    public int getCurrentTimestepsLeft() {
        return currentTimestepsLeft;
    }

    public String toString() {
        return this.currentMatching.toString();
    }

    public class TooManyTimestepsException extends Exception {
        public TooManyTimestepsException(String errorMessage) { super(errorMessage); }
    }
}
