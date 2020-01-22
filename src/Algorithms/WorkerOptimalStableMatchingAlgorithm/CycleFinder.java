package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import Matching.Matching;
import org.apache.poi.ss.formula.functions.Index;
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

    public List<Integer> findCycle() throws FullyExploredVertexDiscoveredException {
        // TODO: I think, but am not sure, that this traverses a lot of strict edges multiple times.
        //  Or rather, it does this only when it can't find cycles.
        //  It will go w1->w2->w3->w4->w5->w6->w7 --- no cycle; where w1->w2 is strict.
        //  Then later, if w3->w4 is strict, it might still go and try w3->w4->w5->w6->w7 all over again.
        //  This makes it a very slow algorithm! I can fix this.
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
                states.put(vertex, 0);
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
        Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(vertex);
        for (DefaultWeightedEdge edge : outgoingEdges) {
            // Condition 2 check. Only traverse this edge if it succeeds.
            if (edgeIsStrictOrSourcedAtMovedHousehold(edge)) {
                int neighbor = graph.getEdgeTarget(edge);
                // Does neighbor complete the path at its start? Then return cycle; by definition
                // it contains a strict edge, namely its first edge.
                if (path.get(0) == neighbor) {
                    return path;
                }
                // Is neighbor present in path, but crucially it is not the first node?
                // Then we only want to return the cycle in this path if it contains a strict edge.
                // Otherwise, do nothing. At any rate there is no need to traverse this neighbor.
                if (states.get(neighbor) == 1) {
                    int pathStart = path.indexOf(neighbor);
                    List<Integer> potentialCycle = path.subList(pathStart, path.size());
                    if (containsStrictEdge(potentialCycle)) {
                        return potentialCycle;
                    }
                    else { continue; }
                } else
                    // Has neighbor not been discovered yet? Then explore it.
                    if (states.get(neighbor) == 0) {
                    ArrayList<Integer> recursedPath = (ArrayList<Integer>) deepClone(path);
                    recursedPath.add(neighbor);
                    List<Integer> cycle = recursivelyFindCycle(recursedPath);
                    if (cycle != null) {
                        return cycle;
                    }
                }
            }
        }
        states.put(vertex, 0);
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

    private boolean containsStrictEdge(List<Integer> potentialCycle) {
        for (int i = 0; i < potentialCycle.size(); i++) {
            int source = potentialCycle.get(i);
            int target = potentialCycle.get((i + 1) % potentialCycle.size());
            if (this.graph.getEdgeWeight(this.graph.getEdge(source, target)) == 1) {
                return true;
            }
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
