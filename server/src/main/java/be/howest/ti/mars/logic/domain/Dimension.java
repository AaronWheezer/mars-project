package be.howest.ti.mars.logic.domain;

public class Dimension {

    private final double width;
    private final double height;
    private final double depth;

    public Dimension(double width, double height, double depth){
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Dimension(double width, double depth){
        this(width, 0, depth);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "Dimensions: " +
                "\nwidth=" + width +
                ",\n height=" + height +
                ",\n depth=" + depth;
    }
}
