package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Matching.Matching;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class CycleFinder {

    private AsSubgraph<Integer, DefaultWeightedEdge> graph;
    List<Integer> vertices;
    // Takes vertex, returns state in {0 = unexplored | 1 = being explored | 2 = fully explored}.
    private HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();
    private Set<Integer> householdIDsMovedByWOSMA;

    public CycleFinder(AsSubgraph<Integer, DefaultWeightedEdge> graph, Set<Integer> householdIDsMovedByWOSMA) {
        this.graph = graph;
        this.vertices = new ArrayList<>(graph.vertexSet());
        for (int vertex : vertices) {
            states.put(vertex, 0);
        }
        this.householdIDsMovedByWOSMA = householdIDsMovedByWOSMA;
    }

    public List<Integer> findCycle() throws FullyExploredVertexDiscoveredException {
        // Implements simple DFS. Returns the first cycle that it can find.
        // Two conditions need to hold:
        // 1) The cycle contains at least one strict edge.
        // 2) All non-strict edges that the cycle contains,
        //    must be sourced at a household that has been moved by WOSMA before (or at nil).
        // TODO: Ensure both conditions hold.
        Iterator<Integer> iterator = vertices.iterator();
        List<Integer> cycle = null;
        while (iterator.hasNext()) {
            int vertex = iterator.next();
            ArrayList<Integer> path = new ArrayList<Integer>();
            path.add(vertex);
            cycle = recursivelyFindCycle(path);
            if (cycle != null) {
                break;
            }
        }
        return cycle;
    }

    private List<Integer> recursivelyFindCycle(ArrayList<Integer> path) throws FullyExploredVertexDiscoveredException {
        int vertex = path.get(path.size()-1);
        states.put(vertex, 1);
        Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(vertex);
        for (DefaultWeightedEdge edge : outgoingEdges) {
            int neighbor = graph.getEdgeTarget(edge);
            if (states.get(neighbor) == 0) {
                ArrayList<Integer> recursedPath = (ArrayList<Integer>) deepClone(path);
                recursedPath.add(neighbor);
                List<Integer> cycle = recursivelyFindCycle(recursedPath);
                if (cycle != null) {
                    return cycle;
                }
            }
            else if (states.get(neighbor) == 1) {
                int pathStart = path.indexOf(neighbor);
                List<Integer> cycle = path.subList(pathStart, path.size()); // TODO: OutOfBounds error, or not?
                return cycle;
            }
            else { // states.get(neighbor) == 2
                throw new FullyExploredVertexDiscoveredException("Found a vertex that was supposed to" +
                        " be already fully explored.");
            }
        }
        states.put(vertex, 2);
        return null;
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
