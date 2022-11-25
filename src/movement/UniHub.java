package movement;

import core.Coord;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.*;
import com.google.gson.*;

public class UniHub {

    private String name;
    private int id;
    private Geometry polygon;
    //List of neighbors of this hub
    private List<UniHub> neighbors = new ArrayList<>();

    public UniHub(Geometry polygon, String name) {
        this.polygon = polygon;
        this.name = name;
        this.id = name.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
                "[UniHub: id=%1$d, name=%2$s, polygon=%3$s]",
                id, name, polygon);
    }

    public Geometry getPolygon() {
        return polygon;
    }

    public void addNeighbor(UniHub hub){
        this.neighbors.add(hub);
    }
}