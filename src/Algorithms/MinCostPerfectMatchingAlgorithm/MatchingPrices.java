package Algorithms.MinCostPerfectMatchingAlgorithm;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class MatchingPrices {

    private Matching matching;
    private ResidualGraph residualGraph;
    private HashMap<Integer, Float> housePrices = new HashMap<Integer, Float>();
    private HashMap<Integer, Float> householdPrices = new HashMap<Integer, Float>();
    private MatchingPrices previousPrices = null;

    public MatchingPrices(Matching matching) {
        this.matching = matching;
    }

    public void setInitialPrices() throws Matching.MatchingEvaluator.HouseholdIncomeTooHighException, Matching.Matching.HouseLinkedToMultipleException, Matching.Matching.HouseLinkedToHouseException {
        MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching);
        if (matching.countEdges() == 0) {
            for (House house : matching.getHouses()) {
                housePrices.put(house.getID(), (float) 0.0);
            }
            for (Household household : matching.getHouseholds()) {
                float minScore = 1; // = 1 - 0;
                for (House house : matching.getHouses()) { // In the current residual graph,
                    // household gets edges from all houses.
                    // TODO: Could replace this with an edge-weight check,
                    //  but I shouldn't do that if I decide to have the residualGraph use
                    //  _reduced_ edge costs as weights. -> Therefore, leave this as-is.
                    float candidateScore = 1 - matchingEvaluator.evaluateIndividualTotalFit(house.getID(), household.getID());
                    if (candidateScore < minScore) {
                        minScore = candidateScore;
                    }
                }
                householdPrices.put(household.getID(), 1-minScore);
            }
        }
        else {
            System.err.println("Cannot initialize prices for a non-empty matching.");
        }
        this.residualGraph = new ResidualGraph(this.matching, this);
    }

    public void updatePrices() {
        // This process indeed does not require the new matching M' and indeed depends on the old matching.
        if (previousPrices == null) {
            System.err.println("Updating the prices of a non-empty matching requires a previous priceset!");
        } else {
            MatchingPrices currentPrices = (MatchingPrices) deepClone(this);
            DijkstraShortestPath<Integer, DefaultWeightedEdge> dijkstraShortestPath
                    = new DijkstraShortestPath<Integer, DefaultWeightedEdge>(this.residualGraph.getGraph());
            ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> sourcePaths
                    = dijkstraShortestPath.getPaths(this.residualGraph.getSourceID());

            for (House house : matching.getHouses()) {
                int houseID = house.getID();
                float previousPrice = previousPrices.getHousePrice(houseID);
                GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(houseID);
                float distInPreviousMatching = (float) shortestPath.getWeight();
                float newPrice = distInPreviousMatching + previousPrice;
                this.setHousePrice(houseID, newPrice);
            }
            for (Household household : matching.getHouseholds()) {
                int householdID = household.getID();
                float previousPrice = previousPrices.getHouseholdPrice(householdID);
                GraphPath<Integer, DefaultWeightedEdge> shortestPath = sourcePaths.getPath(householdID);
                float distInPreviousMatching = (float) shortestPath.getWeight();
                float newPrice = distInPreviousMatching + previousPrice;
                this.setHouseholdPrice(householdID, newPrice);
            }
            previousPrices = currentPrices;
        }
    }

    // Augments the matching and furthermore updates both the matchingPrices (_this_) and the residualGraph.
    public Matching augmentMatchingAndUpdateAll(GraphPath<Integer, DefaultWeightedEdge> augmentingPath) throws Matching.Matching.HouseAlreadyMatchedException, Matching.Matching.HouseholdAlreadyMatchedException, Matching.Matching.IDNotPresentException, Matching.Matching.HouseLinkedToHouseException, Matching.Matching.HouseLinkedToMultipleException {
        this.updatePrices(); // Doing this first so that the updating process still has access to the un-augmented matching...
        this.matching = residualGraph.augmentMatchingAndUpdateResidualGraph(augmentingPath, this); // ...Because this modifies the matching.
        return this.matching;
    }

    public ResidualGraph getResidualGraph() {
        return this.residualGraph;
    }


    public float getHousePrice(int houseID) {
        return this.housePrices.get(houseID);
    }

    public float getHouseholdPrice(int householdID) {
        return this.householdPrices.get(householdID);
    }


    public void setHousePrice(int houseID, float newPrice) {
        this.housePrices.put(houseID, newPrice);
    }

    public void setHouseholdPrice(int householdID, float newPrice) {
        this.householdPrices.put(householdID, newPrice);
    }

    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
