package movement;

import com.google.gson.*;
import core.Coord;
import core.Settings;
import core.SimClock;
import movement.map.UniGraph;
import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import util.Agenda;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TestMovement extends MovementModel{

    private GeometryFactory gf = new GeometryFactory();
    private static List<UniHub> vertices;
    private static UniGraph graph;
    private UniHub genesis;
    private UniHub current, goal;
    private List<UniHub> route = null;
    private int route_index = 0;
    private int current_index = 0;
    private Coord lastWaypoint;
    private SimClock clock;
    private boolean switcher;
    private String[] agenda;
    private static HashMap<String, UniHub> locationMap = new HashMap<String, UniHub>();
    private double timeDeviation;

    private static int id_global=0;
    private int id=0;

    private int[] timeSlots;

    private static boolean parsed = false;
    private static UniGraph fmi;

    private String wktPath;
    //private UniHub hub.getName();


    @Override
    public Path getPath() {
        if((int) SimClock.getTime() > timeSlots[current_index]){
            current = locationMap.get(agenda[current_index]);
            current_index ++;
            goal = locationMap.get(agenda[current_index]);
            route = graph.getPath(current, goal);
        }


        if(route != null){
            if (route_index >= route.size()) {
                route_index = route.indexOf(current);
            }
            current = route.get(route_index);
            route_index++;
            if(current == goal) {
                route = null;
                route_index = 0;
            }
        }

        Point pt;
        Coordinate c;

        do {
            c = randomCoordinate();
            pt = gf.createPoint(c);
        }while (!current.getPolygon().contains(pt));

        final Path p;
        p = new Path( super.generateSpeed() );
        p.addWaypoint( this.lastWaypoint.clone() );

        p.addWaypoint(convert(c));
        this.lastWaypoint = convert(c);
        return p;
    }

    @Override
    public Coord getInitialLocation() {
        /*
        this.id = id_global;
        id_global++;
        System.out.println(id);
        */

        Point pt;
        Coordinate c;

        do {
            c = randomCoordinate();
            pt = gf.createPoint(c);
        }while (!genesis.getPolygon().contains(pt));

        this.lastWaypoint = convert(c);
        return convert(c);
    }

    public TestMovement(Settings settings){
        super(settings);

        if(!parsed) {
            this.wktPath = settings.getSetting("wktPath", null);
            if (wktPath == null) {
                System.err.println("wktPath is null.");
                return;
            }

            String content;
            try {
                content = new Scanner(new File(this.wktPath)).useDelimiter("\\Z").next();
                //System.out.println(content);
            } catch (IOException e) {
                System.err.println(e);
                return;
            }

            // DONE: filter out vertices
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
            vertices = deserializeVertices(jsonObject.get("vertices"));
            for(UniHub hub : vertices) {
                locationMap.put(hub.getName(), hub);
            }
            //System.out.println(jsonObject.get("vertices"));

            // DONE: deserialize edges
            List<Pair<UniHub, UniHub>> edges = deserializeEdges(jsonObject.get("edges"), vertices);

            graph = new UniGraph(vertices, edges);
            parsed = true;
            System.out.println("parsed!");
        }
        clock = SimClock.getInstance();
        switcher = true;
        System.out.println(vertices);
    }

    public TestMovement(TestMovement other){
        super(other);
        Agenda a = new Agenda();
        this.agenda = a.getAgenda();
        this.genesis = locationMap.get(agenda[0]);
        this.current = genesis;
        this.clock = other.clock;
        this.switcher = other.switcher;
        this.timeSlots = a.getTimeSlots();
    }

    @Override
    public MovementModel replicate() {
        return new TestMovement(this);
    }

    private Coord convert(Coordinate c){
        return new Coord(c.getX(), c.getY());
    }

    private Coordinate convert(Coord c){
        return new Coordinate(c.getX(), c.getY());
    }

    private Coordinate randomCoordinate() {
        return new Coordinate(
            rng.nextDouble() * super.getMaxX(),
            rng.nextDouble() * super.getMaxY()
        );
    }

    private List<UniHub> deserializeVertices(JsonElement jsonInput){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(Geometry.class, new GeometryDeserializer());

        Gson gson = gsonBuilder.create();
        //Warning: the following line might have severe consequences on your mental health
        List<UniHub> ret = Arrays.asList(gson.fromJson(jsonInput, UniHub[].class));

        return ret;
    }

    private List<Pair<UniHub, UniHub>> deserializeEdges(JsonElement jsonInput, List<UniHub> hubs){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        Gson gson = gsonBuilder.create();
        edge[] edges = gson.fromJson(jsonInput, edge[].class);

        List<Pair<UniHub, UniHub>> ret = new ArrayList<>();
        for(edge e : edges){
            UniHub first = hubs.stream().filter(hub -> e.first.equals(hub.getName())).findFirst().orElse(null);
            UniHub second = hubs.stream().filter(hub -> e.second.equals(hub.getName())).findFirst().orElse(null);
            ret.add(new Pair<>(first, second));
        }
        return ret;
    }
}
