package Algorithms;

import Matching.Matching;

public class MinCostPerfectMatchingAlgorithm {
    // Adapted from "Algorithm Design" by Jon Kleinberg and Eva Tardos.
    private Matching matching;

    public MinCostPerfectMatchingAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public float FindMinCostPerfectMatching()
            throws Matching.HouseLinkedToMultipleException,
            Matching.HouseLinkedToHouseException {
        if (this.matching.getHouses().size() != this.matching.getHouseholds().size()) {
            System.err.println("Error: Matching must contain as many houses as households.");
            return (float) 0.0;
        }
        this.matching.dissolve();




        return (float) 0.0;
    }
}
