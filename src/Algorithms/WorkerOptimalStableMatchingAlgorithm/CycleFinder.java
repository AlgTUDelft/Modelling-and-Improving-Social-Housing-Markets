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
            states.put(vertex, 1);
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
                DefaultWeightedEdge edge = strictEdgeIterator.next();
                ArrayList<Integer> path = new ArrayList<Integer>();
                path.add(vertex);
                path.add(graph.getEdgeTarget(edge));
                cycle = recursivelyFindCycle(path);
                if (cycle != null) {
                    break;
                }
            }
            if (cycle != null) {
                break;
            }
            states.put(vertex, 2);
        }
        return cycle;
    }

    private List<Integer> recursivelyFindCycle(ArrayList<Integer> path) throws FullyExploredVertexDiscoveredException {
        int vertex = path.get(path.size()-1);
        states.put(vertex, 1);
        Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(vertex);
        for (DefaultWeightedEdge edge : outgoingEdges) {
            // Condition 2 check. Only traverse this edge if it succeeds.
            if (edgeIsStrictOrSourcedAtMovedHousehold(edge)) {
                int neighbor = graph.getEdgeTarget(edge);
                if (states.get(neighbor) == 0) {
                    ArrayList<Integer> recursedPath = (ArrayList<Integer>) deepClone(path);
                    recursedPath.add(neighbor);
                    List<Integer> cycle = recursivelyFindCycle(recursedPath);
                    if (cycle != null) {
                        return cycle;
                    }
                } else if (states.get(neighbor) == 1) {
                    int pathStart = path.indexOf(neighbor);
                    List<Integer> cycle = path.subList(pathStart, path.size()); // TODO: OutOfBounds error, or not?
                    return cycle;
                } else { // states.get(neighbor) == 2
                    throw new FullyExploredVertexDiscoveredException("Found a vertex that was supposed to" +
                            " be already fully explored.");
                }
            }
        }
        states.put(vertex, 2);
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
