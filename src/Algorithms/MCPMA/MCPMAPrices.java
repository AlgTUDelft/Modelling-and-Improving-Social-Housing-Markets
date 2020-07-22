package Algorithms.MCPMA;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;

public class MCPMAPrices {

    private ImprovementGraph improvementGraph;
    private MatchGraph matchGraph;
    private HashMap<HousingMarketVertex, Double> housePrices = new HashMap<HousingMarketVertex, Double>();
    private HashMap<HousingMarketVertex, Double> householdPrices = new HashMap<HousingMarketVertex, Double>();
    private ResidualGraph residualGraph;
    private MCPMAStrategy mcpmaStrategy;

    public MCPMAPrices(ImprovementGraph improvementGraph, MatchGraph matchGraph, MCPMAStrategy mcpmaStrategy) {
        this.improvementGraph = improvementGraph;
        this.matchGraph = matchGraph;
        this.mcpmaStrategy = mcpmaStrategy;
    }

    public void setInitialPrices() throws AlreadyInitiatedException, ResidualGraph.MatchGraphNotEmptyException {
        if (matchGraph.getEdgeCount() > 0) {
            throw new AlreadyInitiatedException("Error: Initial prices have already been created.");
        } else {
            for (House house : this.improvementGraph.getHouses()) {
                housePrices.put(house, 0.0);
            }
            for (DummyHouse dummyHouse : this.improvementGraph.getDummyHouses()) {
                housePrices.put(dummyHouse, 0.0);
            }

            for (Household household : this.improvementGraph.getHouseholds()) {
                double minScore = 1.00;
                for (House neighbor : this.improvementGraph.getNonDummyNeighborsOfHousehold(household)) {
                    if (this.improvementGraph.getEdgeWeight(neighbor, household) < minScore) {
                        minScore = this.improvementGraph.getEdgeWeight(neighbor, household);
                    }
                }
                householdPrices.put(household, minScore);
            }

            for (DummyHousehold dummyHousehold : this.improvementGraph.getDummyHouseholds()) {
                // For dummy households, all incident edges have weight of 1.00.
                householdPrices.put(dummyHousehold, 1.00);
            }
        }
        this.residualGraph = new ResidualGraph(this.improvementGraph, this.matchGraph, this);
    }

    private void updatePrices() {
        // This process indeed does not require the new matching M' and instead depends wholly on the old matching.
        DijkstraShortestPath<Integer, DefaultWeightedEdge> dijkstraShortestPath
                = new DijkstraShortestPath<Integer, DefaultWeightedEdge>(this.residualGraph.getResidualImprovementGraph());
        ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> sourcePaths
                = dijkstraShortestPath.getPaths(this.residualGraph.getSourceID());
        for (House house : this.improvementGraph.getHouses()) {
            int houseID = house.getID();
            double previousPrice = this.getHousePrice(house);
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(houseID);
            if (shortestPath != null) {
                double distInPreviousMatching = shortestPath.getWeight();
                double newPrice = distInPreviousMatching + previousPrice;
                this.setHousePrice(house, newPrice);
            }
        }
        for (DummyHouse dummyHouse : this.improvementGraph.getDummyHouses()) {
            int houseID = dummyHouse.getID();
            double previousPrice = this.getHousePrice(dummyHouse);
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(houseID);
            if (shortestPath != null) {
                double distInPreviousMatching = shortestPath.getWeight();
                double newPrice = distInPreviousMatching + previousPrice;
                this.setHousePrice(dummyHouse, newPrice);
            }
        }
        for (Household household : this.improvementGraph.getHouseholds()) {
            int householdID = household.getID();
            double previousPrice = this.getHouseholdPrice(household);
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(householdID);
            if (shortestPath != null) {
                double distInPreviousMatching = shortestPath.getWeight();
                double newPrice = distInPreviousMatching + previousPrice;
                this.setHouseholdPrice(household, newPrice);
            }
        }
        for (DummyHousehold dummyHousehold : this.improvementGraph.getDummyHouseholds()) {
            int householdID = dummyHousehold.getID();
            double previousPrice = this.getHouseholdPrice(dummyHousehold);
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(householdID);
            if (shortestPath != null) {
                double distInPreviousMatching = shortestPath.getWeight();
                double newPrice = distInPreviousMatching + previousPrice;
                this.setHouseholdPrice(dummyHousehold, newPrice);
            }
        }
    }

    public MatchGraph augmentMatchGraphAndUpdateAll(GraphPath<Integer, DefaultWeightedEdge> augmentingPath) throws ResidualGraph.PathEdgeNotInResidualGraphException {
        this.updatePrices(); // Doing this first so that the updating process still has access to the un-augmented matchGraph...
        this.matchGraph = this.residualGraph.augmentMatchingAndUpdateResidualGraph(augmentingPath, this); // ...Because this modifies the matchGraph.
        return this.matchGraph;
    }

    private void setHousePrice(HousingMarketVertex house, double newPrice) {
        this.housePrices.put(house, newPrice);
    }

    private void setHouseholdPrice(HousingMarketVertex household, double newPrice) {
        this.householdPrices.put(household, newPrice);
    }

    public double getHousePrice(HousingMarketVertex house) {
        return this.housePrices.get(house);
    }

    public double getHouseholdPrice(HousingMarketVertex household) {
        return this.householdPrices.get(household);
    }

    public ResidualGraph getResidualGraph() {
        return residualGraph;
    }

    public MCPMAStrategy getMcpmaStrategy() {
        return mcpmaStrategy;
    }

    public class AlreadyInitiatedException extends Exception {
        public AlreadyInitiatedException(String errorMessage) {
            super(errorMessage);
        }
    }
}
