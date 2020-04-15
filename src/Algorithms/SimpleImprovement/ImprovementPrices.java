package Algorithms.SimpleImprovement;

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

    public void setInitialPrices() throws AlreadyInitiatedException, ResidualImprovementGraph.MatchGraphNotEmptyException {
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

    public void updatePrices() {
        // This process indeed does not require the new matching M' and instead depends wholly on the old matching.
        DijkstraShortestPath<Integer, DefaultWeightedEdge> dijkstraShortestPath
                = new DijkstraShortestPath<Integer, DefaultWeightedEdge>(this.residualImprovementGraph.getResidualImprovementGraph());
        ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> sourcePaths
                = dijkstraShortestPath.getPaths(this.residualImprovementGraph.getSourceID());
        for (House house : this.improvementGraph.getHouses()) {
            int houseID = house.getID();
            double previousPrice = this.getHousePrice(house);
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(houseID);
            double distInPreviousMatching = shortestPath.getWeight();
            double newPrice = distInPreviousMatching + previousPrice;
            this.setHousePrice(house, newPrice);
        }
        for (DummyHouse dummyHouse : this.improvementGraph.getDummyHouses()) {
            int houseID = dummyHouse.getID();
            double previousPrice = this.getHousePrice(dummyHouse);
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(houseID);
            double distInPreviousMatching = shortestPath.getWeight();
            double newPrice = distInPreviousMatching + previousPrice;
            this.setHousePrice(dummyHouse, newPrice);
        }
        for (Household household : this.improvementGraph.getHouseholds()) {
            int householdID = household.getID();
            double previousPrice = this.getHouseholdPrice(household);
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(householdID);
            double distInPreviousMatching = shortestPath.getWeight();
            double newPrice = distInPreviousMatching + previousPrice;
            this.setHouseholdPrice(household, newPrice);
        }
        for (DummyHousehold dummyHousehold : this.improvementGraph.getDummyHouseholds()) {
            int householdID = dummyHousehold.getID();
            double previousPrice = this.getHouseholdPrice(dummyHousehold);
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(householdID);
            double distInPreviousMatching = shortestPath.getWeight();
            double newPrice = distInPreviousMatching + previousPrice;
            this.setHouseholdPrice(dummyHousehold, newPrice);
        }
    }

    public SimpleGraph<HousingMarketVertex, DefaultEdge> augmentMatchGraphAndUpdateAll(GraphPath<Integer, DefaultWeightedEdge> augmentingPath) throws ResidualImprovementGraph.PathEdgeNotInResidualImprovementGraphException {
        this.updatePrices(); // Doing this first so that the updating process still has access to the un-augmented matchGraph...
        this.matchGraph = this.residualImprovementGraph.augmentMatchingAndUpdateResidualGraph(augmentingPath, this); // ...Because this modifies the matchGraph.
        return this.matchGraph;
    }

    public void setHousePrice(HousingMarketVertex house, double newPrice) {
        this.housePrices.put(house, newPrice);
    }

    public void setHouseholdPrice(HousingMarketVertex household, double newPrice) {
        this.householdPrices.put(household, newPrice);
    }

    public double getHousePrice(HousingMarketVertex house) {
        return this.housePrices.get(house);
    }

    public double getHouseholdPrice(HousingMarketVertex household) {
        return this.householdPrices.get(household);
    }

    public ResidualImprovementGraph getResidualImprovementGraph() {
        return residualImprovementGraph;
    }

    public class AlreadyInitiatedException extends Exception {
        public AlreadyInitiatedException(String errorMessage) {
            super(errorMessage);
        }
    }
}
