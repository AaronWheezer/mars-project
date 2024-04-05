package be.howest.ti.mars.logic.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Container {

    private static final int CONTAINER_WITHOUT_ID = -1;
    private int id;
    private double rentprice;
    private final Dimension dimensions;
    private Map<String, Integer> contents;
    private String owner;
    private Location location;

    private Set<String> comments;

    public Container(int id, double rentprice, double width, double height, double depth, Location location){
        this.id = id;
        this.rentprice = rentprice;
        this.dimensions = new Dimension(width, height, depth);
        this.owner = "No owner assigned";
        this.location = location;
        contents = new HashMap<>();
        comments = new HashSet<>();
    }

    public Container(int id, double rentprice, Dimension dimensions, String owner, Location location, Map<String, Integer> contents, Set<String> comments){
        this(id, rentprice, dimensions.getWidth(), dimensions.getHeight(), dimensions.getDepth(), location);
        this.owner = owner;
        this.contents = contents;
        this.comments = comments;
    }

    public Container(double rentprice, double width, double height, double depth, Location location) {
        this(CONTAINER_WITHOUT_ID, rentprice, width, height, depth, location);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getRentprice() {
        return rentprice;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public Map<String, Integer> getContents() {
        return contents;
    }

    public String getOwner() {return owner;}

    public Location getLocation() {return location;}

    public Set<String> getComments(){return comments;}
}