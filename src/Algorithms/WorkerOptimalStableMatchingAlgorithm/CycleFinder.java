package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class CycleFinder {

    private AsSubgraph<Integer, DefaultEdge> graph;
    List<Integer> vertices;
    // Takes vertex, returns state in {0 = unexplored | 1 = being explored | 2 = fully explored}.
    private HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();


    public CycleFinder(AsSubgraph<Integer, DefaultEdge> graph) {
        this.graph = graph;
        this.vertices = new ArrayList<>(graph.vertexSet());
        for (int vertex : vertices) {
            states.put(vertex, 0);
        }
    }

    public List<Integer> findCycle() throws FullyExploredVertexDiscoveredException {
        // Implements simple DFS. Returns the first cycle that it can find.
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
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(vertex);
        for (DefaultEdge edge : outgoingEdges) {
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
