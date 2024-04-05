package be.howest.ti.mars.logic.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Pallet {

    private int id;
    private final Dimension dimensions;

    private String owner;

    private Location location;

    private Map<String, Integer> contents;

    private Set<String> comments;


    public Pallet(int id, double width, double height, double depth, Location location) {
        this(id, width, height, depth, null, location);
    }

    public Pallet(int id, double width, double height, double depth, String owner, Location location) {
        this.id = id;
        this.dimensions = new Dimension(width, height, depth);
        this.owner = owner;
        this.location = location;
        this.contents = new HashMap<>();
        this.comments = new HashSet<>();
    }

    public Pallet(int id, Dimension dimensions, String owner, Location location, Map<String, Integer> contents, Set<String> comments) {
        this.id = id;
        this.dimensions = dimensions;
        this.owner = owner;
        this.location = location;
        this.contents = contents;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public void setId(int anInt) {
        this.id = anInt;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<String, Integer> getContents() {
        return contents;
    }

    public Set<String> getComments(){return comments;}

    public Integer getContentAmount(String item) {
        return contents.get(item);
    }
}
