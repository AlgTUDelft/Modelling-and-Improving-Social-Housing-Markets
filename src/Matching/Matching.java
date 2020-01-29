package Matching;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import HousingMarket.HousingMarketVertex;
import HousingMarket.HouseAndHouseholdIDPair;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import static java.util.stream.Collectors.toSet;

public class Matching implements Serializable {
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchingGraph;
    private int nextID = 0;
    private ArrayList<House> houses = new ArrayList<House>();
    private ArrayList<Household> households = new ArrayList<Household>();
    private ArrayList<Household> householdsWithPriority = new ArrayList<Household>();
    private ArrayList<Household> elderlyHouseholds = new ArrayList<Household>();
    private ArrayList<Integer> SWIChainLengths = new ArrayList<Integer>();
    private ArrayList<Integer> SWICycleLengths = new ArrayList<Integer>();
    private Set<Integer> householdsMovedByWOSMA = new HashSet<Integer>();

    private HousingMarket housingMarket;

    public Matching(HousingMarket housingMarket) {
        this.matchingGraph = new SimpleGraph<>(DefaultEdge.class);
        this.housingMarket = housingMarket;
    }

    public void addHouse(House house) {
        int newInt = getAndIncrementID();
        house.setID(newInt);
        this.houses.add(house);
        this.matchingGraph.addVertex(house);
    }

    public void addHouses(House... houses) {
        for (House house : houses) {
            this.addHouse(house);
        }
    }

    public void addHousehold(Household household) {
        int newInt = getAndIncrementID();
        household.setID(newInt);
        this.households.add(household);
        if (household.getPriority()) {
            this.householdsWithPriority.add(household);
        }
        if (household.getAge() >= 65) {
            this.elderlyHouseholds.add(household);
        }
        this.matchingGraph.addVertex(household);
    }

    public void addHouseholds(Household... households) {
        for (Household household : households) {
            this.addHousehold(household);
        }
    }

    public void removeHouse(int ID) {
        House house = this.getHouse(ID);
        this.houses.remove(house);
        this.matchingGraph.removeVertex(house);
    }

    public void removeHousehold(int ID) {
        Household household = this.getHousehold(ID);
        this.households.remove(household);
        this.householdsWithPriority.remove(household);
        this.elderlyHouseholds.remove(household);
        this.matchingGraph.removeVertex(household);
    }


    private int getAndIncrementID() {
        int result = this.nextID;
        this.nextID++;
        return result;
    }

    public void connect(int houseID, int householdID)
            throws HouseAlreadyMatchedException, HouseholdAlreadyMatchedException {
        House house = this.getHouse(houseID);
        Household household = this.getHousehold(householdID);
        if (!this.matchingGraph.edgesOf(house).isEmpty()) {
            throw new HouseAlreadyMatchedException("Error: House " + house.toString() + " is already matched!");
        } else if (!this.matchingGraph.edgesOf(household).isEmpty()) {
            throw new HouseholdAlreadyMatchedException("Error: Household " + household.toString() + " is already matched!");
        } else {
            this.matchingGraph.addEdge(house, household);
        }
    }

    public void disconnect(int houseID, int householdID) {
        House house = this.getHouse(houseID);
        Household household = this.getHousehold(householdID);
        this.matchingGraph.removeEdge(house, household);
    }

    public void dissolveConnections() throws HouseLinkedToMultipleException, HouseLinkedToHouseException {
        for (House house : this.getHouses()) {
            Household household = this.getHouseholdFromHouse(house.getID());
            if (household != null) {
                disconnect(house.getID(), household.getID());
            }
        }
    }

    public int countEdges() {
        return this.matchingGraph.edgeSet().size();
    }

    public House getHouse(int id) {
        Optional<House> result = this.houses.stream()
                .filter(h -> h.getID() == id)
                .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else { return null; }
    }

    public Household getHousehold(int id) {
        Optional<Household> result = this.households.stream()
                .filter(h -> h.getID() == id)
                .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else { return null; }
    }

    public ArrayList<House> getHouses() {
        return this.houses;
    }

    public ArrayList<Household> getHouseholds() {
        return this.households;
    }

    public ArrayList<Household> getHouseholdsWithPriority() {
        return this.householdsWithPriority;
    }

    public ArrayList<Household> getElderlyHouseholds() {
        return this.elderlyHouseholds;
    }

    public Set<Integer> getHouseholdlessHousesIDs() {
        Set<Integer> householdlessHousesIDs = getHouses().stream().filter(h -> {
            try {
                return getHouseholdFromHouse(h.getID()) == null;
            } catch (HouseLinkedToHouseException e) {
                e.printStackTrace();
            } catch (HouseLinkedToMultipleException e) {
                e.printStackTrace();
            }
            return false;
        }).map(v -> v.getID()).collect(toSet());
        return householdlessHousesIDs;
    }

    public Set<Integer> getHouselessHouseholdsIDs() {
        Set<Integer> houselessHouseholdsIDs = getHouseholds().stream().filter(h -> {
            try {
                return getHouseFromHousehold(h.getID()) == null;
            } catch (HouseholdLinkedToMultipleException e) {
                e.printStackTrace();
            } catch (HouseholdLinkedToHouseholdException e) {
                e.printStackTrace();
            }
            return false;
        }).map(v -> v.getID()).collect(toSet());
        return houselessHouseholdsIDs;
    }

    public Household getHouseholdFromHouse(int houseID)
            throws HouseLinkedToHouseException,HouseLinkedToMultipleException {
        House house = this.getHouse(houseID);
        if (this.matchingGraph.edgesOf(house).size() == 1) {
            DefaultEdge edge = this.matchingGraph.edgesOf(house).iterator().next();
            HousingMarketVertex household = this.matchingGraph.getEdgeTarget(edge);
            if (household instanceof Household) {
                return (Household) household;
            } else throw new HouseLinkedToHouseException("Error: House " + house.toString() + " is linked " +
                    "to house " + household.toString() + "!");
        } else if (this.matchingGraph.edgesOf(house).size() > 1) {
            throw new HouseLinkedToMultipleException("Error: House " + house.toString() + " is linked " +
                    "to multiples vertices!");
        }
        else return null;
    }


    public House getHouseFromHousehold(int householdID)
            throws HouseholdLinkedToHouseholdException,HouseholdLinkedToMultipleException {
        Household household = this.getHousehold(householdID);
        if (this.matchingGraph.edgesOf(household).size() == 1) {
            DefaultEdge edge = this.matchingGraph.edgesOf(household).iterator().next();
            HousingMarketVertex house = this.matchingGraph.getEdgeSource(edge);
            if (house instanceof House) {
                return (House) house;
            } else {
                throw new HouseholdLinkedToHouseholdException("Error: Household " + household.toString() +
                        " is linked to household " + house.toString() + "!");
            }
        } else if (this.matchingGraph.edgesOf(household).size() > 1) {
            throw new HouseholdLinkedToMultipleException("Error: Household " + household.toString()
                    + " is linked to multiples vertices!");
        } else return null;
    }

    public boolean hasEdge(int houseID, int householdID) throws HouseLinkedToMultipleException, HouseLinkedToHouseException {
        Household connectedHousehold = this.getHouseholdFromHouse(houseID);
        if (connectedHousehold == null) { return false; }
        else if (connectedHousehold.getID() == householdID) {
            return true;
        }
        else { return false; }
    }

    public boolean isHouseID(int ID) throws IDNotPresentException {
        if (getHouse(ID) != null) {
            return true;
        } else if (getHousehold(ID) != null) {
            return false;
        } else { throw new IDNotPresentException("Requested ID belonged to neither a house nor a household."); }
    }

    // Part of the MinCostPerfectMatchingAlgorithm.
    public void augment(GraphPath<Integer, DefaultWeightedEdge> graphPath) throws IDNotPresentException, HouseLinkedToMultipleException, HouseLinkedToHouseException, HouseholdAlreadyMatchedException, HouseAlreadyMatchedException {
        ArrayList<HouseAndHouseholdIDPair> toConnect = new ArrayList<HouseAndHouseholdIDPair>();
        List<DefaultWeightedEdge> edgeList = graphPath.getEdgeList();

        for (DefaultWeightedEdge edge : edgeList) {
            // The source node, where the first edge in graphPath starts,
            // isn't present in the regular matching. Thus we want to ignore it.
            // Note that graphPath does not contain the final sink node.
            if (edgeList.indexOf(edge) != 0) {
                int sourceID = graphPath.getGraph().getEdgeSource(edge);
                int targetID = graphPath.getGraph().getEdgeTarget(edge);
                if (isHouseID(sourceID)) { // then targetID belongs to a household.
                    if (this.hasEdge(sourceID, targetID)) {
                        this.disconnect(sourceID, targetID);
                    } else {
                        toConnect.add(new HouseAndHouseholdIDPair(sourceID, targetID));
                    }
                } else { // then targetID belongs to a house.
                    if (this.hasEdge(targetID, sourceID)) {
                        this.disconnect(targetID, sourceID);
                    } else {
                        toConnect.add(new HouseAndHouseholdIDPair(targetID, sourceID));
                    }
                }
            }
        }
        for (HouseAndHouseholdIDPair pair : toConnect) {
            this.connect(pair.getHouseID(), pair.getHouseholdID());
        }
    }

    // Part of the EfficientStableMatchingAlgorithm.
    public void executeCycle(List<Integer> cycle, int nilValue, boolean print) throws HouseholdLinkedToMultipleException, HouseholdLinkedToHouseholdException, HouseholdAlreadyMatchedException, HouseAlreadyMatchedException, MatchingEvaluator.HouseholdIncomeTooHighException, PreferredNoHouseholdlessHouseException {
        // TODO: Check if this needs to be changed following my modifications of WOSMA!!
        int edgesCount = cycle.size();

        boolean isChain = false;

        // Disconnect all households from whatever houses they own, and keep a list of these houses.
        ArrayList<Integer> housesList = new ArrayList<Integer>();
        for (int i = 0; i<edgesCount; i++) {
            int householdID = cycle.get(i);
            if (householdID != nilValue) {
                House house = getHouseFromHousehold(householdID);
                if (house != null) {
                    housesList.add(house.getID());
                    disconnect(house.getID(), householdID);
                } else {
                    housesList.add(null);
                }
                householdsMovedByWOSMA.add(householdID);
            } else {
                isChain = true;
                housesList.add(null);
            }
        }
        if(print) {
            if (isChain) {
                System.out.println("Chain has size: " + edgesCount);
            } else { System.out.println("Cycle has size: " + edgesCount);
            }
        }

        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(this);

        for (int i = 0; i<edgesCount; i++) {
            int sourceVertex;
            int targetVertex;
            if (i == edgesCount-1) {
                sourceVertex = cycle.get(i);
                targetVertex = cycle.get(0);
            } else {
                sourceVertex = cycle.get(i);
                targetVertex = cycle.get(i+1);
            }

            if (sourceVertex != nilValue && targetVertex != nilValue) {
                if (i+1 < housesList.size()) {
                    connect(housesList.get(i + 1), sourceVertex);
                } else {
                    connect(housesList.get(0), sourceVertex);
                }
            } else if (sourceVertex == nilValue) {
                continue; // Household was already previously disconnected, so no change.
            } else { // targetVertex == nilValue, so there is an empty house that the household prefers to their own.
                // We now choose to connect him with that house amongst the empty houses, that they prefer most,
                // so long as they do indeed prefer it to their current house.
                // TODO: Is that method of picking a house legit, though?
                Set<Integer> householdlessHouses = getHouseholdlessHousesIDs();
                float highestScore;
                if (housesList.get(i) == null) {
                    highestScore = 0;
                } else {
                    highestScore = matchingEvaluator.evaluateIndividualTotalFit(housesList.get(i), sourceVertex);
                }
                House bestHouse = null;
                for (int houseID : householdlessHouses) {
                    // _housesList_ houses will either go to *another* household in the chain,
                    // or this household didn't want it anyway, since this is not a cycle.
                    if (!housesList.contains(houseID)) {
                        float candidateScore = matchingEvaluator.evaluateIndividualTotalFit(houseID, sourceVertex);
                        if (candidateScore >= highestScore) {
                            highestScore = candidateScore;
                            bestHouse = getHouse(houseID);
                        }
                    }
                }
                if (bestHouse == null) {
                    throw new PreferredNoHouseholdlessHouseException("Cycle indicated that household would prefer some" +
                            " other house to their current house, but no such house was found.");
                } else {
                    connect(bestHouse.getID(), sourceVertex);
                }
            }
        }

        if (isChain) {
            SWIChainLengths.add(edgesCount);
            SWICycleLengths.add(0);
        } else {
            SWIChainLengths.add(0);
            SWICycleLengths.add(edgesCount);
        }
    }

    public boolean isMaximallyMatched() {
        if (this.houses.size() != this.households.size()) {
            System.err.println("|Houses| != |Households|. Therefore matching can never be perfect.");
            return false;
        } else if (this.getMatchingGraph().edgeSet().size() == this.houses.size()) {
            return true;
        } else { return false; }
    }

    public HousingMarket getHousingMarket() {
        return this.housingMarket;
    }

    public SimpleGraph<HousingMarketVertex, DefaultEdge> getMatchingGraph() {
        return this.matchingGraph;
    }

    public void randomlyRewire() throws HouseLinkedToMultipleException, HouseLinkedToHouseException, HouseholdAlreadyMatchedException, HouseAlreadyMatchedException {
        this.dissolveConnections();
        Random rand = new Random();
        for (Household household : this.getHouseholds()) {
            Set<Integer> householdlessHousesIDs = getHouseholdlessHousesIDs();
            if (householdlessHousesIDs.isEmpty()) {
                break;
            }
            ArrayList<Integer> householdlessHousesIDsArray = new ArrayList<Integer>(this.getHouseholdlessHousesIDs().stream().collect(Collectors.toList()));
            int chosenHouseID = rand.nextInt(householdlessHousesIDsArray.size());
            this.connect(chosenHouseID, household.getID());
        }
    }

    public void resetHouseholdsMovedByWOSMA() {
        this.householdsMovedByWOSMA.clear();
    }

    public int getAmtSWIChainsExecuted() {
        return (int) SWIChainLengths.stream()
                .filter(h -> h > 0)
                .count();
    }

    public int getAmtSWICyclesExecuted() {
        return (int) SWICycleLengths.stream()
                .filter(h -> h > 0)
                .count();
    }

    public float getAverageSWIChainLength() {
        double result = SWIChainLengths.stream()
                .filter(h -> h > 0)
                .mapToDouble(a -> a)
                .average()
                .orElse(0.0);
        return (float) result;
    }

    public float getAverageSWICycleLength() {
        double result = SWICycleLengths.stream()
                .filter(h -> h > 0)
                .mapToDouble(a -> a)
                .average()
                .orElse(0.0);
        return (float) result;
    }

    public Set<Integer> getHouseholdsMovedByWOSMA() {
        return householdsMovedByWOSMA;
    }

    public class HouseLinkedToHouseException extends Exception {
        public HouseLinkedToHouseException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class HouseLinkedToMultipleException extends Exception {
        public HouseLinkedToMultipleException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class HouseholdLinkedToHouseholdException extends Exception {
        public HouseholdLinkedToHouseholdException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class HouseholdLinkedToMultipleException extends Exception {
        public HouseholdLinkedToMultipleException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class HouseAlreadyMatchedException extends Exception {
        public HouseAlreadyMatchedException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class HouseholdAlreadyMatchedException extends Exception {
        public HouseholdAlreadyMatchedException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class IDNotPresentException extends Exception {
        public IDNotPresentException(String errorMessage) { super(errorMessage); }
    }

    public class PreferredNoHouseholdlessHouseException extends Exception {
        public PreferredNoHouseholdlessHouseException(String errorMessage) { super(errorMessage); }
    }

}
