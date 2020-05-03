package Algorithms.MCPMA;

import HousingMarket.House.House;
import HousingMarket.HouseAndHouseholdIDPair;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResidualGraph {
    // HouseID and HouseholdID values are always non-negative, so these values are free for us to use.
    private Integer sourceID = -2;
    private Integer sinkID = -1;
    private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> residualImprovementGraph;
    private ImprovementGraph improvementGraph;
    private MatchGraph matchGraph;
    private MCPMAPrices MCPMAPrices;
    // OK, hopefully this will go right.
    private HashMap<HouseAndHouseholdIDPair, Double> nonReducedEdgeWeights = new HashMap<HouseAndHouseholdIDPair, Double>();

    public ResidualGraph(ImprovementGraph improvementGraph, MatchGraph matchGraph, MCPMAPrices MCPMAPrices) throws MatchGraphNotEmptyException {
        if (matchGraph.getEdgeCount() > 0) {
            throw new MatchGraphNotEmptyException("Error: Can only create Residual Graph on empty Match Graph.");
        }
        this.residualImprovementGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
        this.improvementGraph = improvementGraph;
        this.matchGraph = matchGraph;
        this.MCPMAPrices = MCPMAPrices;
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
                double housePrice = MCPMAPrices.getHousePrice(house);
                double householdPrice = MCPMAPrices.getHouseholdPrice(household);
                double reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                residualImprovementGraph.setEdgeWeight(edge, reducedEdgeWeight);
            }
            for (DummyHousehold dummyHousehold : improvementGraph.getDummyHouseholds()) {
                int houseID = house.getID();
                int dummyHouseholdID = dummyHousehold.getID();
                DefaultWeightedEdge edge = residualImprovementGraph.addEdge(houseID, dummyHouseholdID);
                // nonReducedEdgeWeight is 1.00 for dummies.
                double nonReducedEdgeWeight = 1.00;
                nonReducedEdgeWeights.put(new HouseAndHouseholdIDPair(houseID, dummyHouseholdID), nonReducedEdgeWeight);
                double housePrice = MCPMAPrices.getHousePrice(house);
                double householdPrice = MCPMAPrices.getHouseholdPrice(dummyHousehold);
                double reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                residualImprovementGraph.setEdgeWeight(edge, reducedEdgeWeight);
            }
        }
        for (DummyHouse dummyHouse : improvementGraph.getDummyHouses()) {
            for (Household household : improvementGraph.getHouseholds()) {
                int dummyHouseID = dummyHouse.getID();
                int householdID = household.getID();
                DefaultWeightedEdge edge = residualImprovementGraph.addEdge(dummyHouseID, householdID);
                // nonReducedEdgeWeight is 1.00 for dummies.
                double nonReducedEdgeWeight = 1.00;
                nonReducedEdgeWeights.put(new HouseAndHouseholdIDPair(dummyHouseID, householdID), nonReducedEdgeWeight);
                double housePrice = MCPMAPrices.getHousePrice(dummyHouse);
                double householdPrice = MCPMAPrices.getHouseholdPrice(household);
                double reducedEdgeWeight = housePrice + nonReducedEdgeWeight - householdPrice;
                residualImprovementGraph.setEdgeWeight(edge, reducedEdgeWeight);
            }
            // If there are dummy houses, then there are no dummy households. Hence we ignore those.
        }
    }

    public GraphPath<Integer, DefaultWeightedEdge> findAugmentingPath() {
        DijkstraShortestPath<Integer, DefaultWeightedEdge> dijkstraShortestPath;
        dijkstraShortestPath = new DijkstraShortestPath<>(this.residualImprovementGraph);
        ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> sourcePaths;
        sourcePaths = dijkstraShortestPath.getPaths(sourceID);
        // Since the maximum weight of an edge is 1 (which is also dummies' edges' weight), and between houses and households an augmenting path can
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
                // It is possible that no path to the household exists if household connects only to dummy houses (trivially),
                // yet all of these dummy houses are already matched.
                if (shortestPath != null) {
                    double weightOfShortestPath = shortestPath.getWeight();
                    double priceOfHousehold = this.MCPMAPrices.getHouseholdPrice(household);
                    double candidateTotalWeight = weightOfShortestPath + priceOfHousehold;
                    if (candidateTotalWeight < minimumWeightFound) {
                        minimumWeightFound = candidateTotalWeight;
                        bestPathFound = shortestPath;
                    }
                }
            }
        }
        for (DummyHousehold dummyHousehold : improvementGraph.getDummyHouseholds()) {
            // We only want to check unmatched households, because the path must go directly from the household
            // to the sink node; matched households have no edge to the sink.
            if (matchGraph.edgesOf(dummyHousehold).isEmpty()) {
                GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(dummyHousehold.getID());
                // It is possible that no path to the household exists if household connects only to dummy houses (trivially),
                // yet all of these dummy houses are already matched.
                if (shortestPath != null) {
                    double weightOfShortestPath = shortestPath.getWeight();
                    double priceOfHousehold = this.MCPMAPrices.getHouseholdPrice(dummyHousehold);
                    double candidateTotalWeight = weightOfShortestPath + priceOfHousehold;
                    if (candidateTotalWeight < minimumWeightFound) {
                        minimumWeightFound = candidateTotalWeight;
                        bestPathFound = shortestPath;
                    }
                }
            }
        }
        return bestPathFound;
    }

    public MatchGraph augmentMatchingAndUpdateResidualGraph(GraphPath<Integer, DefaultWeightedEdge> augmentingPath, MCPMAPrices MCPMAPrices) throws PathEdgeNotInResidualGraphException {
        this.augmentMatchGraph(augmentingPath);
        this.updateGraphAfterAugmenting(augmentingPath, MCPMAPrices);
        return this.matchGraph;
    }

    private void augmentMatchGraph(GraphPath<Integer, DefaultWeightedEdge> graphPath) {
        ArrayList<HouseAndHouseholdIDPair> toConnect = new ArrayList<HouseAndHouseholdIDPair>();
        List<DefaultWeightedEdge> edgeList = graphPath.getEdgeList();
        // The source node, where the first edge in graphPath starts,
        // isn't present in the regular matching. Thus we want to ignore it.
        // Note that graphPath does not contain the final sink node.
        for (int i = 1; i < edgeList.size(); i++) {
            DefaultWeightedEdge edge = edgeList.get(i);
            int sourceID = graphPath.getGraph().getEdgeSource(edge);
            int targetID = graphPath.getGraph().getEdgeTarget(edge);
            // Edge 0 is from source to house.
            // Odd edges are from house to household.
            // Even edges are from household to house.
            if (i % 2 == 1) {
                // Source node is house, target node is household.
                HousingMarketVertex house = improvementGraph.getHouseFromID(sourceID);
                HousingMarketVertex household = improvementGraph.getHouseholdFromID(targetID);
                if (matchGraph.containsEdge(house, household)) {
                    matchGraph.removeEdge(house, household);
                } else {toConnect.add(new HouseAndHouseholdIDPair(sourceID, targetID));}
            }
            else {
                // Target node is house, source node is household.
                    HousingMarketVertex household = improvementGraph.getHouseholdFromID(sourceID);
                    HousingMarketVertex house = improvementGraph.getHouseFromID(targetID);
                    if (matchGraph.containsEdge(household, house)) {
                        matchGraph.removeEdge(household, house);
                    } else {toConnect.add(new HouseAndHouseholdIDPair(targetID, sourceID));}
                }

            }
        for (HouseAndHouseholdIDPair pair : toConnect) {
            HousingMarketVertex house = improvementGraph.getHouseFromID(pair.getHouseID());
            HousingMarketVertex household = improvementGraph.getHouseholdFromID(pair.getHouseholdID());
            matchGraph.addEdge(house, household);
        }
    }

    private void updateGraphAfterAugmenting(GraphPath<Integer, DefaultWeightedEdge> augmentingPath, MCPMAPrices newMCPMAPrices) throws PathEdgeNotInResidualGraphException {
        List<DefaultWeightedEdge> edgeList = augmentingPath.getEdgeList();
        Graph<Integer, DefaultWeightedEdge> pathGraph = augmentingPath.getGraph();
        for (int i = 1; i < edgeList.size(); i++) {
            DefaultWeightedEdge edge = edgeList.get(i);
            int source = pathGraph.getEdgeSource(edge);
            int target = pathGraph.getEdgeTarget(edge);
            DefaultWeightedEdge oldEdge = (DefaultWeightedEdge) this.residualImprovementGraph.getEdge(source, target);
            // Change direction.
            if (oldEdge != null) {
                this.residualImprovementGraph.removeEdge(source, target);
                this.residualImprovementGraph.addEdge(target, source);
            } else {
                oldEdge = (DefaultWeightedEdge) this.residualImprovementGraph.getEdge(target, source);
                if (oldEdge != null) {
                    this.residualImprovementGraph.removeEdge(target, source);
                    this.residualImprovementGraph.addEdge(source, target);
                } else {
                    throw new PathEdgeNotInResidualGraphException("An edge from the augmenting path could not be found in the residual graph.");
                }
            }
            if (i % 2 == 1) {
                // Source is house.
                double oldNonReducedWeight = this.nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(source, target));
                this.nonReducedEdgeWeights.put(new HouseAndHouseholdIDPair(source, target), oldNonReducedWeight * -1);
            } else {
                // Source is household.
                double oldNonReducedWeight = this.nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(target, source));
                this.nonReducedEdgeWeights.put(new HouseAndHouseholdIDPair(target, source), oldNonReducedWeight * -1);
            }
        }

        // Matched houses and households must lose their edges to the source and sink, respectively.
        List<Integer> vertexList = augmentingPath.getVertexList();
        // Skip source node, hence i = 1.
        for (int i = 1; i < vertexList.size(); i++) {
            int vertex = vertexList.get(i);
            if (i % 2 == 1) {
                // vertex is house.
                HousingMarketVertex house = this.improvementGraph.getHouseFromID(vertex);
                if (!matchGraph.edgesOf(house).isEmpty()) { // Matched
                    this.residualImprovementGraph.removeEdge(sourceID, vertex);
                }
            } else {
                // vertex is household.
                HousingMarketVertex household = this.improvementGraph.getHouseholdFromID(vertex);
                if (!matchGraph.edgesOf(household).isEmpty()) { // Matched
                    this.residualImprovementGraph.removeEdge(vertex, sinkID);
                }
            }

        }

        updateReducedEdgeWeightsAndPrices(newMCPMAPrices);
        this.MCPMAPrices = newMCPMAPrices;
    }

    private void updateReducedEdgeWeightsAndPrices(MCPMAPrices newMCPMAPrices) throws PathEdgeNotInResidualGraphException {
        // TODO: Watch out here! For some household/house, do I take only neighbors, or the entire other side?
        //  I think the latter is true, as coded, but this merits some more thought.
        for (House house : this.improvementGraph.getHouses()) {
            for (Household household : this.improvementGraph.getHouseholds()) {
                int houseID = house.getID();
                int householdID = household.getID();
                DefaultWeightedEdge edge = this.residualImprovementGraph.getEdge(houseID, householdID);
                if (edge != null) {
                    double nextHousePrice = newMCPMAPrices.getHousePrice(house);
                    double nextHouseholdPrice = newMCPMAPrices.getHouseholdPrice(household);
                    double oldNonReducedWeight = nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(houseID, householdID));
                    double nextEdgeWeight = nextHousePrice +
                            oldNonReducedWeight - nextHouseholdPrice;
                    this.residualImprovementGraph.setEdgeWeight(edge, nextEdgeWeight);
                }
                else {
                    edge = this.residualImprovementGraph.getEdge(householdID, houseID);
                    if (edge != null) {
                        double nextHousePrice = newMCPMAPrices.getHousePrice(house);
                        double nextHouseholdPrice = newMCPMAPrices.getHouseholdPrice(household);
                        double oldNonReducedWeight = nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(houseID, householdID));
                        double nextEdgeWeight = nextHouseholdPrice +
                                oldNonReducedWeight - nextHousePrice;
                        this.residualImprovementGraph.setEdgeWeight(edge, nextEdgeWeight);
                    }
                    else {
                        if (this.MCPMAPrices.getMcpmaStrategy() == MCPMAStrategy.REGULAR) {
                            throw new PathEdgeNotInResidualGraphException("An edge from the augmenting path could not be found in the residual graph.");
                        }
                    }
                }
            }
            for (DummyHousehold dummyHousehold : this.improvementGraph.getDummyHouseholds()) {
                int houseID = house.getID();
                int householdID = dummyHousehold.getID();
                DefaultWeightedEdge edge = this.residualImprovementGraph.getEdge(houseID, householdID);
                if (edge != null) {
                    double nextHousePrice = newMCPMAPrices.getHousePrice(house);
                    double nextHouseholdPrice = newMCPMAPrices.getHouseholdPrice(dummyHousehold);
                    double oldNonReducedWeight = nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(houseID, householdID));
                    double nextEdgeWeight = nextHousePrice +
                            oldNonReducedWeight - nextHouseholdPrice;
                    this.residualImprovementGraph.setEdgeWeight(edge, nextEdgeWeight);
                }
                else {
                    edge = this.residualImprovementGraph.getEdge(householdID, houseID);
                    if (edge != null) {
                        double nextHousePrice = newMCPMAPrices.getHousePrice(house);
                        double nextHouseholdPrice = newMCPMAPrices.getHouseholdPrice(dummyHousehold);
                        double oldNonReducedWeight = nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(houseID, householdID));
                        double nextEdgeWeight = nextHouseholdPrice +
                                oldNonReducedWeight - nextHousePrice;
                        this.residualImprovementGraph.setEdgeWeight(edge, nextEdgeWeight);
                    }
                    else {
                        if (this.MCPMAPrices.getMcpmaStrategy() == MCPMAStrategy.REGULAR) {
                            throw new PathEdgeNotInResidualGraphException("An edge from the augmenting path could not be found in the residual graph.");
                        }
                    }
                }
            }
        }
        for (DummyHouse dummyHouse : this.improvementGraph.getDummyHouses()) {
            for (Household household : this.improvementGraph.getHouseholds()) {
                int houseID = dummyHouse.getID();
                int householdID = household.getID();
                DefaultWeightedEdge edge = this.residualImprovementGraph.getEdge(houseID, householdID);
                if (edge != null) {
                    double nextHousePrice = newMCPMAPrices.getHousePrice(dummyHouse);
                    double nextHouseholdPrice = newMCPMAPrices.getHouseholdPrice(household);
                    double oldNonReducedWeight = nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(houseID, householdID));
                    double nextEdgeWeight = nextHousePrice +
                            oldNonReducedWeight - nextHouseholdPrice;
                    this.residualImprovementGraph.setEdgeWeight(edge, nextEdgeWeight);
                }
                else {
                    edge = this.residualImprovementGraph.getEdge(householdID, houseID);
                    if (edge != null) {
                        double nextHousePrice = newMCPMAPrices.getHousePrice(dummyHouse);
                        double nextHouseholdPrice = newMCPMAPrices.getHouseholdPrice(household);
                        double oldNonReducedWeight = nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(houseID, householdID));
                        double nextEdgeWeight = nextHouseholdPrice +
                                oldNonReducedWeight - nextHousePrice;
                        this.residualImprovementGraph.setEdgeWeight(edge, nextEdgeWeight);
                    }
                    else {
                        if (this.MCPMAPrices.getMcpmaStrategy() == MCPMAStrategy.REGULAR) {
                            throw new PathEdgeNotInResidualGraphException("An edge from the augmenting path could not be found in the residual graph.");
                        }
                    }
                }
            }
            for (DummyHousehold dummyHousehold : this.improvementGraph.getDummyHouseholds()) {
                int houseID = dummyHouse.getID();
                int householdID = dummyHousehold.getID();
                DefaultWeightedEdge edge = this.residualImprovementGraph.getEdge(houseID, householdID);
                if (edge != null) {
                    double nextHousePrice = newMCPMAPrices.getHousePrice(dummyHouse);
                    double nextHouseholdPrice = newMCPMAPrices.getHouseholdPrice(dummyHousehold);
                    double oldNonReducedWeight = nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(houseID, householdID));
                    double nextEdgeWeight = nextHousePrice +
                            oldNonReducedWeight - nextHouseholdPrice;
                    this.residualImprovementGraph.setEdgeWeight(edge, nextEdgeWeight);
                }
                else {
                    edge = this.residualImprovementGraph.getEdge(householdID, houseID);
                    if (edge != null) {
                        double nextHousePrice = newMCPMAPrices.getHousePrice(dummyHouse);
                        double nextHouseholdPrice = newMCPMAPrices.getHouseholdPrice(dummyHousehold);
                        double oldNonReducedWeight = nonReducedEdgeWeights.get(new HouseAndHouseholdIDPair(houseID, householdID));
                        double nextEdgeWeight = nextHouseholdPrice +
                                oldNonReducedWeight - nextHousePrice;
                        this.residualImprovementGraph.setEdgeWeight(edge, nextEdgeWeight);
                    }
                    else {
                        if (this.MCPMAPrices.getMcpmaStrategy() == MCPMAStrategy.REGULAR) {
                            throw new PathEdgeNotInResidualGraphException("An edge from the augmenting path could not be found in the residual graph.");
                        }
                    }
                }
            }
        }
    }

    public SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> getResidualImprovementGraph() {
        return this.residualImprovementGraph;
    }

    public Integer getSourceID() {
        return sourceID;
    }

    public class MatchGraphNotEmptyException extends Exception {
        public MatchGraphNotEmptyException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class PathEdgeNotInResidualGraphException extends Exception {
        public PathEdgeNotInResidualGraphException(String errorMessage) {
            super(errorMessage);
        }
    }
}
