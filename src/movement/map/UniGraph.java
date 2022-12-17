package movement.map;

import movement.UniHub;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.List;

public class UniGraph {
    private final DijkstraShortestPath<UniHub, DefaultEdge> dijkstraShortestPath;

    public UniGraph(List<UniHub> vertices, List<Pair<UniHub, UniHub>> edges) {
        Graph<UniHub, DefaultEdge> graph = createUniGraph(vertices, edges);
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public List<UniHub> getPath(UniHub source, UniHub sink) {
        return dijkstraShortestPath.getPath(source, sink).getVertexList();
    }


    private Graph<UniHub, DefaultEdge> createUniGraph(List<UniHub> uniHubs, List<Pair<UniHub, UniHub>> edges) {
        Graph<UniHub, DefaultEdge> uni = new SimpleGraph<>(DefaultEdge.class);

        for (UniHub uniHub : uniHubs) {
            uni.addVertex(uniHub);
        }

        for (Pair<UniHub, UniHub> edge : edges) {
            UniHub firstVertex = edge.getFirst();
            UniHub secondVertex = edge.getSecond();
            uni.addEdge(firstVertex, secondVertex);
        }

        return uni;
    }
}
