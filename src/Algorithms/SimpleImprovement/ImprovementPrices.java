package Algorithms.SimpleImprovement;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;

public class ImprovementPrices {

    private ImprovementGraph improvementGraph;
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchGraph;
    private HashMap<HousingMarketVertex, Double> housePrices = new HashMap<HousingMarketVertex, Double>();
    private HashMap<HousingMarketVertex, Double> householdPrices = new HashMap<HousingMarketVertex, Double>();
    private ResidualImprovementGraph residualImprovementGraph;

    public ImprovementPrices(ImprovementGraph improvementGraph, SimpleGraph<HousingMarketVertex, DefaultEdge> matchGraph) {
        this.improvementGraph = improvementGraph;
        this.matchGraph = matchGraph;
    }

    public void setInitialPrices() throws AlreadyInitiatedException {
        if (matchGraph.edgeSet().size() > 0) {
            throw new AlreadyInitiatedException("Error: Initial prices have already been created.");
        } else {
            for (House house : this.improvementGraph.getHouses()) {
                housePrices.put(house, 0.0);
            }
            for (DummyHouse dummyHouse : this.improvementGraph.getDummyHouses()) {
                housePrices.put(dummyHouse, 0.0);
            }

            for (Household household : this.improvementGraph.getHouseholds()) {
                double minScore = 1;
                for (House neighbor : this.improvementGraph.getNonDummyNeighborsOfHousehold(household)) {
                    if (this.improvementGraph.getEdgeWeight(neighbor, household) < minScore) {
                        minScore = this.improvementGraph.getEdgeWeight(neighbor, household);
                    }
                }
                householdPrices.put(household, minScore);
            }

            for (DummyHousehold dummyHousehold : this.improvementGraph.getDummyHouseholds()) {
                // For dummy households, all incident edges have weight of 1.0.
                householdPrices.put(dummyHousehold, 1.0);
            }
        }
        this.residualImprovementGraph = new ResidualImprovementGraph(this.improvementGraph, this.matchGraph, this);
    }

    public class AlreadyInitiatedException extends Exception {
        public AlreadyInitiatedException(String errorMessage) {
            super(errorMessage);
        }
    }
}
