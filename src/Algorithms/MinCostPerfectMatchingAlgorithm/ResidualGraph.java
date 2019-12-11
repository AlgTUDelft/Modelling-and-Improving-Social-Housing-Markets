package Algorithms.MinCostPerfectMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.HouseAndHouseholdPair;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResidualGraph {
    private SimpleDirectedWeightedGraph residualGraph;
    private Matching matching;
    private MatchingPrices matchingPrices;
    private ArrayList<Integer> houseIDs = new ArrayList<Integer>();
    private ArrayList<Integer> householdIDs = new ArrayList<Integer>();
    // HouseID and HouseholdID values are always nonnegative, so these values are free.
    private Integer sourceID = -2;
    private Integer sinkID = -1;
    private HashMap<HouseAndHouseholdPair, Float> nonReducedEdgeWeights = new HashMap<HouseAndHouseholdPair, Float>();


    public ResidualGraph(Matching matching, MatchingPrices matchingPrices) throws
            MatchingEvaluator.HouseholdIncomeTooHighException, MatchingNotEmptyException {
        if (matching.countEdges() > 0) {
            throw new MatchingNotEmptyException("Can only call ResidualGraph creation method on empty matching.");
        }
        this.residualGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
        this.matching = matching;
        this.matchingPrices = matchingPrices;
        residualGraph.addVertex(sourceID);
        residualGraph.addVertex(sinkID);
        for (House house : matching.getHouses()) {
            int houseID = house.getID();
            houseIDs.add(houseID);
            residualGraph.addVertex(houseID);
            DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(sourceID, houseID);
            residualGraph.setEdgeWeight(edge, 0);
        }
        for (Household household : matching.getHouseholds()) {
            int householdID = household.getID();
            householdIDs.add(householdID);
            residualGraph.addVertex(householdID);
            DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(householdID, sinkID);
            residualGraph.setEdgeWeight(edge, 0);
        }

        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);

        for (House house : matching.getHouses()) {
            for (Household household : matching.getHouseholds()) {
                int houseID = house.getID();
                int householdID = household.getID();
                DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(houseID, householdID);
                // (1-w) instead of w because we want to maximize sum(w) where w in [0,1].
                // Thus we want to minimize 1-w.
                float nonReducedEdgeWeight = 1 - matchingEvaluator.evaluateIndividualTotalFit(houseID, householdID);
                nonReducedEdgeWeights.put(new HouseAndHouseholdPair(houseID, householdID), nonReducedEdgeWeight);
                float housePrice = matchingPrices.getHousePrice(houseID);
                float householdPrice = matchingPrices.getHouseholdPrice(householdID);
                float reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                residualGraph.setEdgeWeight(edge, reducedEdgeWeight);
            }
        }
    }

    public GraphPath<Integer, DefaultWeightedEdge> findAugmentingPath() throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {
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
            // We only want to check unmatched households, because the path must go directly from the household
            // to the sink node; matched households have no edge to the sink.
            if (this.matching.getHouseFromHousehold(householdID) == null) {
                GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(householdID);
                float weightOfShortestPath = (float) shortestPath.getWeight();
                float priceOfHousehold = this.matchingPrices.getHouseholdPrice(householdID);
                float candidateTotalWeight = weightOfShortestPath + priceOfHousehold;
                if (candidateTotalWeight < minimumWeightFound) {
                    minimumWeightFound = candidateTotalWeight;
                    bestPathFound = shortestPath;
                }
            }
        }
        return bestPathFound;
    }

    public Matching augmentMatchingAndUpdateResidualGraph(GraphPath<Integer, DefaultWeightedEdge> augmentingPath, MatchingPrices matchingPrices) throws Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.IDNotPresentException, Matching.HouseLinkedToHouseException, Matching.HouseLinkedToMultipleException, PathEdgeNotInResidualGraphException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {
        this.matching.augment(augmentingPath);
        this.updateGraphAfterAugmenting(augmentingPath, matchingPrices);
        return this.matching;
    }

    public void updateGraphAfterAugmenting(GraphPath<Integer, DefaultWeightedEdge> augmentingPath, MatchingPrices newMatchingPrices) throws PathEdgeNotInResidualGraphException, Matching.IDNotPresentException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {
        List<DefaultWeightedEdge> edgeList = augmentingPath.getEdgeList();
        Graph<Integer, DefaultWeightedEdge> pathGraph = augmentingPath.getGraph();
        for (DefaultWeightedEdge edge : edgeList) {
            if (edgeList.indexOf(edge) != 0) {
                int source = pathGraph.getEdgeSource(edge);
                int target = pathGraph.getEdgeTarget(edge);
                DefaultWeightedEdge oldEdge = (DefaultWeightedEdge) this.residualGraph.getEdge(source, target);
                // Change direction;
                if (oldEdge != null) {
                    this.residualGraph.removeEdge(source, target);
                    this.residualGraph.addEdge(target, source);
                } else {
                    oldEdge = (DefaultWeightedEdge) this.residualGraph.getEdge(target, source);
                    if (oldEdge != null) {
                        this.residualGraph.removeEdge(target, source);
                        this.residualGraph.addEdge(source, target);
                    } else {
                        throw new PathEdgeNotInResidualGraphException("An edge from the augmenting path could not be found in the residual graph.");
                    }
                }
                // Reverse ReducedEdgeWeight to reflect flipping of direction.
                if (this.matching.isHouseID(source)) {
                    float oldNonReducedWeight = this.nonReducedEdgeWeights.get(new HouseAndHouseholdPair(source, target));
                    this.nonReducedEdgeWeights.put(new HouseAndHouseholdPair(source, target), oldNonReducedWeight * -1);
                } else {
                    float oldNonReducedWeight = this.nonReducedEdgeWeights.get(new HouseAndHouseholdPair(target, source));
                    this.nonReducedEdgeWeights.put(new HouseAndHouseholdPair(source, target), oldNonReducedWeight * -1);
                }
            }
        }

        // Newly unmatched houses and households must get edges to source and sink, respectively.
        // Newly matched houses and households must lose these edges.
        for (int vertex : augmentingPath.getVertexList()) {
            if (vertex != this.getSourceID()) {
                if (this.matching.isHouseID(vertex)) {
                    if (this.matching.getHouseholdFromHouse(vertex) == null) { // unmatched
                        this.residualGraph.addEdge(this.getSourceID(), vertex);
                        this.residualGraph.setEdgeWeight(this.getSourceID(), vertex,0);
                    } else { // matched
                        this.residualGraph.removeEdge(this.getSourceID(), vertex);
                    }
                } else {
                    if (this.matching.getHouseFromHousehold(vertex) == null) { //unmatched
                        this.residualGraph.addEdge(vertex, this.getSinkID());
                        this.residualGraph.setEdgeWeight(vertex, this.getSinkID(),0);
                    } else { // matched
                        this.residualGraph.removeEdge(vertex, this.getSinkID());
                    }
                }
            }
        }

        updateReducedEdgeWeightsAndPrices(newMatchingPrices);
        this.matchingPrices = newMatchingPrices;
    }

    public void updateReducedEdgeWeightsAndPrices(MatchingPrices newMatchingPrices) {
        for (House house : this.matching.getHouses()) {
            for (Household household : matching.getHouseholds()) {
                int houseID = house.getID();
                int householdID = household.getID();
                DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.getEdge(houseID, householdID);
                if (edge != null) {
                    float nextHousePrice = newMatchingPrices.getHousePrice(houseID);
                    float nextHouseholdPrice = newMatchingPrices.getHouseholdPrice(householdID);
                    float nextEdgeWeight = nextHousePrice +
                            nonReducedEdgeWeights.get(new HouseAndHouseholdPair(houseID, householdID)) - nextHouseholdPrice;
                    residualGraph.setEdgeWeight(edge, nextEdgeWeight);
                }
                else {
                    edge = (DefaultWeightedEdge) residualGraph.getEdge(householdID, houseID);
                    if (edge == null) {
                        System.err.println("Residual graph contained no edge between a house and a household.");
                        break;
                    } else {
                        float nextHousePrice = newMatchingPrices.getHousePrice(houseID);
                        float nextHouseholdPrice = newMatchingPrices.getHouseholdPrice(householdID);
                        float nextEdgeWeight = nextHouseholdPrice +
                                nonReducedEdgeWeights.get(new HouseAndHouseholdPair(houseID, householdID)) - nextHousePrice;
                        residualGraph.setEdgeWeight(edge, nextEdgeWeight);
                    }
                }
            }
        }
    }


    public SimpleDirectedWeightedGraph getGraph() {
        return this.residualGraph;
    }

    public Matching getMatching() {
        return this.matching;
    }

    public int getSourceID() {
        return this.sourceID;
    }

    public int getSinkID() {
        return this.sinkID;
    }

    public class PathEdgeNotInResidualGraphException extends Exception {
        public PathEdgeNotInResidualGraphException(String errorMessage) { super(errorMessage); }
    }

    public class MatchingNotEmptyException extends Exception {
        public MatchingNotEmptyException(String errorMessage) { super(errorMessage); }
    }
}
