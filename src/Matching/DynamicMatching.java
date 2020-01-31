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

    protected Matching inputMatching;

    private final Matching initialMatching;
    private final int initialTimestepsLeft;
    private final ArrayList<House> initialHousesToArrive = new ArrayList<House>();
    private final ArrayList<Household> initialHouseholdsToArrive = new ArrayList<Household>();

    private Matching currentMatching;
    private int currentTimestepsLeft;
    private ArrayList<House> currentHousesToArrive;
    private ArrayList<Household> currentHouseholdsToArrive;

    protected boolean oneSided; // false means two-sided arrival. One-sided means houses are set and households arrive.

    // TODO: Analyze scores.
    //       -> There isn't really any consistency to these, although fortunately they're all below optimal...
    //       -> Okay, wait, it seems that at least all Afterwards scores are lower than AfterwardsFindMax,
    //          at least with avgME. But it's still puzzling that PerStep often performs better than
    //          Afterwards, and sometimes even better than AfterwardsFindMax.
    //          So two things need to be explained:
    //          - Why does PerStep often perform better than Afterwards?
    //          - Why does PerStep sometimes perform better than even AfterwardsFindMax?
    //          Update: I've found a (one-sided) case where Afterwards performs better than AfterwardsFindMax.
    //                  -> For this, I guess the below explanation suffices.
    //                     It only happens rarely, which roughly fits with that explanation.
    //
    // TODO: Double-check findMax in finding cycles; does it really capture the kinds of cycles (re: strictness of edges, etc.)
    //        that we want it to capture? -> e.g. split functions into two for easier checking.
    //        - Check functions in TwoLabeledGraph. -- DONE
    //        - Check functions in CycleFinder. -- DONE
    //        --> OK, so what's happening is that Tarjan finds only fully strict cycles,
    //            whereas CycleFinder finds cycles that are either fully strict,
    //            or, insofar as they aren't, consisting of households that have moved before
    //            in this timestep.
    //            So it makes sense that _this_ could work better than Tarjan/findMax,
    //            though it's still somewhat surprising.
    //            -> This explains why Afterwards might do better than findMax. However, at least with avgME,
    //               this doesn't seem to be the case anymore.
    //            -> Also, at any rate, AfterwardsFindMax is a kind of greedy solution: If you do the best cycles first,
    //               maybe you'll miss out on a lot of average cycles which all add up over time?
    public DynamicMatching(Matching matching, int timestepCount, boolean oneSided) throws TooManyTimestepsException, Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
        inputMatching = matching;
        this.oneSided = oneSided;
        if (timestepCount > inputMatching.getHouseholds().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of households.");
        }
        if (!oneSided && timestepCount > inputMatching.getHouses().size()) {
            throw new TooManyTimestepsException("Amount of timesteps exceeds amount of houses.");
        }
        Matching initialMatching = (Matching) deepClone(inputMatching);
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
        WorkerOptimalStableMatchingAlgorithm wosma
                = new WorkerOptimalStableMatchingAlgorithm(initialMatching);
        // TODO: set findMax to true?
        this.initialMatching = wosma.findWorkerOptimalStableMatching(false,false);
        this.currentMatching = (Matching) deepClone(initialMatching);
        this.currentHousesToArrive = (ArrayList<House>) deepClone(initialHousesToArrive);
        this.currentHouseholdsToArrive = (ArrayList<Household>) deepClone(initialHouseholdsToArrive);
        this.initialTimestepsLeft = timestepCount;
        this.currentTimestepsLeft = (Integer) deepClone(timestepCount);
    }


    public Matching advanceTimeAndSolvePerStep(int timestepCount, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException {
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
            runAlgorithm(false, print);
        }
        Matching resultingMatching = (Matching) deepClone(currentMatching);
//        checkIfHouselessHouseholdsHaveNoPreferredEmptyHouse();
        return resultingMatching;
    }

    public Matching advanceTimeFullyThenSolve(int timestepCount, boolean findMax, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseIDAlreadyPresentException, Matching.HouseholdIDAlreadyPresentException {
        for (int i = 0; i < timestepCount; i++) {
            if(print) { System.out.println("Timestep " + i); }
            simulateEnvironmentTimestep();
        }
        runAlgorithm(findMax, print);
        // TODO: Include the following?
        runAlgorithm(false, false);
        Matching resultingMatching = (Matching) deepClone(currentMatching);
        return resultingMatching;
    }

    private void simulateEnvironmentTimestep() throws Matching.HouseholdIDAlreadyPresentException, Matching.HouseIDAlreadyPresentException {
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

    protected void runAlgorithm(boolean findMax, boolean print) throws Matching.HouseholdLinkedToMultipleException, CycleFinder.FullyExploredVertexDiscoveredException, Matching.PreferredNoHouseholdlessHouseException, Matching.HouseLinkedToMultipleException, MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToHouseholdException {
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

    public class TooManyTimestepsException extends Exception {
        public TooManyTimestepsException(String errorMessage) { super(errorMessage); }
    }
}
