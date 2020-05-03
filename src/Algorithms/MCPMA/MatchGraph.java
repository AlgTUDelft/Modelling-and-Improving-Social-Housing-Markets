package Algorithms.MCPMA;

import HousingMarket.HousingMarketVertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.Serializable;
import java.util.Set;

public class MatchGraph implements Serializable {

    private SimpleGraph<HousingMarketVertex, DefaultEdge> matchGraph;

    public MatchGraph() {
        this.matchGraph = new SimpleGraph<>(DefaultEdge.class);
    }

    public void addVertex(HousingMarketVertex vertex) {
        this.matchGraph.addVertex(vertex);
    }

    public Set<DefaultEdge> getEdges() {
        return this.matchGraph.edgeSet();
    }

    public HousingMarketVertex getEdgeSource(DefaultEdge edge) {
        return this.matchGraph.getEdgeSource(edge);
    }

    public HousingMarketVertex getEdgeTarget(DefaultEdge edge) {
        return this.matchGraph.getEdgeTarget(edge);
    }

    public int getEdgeCount() {
        return this.matchGraph.edgeSet().size();
    }

    public Set<DefaultEdge> edgesOf(HousingMarketVertex vertex) {
        return this.matchGraph.edgesOf(vertex);
    }

    public boolean containsEdge(HousingMarketVertex source, HousingMarketVertex target) {
        return this.matchGraph.containsEdge(source, target);
    }

    public void addEdge(HousingMarketVertex source, HousingMarketVertex target) {
        this.matchGraph.addEdge(source, target);
    }

    public void removeEdge(HousingMarketVertex source, HousingMarketVertex target) {
        this.matchGraph.removeEdge(source, target);
    }

    public void removeVertex(HousingMarketVertex vertex) {
        this.matchGraph.removeVertex(vertex);
    }

    @Override
    public String toString() {
        return matchGraph.vertexSet() + ", " + matchGraph.edgeSet();
    }
}
