package Matching;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import HousingMarket.HousingMarketVertex;
import HousingMarket.HouseAndHouseholdIDPair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

public class Matching implements Serializable {
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchingGraph;
    private int nextID = 0;
    private ArrayList<House> houses = new ArrayList<House>();
    private ArrayList<Household> households = new ArrayList<Household>();
    private ArrayList<Household> householdsWithPriority = new ArrayList<Household>();
    private ArrayList<Household> elderlyHouseholds = new ArrayList<Household>();
    private ArrayList<House> householdlessHouses = new ArrayList<House>();
    private ArrayList<Household> houselessHouseholds = new ArrayList<Household>();
    private int amtSWIChainsExecuted = 0;
    private int amtSWICyclesExecuted = 0;


    private HousingMarket housingMarket;

    public Matching(HousingMarket housingMarket) {
        this.matchingGraph = new SimpleGraph<>(DefaultEdge.class);
        this.housingMarket = housingMarket;
    }

    public void addHouse(House house) {
        int newInt = getAndIncrementID();
        house.setID(newInt);
        this.houses.add(house);
        this.householdlessHouses.add(house);
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
        this.houselessHouseholds.add(household);
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
        this.householdlessHouses.remove(house);
        this.matchingGraph.removeVertex(house);
    }

    public void removeHousehold(int ID) {
        Household household = this.getHousehold(ID);
        this.households.remove(household);
        this.householdsWithPriority.remove(household);
        this.elderlyHouseholds.remove(household);
        this.houselessHouseholds.remove(household);
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
            this.householdlessHouses.remove(house);
            this.houselessHouseholds.remove(household);
        }
    }

    public void disconnect(int houseID, int householdID) {
        House house = this.getHouse(houseID);
        Household household = this.getHousehold(householdID);
        this.matchingGraph.removeEdge(house, household);
        this.householdlessHouses.add(house);
        this.houselessHouseholds.add(household);
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

    public ArrayList<House> getHouseholdlessHouses() {
        return this.householdlessHouses;
    }

    public ArrayList<Household> getHouselessHouseholds() {
        return this.houselessHouseholds;
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
            } else { throw new HouseholdLinkedToHouseholdException("Error: Household " + household.toString() +
                    " is linked to household " + house.toString() + "!");}
        } else if (this.matchingGraph.edgesOf(household).size() > 1) {
            throw new HouseholdLinkedToMultipleException("Error: Household " + household.toString()
                    + " is linked to multiples vertices!");
        }
        else return null;
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
    public void effectuateStrictCycle(List<Integer> cycle, int nilValue) throws HouseholdLinkedToMultipleException, HouseholdLinkedToHouseholdException, HouseholdAlreadyMatchedException, HouseAlreadyMatchedException, MatchingEvaluator.HouseholdIncomeTooHighException, PreferredNoHouseholdlessHouseException {
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
            } else {
                isChain = true;
                housesList.add(null);
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
                ArrayList<House> householdlessHouses = getHouseholdlessHouses();
                float highestScore;
                if (housesList.get(i) == null) {
                    highestScore = 0;
                } else {
                    highestScore = matchingEvaluator.evaluateIndividualTotalFit(housesList.get(i), sourceVertex);
                }
                House bestHouse = null;
                for (House house : householdlessHouses) {
                    float candidateScore = matchingEvaluator.evaluateIndividualTotalFit(house.getID(), sourceVertex);
                    if (candidateScore > highestScore) {
                        highestScore = candidateScore;
                        bestHouse = house;
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
            this.amtSWIChainsExecuted++;
        } else { this.amtSWICyclesExecuted++; }
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

    public int getAmtSWIChainsExecuted() {
        return amtSWIChainsExecuted;
    }

    public int getAmtSWICyclesExecuted() {
        return amtSWICyclesExecuted;
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
