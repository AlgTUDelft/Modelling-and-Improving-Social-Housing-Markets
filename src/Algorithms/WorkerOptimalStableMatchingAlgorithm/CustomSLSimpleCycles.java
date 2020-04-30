//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
// Adapted from SzwarcfiterLauerSimpleCycles class from JgraphT package.

package Algorithms.WorkerOptimalStableMatchingAlgorithm;

import java.util.*;
import java.lang.Integer;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.graph.DefaultWeightedEdge;


public class CustomSLSimpleCycles {
    private Graph<Integer, DefaultWeightedEdge> graph;
    private List<List<Integer>> cycles = null;
    private Integer[] iToInteger = null;
    private Map<Integer, java.lang.Integer> vToI = null;
    private Map<Integer, Set<Integer>> bSets = null;
    private ArrayDeque<Integer> stack = null;
    private Set<Integer> marked = null;
    private Map<Integer, Set<Integer>> removed = null;
    private int[] position = null;
    private boolean[] reach = null;
    private List<Integer> startVertices = null;
    private int deniedCycles = 0;

    public CustomSLSimpleCycles() {
    }

    public CustomSLSimpleCycles(Graph<Integer, DefaultWeightedEdge> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
    }

    public Graph<Integer, DefaultWeightedEdge> getGraph() {
        return this.graph;
    }

    public void setGraph(Graph<Integer, DefaultWeightedEdge> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
    }

    public List<List<Integer>> findSimpleCycles() throws InterruptedException {
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        } else {
            this.initState();
            KosarajuStrongConnectivityInspector<Integer, DefaultWeightedEdge> inspector = new KosarajuStrongConnectivityInspector(this.graph);
            List<Set<Integer>> sccs = inspector.stronglyConnectedSets();
            Iterator var3 = sccs.iterator();

            while(var3.hasNext()) {
                Set<Integer> scc = (Set)var3.next();
                int maxInDegree = -1;
                Integer startVertex = null;
                Iterator var7 = scc.iterator();

                while(var7.hasNext()) {
                    Integer integer = (Integer) var7.next();
                    int inDegree = this.graph.inDegreeOf(integer);
                    if (inDegree > maxInDegree) {
                        maxInDegree = inDegree;
                        startVertex = integer;
                    }
                }

                this.startVertices.add(startVertex);
            }

            var3 = this.startVertices.iterator();

            while(var3.hasNext()) {
                Integer vertex = (Integer) var3.next();
                this.cycle(this.toI(vertex), 0);
            }

            List<List<Integer>> result = this.cycles;
            this.clearState();
            return result;
        }
    }

    public List<Integer> findSimpleCycle() throws InterruptedException {
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        } else {
            this.initState();
            KosarajuStrongConnectivityInspector<Integer, DefaultWeightedEdge> inspector = new KosarajuStrongConnectivityInspector(this.graph);
            List<Set<Integer>> sccs = inspector.stronglyConnectedSets();
            Iterator var3 = sccs.iterator();

            while(var3.hasNext()) {
                Set<Integer> scc = (Set)var3.next();
                int maxInDegree = -1;
                Integer startVertex = null;
                Iterator var7 = scc.iterator();

                while(var7.hasNext()) {
                    Integer integer = (Integer) var7.next();
                    int inDegree = this.graph.inDegreeOf(integer);
                    if (inDegree > maxInDegree) {
                        maxInDegree = inDegree;
                        startVertex = integer;
                    }
                }

                this.startVertices.add(startVertex);
            }

            var3 = this.startVertices.iterator();

            while(var3.hasNext() && cycles.isEmpty()) {
                Integer vertex = (Integer) var3.next();
                this.cycle(this.toI(vertex), 0);
            }

            List<List<Integer>> cycles = this.cycles;
//            List<List<Integer>> result = this.cycles;
            this.clearState();
            if (cycles.isEmpty()) {
                return null;
            } else {
                return cycles.get(0);
            }
        }
    }

    private boolean cycle(int v, int q) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        boolean foundCycle = false;
        Integer vInteger = this.toV(v);
        this.marked.add(vInteger);
        this.stack.push(vInteger);
        int t = this.stack.size();
        this.position[v] = t;
        if (!this.reach[v]) {
            q = t;
        }

        Set<Integer> avRemoved = this.getRemoved(vInteger);
        Set<DefaultWeightedEdge> edgeSet = this.graph.outgoingEdgesOf(vInteger);
        Iterator var8 = edgeSet.iterator();

        while(true) {
            while(true) {
                Object wV;
                do {
                    if (!var8.hasNext()) {
                        this.stack.pop();
                        if (foundCycle) {
                            this.unmark(v);
                        }

                        this.reach[v] = true;
                        this.position[v] = this.graph.vertexSet().size();
                        return foundCycle;
                    }

                    DefaultWeightedEdge defaultWeightedEdge = (DefaultWeightedEdge) var8.next();
                    wV = this.graph.getEdgeTarget(defaultWeightedEdge);
                } while(avRemoved.contains(wV));

                int w = this.toI((Integer) wV);
                if (!this.marked.contains(wV)) {
                    boolean gotCycle = this.cycle(w, q);
                    if (gotCycle) {
                        foundCycle = true;
                    } else {
                        this.noCycle(v, w);
                    }
                } else if (this.position[w] > q) {
                    this.noCycle(v, w);
                } else {
                    foundCycle = true;
                    List<Integer> cycle = new ArrayList();
                    Iterator it = this.stack.descendingIterator();

                    Object current;
                    while(it.hasNext()) {
                        current = it.next();
                        if (wV.equals(current)) {
                            break;
                        }
                    }

                    cycle.add((Integer) wV);

                    while(it.hasNext()) {
                        current = it.next();
                        cycle.add((Integer) current);
                        if (current.equals(vInteger)) {
                            break;
                        }
                    }

                    // this.cycles.add(cycle);
                    if (calculateCycleScore(cycle) > 0.0001) {
                        this.cycles.add(cycle);
                    } else { deniedCycles++; }
                }
            }
        }
    }

    private void noCycle(int x, int y) {
        Integer xInteger = this.toV(x);
        Integer yInteger = this.toV(y);
        Set<Integer> by = this.getBSet(yInteger);
        Set<Integer> axRemoved = this.getRemoved(xInteger);
        by.add(xInteger);
        axRemoved.add(yInteger);
    }

    private void unmark(int x) {
        Integer xInteger = this.toV(x);
        this.marked.remove(xInteger);
        Set<Integer> bx = this.getBSet(xInteger);
        Iterator var4 = bx.iterator();

        while(var4.hasNext()) {
            Integer yInteger = (Integer) var4.next();
            Set<Integer> ayRemoved = this.getRemoved(yInteger);
            ayRemoved.remove(xInteger);
            if (this.marked.contains(yInteger)) {
                this.unmark(this.toI(yInteger));
            }
        }

        bx.clear();
    }

    private void initState() {
        this.cycles = new ArrayList();
        this.iToInteger = Arrays.copyOf(this.graph.vertexSet().toArray(), this.graph.vertexSet().size(), Integer[].class);
        this.vToI = new HashMap();
        this.bSets = new HashMap();
        this.stack = new ArrayDeque();
        this.marked = new HashSet();
        this.removed = new HashMap();
        int size = this.graph.vertexSet().size();
        this.position = new int[size];
        this.reach = new boolean[size];
        this.startVertices = new ArrayList();

        for(int i = 0; i < this.iToInteger.length; ++i) {
            this.vToI.put(this.iToInteger[i], i);
        }

    }

    private void clearState() {
        this.cycles = null;
        this.iToInteger = null;
        this.vToI = null;
        this.bSets = null;
        this.stack = null;
        this.marked = null;
        this.removed = null;
        this.position = null;
        this.reach = null;
        this.startVertices = null;
    }

    private java.lang.Integer toI(Integer integer) {
        return (java.lang.Integer)this.vToI.get(integer);
    }

    private Integer toV(int i) {
        return this.iToInteger[i];
    }

    private Set<Integer> getBSet(Integer integer) {
        return (Set)this.bSets.computeIfAbsent(integer, (k) -> {
            return new HashSet();
        });
    }

    private Set<Integer> getRemoved(Integer integer) {
        return (Set)this.removed.computeIfAbsent(integer, (k) -> {
            return new HashSet();
        });
    }

    public float calculateCycleScore(List<java.lang.Integer> cycle) {
        ArrayList<org.jgrapht.graph.DefaultWeightedEdge> edges = new ArrayList<org.jgrapht.graph.DefaultWeightedEdge>(cycle.size());
        for (int i = 0; i < cycle.size(); i++) {
            int source = cycle.get(i);
            int target = cycle.get((i + 1) % cycle.size());
            org.jgrapht.graph.DefaultWeightedEdge edge = graph.getEdge(source, target);
            edges.add(i, edge);
        }
        return sumWeightOfEdges(edges);
    }

    public float sumWeightOfEdges(ArrayList<org.jgrapht.graph.DefaultWeightedEdge> edges) {
        float score = 0;
        for (org.jgrapht.graph.DefaultWeightedEdge edge : edges) {
            score = score + (float) graph.getEdgeWeight(edge);
        }
        return score;
    }

    public int getDeniedCyclesCount() {
        return deniedCycles;
    }
}
