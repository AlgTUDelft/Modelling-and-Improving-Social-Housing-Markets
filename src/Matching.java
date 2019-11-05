import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarket;
import HousingMarket.HousingMarketVertex;

import java.util.ArrayList;
import java.util.HashSet;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class Matching {
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchingGraph;
    private HashSet<House> houses;
    private HashSet<Household> households;

    private HousingMarket housingMarket;

    public Matching(HousingMarket housingMarket) {
        this.matchingGraph = new SimpleGraph<>(DefaultEdge.class);
        this.housingMarket = housingMarket;
    }

    public void addHouse(House house) {
        this.houses.add(house);
        this.matchingGraph.addVertex(house);
    }

    public void addHousehold(Household household) {
        this.households.add(household);
        this.matchingGraph.addVertex(household);
    }

    public void removeHouse(House house) {
        this.houses.remove(house);
        this.matchingGraph.removeVertex(house);
    }

    public void removeHousehold(Household household) {
        this.households.remove(household);
        this.matchingGraph.removeVertex(household);
    }

    public void connect(House house, Household household) {
        // TODO: Only if neither currently are linked to anything else.
        this.matchingGraph.addEdge(house, household);
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
            } else throw new HouseLinkedToHouseException("House " + house.toString() + " is linked " +
                    "to house " + household.toString() + "!");
        } else if (this.matchingGraph.edgesOf(house).size() > 1) {
            throw new HouseLinkedToMultipleException("House " + house.toString() + " is linked " +
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
            } else throw new HouseholdLinkedToHouseholdException("Household " + household.toString() +
                    " is linked to household " + house.toString() + "!");
        } else if (this.matchingGraph.edgesOf(household).size() > 1) {
            throw new HouseholdLinkedToMultipleException("Household " + household.toString()
                    + " is linked to multiples vertices!");
        }
        else return null;
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



    // TODO:
    //  * Figure out what kinds of results this should show
    //    (or should everything go into the evaluator class?)
    //  * Show aggregated results of matching.


}
