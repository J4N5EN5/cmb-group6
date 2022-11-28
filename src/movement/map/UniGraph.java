package movement.map;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.List;

public class UniGraph {

    DijkstraShortestPath<String, DefaultEdge> dijkstraShortestPath;

    public UniGraph() {
        Graph<String, DefaultEdge> uniGraph = createUniGraph();
         dijkstraShortestPath = new DijkstraShortestPath<>(uniGraph);
    }

    private List<String> getPath(String source, String sink) {
        return dijkstraShortestPath.getPath(source, sink).getVertexList();
    }


    private Graph<String, DefaultEdge> createUniGraph()
    {
        Graph<String, DefaultEdge> uni = new SimpleGraph<>(DefaultEdge.class);

        // Entry points
        String metro = "metro";
        String parkplatz = "parkplatz";

        // Lecture halls
        String interims = "interims";
        String hs1 = "hs1";
        String hs2 = "hs2";
        String hs3 = "hs3";

        // Profs offices
        String O_04 = "O-04";
        String O_05 = "O-05";
        String O_06 = "O-05";
        String O_07 = "O-07";
        String O_08 = "O-08";
        String O_09 = "O-09";
        String O_10 = "O-10";
        String O_11 = "O-11";
        String O_12 = "O-12";
        String O_13 = "O-13";

        // Free-time
        String basketball = "basketball";
        String volleyball = "volleyball";
        String terrace = "terrace";
        String mensa = "mensa";

        // Study
        String library = "library";
        String rechnerhalle = "rechnerhalle";

        // routing points
        String R_01 = "R-01";
        String R_02 = "R-02";
        String R_03 = "R-03";
        String R_04 = "R-04";
        String R_05 = "R-05";
        String R_06 = "R-06";
        String R_07 = "R-07";
        String R_08 = "R-08";
        String R_09 = "R-09";
        String R_10 = "R-10";
        String R_11 = "R-11";
        String R_12 = "R-12";
        String R_13 = "R-13";
        String R_14 = "R-14";
        String R_15 = "R-15";
        String R_16 = "R-16";
        String R_17 = "R-17";
        String R_18 = "R-18";
        String R_19 = "R-19";


        // VERTICES
        uni.addVertex(metro);
        uni.addVertex(parkplatz);
        uni.addVertex(interims);
        uni.addVertex(hs1);
        uni.addVertex(hs2);
        uni.addVertex(hs3);
        uni.addVertex(O_04);
        uni.addVertex(O_05);
        uni.addVertex(O_06);
        uni.addVertex(O_07);
        uni.addVertex(O_08);
        uni.addVertex(O_09);
        uni.addVertex(O_10);
        uni.addVertex(O_11);
        uni.addVertex(O_12);
        uni.addVertex(O_13);
        uni.addVertex(basketball);
        uni.addVertex(volleyball);
        uni.addVertex(terrace);
        uni.addVertex(mensa);
        uni.addVertex(library);
        uni.addVertex(rechnerhalle);
        uni.addVertex(R_01);
        uni.addVertex(R_02);
        uni.addVertex(R_03);
        uni.addVertex(R_04);
        uni.addVertex(R_05);
        uni.addVertex(R_06);
        uni.addVertex(R_07);
        uni.addVertex(R_08);
        uni.addVertex(R_09);
        uni.addVertex(R_10);
        uni.addVertex(R_11);
        uni.addVertex(R_12);
        uni.addVertex(R_13);
        uni.addVertex(R_14);
        uni.addVertex(R_15);
        uni.addVertex(R_16);
        uni.addVertex(R_17);
        uni.addVertex(R_18);
        uni.addVertex(R_19);


        // add edges to create a circuit
        uni.addEdge(metro, R_01);
        uni.addEdge(R_01, interims);
        uni.addEdge(R_01, R_02);
        uni.addEdge(R_02, R_03);
        uni.addEdge(R_03, basketball);
        uni.addEdge(R_03, volleyball);
        uni.addEdge(R_02, R_04);
        uni.addEdge(R_04, R_05);
        uni.addEdge(R_05, R_06);
        uni.addEdge(R_05, R_09);
        uni.addEdge(R_05, R_10);
        uni.addEdge(R_05, R_11);
        uni.addEdge(R_05, hs1);
        uni.addEdge(R_05, hs2);
        uni.addEdge(R_05, hs3);
        uni.addEdge(R_06, R_07);
        uni.addEdge(R_06, R_08);
        uni.addEdge(R_07, parkplatz);
        uni.addEdge(R_08, O_04);
        uni.addEdge(R_09, O_06);
        uni.addEdge(R_10, O_05);
        uni.addEdge(R_11, R_10);
        uni.addEdge(R_11, R_12);
        uni.addEdge(R_11, R_13);
        uni.addEdge(R_11, R_14);
        uni.addEdge(R_11, hs3);
        uni.addEdge(R_11, rechnerhalle);
        uni.addEdge(R_11, terrace);
        uni.addEdge(R_11, mensa);
        uni.addEdge(R_12, O_08);
        uni.addEdge(R_13, O_07);
        uni.addEdge(R_14, R_13);
        uni.addEdge(R_14, R_15);
        uni.addEdge(R_14, R_16);
        uni.addEdge(R_14, R_17);
        uni.addEdge(R_14, R_18);
        uni.addEdge(R_16, R_17);
        uni.addEdge(R_16, R_18);
        uni.addEdge(R_17, R_18);
        uni.addEdge(R_16, O_12);
        uni.addEdge(R_17, O_09);
        uni.addEdge(R_18, R_19);
        uni.addEdge(R_18, O_11);
        uni.addEdge(R_18, library);
        uni.addEdge(R_19, O_13);

        return uni;
    }


    public static void main(String[] args) {
        UniGraph uniGraph = new UniGraph();

        String source = "library";
        String sink = "basketball";

        List<String> route = uniGraph.getPath(source, sink);
        System.out.println(route);
    }

}
