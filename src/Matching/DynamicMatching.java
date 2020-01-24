package Matching;

import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.WorkerOptimalStableMatchingAlgorithm;
import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class DynamicMatching {

    private final Matching initialMatching;
    private Matching currentMatching;
    private int timestepsLeft;
    private final ArrayList<House> initialHousesToArrive = new ArrayList<House>();
    private final ArrayList<Household> initialHouseholdsToArrive = new ArrayList<Household>();
    private ArrayList<House> currentHousesToArrive;
    private ArrayList<Household> currentHouseholdsToArrive;
    private boolean oneSided; // false means two-sided arrival. One-sided means houses are set and households arrive.


    public DynamicMatching(Matching matching, int timestepCount, boolean oneSided) throws TooManyTimestepsException {
        this.initialMatching = matching;
        this.currentMatching = (Matching) deepClone(matching);
        this.oneSided = oneSided;
        if (timestepCount > initialMatching.getHouseholds().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of households.");
        }
        for (int step = 0; step < timestepCount; step++) {
            House randomHouse = initialMatching.getHouses().get(new Random().nextInt(initialMatching.getHouses().size()));
            initialMatching.removeHouse(randomHouse.getID());
            this.initialHousesToArrive.add(randomHouse);
            if (!oneSided) {
                Household randomHousehold = initialMatching.getHouseholds().get(new Random().nextInt(initialMatching.getHouseholds().size()));
                initialMatching.removeHousehold(randomHousehold.getID());
                this.initialHouseholdsToArrive.add(randomHousehold);
            }
        }
        currentHousesToArrive = (ArrayList<House>) deepClone(initialHousesToArrive);
        currentHouseholdsToArrive = (ArrayList<Household>) deepClone(initialHouseholdsToArrive);
        this.timestepsLeft = timestepCount;
    }

    public Matching advanceTimeAndSolvePerStep(int timestepCount) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        for (int i = 0; i < timestepCount; i++) {
            simulateEnvironmentTimestep();
            runAlgorithm();
        }
        Matching resultingMatching = (Matching) deepClone(currentMatching);
        resetState();
        return resultingMatching;
    }

    public Matching advanceTimeFullyThenSolve(int timestepCount) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        for (int i = 0; i < timestepCount; i++) {
            simulateEnvironmentTimestep();
        }
        runAlgorithm();
        Matching resultingMatching = (Matching) deepClone(currentMatching);
        resetState();
        return resultingMatching;
    }

    private Matching simulateEnvironmentTimestep() {
        if (timestepsLeft == 0) {
            System.err.print("Simulation has ended; cannot advance time further.");
        } else {
            House house = currentHousesToArrive.get(0);
            currentHousesToArrive.remove(house);
            currentMatching.addHouse(house);
            if (!oneSided) {
                Household household = currentHouseholdsToArrive.get(0);
                currentHouseholdsToArrive.remove(household);
                currentMatching.addHousehold(household);
            }
            timestepsLeft--;
        }
        return currentMatching;
    }

    private void runAlgorithm() throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        WorkerOptimalStableMatchingAlgorithm wosma
                = new WorkerOptimalStableMatchingAlgorithm(currentMatching);
        this.currentMatching = wosma.findWorkerOptimalStableMatching();
    }

    private void resetState() {
        this.currentMatching = (Matching) deepClone(this.initialMatching);
        this.currentHousesToArrive = (ArrayList<House>) deepClone(this.initialHousesToArrive);
        this.currentHouseholdsToArrive = (ArrayList<Household>) deepClone(this.initialHouseholdsToArrive);
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

    public class TooManyTimestepsException extends Exception {
        public TooManyTimestepsException(String errorMessage) { super(errorMessage); }
    }
}
