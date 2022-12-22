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

public class UniMovement extends MovementModel{

    private GeometryFactory gf = new GeometryFactory();
    private static UniGraph graph;
    private UniHub genesis;
    private UniHub current;
    private UniHub goal;
    private List<UniHub> route = null;
    private int route_index = 0;
    private int current_index = 0;
    private Coord lastWaypoint;
    private SimClock clock;
    private String[] agenda;
    private static HashMap<String, UniHub> locationMap = new HashMap<String, UniHub>();
    private int[] timeSlots;
    private static boolean parsed = false;

    public record UniHub(Geometry polygon, String name) {
        @Override
            public String toString() {
                return String.format(
                        "[UniHub: name=%1$s, polygon=%2$s]",
                        name, polygon);
            }
    }

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
        }while (!current.polygon().contains(pt));

        final Path p;
        p = new Path( super.generateSpeed() );
        p.addWaypoint( this.lastWaypoint.clone() );

        p.addWaypoint(convert(c));
        this.lastWaypoint = convert(c);
        return p;
    }

    @Override
    public Coord getInitialLocation() {
        Point pt;
        Coordinate c;

        do {
            c = randomCoordinate();
            pt = gf.createPoint(c);
        }while (!genesis.polygon().contains(pt));

        this.lastWaypoint = convert(c);
        return convert(c);
    }

    public UniMovement(Settings settings){
        super(settings);

        if(!parsed) {
            String jsonPath = settings.getSetting("wktPath", null);
            if (jsonPath == null) {
                System.err.println("wktPath is null.");
                return;
            }

            String content;
            try {
                content = new Scanner(new File(jsonPath)).useDelimiter("\\Z").next();
            } catch (IOException e) {
                System.err.println(e);
                return;
            }

            // DONE: filter out vertices
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
            List<UniHub> vertices = deserializeVertices(jsonObject.get("vertices"));
            for(UniHub hub : vertices) {
                locationMap.put(hub.name(), hub);
            }

            // DONE: deserialize edges
            List<Pair<UniHub, UniHub>> edges = deserializeEdges(jsonObject.get("edges"), vertices);

            graph = new UniGraph(vertices, edges);
            parsed = true;
            System.out.println("parsed!");
        }
        clock = SimClock.getInstance();
    }

    public UniMovement(UniMovement other){
        super(other);
        Agenda a = new Agenda();
        this.agenda = a.getAgenda();
        //this.agenda = new String[] {"interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims", "interims"};
        this.genesis = locationMap.get(agenda[0]);
        this.current = genesis;
        this.clock = other.clock;
        this.timeSlots = a.getTimeSlots();
    }

    @Override
    public MovementModel replicate() {
        return new UniMovement(this);
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
        return Arrays.asList(gson.fromJson(jsonInput, UniHub[].class));
    }

    private List<Pair<UniHub, UniHub>> deserializeEdges(JsonElement jsonInput, List<UniHub> hubs){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        Gson gson = gsonBuilder.create();
        edge[] edges = gson.fromJson(jsonInput, edge[].class);

        List<Pair<UniHub, UniHub>> ret = new ArrayList<>();
        for(edge e : edges){
            UniHub first = hubs.stream().filter(hub -> e.first.equals(hub.name())).findFirst().orElse(null);
            UniHub second = hubs.stream().filter(hub -> e.second.equals(hub.name())).findFirst().orElse(null);
            ret.add(new Pair<>(first, second));
        }
        return ret;
    }

    public static void reset() {
        Agenda.reset();
    }


}
