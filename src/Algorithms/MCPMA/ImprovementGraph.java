package Algorithms.MCPMA;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import Main.GradingStrategy;
import Matching.Matching;
import Main.Grader;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Optional;

public class ImprovementGraph {
    private SimpleWeightedGraph<HousingMarketVertex, DefaultWeightedEdge> improvementGraph
            = new SimpleWeightedGraph<HousingMarketVertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    private Matching matching;
    private ArrayList<House> houses; // Does not include dummies
    private ArrayList<Household> households; // Does not include dummies
    private ArrayList<DummyHouse> dummyHouses = new ArrayList<DummyHouse>();
    private ArrayList<DummyHousehold> dummyHouseholds = new ArrayList<DummyHousehold>();
    private int nextDummyID;
    Grader grader;

    // Warning: This algorithm takes only empty houses into account if MCPMAStrategy == Improvement.
    public ImprovementGraph(Matching matching, MCPMAStrategy mcpmaStrategy, GradingStrategy gradingStrategy) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {
        this.matching = matching;
        this.grader = matching.getGrader();
        this.households = new ArrayList<>(matching.getHouseholds().size());

        // Default case |H| == |F|
        switch (mcpmaStrategy) {
            case REGULAR:
                this.houses = new ArrayList<>(matching.getHouses().size());
                for (int i = 0; i < matching.getHouses().size(); i++) {
                    House house = matching.getHouses().get(i);
                    this.houses.add(i, house);
                    improvementGraph.addVertex(house);
                } break;
            case IMPROVEMENT:
                this.houses = new ArrayList<>(matching.getHouseholdlessHousesIDs().size());
                for (int houseID : matching.getHouseholdlessHousesIDs()) {
                    House house = matching.getHouse(houseID);
                    this.houses.add(house);
                    improvementGraph.addVertex(house);
                } break;
        }

        for (int i = 0; i < matching.getHouseholds().size(); i++) {
            Household household = matching.getHouseholds().get(i);
            this.households.add(i,household);
            improvementGraph.addVertex(household);
        }

        nextDummyID = matching.getNextID();

        // Case |H| > |F|
        if (this.houses.size() > this.households.size()) {
            int diff = this.houses.size() - this.households.size();
            for (int i = 0; i < diff; i++) {
                DummyHousehold dummyHousehold = new DummyHousehold(nextDummyID);
                nextDummyID++;
                this.dummyHouseholds.add(dummyHousehold);
                improvementGraph.addVertex(dummyHousehold);
            }
        }
        // Case |H| < |F|
        else if (this.houses.size() < this.households.size()) {
            int diff = this.households.size() - this.houses.size();
            for (int i = 0; i < diff; i++) {
                DummyHouse dummyHouse = new DummyHouse(nextDummyID);
                nextDummyID++;
                this.dummyHouses.add(dummyHouse);
                improvementGraph.addVertex(dummyHouse);
            }
        }

        // Create edges and assign weights for regular houses and households
        for (Household household : this.households){
            House currentHouseholdMatch = matching.getHouseFromHousehold(household.getID());
            float currentHouseholdFit = 0;
            if (currentHouseholdMatch != null) {
                currentHouseholdFit = grader.apply(currentHouseholdMatch.getID(), household.getID(), gradingStrategy);
            }
            for (House house : this.houses) {
                float fitWithHouse = grader.apply(house.getID(), household.getID(), gradingStrategy);
                DefaultWeightedEdge edge = this.improvementGraph.addEdge(house, household);
                switch (mcpmaStrategy) {
                    case REGULAR:
                        // "1.00 - X" because we want to maximize, not minimize;
                        // and difference between fits is no more than 1.00 (in the case of dummies).
                        improvementGraph.setEdgeWeight(edge, 1.00 - fitWithHouse);
                        break;
                    case IMPROVEMENT:
                        if (fitWithHouse > currentHouseholdFit) {
                            // "1.00 - X" because we want to maximize, not minimize;
                            // and difference between fits is no more than 1.00 (in the case of dummies).
                            improvementGraph.setEdgeWeight(edge, 1.00 - (fitWithHouse - currentHouseholdFit));
                        } break;
                }
            }
        }

        // Create edges and assign weights for dummy houses and households
        // For dummy houses...
        for (DummyHouse dummyHouse : this.dummyHouses) {
            for (Household household : this.households) {
                DefaultWeightedEdge edge = this.improvementGraph.addEdge(dummyHouse, household);
                // Weight of 1.00 because this dummy house represents not getting a house
                // and we would prefer real houses being assigned over dummy houses at all times.
                improvementGraph.setEdgeWeight(edge, 1.00);
            }
        }

        // ...and for dummy households. Of course, only either of these will be performed.
        for (DummyHousehold dummyHousehold : this.dummyHouseholds) {
            for (House house : this.houses) {
                DefaultWeightedEdge edge = this.improvementGraph.addEdge(house, dummyHousehold);
                // Weight of 1.00 because this dummy household represents no household
                // and we would prefer houses being assigned to real households whenever possible.
                improvementGraph.setEdgeWeight(edge, 1.00);
            }
        }
    }

    public double getEdgeWeight(HousingMarketVertex house, HousingMarketVertex household) {
        if (this.improvementGraph.containsEdge(house, household)) {
            return this.improvementGraph.getEdgeWeight(this.improvementGraph.getEdge(house, household));
        }
        else { return 1.00; }
    }

    public ArrayList<House> getNonDummyNeighborsOfHousehold(Household household) {
        ArrayList<House> result = new ArrayList<House>();
        for (DefaultWeightedEdge edge : this.improvementGraph.edgesOf(household)) {
            HousingMarketVertex neighbor = (HousingMarketVertex) this.improvementGraph.getEdgeSource(edge);
            // Skip DummyHouses.
            if (neighbor instanceof House) {
                result.add((House) neighbor);
            }
        }
        return result;
    }

    public ArrayList<Household> getNonDummyNeighborsOfHouse(House house) {
        ArrayList<Household> result = new ArrayList<Household>();
        for (DefaultWeightedEdge edge : this.improvementGraph.edgesOf(house)) {
            HousingMarketVertex neighbor = (HousingMarketVertex) this.improvementGraph.getEdgeTarget(edge);
            // Skip DummyHouseholds.
            if (neighbor instanceof Household) {
                result.add((Household) neighbor);
            }
        }
        return result;
    }

    public ArrayList<House> getHouses() {
        return houses;
    }

    public ArrayList<Household> getHouseholds() {
        return households;
    }

    public HousingMarketVertex getHouseFromID(int ID) {
        Optional<House> result = this.houses.stream()
                .filter(h -> h.getID() == ID)
                .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {

            Optional<DummyHouse> dummyResult = this.dummyHouses.stream()
                    .filter(h -> h.getID() == ID)
                    .findFirst();
            return dummyResult.orElse(null);
        }
    }

    public HousingMarketVertex getHouseholdFromID(int ID) {
        Optional<Household> result = this.households.stream()
                .filter(h -> h.getID() == ID)
                .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {

            Optional<DummyHousehold> dummyResult = this.dummyHouseholds.stream()
                    .filter(h -> h.getID() == ID)
                    .findFirst();
            return dummyResult.orElse(null);
        }
    }

    public ArrayList<DummyHouse> getDummyHouses() {
        return dummyHouses;
    }

    public ArrayList<DummyHousehold> getDummyHouseholds() {
        return dummyHouseholds;
    }
}
