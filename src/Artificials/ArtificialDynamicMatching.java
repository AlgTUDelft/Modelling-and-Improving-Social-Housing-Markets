package Artificials;

import Algorithms.WorkerOptimalStableMatchingAlgorithm.CycleFinder;
import Algorithms.WorkerOptimalStableMatchingAlgorithm.WorkerOptimalStableMatchingAlgorithm;
import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class ArtificialDynamicMatching {

    protected ArtificialMatching inputArtificialMatching;

    private final ArtificialMatching initialArtificialMatching;
    private final int initialTimestepsLeft;
    private final ArrayList<House> initialHousesToArrive = new ArrayList<House>();
    private final ArrayList<Household> initialHouseholdsToArrive = new ArrayList<Household>();

    private ArtificialMatching currentArtificialMatching;
    private int currentTimestepsLeft;
    private ArrayList<House> currentHousesToArrive;
    private ArrayList<Household> currentHouseholdsToArrive;

    protected boolean oneSided; // false means two-sided arrival. One-sided means houses are set and households arrive.

    public ArtificialDynamicMatching(ArtificialMatching artificialMatching, int timestepCount, boolean oneSided) throws TooManyTimestepsException, Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException {
        inputArtificialMatching = artificialMatching;
        this.oneSided = oneSided;
        if (timestepCount > inputArtificialMatching.getHouseholds().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of households.");
        }
        if (!oneSided && timestepCount > inputArtificialMatching.getHouses().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of houses.");
        }
        ArtificialMatching initialArtificialMatching = (ArtificialMatching) deepClone(inputArtificialMatching);
        for (int step = 0; step < timestepCount; step++) {
            Household lastHousehold = initialArtificialMatching.getHouseholds().get(initialArtificialMatching.getHouseholds().size()-1);
            initialArtificialMatching.removeHousehold(lastHousehold.getID());
            this.initialHouseholdsToArrive.add(lastHousehold);
            if (!oneSided) {
                House lastHouse = initialArtificialMatching.getHouses().get(initialArtificialMatching.getHouses().size()-1);
                initialArtificialMatching.removeHouse(lastHouse.getID());
                this.initialHousesToArrive.add(lastHouse);
            }
        }
//        ArtificialWOSMA artificialWOSMA
//                = new ArtificialWOSMA(initialArtificialMatching);
//        // TODO: set findMax to true?
        this.initialArtificialMatching = initialArtificialMatching;//artificialWOSMA.findWorkerOptimalStableMatching(false,false);
        this.currentArtificialMatching = (ArtificialMatching) deepClone(initialArtificialMatching);
        this.currentHousesToArrive = (ArrayList<House>) deepClone(initialHousesToArrive);
        this.currentHouseholdsToArrive = (ArrayList<Household>) deepClone(initialHouseholdsToArrive);
        this.initialTimestepsLeft = timestepCount;
        this.currentTimestepsLeft = (Integer) deepClone(timestepCount);
    }


    public ArtificialMatching advanceTimeAndSolvePerStep(int timestepCount, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException {
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
            runAlgorithm(false, print);
        }
        ArtificialMatching resultingMatching = (ArtificialMatching) deepClone(currentArtificialMatching);
//        checkIfHouselessHouseholdsHaveNoPreferredEmptyHouse();
        return resultingMatching;
    }

    public ArtificialMatching advanceTimeFullyThenSolve(int timestepCount, boolean findMax, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException {
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
        }
        runAlgorithm(findMax, print);
        // TODO: Include the following?
//        runAlgorithm(false, false);
        ArtificialMatching resultingMatching = (ArtificialMatching) deepClone(currentArtificialMatching);
        return resultingMatching;
    }

    private void simulateEnvironmentTimestep() throws Matching.HouseholdIDAlreadyPresentException, Matching.HouseIDAlreadyPresentException {
        if (currentTimestepsLeft == 0) {
            System.err.print("Simulation has ended; cannot advance time further.");
        } else {
            Household household = currentHouseholdsToArrive.get(0);
            currentHouseholdsToArrive.remove(household);
            currentArtificialMatching.addHousehold(household);
            if (!oneSided) {
                House house = currentHousesToArrive.get(0);
                currentHousesToArrive.remove(house);
                currentArtificialMatching.addHouse(house);
            }
            currentTimestepsLeft--;
        }
    }

    protected void runAlgorithm(boolean findMax, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        ArtificialWOSMA wosma
                = new ArtificialWOSMA(currentArtificialMatching);
        wosma.findWorkerOptimalStableMatching(findMax, print); // Modifies currentMatching.
    }

    private void checkIfHouselessHouseholdsHaveNoPreferredEmptyHouse() throws MatchingEvaluator.HouseholdIncomeTooHighException {
        Set<Integer> houselessHouseholds = this.currentArtificialMatching.getHouselessHouseholdsIDs();
        Set<Integer> householdlessHouses = this.currentArtificialMatching.getHouseholdlessHousesIDs();
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(this.currentArtificialMatching);
        for (int houselessHouseholdID : houselessHouseholds) {
            for (int householdlessHouseID : householdlessHouses) {
                if (matchingEvaluator.evaluateIndividualTotalFit(householdlessHouseID, houselessHouseholdID) > 0) {
                    System.out.println("Got here!");
                }
            }
        }
    }

    public void resetState() {
        this.currentArtificialMatching = (ArtificialMatching) deepClone(this.initialArtificialMatching);
        this.currentHousesToArrive = (ArrayList<House>) deepClone(this.initialHousesToArrive);
        this.currentHouseholdsToArrive = (ArrayList<Household>) deepClone(this.initialHouseholdsToArrive);
        this.currentTimestepsLeft = (Integer) deepClone(initialTimestepsLeft);
    }

    public ArtificialMatching getInitialArtificialMatching() {
        return initialArtificialMatching;
    }

    public ArtificialMatching getInputArtificialMatching() {
        return inputArtificialMatching;
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
