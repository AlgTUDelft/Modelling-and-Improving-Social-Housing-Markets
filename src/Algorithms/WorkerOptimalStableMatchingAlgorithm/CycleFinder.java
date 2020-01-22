package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Matching.Matching;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class CycleFinder {

    private AsSubgraph<Integer, DefaultWeightedEdge> graph;
    List<Integer> vertices;
    // Takes vertex, returns state in {0 = unexplored | 1 = being explored | 2 = fully explored}.
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

    public List<Integer> findCycle() throws FullyExploredVertexDiscoveredException {
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
            Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(vertex);
            List<DefaultWeightedEdge> outgoingStrictEdges = outgoingEdges.stream()
                    .filter(e -> graph.getEdgeWeight(e) == 1)
                    .collect(Collectors.toList());
            Iterator<DefaultWeightedEdge> strictEdgeIterator = outgoingStrictEdges.iterator();

            // ...Iterate over all its strict outgoing edges to find a cycle.
            // This satisfies condition 1.
            // Furthermore, because every strict edge is in this manner tried,
            // we will never miss an eligible cycle.
            while (strictEdgeIterator.hasNext()) {
                states.put(vertex, 1);
                DefaultWeightedEdge edge = strictEdgeIterator.next();
                ArrayList<Integer> path = new ArrayList<Integer>();
                path.add(vertex);
                path.add(graph.getEdgeTarget(edge));
                cycle = recursivelyFindCycle(path);
                if (cycle != null) {
                    break;
                }
                for (int anyVertex : vertices) {
                    states.put(anyVertex, 0);
                }
            }
            if (cycle != null) {
                break;
            }
        }
        return cycle;
    }

    private List<Integer> recursivelyFindCycle(ArrayList<Integer> path) throws FullyExploredVertexDiscoveredException {
        int vertex = path.get(path.size()-1);
        states.put(vertex, 1);
        // TODO: Get rid of states, and instead check for cycles by exploring until we find a node,
        //  not whose state is 1, but rather whose value equals the path's initial value.
        //  However, even if we did that, we would still get stuck in an endless
        //  w1 -> w2 -> w3 -> w4 -> w3 -> w4 -> w3...
        //  In order to avoid this, we might like to maybe do the following:
        //  * Keep track of nodes' states, but only within a single recursion-head (as defined by findCycle()).
        //  * If we find a node equalling the initial node, that's a cycle!
        //  * If we find a neighbor with state 1 (but failing the above condition),
        //    then don't traverse that edge. It would only lead to a potentially bad cycle.
        Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(vertex);
        for (DefaultWeightedEdge edge : outgoingEdges) {
            // Condition 2 check. Only traverse this edge if it succeeds.
            if (edgeIsStrictOrSourcedAtMovedHousehold(edge)) {
                int neighbor = graph.getEdgeTarget(edge);
                if (path.get(0) == neighbor) {
                    return path;
                }
                if (states.get(neighbor) == 1) {
                    continue;
                } else if (states.get(neighbor) == 0) {
                    ArrayList<Integer> recursedPath = (ArrayList<Integer>) deepClone(path);
                    recursedPath.add(neighbor);
                    List<Integer> cycle = recursivelyFindCycle(recursedPath);
                    if (cycle != null) {
                        return cycle;
                    }
                }
            }
        }
        return null;
    }

    private boolean edgeIsStrictOrSourcedAtMovedHousehold(DefaultWeightedEdge edge) {
        if (graph.getEdgeWeight(edge) == 1) {
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

    public class FullyExploredVertexDiscoveredException extends Exception {
        public FullyExploredVertexDiscoveredException(String errorMessage) { super(errorMessage); }
    }

}
