package Algorithms.SimpleImprovement;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import Matching.Matching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import Matching.MatchingEvaluator;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;

public class ImprovementGraph {
    private SimpleWeightedGraph<HousingMarketVertex, DefaultWeightedEdge> improvementGraph
            = new SimpleWeightedGraph<HousingMarketVertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    private Matching matching;
    private MatchingEvaluator matchingEvaluator;
    private ArrayList<House> houses = new ArrayList<>(); // May include dummies
    private ArrayList<Household> households = new ArrayList<>(); // May include dummies
    private ArrayList<DummyHouse> dummyHouses = new ArrayList<DummyHouse>();
    private ArrayList<DummyHousehold> dummyHouseholds = new ArrayList<DummyHousehold>();
    private int nextDummyID = -1;

    public ImprovementGraph(Matching matching) throws Matching.Matching.HouseholdLinkedToMultipleException, Matching.Matching.HouseholdLinkedToHouseholdException, Matching.MatchingEvaluator.HouseholdIncomeTooHighException {
        this.matching = matching;
        this.matchingEvaluator = new MatchingEvaluator(matching);

        // Default case |H| == |F|
        for (House house : matching.getHouses()) {
            this.houses.add(house);
            improvementGraph.addVertex(house);
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
            float currentHouseholdFit = 0;
            if (currentHouseholdMatch != null) {
                matchingEvaluator.evaluateIndividualTotalFit(currentHouseholdMatch.getID(), household.getID());
            }
            for (House house : this.houses) {
                float fitWithHouse = matchingEvaluator.evaluateIndividualTotalFit(house.getID(), household.getID());
                if (fitWithHouse > currentHouseholdFit) {
                    DefaultWeightedEdge edge = this.improvementGraph.addEdge(house, household);
                    // "1 - X" because we want to maximize, not minimize; and difference between fits is no more than 1.
                    improvementGraph.setEdgeWeight(edge, 1- (fitWithHouse - currentHouseholdFit));
                }
            }
        }

        // Create edges and assign weights for dummy houses and households
        // For dummy houses...
        for (DummyHouse dummyHouse : this.dummyHouses) {
            for (Household household : this.households) {
                DefaultWeightedEdge edge = this.improvementGraph.addEdge(dummyHouse, household);
                // Weight of 1 because this dummy house represents not getting a house.
                improvementGraph.setEdgeWeight(edge, 1);
            }
        }

        // ...and for dummy households. Of course, only either of these will be performed.
        for (DummyHousehold dummyHousehold : this.dummyHouseholds) {
            for (House house : this.houses) {
                DefaultWeightedEdge edge = this.improvementGraph.addEdge(house, dummyHousehold);
                // Weight of 1 because this dummy household represents no household.
                improvementGraph.setEdgeWeight(edge, 1);
            }
        }
    }

    public double getEdgeWeight(HousingMarketVertex house, HousingMarketVertex household) {
        if (this.improvementGraph.containsEdge(house, household)) {
            return this.improvementGraph.getEdgeWeight(this.improvementGraph.getEdge(house, household));
        }
        else { return 1; }
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

    public ArrayList<DummyHouse> getDummyHouses() {
        return dummyHouses;
    }

    public ArrayList<DummyHousehold> getDummyHouseholds() {
        return dummyHouseholds;
    }
}
