package Algorithms.EfficientStableMatchingAlgorithm;

import Matching.Matching;

// Based on the "Efficient and Stable Matching Algorithm (ESMA)" from "Two-sided matching with indifferences",
// by Aytek Erdil and Haluk Ergin. Note that this version of the algorithm omits step 0. This is because we start with
// some existing matching rather than with an empty one; thus the DA-algorithm cannot realistically be run beforehand.
// As a result, despite the algorithm's name, it does not guarantee stability!
public class EfficientStableMatchingAlgorithm {
    private Matching matching;

    public EfficientStableMatchingAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public Matching findEfficientStableMatching() {
        TwoLabeledGraph twoLabeledGraph = new TwoLabeledGraph(matching);
        StrictCycle strictCycle = twoLabeledGraph.findStrictCycle();
        while (strictCycle != null) {
            matching.effectStrictCycle(strictCycle);
            twoLabeledGraph.update(strictCycle);
            strictCycle = twoLabeledGraph.findStrictCycle();
        }
    }


}
