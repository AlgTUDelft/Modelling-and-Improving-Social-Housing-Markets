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
import java.util.Set;

public class DynamicMatching {

    private final Matching inputMatching;

    private final Matching initialMatching;
    private final int initialTimestepsLeft;
    private final ArrayList<House> initialHousesToArrive = new ArrayList<House>();
    private final ArrayList<Household> initialHouseholdsToArrive = new ArrayList<Household>();

    private Matching currentMatching;
    private int currentTimestepsLeft;
    private ArrayList<House> currentHousesToArrive;
    private ArrayList<Household> currentHouseholdsToArrive;

    private boolean oneSided; // false means two-sided arrival. One-sided means houses are set and households arrive.

    // TODO: Scores of 'per-step' are higher than 'all-afterwards', which is...understandable, given that we
    //  essentially pick a cycle randomly; but it's not so great for comparison purposes. Time to dive back into
    //  CycleFinder and change how it works...!
    public DynamicMatching(Matching matching, int timestepCount, boolean oneSided) throws TooManyTimestepsException, Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        inputMatching = matching;
        WorkerOptimalStableMatchingAlgorithm wosma
                = new WorkerOptimalStableMatchingAlgorithm((Matching) deepClone(inputMatching));
        this.initialMatching = wosma.findWorkerOptimalStableMatching(false,false);
        this.oneSided = oneSided;
        if (timestepCount > initialMatching.getHouseholds().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of households.");
        }
        if (!oneSided && timestepCount > initialMatching.getHouses().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of houses.");
        }
        for (int step = 0; step < timestepCount; step++) {
            Household randomHousehold = initialMatching.getHouseholds().get(new Random().nextInt(initialMatching.getHouseholds().size()));
            initialMatching.removeHousehold(randomHousehold.getID());
            this.initialHouseholdsToArrive.add(randomHousehold);
            if (!oneSided) {
                House randomHouse = initialMatching.getHouses().get(new Random().nextInt(initialMatching.getHouses().size()));
                initialMatching.removeHouse(randomHouse.getID());
                this.initialHousesToArrive.add(randomHouse);
            }
        }
        this.currentMatching = (Matching) deepClone(initialMatching);
        this.currentHousesToArrive = (ArrayList<House>) deepClone(initialHousesToArrive);
        this.currentHouseholdsToArrive = (ArrayList<Household>) deepClone(initialHouseholdsToArrive);
        this.initialTimestepsLeft = timestepCount;
        this.currentTimestepsLeft = (Integer) deepClone(timestepCount);
    }


    public Matching advanceTimeAndSolvePerStep(int timestepCount, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
            runAlgorithm(false, print);
        }
        Matching resultingMatching = (Matching) deepClone(currentMatching);
//        checkIfHouselessHouseholdsHaveNoPreferredEmptyHouse();
        resetState();
        return resultingMatching;
    }

    public Matching advanceTimeFullyThenSolve(int timestepCount, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
        }
        runAlgorithm(true, print);
        Matching resultingMatching = (Matching) deepClone(currentMatching);
        resetState();
        return resultingMatching;
    }

    private void simulateEnvironmentTimestep() {
        if (currentTimestepsLeft == 0) {
            System.err.print("Simulation has ended; cannot advance time further.");
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

    private void runAlgorithm(boolean findMax, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        WorkerOptimalStableMatchingAlgorithm wosma
                = new WorkerOptimalStableMatchingAlgorithm(currentMatching);
        wosma.findWorkerOptimalStableMatching(findMax, print); // Modifies currentMatching.
    }

    private void checkIfHouselessHouseholdsHaveNoPreferredEmptyHouse() throws MatchingEvaluator.HouseholdIncomeTooHighException {
        Set<Integer> houselessHouseholds = this.currentMatching.getHouselessHouseholdsIDs();
        Set<Integer> householdlessHouses = this.currentMatching.getHouseholdlessHousesIDs();
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(this.currentMatching);
        for (int houselessHouseholdID : houselessHouseholds) {
            for (int householdlessHouseID : householdlessHouses) {
                if (matchingEvaluator.evaluateIndividualTotalFit(householdlessHouseID, houselessHouseholdID) > 0) {
                    System.out.println("Got here!");
                }
            }
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
