package Algorithms.SimpleImprovement;

import Algorithms.MinCostPerfectMatchingAlgorithm.MatchingPrices;
import HousingMarket.House.House;
import HousingMarket.HouseAndHouseholdIDPair;
import HousingMarket.HousingMarketVertex;
import Matching.Matching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;

public class ResidualImprovementGraph {
    // HouseID and HouseholdID values are always non-negative, so these values are free for us to use.
    private Integer sourceID = -2;
    private Integer sinkID = -1;
    private SimpleDirectedWeightedGraph residualImprovementGraph;
    private ImprovementGraph improvementGraph;
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchGraph;
    private ImprovementPrices improvementPrices;
    // OK, hopefully this will go right.
    private HashMap<HouseAndHouseholdIDPair, Float> nonReducedEdgeWeights = new HashMap<HouseAndHouseholdIDPair, Float>();

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
            DefaultWeightedEdge
        }

    }

    public class MatchGraphNotEmptyException extends Exception {
        public MatchGraphNotEmptyException(String errorMessage) {
            super(errorMessage);
        }
    }
}
