package Algorithms.MinCostPerfectMatchingAlgorithm;

import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

public class MinCostPerfectMatchingAlgorithm {
    // Adapted from "Algorithm Design" by Jon Kleinberg and Eva Tardos.
    private Matching matching;

    public MinCostPerfectMatchingAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public Matching findMinCostPerfectMatching()
            throws Matching.HouseLinkedToMultipleException,
            Matching.HouseLinkedToHouseException, MatchingEvaluator.HouseholdIncomeTooHighException, BipartiteSidesUnequalSize, Matching.IDNotPresentException, Matching.HouseAlreadyMatchedException, Matching.HouseholdAlreadyMatchedException, ResidualGraph.PathEdgeNotInResidualGraphException, Matching.HouseholdLinkedToMultipleException, Matching.HouseholdLinkedToHouseholdException, ResidualGraph.MatchingNotEmptyException {
        if (this.matching.getHouses().size() != this.matching.getHouseholds().size()) {
            throw new BipartiteSidesUnequalSize("Error: Matching must contain as many houses as households.");
        }
        this.matching.dissolveConnections();

        MatchingPrices matchingPrices = new MatchingPrices(this.matching);
        matchingPrices.setInitialPrices();
        int i = 0;
        while (!this.matching.isMaximallyMatched()) {
            System.out.println(i);
            GraphPath<Integer, DefaultWeightedEdge> augmentingPath = matchingPrices.getResidualGraph().findAugmentingPath();
            this.matching = matchingPrices.augmentMatchingAndUpdateAll(augmentingPath);
            i++;
        }

        return this.matching;
    }

    public class BipartiteSidesUnequalSize extends Exception {
        public BipartiteSidesUnequalSize(String errorMessage) {
            super(errorMessage);
        }
    }
}
