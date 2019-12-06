package Algorithms.MinCostPerfectMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.apache.commons.math3.geometry.spherical.twod.Vertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;

// TODO: Finish ResidualGraph class.
public class ResidualGraph {
    private SimpleDirectedWeightedGraph residualGraph;
    private ArrayList<Integer> houseIDs = new ArrayList<Integer>();
    private ArrayList<Integer> householdIDs = new ArrayList<Integer>();
    // Strings to differentiate them from normal vertices, which are identified through integers.
    private String sourceID = "Source";
    private String sinkID = "Sink";

    public ResidualGraph(Matching matching)
            throws Matching.HouseLinkedToMultipleException,
            Matching.HouseLinkedToHouseException,
            Matching.MatchingEvaluator.HouseholdIncomeTooHighException {
        this.residualGraph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
        residualGraph.addVertex(sourceID);
        residualGraph.addVertex(sinkID);
        for (House house : matching.getHouses()) {
            int houseID = house.getID();
            houseIDs.add(houseID);
            residualGraph.addVertex(houseID);
            DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(sourceID, houseID);
            residualGraph.setEdgeWeight(edge, 0); // TODO: Really 0?
        }
        for (Household household : matching.getHouseholds()) {
            int householdID = household.getID();
            householdIDs.add(householdID);
            residualGraph.addVertex(householdID);
            DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(householdID, sinkID);
            residualGraph.setEdgeWeight(edge, 0); // TODO: Really 0?
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
                    residualGraph.setEdgeWeight(edge, -(1-matchingEvaluator.evaluateIndividualTotalFit(houseID, householdID)));
                }
                else {
                    DefaultWeightedEdge edge = (DefaultWeightedEdge) residualGraph.addEdge(houseID, householdID);
                    // (1-w) instead of w because we want to maximize sum(w) where w in [0,1].
                    // Thus we want to minimize 1-w.
                    residualGraph.setEdgeWeight(edge, 1-matchingEvaluator.evaluateIndividualTotalFit(houseID, householdID));
                }
            }
        }

        System.out.println("test");

    }


}
