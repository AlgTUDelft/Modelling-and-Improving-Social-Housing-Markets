package Algorithms.MCPMA;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import org.apache.xmlbeans.impl.regex.Match;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

public class MCPMA {

    private ImprovementGraph improvementGraph;
    private MatchGraph matchGraph;
    // Algorithm is functionally agnostic to strategy variant, which matters only to the improvement graph.
    // The only reason it's included here is because it's useful to throw an error at some point in ResidualGraph
    // when this strategy is REGULAR.
    private MCPMAStrategy mcpmaStrategy;

    public MCPMA(ImprovementGraph improvementGraph, MCPMAStrategy mcpmaStrategy) throws UnequalSidesException {
        this.improvementGraph = improvementGraph;
        this.mcpmaStrategy = mcpmaStrategy;
        if (improvementGraph.getHouses().size() + improvementGraph.getDummyHouses().size()
                != improvementGraph.getHouseholds().size() + improvementGraph.getDummyHouseholds().size()) {
            throw new UnequalSidesException("Error: Improvement graph does not contain equal amount of houses and households.");
        }
        // Create matchGraph
        matchGraph = new MatchGraph();
        for (House house : improvementGraph.getHouses()) {
            matchGraph.addVertex(house);
        }
        for (Household household : improvementGraph.getHouseholds()) {
            matchGraph.addVertex(household);
        }
        for (DummyHouse dummyHouse : improvementGraph.getDummyHouses()) {
            matchGraph.addVertex(dummyHouse);
        }
        for (DummyHousehold dummyHousehold : improvementGraph.getDummyHouseholds()) {
            matchGraph.addVertex(dummyHousehold);
        }
        // Note that matchGraph starts without any matches; hence no edges are added.
    }

    // Find optimal matching.
    public MatchGraph findOptimalMatching(boolean print) throws MCPMAPrices.AlreadyInitiatedException, ResidualGraph.MatchGraphNotEmptyException, ResidualGraph.PathEdgeNotInResidualGraphException, InterruptedException {
        MCPMAPrices MCPMAPrices = new MCPMAPrices(improvementGraph, matchGraph, mcpmaStrategy);
        MCPMAPrices.setInitialPrices();
        int i = 0;
        boolean shouldContinue = matchGraph.getEdgeCount() != improvementGraph.getHouseholds().size() + improvementGraph.getDummyHouseholds().size();
        while (shouldContinue) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (print) {
                System.out.println("State " + i + ":" + this.matchGraph);
            }

            GraphPath<Integer, DefaultWeightedEdge> augmentingPath = MCPMAPrices.getResidualGraph().findAugmentingPath();
            if (augmentingPath == null) {
                shouldContinue = false;
            } else {
                if (print) {
                    System.out.println("Augmenting path " + i + ": " + augmentingPath);
                }
                this.matchGraph = MCPMAPrices.augmentMatchGraphAndUpdateAll(augmentingPath);
                i++;
                shouldContinue = matchGraph.getEdgeCount() != improvementGraph.getHouseholds().size() + improvementGraph.getDummyHouseholds().size();
            }
        }
        return matchGraph;
    }

    public class UnequalSidesException extends Exception {
        public UnequalSidesException(String errorMessage) {
            super(errorMessage);
        }
    }

}