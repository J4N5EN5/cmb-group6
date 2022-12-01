package movement.map;

import movement.UniHub;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniGraph {
    List<UniHub> uniHubs = new ArrayList<>();
    Graph<UniHub, DefaultEdge> graph;
    DijkstraShortestPath<UniHub, DefaultEdge> dijkstraShortestPath;

    public UniGraph(/* List<UniHub> uniHubs, List<Pair<UniHub,UniHub>> edges */) throws IOException, ParseException {
        this.graph = createUniGraph();
        dijkstraShortestPath = new DijkstraShortestPath<>(this.graph);
    }

    private List<UniHub> getPath(UniHub source, UniHub sink) {
        return dijkstraShortestPath.getPath(source, sink).getVertexList();
    }


    private Graph<UniHub, DefaultEdge> createUniGraph() throws IOException, ParseException {
        Graph<UniHub, DefaultEdge> uni = new SimpleGraph<>(DefaultEdge.class);

        Object uniLayoutObj = new JSONParser().parse(new FileReader("data/uni_layout.json"));
        JSONObject uniLayout = (JSONObject) uniLayoutObj;

        JSONArray vertices = (JSONArray) uniLayout.get("vertices");

        HashMap<Long, UniHub> verticesMap = createVertexMapByName(vertices);

        for (Map.Entry<Long, UniHub> vertex : verticesMap.entrySet()) {
            uni.addVertex(vertex.getValue());
        }


        JSONArray edges = (JSONArray) uniLayout.get("edges");

        for (Object o : edges) {
            JSONObject edge = (JSONObject) o;
            long firstId = (long) edge.get("firstVertex");
            long secondID = (long) edge.get("secondVertex");
            uni.addEdge(verticesMap.get(firstId), verticesMap.get(secondID));
        }

        return uni;
    }

    private HashMap<Long, UniHub> createVertexMapByName(JSONArray vertices) {
        HashMap<Long, UniHub> verticesMap = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++){
            JSONObject vertex = (JSONObject) vertices.get(i);
            UniHub uniHub = createUniHubFromJSON(vertex);
            verticesMap.put((Long) vertex.get("name"), uniHub);
        }

        return verticesMap;
    }

    private UniHub createUniHubFromJSON(JSONObject vertex) {
        long id = (long) vertex.get("id");
        String name = (String) vertex.get("name");
        UniHub uniHub = new UniHub(null, name);
        return uniHub;
    }

    public static void main(String[] args) throws IOException, ParseException {
        UniGraph uniGraph = new UniGraph();
    }
}
