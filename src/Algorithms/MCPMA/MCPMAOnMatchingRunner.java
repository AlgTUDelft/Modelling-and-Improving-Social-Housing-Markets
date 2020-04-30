package Algorithms.MCPMA;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import static Miscellaneous.DeepCloner.deepClone;

public class MCPMAOnMatchingRunner {

    private Matching matching;
    private MCPMAStrategy mcpmaStrategy;
    private ImprovementGraph improvementGraph;
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchGraph;
    private MCPMA MCPMA;

    public MCPMAOnMatchingRunner(Matching matching, MCPMAStrategy mcpmaStrategy) throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, MCPMA.UnequalSidesException {
        this.matching = (Matching) deepClone(matching);
        this.mcpmaStrategy = mcpmaStrategy;
        improvementGraph = new ImprovementGraph(this.matching, mcpmaStrategy);
        MCPMA = new MCPMA(improvementGraph, mcpmaStrategy);
    }

    public Matching optimizeMatching(boolean print) throws MCPMAPrices.AlreadyInitiatedException, ResidualGraph.PathEdgeNotInResidualGraphException, ResidualGraph.MatchGraphNotEmptyException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException, Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException, InterruptedException {
        matchGraph = MCPMA.findOptimalMatching(print);
        switch(this.mcpmaStrategy) {
            case REGULAR: matching = parseMatchGraphRegular(); break;
            case IMPROVEMENT: matching = parseMatchGraphImprovement(); break;
        }
        return matching;
    }

    private Matching parseMatchGraphRegular() throws Matching.HouseLinkedToMultipleException, Matching.HouseLinkedToHouseException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        matching.dissolveConnections();
        for (DefaultEdge edge : matchGraph.edgeSet()) {
            HousingMarketVertex source = matchGraph.getEdgeSource(edge);
            HousingMarketVertex target = matchGraph.getEdgeTarget(edge);
            if (source instanceof DummyHouse || source instanceof DummyHousehold
                    ||  target instanceof DummyHousehold || target instanceof DummyHouse) {
                // Do nothing. Houses (resp. households) matched to DummyHouseholds (resp. DummyHouses)
                // will remain unmatched.
            } else {
                House house;
                Household household;
                if (source instanceof House) {
                    house = (House) source;
                    household = (Household) target;
                } else {
                    house = (House) target;
                    household = (Household) source;
                }
                // Match house and household.
                matching.connect(house.getID(), household.getID());
            }
        }
        return matching;
    }

    private Matching parseMatchGraphImprovement() throws Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, Matching.HouseholdAlreadyMatchedException, Matching.HouseAlreadyMatchedException {
        for (DefaultEdge edge : matchGraph.edgeSet()) {
            HousingMarketVertex source = matchGraph.getEdgeSource(edge);
            HousingMarketVertex target = matchGraph.getEdgeTarget(edge);
            if (source instanceof DummyHouse || source instanceof DummyHousehold
                    ||  target instanceof DummyHousehold || target instanceof DummyHouse) {
                // Do nothing. These households may still keep their old houses,
                // which weren't taken into account by this algorithm.
            } else {
                House house;
                Household household;
                if (source instanceof House) {
                    house = (House) source;
                    household = (Household) target;
                } else {
                    house = (House) target;
                    household = (Household) source;
                }
                // First disconnect from existing house...
                House oldHouse = matching.getHouseFromHousehold(household.getID());
                if (oldHouse != null) {
                    matching.disconnect(oldHouse.getID(), household.getID());
                }
                // ...then connect to new house.
                matching.connect(house.getID(), household.getID());
            }
        }
        return matching;
    }

}
