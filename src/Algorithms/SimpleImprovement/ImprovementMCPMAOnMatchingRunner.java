package Algorithms.SimpleImprovement;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import HousingMarket.HousingMarketVertex;
import Matching.Matching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class ImprovementMCPMAOnMatchingRunner {

    private Matching matching;
    private ImprovementGraph improvementGraph;
    private ImprovementMCPMA improvementMCPMA;
    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchGraph;

    public ImprovementMCPMAOnMatchingRunner(Matching matching) throws Matching.MatchingEvaluator.HouseholdIncomeTooHighException, Matching.Matching.HouseholdLinkedToMultipleException, Matching.Matching.HouseholdLinkedToHouseholdException, ImprovementMCPMA.UnequalSidesException {
        this.matching = matching;
        improvementGraph = new ImprovementGraph(matching);
        improvementMCPMA = new ImprovementMCPMA(improvementGraph);
    }

    // Warning: This algorithm only takes empty houses into account!
    public Matching optimizeMatching(boolean print) throws ImprovementPrices.AlreadyInitiatedException, ResidualImprovementGraph.PathEdgeNotInResidualImprovementGraphException, ResidualImprovementGraph.MatchGraphNotEmptyException, Matching.Matching.HouseholdLinkedToMultipleException, Matching.Matching.HouseholdLinkedToHouseholdException, Matching.Matching.HouseholdAlreadyMatchedException, Matching.Matching.HouseAlreadyMatchedException {
        matchGraph = improvementMCPMA.findOptimalMatching(print);
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
