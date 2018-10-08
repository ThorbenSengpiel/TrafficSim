package de.trafficsim.util.geometry;

public class Position {

    public final double x;
    public final double y;

    public Position() {
        this(0, 0);
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Position p) {
        double dx = p.x - x;
        double dy = p.y - y;
        return Math.sqrt(dx*dx+dy*dy);
    }

    public Position add(Position p) {
        return new Position(x + p.x, y + p.y);
    }

    public Position sub(Position p) {
        return new Position(x - p.x, y - p.y);
    }

    public Position scale(double s) {
        return new Position(x * s, y * s);
    }

    public Position snapToGrid(double gridSpacing) {
        double xg = Math.round(x / gridSpacing) * gridSpacing;
        double yg = Math.round(y / gridSpacing) * gridSpacing;
        return new Position(xg, yg);
    }

    @Override
    public String toString() {
        return "[" +"x=" + x +", y=" + y +']';
    }
}