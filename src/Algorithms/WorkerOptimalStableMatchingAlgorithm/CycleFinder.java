package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import static Miscellaneous.DeepCloner.deepClone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class CycleFinder {

    private AsSubgraph<Integer, DefaultWeightedEdge> graph;
    List<Integer> vertices;
    // Takes vertex, returns state in {0 = unexplored | 1 = being explored}.
    private HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();
    private Set<Integer> householdIDsMovedByWOSMA;
    private int nilValue;

    public CycleFinder(AsSubgraph<Integer, DefaultWeightedEdge> graph, Set<Integer> householdIDsMovedByWOSMA, int nilValue) {
        this.graph = graph;
        this.vertices = new ArrayList<>(graph.vertexSet());
        for (int vertex : vertices) {
            states.put(vertex, 0);
        }
        this.householdIDsMovedByWOSMA = householdIDsMovedByWOSMA;
        this.nilValue = nilValue;
    }

    public List<Integer> findCycle() throws FullyExploredVertexDiscoveredException, InterruptedException {
        // Implements simple DFS. Returns the first cycle that it can find.
        // Two conditions need to hold:
        // 1) The cycle contains at least one strict edge.
        // 2) All non-strict edges that the cycle contains,
        //    must be sourced at a household that has been moved by WOSMA before (or at nil).
        Iterator<Integer> vertexIterator = vertices.iterator();
        List<Integer> cycle = null;
        // For each vertex...
        while (vertexIterator.hasNext()) {
            int vertex = vertexIterator.next();
            ArrayList<Integer> path = new ArrayList<Integer>();
            path.add(vertex);
            cycle = recursivelyFindCycle(path);
            if (cycle != null) {
                break;
            }
        }
        return cycle;
    }

    private List<Integer> recursivelyFindCycle(ArrayList<Integer> path) throws FullyExploredVertexDiscoveredException, InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        int vertex = path.get(path.size()-1);
        states.put(vertex, 1);
        Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(vertex);
        for (DefaultWeightedEdge edge : outgoingEdges) {
            // Condition 2 check. Only traverse this edge if it succeeds.
            if (edgeIsStrictOrSourcedAtMovedHousehold(edge)) {
                int neighbor = graph.getEdgeTarget(edge);
                // Is neighbor present in path (because it is currently being discovered)?
                // Then we only want to return the cycle in this path if it contains a strict edge.
                // Otherwise, do nothing. At any rate there is no need to traverse this neighbor.
                if (states.get(neighbor) == 1) {
                    int pathStart = path.indexOf(neighbor);
                    List<Integer> potentialCycle = path.subList(pathStart, path.size());
                    // Condition 1 check. Only return cycle if it contains a strict edge. Otherwise, continue looking.
                    if (containsStrictEdge(potentialCycle)) {
                        return potentialCycle;
                    }
                } else
                    // Has neighbor not been discovered yet? Then explore it.
                    if (states.get(neighbor) == 0) {
                    ArrayList<Integer> recursedPath = (ArrayList<Integer>) deepClone(path);
                    recursedPath.add(neighbor);
                    List<Integer> cycle = recursivelyFindCycle(recursedPath);
                    if (cycle != null) {
                        // Condition 1 check. Only return cycle if it contains a strict edge. Otherwise, continue looking.
                        if (containsStrictEdge(cycle)) {
                            return cycle;
                        }
                    }
                } else { // states.get(neighbor) == 2
                        continue; // We know that if we find a fully explored node, it won't give us a valid cycle.
                        // Note that fully explored nodes may yet be found.
                        // Example: Suppose that we have fully explored node w1 along all of its valid edges.
                        // Then it might still have an invalid edge w1->w2, which fails our check. However, there
                        // could be edges w2->w3->w1, which may all be valid. In this case, we might find w1 while
                        // exploring w2; but it is also true that the cycle w2->w3->w1 isn't valid, because w1->w2
                        // is not a valid edge. Since w1 has been fully explored along all valid outgoing edges,
                        // we won't find a valid cycle here, thus we can just ignore w1 while exploring w2.
                    }
            }
        }
        states.put(vertex, 2);
        return null;
    }

    private boolean edgeIsStrictOrSourcedAtMovedHousehold(DefaultWeightedEdge edge) {
        if (graph.getEdgeWeight(edge) > 0) {
            return true; // Edge is itself strict.
        }
        int source = graph.getEdgeSource(edge);
        if (source == nilValue) {
            return true; // Edge does not refer to the move of some household, so we don't mind.
        }
        if (householdIDsMovedByWOSMA.contains(source)) {
            return true; // Household that this edge's inclusion would move, has been moved before.
        }
        return false;
    }

    private boolean containsStrictEdge(List<Integer> potentialCycle) {
        for (int i = 0; i < potentialCycle.size(); i++) {
            int source = potentialCycle.get(i);
            int target = potentialCycle.get((i + 1) % potentialCycle.size());
            if (this.graph.getEdgeWeight(this.graph.getEdge(source, target)) > 0) {
                return true;
            }
        }
        return false;
    }

    public class FullyExploredVertexDiscoveredException extends Exception {
        public FullyExploredVertexDiscoveredException(String errorMessage) { super(errorMessage); }
    }

}
