package Matching;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import HousingMarket.HousingMarketVertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Matching implements Serializable {
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchingGraph;
    private AtomicLong nextHouseID = new AtomicLong(0);
    private AtomicLong nextHouseholdID = new AtomicLong(0);
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
        long newLong = nextHouseID.getAndIncrement();
        house.setID(new AtomicLong(newLong));
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
        long newLong = nextHouseholdID.getAndIncrement();
        household.setID(new AtomicLong(newLong));
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

    public void removeHouse(House house) {
        this.houses.remove(house);
        this.householdlessHouses.remove(house);
        this.matchingGraph.removeVertex(house);
    }

    public void removeHousehold(Household household) {
        this.households.remove(household);
        this.householdsWithPriority.remove(household);
        this.elderlyHouseholds.remove(household);
        this.houselessHouseholds.remove(household);
        this.matchingGraph.removeVertex(household);
    }

    public void connect(House house, Household household)
            throws HouseAlreadyMatchedException, HouseholdAlreadyMatchedException {
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

    public void disconnect(House house, Household household) {
        this.matchingGraph.removeEdge(house, household);
        this.householdlessHouses.add(house);
        this.houselessHouseholds.add(household);
    }

    public House getHouse(Integer index) {
        return this.houses.get(index);
    }

    public Household getHousehold(Integer index) {
        return this.households.get(index);
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

    public Household getHouseholdFromHouse(House house)
            throws HouseLinkedToHouseException,HouseLinkedToMultipleException {
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


    public House getHouseFromHousehold(Household household)
            throws HouseholdLinkedToHouseholdException,HouseholdLinkedToMultipleException {
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
