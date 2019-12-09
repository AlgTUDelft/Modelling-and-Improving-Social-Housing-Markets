package Algorithms.MinCostPerfectMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;

public class ResidualGraph {
    private SimpleDirectedWeightedGraph residualGraph;
    private ArrayList<Integer> houseIDs = new ArrayList<Integer>();
    private ArrayList<Integer> householdIDs = new ArrayList<Integer>();
    // Strings to differentiate them from normal vertices, which are identified through integers.
    private Integer sourceID = -2;
    private Integer sinkID = -1;
    private Matching matching;
    private MatchingPrices matchingPrices;

    public ResidualGraph(Matching matching, MatchingPrices matchingPrices)
            throws Matching.HouseLinkedToMultipleException,
            Matching.HouseLinkedToHouseException,
            Matching.MatchingEvaluator.HouseholdIncomeTooHighException {
        this.residualGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
        this.matching = matching;
        residualGraph.addVertex(sourceID);
        residualGraph.addVertex(sinkID);
        for (House house : matching.getHouses()) {
            int houseID = house.getID();
            houseIDs.add(houseID);
            residualGraph.addVertex(houseID);
            if (matching.getHouseholdFromHouse(houseID) == null) {
                DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(sourceID, houseID);
                residualGraph.setEdgeWeight(edge, 0);
            }
        }
        for (Household household : matching.getHouseholds()) {
            int householdID = household.getID();
            householdIDs.add(householdID);
            residualGraph.addVertex(householdID);
            if (matching.getHouseholdFromHouse(householdID) == null) {
                DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(householdID, sinkID);
                residualGraph.setEdgeWeight(edge, 0);
            }
        }

        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);

        for (House house : matching.getHouses()) {
            for (Household household : matching.getHouseholds()) {
                int houseID = house.getID();
                int householdID = household.getID();
                if (matching.hasEdge(houseID, householdID)) {
                    DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(householdID, houseID);
                    // (1-w) instead of w because we want to maximize sum(w) where w in [0,1].
                    // Thus we want to minimize 1-w.
                    float nonReducedEdgeWeight = -(1 - matchingEvaluator.evaluateIndividualTotalFit(houseID, householdID));
                    float householdPrice = this.matchingPrices.getHouseholdPrice(householdID);
                    float housePrice = this.matchingPrices.getHousePrice(houseID);
                    float reducedEdgeWeight = householdPrice + nonReducedEdgeWeight - housePrice;
                    residualGraph.setEdgeWeight(edge, reducedEdgeWeight);
                } else {
                    DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(houseID, householdID);
                    // (1-w) instead of w because we want to maximize sum(w) where w in [0,1].
                    // Thus we want to minimize 1-w.
                    float nonReducedEdgeWeight = 1 - matchingEvaluator.evaluateIndividualTotalFit(houseID, householdID);
                    float housePrice = this.matchingPrices.getHousePrice(houseID);
                    float householdPrice = this.matchingPrices.getHouseholdPrice(householdID);
                    float reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                    residualGraph.setEdgeWeight(edge, reducedEdgeWeight);
                }
            }
        }
    }

    public void updateReducedEdgeWeightsAndPrices(MatchingPrices matchingPrices) {
        for (House house : this.matching.getHouses()) {
            for (Household household : matching.getHouseholds()) {
                int houseID = house.getID();
                int householdID = household.getID();
                DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.getEdge(houseID, householdID);
                if (edge != null) {
                    float previousHousePrice = this.matchingPrices.getHousePrice(houseID);
                    float nextHousePrice = matchingPrices.getHousePrice(houseID);
                    float previousHouseholdPrice = this.matchingPrices.getHouseholdPrice(householdID);
                    float nextHouseholdPrice = matchingPrices.getHouseholdPrice(householdID);
                    float previousEdgeWeight = (float) residualGraph.getEdgeWeight(edge);
                    float nextEdgeWeight = previousEdgeWeight - previousHousePrice + nextHousePrice
                            + previousHouseholdPrice - nextHouseholdPrice;
                    residualGraph.setEdgeWeight(edge, nextEdgeWeight);
                }
                else {
                    edge = (DefaultWeightedEdge) residualGraph.getEdge(householdID, houseID);
                    if (edge == null) {
                        System.err.println("Residual graph contained null edge between a house and a household.");
                        break;
                    } else {
                        float previousHousePrice = this.matchingPrices.getHousePrice(houseID);
                        float nextHousePrice = matchingPrices.getHousePrice(houseID);
                        float previousHouseholdPrice = this.matchingPrices.getHouseholdPrice(householdID);
                        float nextHouseholdPrice = matchingPrices.getHouseholdPrice(householdID);
                        float previousEdgeWeight = (float) residualGraph.getEdgeWeight(edge);
                        float nextEdgeWeight = previousEdgeWeight - previousHouseholdPrice + nextHouseholdPrice
                                + previousHousePrice - nextHousePrice;
                        residualGraph.setEdgeWeight(edge, nextEdgeWeight);
                    }
                }
            }
        }

        this.matchingPrices = matchingPrices;
    }

    public GraphPath<Integer, DefaultWeightedEdge> findAugmentingPath() {
        // TODO: Find a way to replace Dijkstra here with saved information from its call in this.matchingPrices.
        DijkstraShortestPath<Integer, DefaultWeightedEdge> dijkstraShortestPath
                = new DijkstraShortestPath<Integer, DefaultWeightedEdge>(this.getGraph());
        ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> sourcePaths
                = dijkstraShortestPath.getPaths(this.getSourceID());

        // Since the maximum weight of an edge is 1, and between houses and households an augmenting path can
        // have a weight no more than |H| + |HH| - 1 (if it zigzags across all possible nodes), all augmenting paths'
        // weights will be lower than this.
        float minimumWeightFound = householdIDs.size() + houseIDs.size();
        GraphPath<Integer, DefaultWeightedEdge> bestPathFound = null;
        for (int householdID : this.householdIDs) {
            GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(householdID);
            float weightOfShortestPath = (float) shortestPath.getWeight();
            float priceOfHousehold = this.matchingPrices.getHouseholdPrice(householdID);
            float candidateTotalWeight = weightOfShortestPath + priceOfHousehold;
            if (candidateTotalWeight < minimumWeightFound) {
                minimumWeightFound = candidateTotalWeight;
                bestPathFound = shortestPath;
            }
        }
        return bestPathFound;
    }

    public void updateGraphAfterAugmenting() {
        // TODO: finish.
    }

    public SimpleDirectedWeightedGraph getGraph() {
        return this.residualGraph;
    }

    public int getSourceID() {
        return this.sourceID;
    }

    public int getSinkID() {
        return this.sinkID;
    }
}
