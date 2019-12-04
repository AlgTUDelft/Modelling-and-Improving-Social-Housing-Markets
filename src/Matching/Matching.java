package Matching;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import HousingMarket.HousingMarketVertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

import org.jgrapht.graph.DefaultEdge;
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

    public void dissolve() throws HouseLinkedToMultipleException, HouseLinkedToHouseException {
        for (House house : this.getHouses()) {
            Household household = this.getHouseholdFromHouse(house.getID());
            if (household != null) {
                disconnect(house.getID(), household.getID());
            }
        }
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
        if (this.getHouseholdFromHouse(houseID).getID() == householdID) {
            return true;
        }
        else { return false; }
    }


        public HousingMarket getHousingMarket() {
        return this.housingMarket;
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

}
