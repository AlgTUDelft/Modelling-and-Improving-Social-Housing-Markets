package Algorithms.SimpleImprovement;

import Algorithms.MinCostPerfectMatchingAlgorithm.MatchingPrices;
import HousingMarket.House.House;
import HousingMarket.HouseAndHouseholdIDPair;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import Matching.Matching;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;

public class ResidualImprovementGraph {
    // HouseID and HouseholdID values are always non-negative, so these values are free for us to use.
    private Integer sourceID = -2;
    private Integer sinkID = -1;
    private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> residualImprovementGraph;
    private ImprovementGraph improvementGraph;
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchGraph;
    private ImprovementPrices improvementPrices;
    // OK, hopefully this will go right.
    private HashMap<HouseAndHouseholdIDPair, Double> nonReducedEdgeWeights = new HashMap<HouseAndHouseholdIDPair, Double>();

    public ResidualImprovementGraph(ImprovementGraph improvementGraph, SimpleGraph<HousingMarketVertex, DefaultEdge> matchGraph, ImprovementPrices improvementPrices) throws MatchGraphNotEmptyException {
        if (matchGraph.edgeSet().size() > 0) {
            throw new MatchGraphNotEmptyException("Error: Can only create Residual Improvement Graph on empty Match Graph.");
        }
        this.residualImprovementGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
        this.improvementGraph = improvementGraph;
        this.matchGraph = matchGraph;
        this.improvementPrices = improvementPrices;
        residualImprovementGraph.addVertex(sourceID);
        residualImprovementGraph.addVertex(sinkID);
        for (House house : improvementGraph.getHouses()) {
            this.residualImprovementGraph.addVertex(house.getID());
            DefaultWeightedEdge edge = this.residualImprovementGraph.addEdge(sourceID, house.getID());
            this.residualImprovementGraph.setEdgeWeight(edge, 0);
        }
        for (DummyHouse dummyHouse : improvementGraph.getDummyHouses()) {
            this.residualImprovementGraph.addVertex(dummyHouse.getID());
            DefaultWeightedEdge edge = this.residualImprovementGraph.addEdge(sourceID, dummyHouse.getID());
            this.residualImprovementGraph.setEdgeWeight(edge, 0);
        }
        for (Household household : improvementGraph.getHouseholds()) {
            this.residualImprovementGraph.addVertex(household.getID());
            DefaultWeightedEdge edge = this.residualImprovementGraph.addEdge(household.getID(), sinkID);
            this.residualImprovementGraph.setEdgeWeight(edge, 0);
        }
        for (DummyHousehold dummyHousehold : improvementGraph.getDummyHouseholds()) {
            this.residualImprovementGraph.addVertex(dummyHousehold.getID());
            DefaultWeightedEdge edge = this.residualImprovementGraph.addEdge(dummyHousehold.getID(), sinkID);
            this.residualImprovementGraph.setEdgeWeight(edge, 0);
        }

        for (House house : improvementGraph.getHouses()) {
            for (Household household : improvementGraph.getNonDummyNeighborsOfHouse(house)) {
                int houseID = house.getID();
                int householdID = household.getID();
                DefaultWeightedEdge edge = residualImprovementGraph.addEdge(houseID, householdID);
                double nonReducedEdgeWeight = improvementGraph.getEdgeWeight(house, household);
                nonReducedEdgeWeights.put(new HouseAndHouseholdIDPair(houseID, householdID), nonReducedEdgeWeight);
                double housePrice = improvementPrices.getHousePrice(house);
                double householdPrice = improvementPrices.getHouseholdPrice(household);
                double reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                residualImprovementGraph.setEdgeWeight(edge, reducedEdgeWeight);
            }
            for (DummyHousehold dummyHousehold : improvementGraph.getDummyHouseholds()) {
                int houseID = house.getID();
                int dummyHouseholdID = dummyHousehold.getID();
                DefaultWeightedEdge edge = residualImprovementGraph.addEdge(houseID, dummyHouseholdID);
                // nonReducedEdgeWeight is 1 for dummies.
                double nonReducedEdgeWeight = 1.0;
                nonReducedEdgeWeights.put(new HouseAndHouseholdIDPair(houseID, dummyHouseholdID), nonReducedEdgeWeight);
                double housePrice = improvementPrices.getHousePrice(house);
                double householdPrice = improvementPrices.getHouseholdPrice(dummyHousehold);
                double reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                residualImprovementGraph.setEdgeWeight(edge, reducedEdgeWeight);
            }
        }
        for (DummyHouse dummyHouse : improvementGraph.getDummyHouses()) {
            for (Household household : improvementGraph.getHouseholds()) {
                int dummyHouseID = dummyHouse.getID();
                int householdID = household.getID();
                DefaultWeightedEdge edge = residualImprovementGraph.addEdge(dummyHouseID, householdID);
                // nonReducedEdgeWeight is 1 for dummies.
                double nonReducedEdgeWeight = 1.0;
                nonReducedEdgeWeights.put(new HouseAndHouseholdIDPair(dummyHouseID, householdID), nonReducedEdgeWeight);
                double housePrice = improvementPrices.getHousePrice(dummyHouse);
                double householdPrice = improvementPrices.getHouseholdPrice(household);
                double reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                residualImprovementGraph.setEdgeWeight(edge, reducedEdgeWeight);
            }
            // If there are dummy houses, then there are no dummy households. Hence we ignore those.
        }
    }

    public GraphPath<Integer, DefaultWeightedEdge> findAugmentingPath() {
        DijkstraShortestPath<Integer, DefaultWeightedEdge> dijkstraShortestPath;
        dijkstraShortestPath = new DijkstraShortestPath<Integer, DefaultWeightedEdge>(this.residualImprovementGraph);
        ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> sourcePaths;
        sourcePaths = dijkstraShortestPath.getPaths(sourceID);
        // Since the maximum weight of an edge is 1, and between houses and households an augmenting path can
        // have a weight no more than |H| + |HH| - 1 (if it zigzags across all possible nodes), all augmenting paths'
        // weights will be lower than this.
        double minimumWeightFound = this.improvementGraph.getHouseholds().size()
                + this.improvementGraph.getDummyHouseholds().size()
                + this.improvementGraph.getHouses().size()
                + this.improvementGraph.getDummyHouses().size();
        GraphPath<Integer, DefaultWeightedEdge> bestPathFound = null;
        for (Household household : improvementGraph.getHouseholds()) {
            // We only want to check unmatched households, because the path must go directly from the household
            // to the sink node; matched households have no edge to the sink.
            if (matchGraph.edgesOf(household).isEmpty()) {
                GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(household.getID());
                double weightOfShortestPath = shortestPath.getWeight();
                double priceOfHousehold = this.improvementPrices.getHouseholdPrice(household);
                double candidateTotalWeight = weightOfShortestPath + priceOfHousehold;
                if (candidateTotalWeight < minimumWeightFound) {
                    minimumWeightFound = candidateTotalWeight;
                    bestPathFound = shortestPath;
                }
            }
        }
        for (DummyHousehold dummyHousehold : improvementGraph.getDummyHouseholds()) {
            // We only want to check unmatched households, because the path must go directly from the household
            // to the sink node; matched households have no edge to the sink.
            if (matchGraph.edgesOf(dummyHousehold).isEmpty()) {
                GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(dummyHousehold.getID());
                double weightOfShortestPath = shortestPath.getWeight();
                double priceOfHousehold = this.improvementPrices.getHouseholdPrice(dummyHousehold);
                double candidateTotalWeight = weightOfShortestPath + priceOfHousehold;
                if (candidateTotalWeight < minimumWeightFound) {
                    minimumWeightFound = candidateTotalWeight;
                    bestPathFound = shortestPath;
                }
            }
        }
        return bestPathFound;
    }

    public SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> getResidualImprovementGraph() {
        return residualImprovementGraph;
    }

    public Integer getSourceID() {
        return sourceID;
    }

    public class MatchGraphNotEmptyException extends Exception {
        public MatchGraphNotEmptyException(String errorMessage) {
            super(errorMessage);
        }
    }
}
