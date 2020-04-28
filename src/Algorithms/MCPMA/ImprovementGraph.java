package Algorithms.MCPMA;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import Matching.Matching;
import org.jgrapht.graph.DefaultWeightedEdge;
import Matching.MatchingEvaluator;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Optional;

public class ImprovementGraph {
    private SimpleWeightedGraph<HousingMarketVertex, DefaultWeightedEdge> improvementGraph
            = new SimpleWeightedGraph<HousingMarketVertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    private Matching matching;
    private MatchingEvaluator matchingEvaluator;
    private ArrayList<House> houses = new ArrayList<>(); // Does not include dummies
    private ArrayList<Household> households = new ArrayList<>(); // Does not include dummies
    private ArrayList<DummyHouse> dummyHouses = new ArrayList<DummyHouse>();
    private ArrayList<DummyHousehold> dummyHouseholds = new ArrayList<DummyHousehold>();
    private int nextDummyID;

    // Warning: This algorithm takes only empty houses into account if MCPMAStrategy == Improvement.
    public ImprovementGraph(Matching matching, MCPMAStrategy mcpmaStrategy) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MatchingEvaluator.HouseholdIncomeTooHighException {
        this.matching = matching;
        this.matchingEvaluator = new MatchingEvaluator(matching);

        // Default case |H| == |F|
        switch (mcpmaStrategy) {
            case REGULAR:
                for (House house : matching.getHouses()) {
                    this.houses.add(house);
                    improvementGraph.addVertex(house);
                } break;
            case IMPROVEMENT:
                for (int houseID : matching.getHouseholdlessHousesIDs()) {
                    House house = matching.getHouse(houseID);
                    this.houses.add(house);
                    improvementGraph.addVertex(house);
                } break;
        }

        for (Household household : matching.getHouseholds()) {
            this.households.add(household);
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
            // TODO: change floats to doubles
            float currentHouseholdFit = 0;
            if (currentHouseholdMatch != null) {
                currentHouseholdFit = matchingEvaluator.evaluateIndividualTotalFit(currentHouseholdMatch.getID(), household.getID());
            }
            for (House house : this.houses) {
                float fitWithHouse = matchingEvaluator.evaluateIndividualTotalFit(house.getID(), household.getID());
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
            if (dummyResult.isPresent()) {
                return dummyResult.get();
            } else { return null; }
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
            if (dummyResult.isPresent()) {
                return dummyResult.get();
            } else { return null; }
        }
    }

    public ArrayList<DummyHouse> getDummyHouses() {
        return dummyHouses;
    }

    public ArrayList<DummyHousehold> getDummyHouseholds() {
        return dummyHouseholds;
    }
}
