package Algorithms.MinCostPerfectMatchingAlgorithm;

import HousingMarket.House.House;
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
import java.util.List;

public class ResidualGraph {
    private SimpleDirectedWeightedGraph residualGraph;
    private Matching matching;
    private MatchingPrices matchingPrices;
    private ArrayList<Integer> houseIDs = new ArrayList<Integer>();
    private ArrayList<Integer> householdIDs = new ArrayList<Integer>();
    // Strings to differentiate them from normal vertices, which are identified through integers.
    private Integer sourceID = -2;
    private Integer sinkID = -1;

    // TODO: Change to reflect the fact that residualgraph is called on a dissolved matching rather than on any incomplete ones?
    public ResidualGraph(Matching matching, MatchingPrices matchingPrices)
            throws Matching.HouseLinkedToMultipleException,
            Matching.HouseLinkedToHouseException,
            MatchingEvaluator.HouseholdIncomeTooHighException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException {
        this.residualGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
        this.matching = matching;
        this.matchingPrices = matchingPrices;
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
            if (matching.getHouseFromHousehold(householdID) == null) {
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
                    float householdPrice = matchingPrices.getHouseholdPrice(householdID);
                    float housePrice = matchingPrices.getHousePrice(houseID);
                    float reducedEdgeWeight = householdPrice + nonReducedEdgeWeight - housePrice;
                    residualGraph.setEdgeWeight(edge, reducedEdgeWeight);
                } else {
                    DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(houseID, householdID);
                    // (1-w) instead of w because we want to maximize sum(w) where w in [0,1].
                    // Thus we want to minimize 1-w.
                    float nonReducedEdgeWeight = 1 - matchingEvaluator.evaluateIndividualTotalFit(houseID, householdID);
                    float housePrice = matchingPrices.getHousePrice(houseID);
                    float householdPrice = matchingPrices.getHouseholdPrice(householdID);
                    float reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                    residualGraph.setEdgeWeight(edge, reducedEdgeWeight);
                }
            }
        }
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
            // TODO: Indeed including the priceOfHousehold?
            float candidateTotalWeight = weightOfShortestPath + priceOfHousehold;
            if (candidateTotalWeight < minimumWeightFound) {
                minimumWeightFound = candidateTotalWeight;
                bestPathFound = shortestPath;
            }
        }
        return bestPathFound;
    }

    public Matching augmentMatchingAndUpdateResidualGraph(GraphPath<Integer, DefaultWeightedEdge> augmentingPath, MatchingPrices matchingPrices) throws Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, Matching.IDNotPresentException, Matching.HouseLinkedToHouseException, Matching.HouseLinkedToMultipleException, PathEdgeNotInResidualGraphException {
        this.matching.augment(augmentingPath);
        this.updateGraphAfterAugmenting(augmentingPath, matchingPrices);
        return this.matching;
    }

    // Doesn't update reduced edge weights or _this.matchingPrices_.
    public void updateGraphAfterAugmenting(GraphPath<Integer, DefaultWeightedEdge> augmentingPath, MatchingPrices newMatchingPrices) throws PathEdgeNotInResidualGraphException {
        List<DefaultWeightedEdge> edgeList = augmentingPath.getEdgeList();
        Graph<Integer, DefaultWeightedEdge> pathGraph = augmentingPath.getGraph();
        for (DefaultWeightedEdge edge : edgeList) {
            if (edgeList.indexOf(edge) != 0) {
                int source = pathGraph.getEdgeSource(edge);
                int target = pathGraph.getEdgeTarget(edge);
                DefaultWeightedEdge oldEdge = (DefaultWeightedEdge) this.residualGraph.getEdge(source, target);

                // Only the old non-reduced edge weight should be multiplied by -1 if the edge direction is changed.
                // What, then, does the new reduced edge weight become? Surprisingly, it is equal to the
                // old reduced edge weight, multiplied by -1.
                //
                // Explanation of calculation:
                // oldReducedEdgeWeight = p(source) + nonReducedEdgeWeight - p(target)
                // newReducedEdgeWeight = p(target) + (-nonReducedEdgeWeight) + p(source)
                // -nonReducedEdgeWeight = - (oldReducedEdgeWeight - p(source) + p(target))
                // -> newReducedEdgeWeight = - oldReducedEdgeWeight

                if (oldEdge != null) {
                    float oldReducedEdgeWeight = (float) this.residualGraph.getEdgeWeight(oldEdge);
                    float newReducedEdgeWeight = -oldReducedEdgeWeight;
                    this.residualGraph.removeEdge(source, target);
                    DefaultWeightedEdge newEdge = (DefaultWeightedEdge) this.residualGraph.addEdge(target, source);
                    this.residualGraph.setEdgeWeight(newEdge, newReducedEdgeWeight);
                } else {
                    oldEdge = (DefaultWeightedEdge) this.residualGraph.getEdge(target, source);
                    if (oldEdge != null) {
                        float oldReducedEdgeWeight = (float) this.residualGraph.getEdgeWeight(oldEdge);
                        float newReducedEdgeWeight = -oldReducedEdgeWeight;
                        this.residualGraph.removeEdge(target, source);
                        DefaultWeightedEdge newEdge = (DefaultWeightedEdge) this.residualGraph.addEdge(source, target);
                        this.residualGraph.setEdgeWeight(newEdge, newReducedEdgeWeight);
                    } else {
                        throw new PathEdgeNotInResidualGraphException("An edge from the augmenting path could not be found in the residual graph.");
                    }
                }
            }
        }

        // Since first house and last household in this path were previously unmatched,
        // there is one edge between the source and the first house, and one between the last household and the sink,
        // that must now be removed; since both the house and the household are now matched.
        int firstTarget = augmentingPath.getGraph().getEdgeTarget(edgeList.get(0));
        int lastSource = augmentingPath.getEndVertex();
        this.residualGraph.removeEdge(this.getSourceID(), firstTarget);
        this.residualGraph.removeEdge(lastSource, this.getSinkID());


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
                    float previousHousePrice = this.matchingPrices.getHousePrice(houseID);
                    float nextHousePrice = newMatchingPrices.getHousePrice(houseID);
                    float previousHouseholdPrice = this.matchingPrices.getHouseholdPrice(householdID);
                    float nextHouseholdPrice = newMatchingPrices.getHouseholdPrice(householdID);
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
                        float nextHousePrice = newMatchingPrices.getHousePrice(houseID);
                        float previousHouseholdPrice = this.matchingPrices.getHouseholdPrice(householdID);
                        float nextHouseholdPrice = newMatchingPrices.getHouseholdPrice(householdID);
                        float previousEdgeWeight = (float) residualGraph.getEdgeWeight(edge);
                        float nextEdgeWeight = previousEdgeWeight - previousHouseholdPrice + nextHouseholdPrice
                                + previousHousePrice - nextHousePrice;
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
}
