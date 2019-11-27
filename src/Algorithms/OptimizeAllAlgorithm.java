package Algorithms;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import TreeNode.TreeNode;

import java.util.HashSet;

public class OptimizeAllAlgorithm {
    private Matching matching;

    public OptimizeAllAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public float optimizeAvailables()
            throws Matching.Matching.HouseholdLinkedToHouseholdException,
            Matching.Matching.HouseholdLinkedToMultipleException,
            Matching.Matching.HouseLinkedToHouseException,
            Matching.Matching.HouseLinkedToMultipleException {
        return optimizeAll(this.matching.getHouseholdlessHouses(),
                this.matching.getHouselessHouseholds());

    }
    public float optimizeAll(HashSet<House> houses, HashSet<Household> households)
            throws Matching.Matching.HouseholdLinkedToMultipleException,
            Matching.Matching.HouseholdLinkedToHouseholdException,
            Matching.Matching.HouseLinkedToMultipleException,
            Matching.Matching.HouseLinkedToHouseException {
        // TODO: Optimize such that the not the summed individual total score is used,
        //  but the actual weighted total.

        // Dissolve connections.
        for (House house : houses) {
            Household household = matching.getHouseholdFromHouse(house);
            if (household != null) {
                matching.disconnect(house, household);
            }
        }
        for (Household household : households) {
            House house = matching.getHouseFromHousehold(household);
            if (house != null) {
                matching.disconnect(house, household);
            }
        }

        // Say we have a bipartite graph with n1 + n2 vertices.
        // We want to get as high a total fit-quality as possible.
        // Since our fit-values cannot be negative, this means that we'll at least want
        // to maximize the amount of edges in the final graph.
        // However, each vertex may have at most one incident edge.
        // The final bipartite graph will thus have L = min(n1, n2) edges.
        // This means that, if M = max(n1, n2), we have M!/(M-L)! different possible configurations
        // of the final graph (since for each vertex on the smaller side, there will be
        // first M options, next M-1, and so forth, until all edges have been set.)
        // We can enumerate over each of these by looking at the side with the fewest vertices,
        // and create an iterative tree of choices.

        int L;
        int M;
        int side;

        if (houses.size() <= households.size()) {
            L = houses.size();
            M = households.size();
            side = 0; //0 denoting the houses-side.
        }
        else {
            L = households.size();
            M = houses.size();
            side = 1; //1 denoting the households-side.
        }

        // create enumeration of all possibilities - recursively!

        HashSet<Integer> unclaimedNumbers = new HashSet<Integer>();
        int index = 1;
        while (index <= M) {
            unclaimedNumbers.add(index);
            index++;
        }
        TreeNode<Integer> possibilities = recursivelyEnumeratePossibilities(1, L, unclaimedNumbers);

        System.out.println("Got here!");
        // run each possibility

        // compare scores

        return (float) 0.0;

    }

    private TreeNode<Integer> recursivelyEnumeratePossibilities(int current, int total, HashSet<Integer> unclaimedNumbers) {
        TreeNode<Integer> currentNode = new TreeNode<Integer>(current);
        if(current == total) {
            for (Integer finalChoice : unclaimedNumbers) {
                TreeNode<Integer> leafNode = new TreeNode<Integer>(finalChoice);
                currentNode.addChild(leafNode);
//            Iterator<Integer> iterator = unclaimedNumbers.iterator();
//            if (iterator.hasNext()) {
//                int finalInteger = iterator.next();
            }
        }
        if(current < total) {
            for (Integer currentChoice : unclaimedNumbers) {
                HashSet<Integer> newUnclaimedNumbers = unclaimedNumbers;
                newUnclaimedNumbers.add(current);
                TreeNode<Integer> child = recursivelyEnumeratePossibilities(current+1, total, newUnclaimedNumbers);
                currentNode.addChild(child);
            }
        }

        return currentNode;
    }
}
