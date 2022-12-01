package movement;

import com.google.gson.GsonBuilder;
import core.Coord;
import core.Settings;
import core.SimClock;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.*;

import com.google.gson.*;

public class TestMovement extends MovementModel{

    private GeometryFactory gf = new GeometryFactory();
    private List<UniHub> locs = new ArrayList<>();
    private UniHub genesis, current;
    private Coord lastWaypoint;
    private SimClock clock;
    private boolean switcher;

    private static int id_global=0;
    private int id=0;

    private String wktPath;

    @Override
    public Path getPath() {
        if(clock.getTime() > 3000 && switcher){
            if(rng.nextDouble() > 0.5){
                this.current = locs.get(1);
            }
            switcher = false;
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

        this.id = id_global;
        id_global++;

        System.out.println(id);

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

        this.wktPath = settings.getSetting("wktPath", null);
        if(wktPath==null){
            System.err.println("wktPath is null.");
            return;
        }

        String content;
        try {
            content = new Scanner(new File(this.wktPath)).useDelimiter("\\Z").next();
            System.out.println(content);
        } catch (IOException e){
            System.err.println(e);
            return;
        }

        locs = deserialize(content);

        genesis = locs.get(0);
        current = locs.get(0);
        clock = SimClock.getInstance();
        switcher = true;
        System.out.println(locs);
    }

    public TestMovement(TestMovement other){
        super(other);
        this.genesis = other.genesis;
        this.current = other.current;
        this.clock = other.clock;
        this.switcher = other.switcher;
        this.locs = other.locs;
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

    private List<UniHub> deserialize(String jsonInput){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(Geometry.class, new GeometryDeserializer());

        Gson gson = gsonBuilder.create();
        //Warning: the following line might have severe consequences on your mental health
        List<UniHub> ret = Arrays.asList(gson.fromJson(jsonInput, UniHub[].class));

        return ret;
    }
}
