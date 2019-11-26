import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import HousingMarket.HousingMarketVertex;

import java.util.HashSet;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Matching {
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchingGraph;
    private HashSet<House> houses = new HashSet<House>();
    private HashSet<Household> households = new HashSet<Household>();

    private HousingMarket housingMarket;

    public Matching(HousingMarket housingMarket) {
        this.matchingGraph = new SimpleGraph<>(DefaultEdge.class);
        this.housingMarket = housingMarket;
    }

    public void addHouse(House house) {
        this.houses.add(house);
        this.matchingGraph.addVertex(house);
    }

    public void addHouses(House... houses) {
        for (House house : houses) {
            this.addHouse(house);
        }
    }

    public void addHousehold(Household household) {
        this.households.add(household);
        this.matchingGraph.addVertex(household);
    }

    public void addHouseholds(Household... households) {
        for (Household household : households) {
            this.addHousehold(household);
        }
    }

    public void removeHouse(House house) {
        this.houses.remove(house);
        this.matchingGraph.removeVertex(house);
    }

    public void removeHousehold(Household household) {
        this.households.remove(household);
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
        }
    }

    public void disconnect(House house, Household household) {
        this.matchingGraph.removeEdge(house, household);
    }

    public HashSet<House> getHouses() {
        return this.houses;
    }

    public HashSet<Household> getHouseholds() {
        return this.households;
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
