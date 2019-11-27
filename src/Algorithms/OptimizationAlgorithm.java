package Algorithms;

import HousingMarket.House.House;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;
import TreeNode.TreeNode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class OptimizationAlgorithm {
    private Matching matching;

    public OptimizationAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public float optimizeAvailables()
            throws Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseholdLinkedToMultipleException,
            Matching.HouseLinkedToHouseException,
            Matching.HouseLinkedToMultipleException,
            MatchingEvaluator.HouseholdIncomeTooHighException,
            Matching.HouseholdAlreadyMatchedException,
            Matching.HouseAlreadyMatchedException {
        return optimizeAll(this.matching.getHouseholdlessHouses(),
                this.matching.getHouselessHouseholds());

    }
    public float optimizeAll(ArrayList<House> houses, ArrayList<Household> households)
            throws Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseLinkedToMultipleException,
            Matching.HouseLinkedToHouseException,
            MatchingEvaluator.HouseholdIncomeTooHighException,
            Matching.HouseAlreadyMatchedException,
            Matching.HouseholdAlreadyMatchedException {
        // TODO: Optimize such that not the summed individual total score is used,
        //  but the actual weighted total.

        MatchingEvaluator evaluator = new MatchingEvaluator(matching);
        float oldResult = evaluator.evaluateTotal(true);
        System.out.print("\n");
        if (houses.size() == 0 || households.size() == 0) {
            System.out.println("Either there were no houseless households, or no householdless houses. Algorithm made no changes.");
            return oldResult;
        }
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
        int index = 0;
        while (index < M) {
            index++;
            unclaimedNumbers.add(index);
        }
        TreeNode<Integer> possibilities = recursivelyEnumeratePossibilities(0, L, 0, unclaimedNumbers);

        // try each possibility
        Matching bestMatching = recursivelyTryPossibilities(matching, 0, possibilities, 0, side);
        float newResult = new MatchingEvaluator(bestMatching).evaluateTotal(true);
        System.out.print("\n");

        // compare scores
        if (newResult < oldResult) {
            System.err.println("Error! The best-found matching was worse than the given one.");
        }
        System.out.println("Old score was: " + oldResult + ". New score is: " + newResult + ". " +
                "Thus the given matching was improved by " + (newResult - oldResult)/oldResult * 100 + "%.");
        return newResult;

    }


    private TreeNode<Integer> recursivelyEnumeratePossibilities(int currentIndex, int total, int currentChoice, HashSet<Integer> unclaimedNumbers) {
        TreeNode<Integer> currentNode = new TreeNode<Integer>(currentChoice);
        if(currentIndex == total) {
            for (Integer finalChoice : unclaimedNumbers) {
                TreeNode<Integer> leafNode = new TreeNode<Integer>(finalChoice);
                currentNode.addChild(leafNode);
            }
        }
        else if(currentIndex < total) {
            for (Integer newChoice : unclaimedNumbers) {
                HashSet<Integer> newUnclaimedNumbers = new HashSet<Integer>(unclaimedNumbers);
                newUnclaimedNumbers.remove(newChoice);
                TreeNode<Integer> child = recursivelyEnumeratePossibilities(currentIndex+1,
                        total, newChoice, newUnclaimedNumbers);
                currentNode.addChild(child);
            }
        }

        return currentNode;
    }


    private Matching recursivelyTryPossibilities(Matching matching, float highScore, TreeNode<Integer> currentNode,
                                                 Integer currentIndex, Integer side)
            throws Matching.HouseholdAlreadyMatchedException,
            Matching.HouseAlreadyMatchedException,
            Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException,
            MatchingEvaluator.HouseholdIncomeTooHighException {
        if (!currentNode.hasChildren()) {
            if (currentIndex.equals(0)) {
                System.out.println("Possibilities-tree was empty.");
                return matching;
            } else {
                Matching newMatching = (Matching) deepClone(matching);
                if (side.equals(0)) {
                    newMatching.connect(newMatching.getHouseholdlessHouse(currentIndex-1),
                            newMatching.getHouselessHousehold(currentNode.getData()-1));
                }
                if (side.equals(1)) {
                    newMatching.connect(newMatching.getHouseholdlessHouse(currentNode.getData()-1),
                            newMatching.getHouselessHousehold(currentIndex-1));
                }
                float score = new MatchingEvaluator(newMatching).evaluateTotal(false);
                if (score > highScore) {
                    return newMatching;
                } else { return matching; } // TODO: Wait, is this what we want to do?
            }
        }
        else {
            Matching bestCurrentMatching = matching;
            float highestScoreFound = highScore;
            for (TreeNode<Integer> child : currentNode.getChildren()) {
                Matching bestFoundMatching = recursivelyTryPossibilities(matching, highScore, child, currentIndex+1, side);
                float bestfoundScore = new MatchingEvaluator(bestFoundMatching).evaluateTotal(false);
                if (bestfoundScore > highestScoreFound) {
                    highestScoreFound = bestfoundScore;
                    bestCurrentMatching = bestFoundMatching;
                }
            }
            return bestCurrentMatching;
        }
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
