package Algorithms.OptimizationAlgorithm;

import HousingMarket.House.House;
import HousingMarket.HouseAndHouseholdIDPair;
import HousingMarket.Household.Household;
import Matching.Matching;
import Matching.MatchingEvaluator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class OptimizationAlgorithm {
    private Matching matching;

    public OptimizationAlgorithm(Matching matching) {
        this.matching = matching;
    }

    public OptimizationAlgorithmResult optimizeN(int n)
            throws Matching.HouseLinkedToMultipleException,
            Matching.HouseLinkedToHouseException,
            Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseAlreadyMatchedException,
            Matching.HouseholdLinkedToMultipleException,
            MatchingEvaluator.HouseholdIncomeTooHighException,
            Matching.HouseholdAlreadyMatchedException {
        ArrayList<House> houses = (ArrayList<House>) deepClone(matching.getHouses());
        Collections.shuffle(houses);
        ArrayList<House> housesToOptimize = new ArrayList<House>(houses.subList(0,n));
        ArrayList<Household> householdsToOptimize = new ArrayList<Household>();
        for (int i = 0; i < n; i++) {
            House house = houses.get(i);
            housesToOptimize.add(house);
            Household household = matching.getHouseholdFromHouse(house.getID());
            householdsToOptimize.add(household);
        }
        return optimizeAll(housesToOptimize, householdsToOptimize, false);
    }

    public OptimizationAlgorithmResult optimizeAvailables()
            throws Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseholdLinkedToMultipleException,
            Matching.HouseLinkedToHouseException,
            Matching.HouseLinkedToMultipleException,
            MatchingEvaluator.HouseholdIncomeTooHighException,
            Matching.HouseholdAlreadyMatchedException,
            Matching.HouseAlreadyMatchedException {
        ArrayList<House> householdlessHouses = new ArrayList<House>(this.matching.getHouseholdlessHouses().stream().map(v -> this.matching.getHouse(v)).collect(Collectors.toList()));
        ArrayList<Household> houselessHouseholds = new ArrayList<Household>(this.matching.getHouselessHouseholds().stream().map(v -> this.matching.getHousehold(v)).collect(Collectors.toList()));
        return optimizeAll(householdlessHouses,
                houselessHouseholds, true);

    }
    public OptimizationAlgorithmResult optimizeAll(ArrayList<House> houses, ArrayList<Household> households, boolean printOutput)
            throws Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseLinkedToMultipleException,
            Matching.HouseLinkedToHouseException,
            MatchingEvaluator.HouseholdIncomeTooHighException,
            Matching.HouseAlreadyMatchedException,
            Matching.HouseholdAlreadyMatchedException {

        MatchingEvaluator evaluator = new MatchingEvaluator(matching);
        float oldResult = evaluator.evaluateTotal(printOutput);
        if(printOutput) {
            System.out.print("\n");
        }
        if (houses.size() == 0 || households.size() == 0) {
            System.out.println("Either there were no houseless households, or no householdless houses. Algorithm made no changes.");
            OptimizationAlgorithmResult optimizationAlgorithmResult = new OptimizationAlgorithmResult(-1, -1, 0, 0, 0);
            return optimizationAlgorithmResult;
        }
        // Dissolve connections.
        for (House house : houses) {
            Household household = matching.getHouseholdFromHouse(house.getID());
            if (household != null) {
                matching.disconnect(house.getID(), household.getID());
            }
        }
        for (Household household : households) {
            House house = matching.getHouseFromHousehold(household.getID());
            if (house != null) {
                matching.disconnect(house.getID(), household.getID());
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
        SourcesSide side;
        // create enumeration of all possibilities - recursively!
        ArrayList<Integer> sources = new ArrayList<Integer>();
        HashSet<Integer> unclaimedTargets = new HashSet<Integer>();

        if (houses.size() <= households.size()) {
            L = houses.size();
            M = households.size();
            side = SourcesSide.HOUSES;
            for (House house : houses) {
                sources.add(house.getID());
            }
            for (Household household : households) {
                unclaimedTargets.add(household.getID());
            }
        }
        else {
            L = households.size();
            M = houses.size();
            side = SourcesSide.HOUSEHOLDS;
            for (Household household : households) {
                sources.add(household.getID());
            }
            for (House house : houses) {
                unclaimedTargets.add(house.getID());
            }
        }

        TreeNode<HouseAndHouseholdIDPair> emptyPossibilitiesRoot = new TreeNode<HouseAndHouseholdIDPair>(
                new HouseAndHouseholdIDPair(-1, -1));

        TreeNode<HouseAndHouseholdIDPair> filledPossibilitiesRoot
                = recursivelyEnumeratePossibilities(emptyPossibilitiesRoot,unclaimedTargets,sources, side);



        // try each possibility
        Matching bestMatching = recursivelyTryPossibilities(matching, filledPossibilitiesRoot);
        float newResult = new MatchingEvaluator(bestMatching).evaluateTotal(printOutput);
        if (printOutput) {
            System.out.print("\n");
        }

        // compare scores
        if (newResult < oldResult) {
            // TODO: We got here once...
            System.err.println("Error! The best-found matching was worse than the given one.");
        }

        float percentage = (side == SourcesSide.HOUSES) ? (float) L / matching.getHouses().size() : (float) L / matching.getHouseholds().size();
        if (printOutput) {
            String text = (side == SourcesSide.HOUSES) ? " houses" : " households";
            System.out.println("Old score was: " + oldResult + ". New score is: " + newResult + ".\n" +
                    "Thus the given matching was improved by " + (newResult - oldResult) / oldResult * 100 + "%.\n" +
                    "Note that there were " + L + " (= " + percentage * 100 + "%) " + text + " that we could rewire.");
        }
        OptimizationAlgorithmResult optimizationAlgorithmResult = new OptimizationAlgorithmResult(oldResult, newResult, (newResult - oldResult)/oldResult * 100, L, percentage*100);
        return optimizationAlgorithmResult;

    }

    private TreeNode<HouseAndHouseholdIDPair> recursivelyEnumeratePossibilities(TreeNode<HouseAndHouseholdIDPair> root,
                                                                                HashSet<Integer> unclaimedTargets,
                                                                                ArrayList<Integer> sources, SourcesSide side) {
        for (int t : unclaimedTargets) {
            HashSet<Integer> newUnclaimedTargets = new HashSet<Integer>(unclaimedTargets);
            newUnclaimedTargets.remove(t);
            TreeNode<HouseAndHouseholdIDPair> node = auxiliaryRecursivelyEnumeratePossibilities(0, t, sources,
                    newUnclaimedTargets, side);
            root.addChild(node);
        }
        return root;
    }

    private TreeNode<HouseAndHouseholdIDPair> auxiliaryRecursivelyEnumeratePossibilities(int currentSourceIndex, int currentTarget,
                                                                                         ArrayList<Integer> sources, HashSet<Integer> unclaimedTargets, SourcesSide side) {
        HouseAndHouseholdIDPair current;
        if (side == SourcesSide.HOUSES) {
            current = new HouseAndHouseholdIDPair(sources.get(currentSourceIndex), currentTarget);
        } else { // side = SourcesSide.HOUSEHOLDS.
            current = new HouseAndHouseholdIDPair(currentTarget, sources.get(currentSourceIndex)); }

        TreeNode<HouseAndHouseholdIDPair> currentNode = new TreeNode<HouseAndHouseholdIDPair>(current);

        if(currentSourceIndex < sources.size() - 1) {
            for (int newChoice : unclaimedTargets) {
                HashSet<Integer> newUnclaimedTargets = new HashSet<Integer>(unclaimedTargets);
                newUnclaimedTargets.remove(newChoice);
                TreeNode<HouseAndHouseholdIDPair> child = auxiliaryRecursivelyEnumeratePossibilities(currentSourceIndex+1,
                        newChoice, sources, newUnclaimedTargets, side);
                currentNode.addChild(child);
            }
        } // else do nothing

        return currentNode;
    }

    private Matching recursivelyTryPossibilities(Matching matching, TreeNode<HouseAndHouseholdIDPair> root)
            throws MatchingEvaluator.HouseholdIncomeTooHighException,
            Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException,
            Matching.HouseAlreadyMatchedException,
            Matching.HouseholdAlreadyMatchedException {
        float highScore = (float) 0.0;
        Matching currentMatching = (Matching) deepClone(matching);
        Matching bestMatching = (Matching) deepClone(matching);
        for (TreeNode<HouseAndHouseholdIDPair> child : root.getChildren()) {
            Matching bestChildMatching = auxiliaryRecursivelyTryPossibilities(currentMatching, highScore, child);
            float childScore = new MatchingEvaluator(bestChildMatching).evaluateTotal(false);
            if (childScore > highScore) {
                highScore = childScore;
                bestMatching = bestChildMatching;
            }
        }
        return bestMatching;
    }

    private Matching auxiliaryRecursivelyTryPossibilities(Matching matching, float highScore, TreeNode<HouseAndHouseholdIDPair> currentNode)
            throws Matching.HouseholdAlreadyMatchedException,
            Matching.HouseAlreadyMatchedException,
            MatchingEvaluator.HouseholdIncomeTooHighException,
            Matching.HouseholdLinkedToMultipleException,
            Matching.HouseholdLinkedToHouseholdException {

          // TODO: Check if description still accurate.
        // We have a tree enumerating all possible combinations of edges.
        // In the beginning, no possible edge has been realized.
        // We note that since the minimum fit-value is 0,
        // given a candidate matching, its score will never be lowered if we realize one of its edges.
        // For simplicity's sake, let's suppose that side = 0.
        // We perform a breadth-first search of this tree.
        // Base step: At some base node _leaf_, we first check which node we're at,
        // and next we check which node it should connect to. We then make this choice of connection,
        // and return this new matching. By definition, this new matching is the best matching at this stage.
        // Recursion step: At some node _n_, we first check which node we're at.
        // We then call the recursion formula to find, for each of n's children, the best matching that could be gotten
        // at said child node in the tree. We then pick the best of those matchings, and return it.
        // This completes the recursion. We end up with the best matching that could be found.

        HouseAndHouseholdIDPair data = currentNode.getData();
        Matching modifiedMatching = (Matching) deepClone(matching);
        modifiedMatching.connect(data.getHouseID(), data.getHouseholdID());
        Matching bestMatching = modifiedMatching;
        if (currentNode.hasChildren()) {
            for (TreeNode<HouseAndHouseholdIDPair> child : currentNode.getChildren()) {
                Matching bestChildMatching = auxiliaryRecursivelyTryPossibilities(modifiedMatching, highScore, child);
                float childScore = new MatchingEvaluator(bestChildMatching).evaluateTotal(false);
                if (childScore > highScore) {
                    highScore = childScore;
                    bestMatching = bestChildMatching;
                }
            }
        }
        return bestMatching;
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
