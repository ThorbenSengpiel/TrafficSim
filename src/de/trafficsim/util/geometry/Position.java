package de.trafficsim.util.geometry;

public class Position {

    public static final Position ZERO = new Position(0, 0);
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

    public Position getCenterBetween(Position p) {
        double fromX;
        double fromY;
        double toX;
        double toY;

        if (x > p.x) {
            fromX = p.x;
            toX = x;
        } else {
            fromX = x;
            toX = p.x;
        }

        if (y > p.y) {
            fromY = p.y;
            toY = y;
        } else {
            fromY = y;
            toY = p.y;
        }

        return new Position((toX - fromX) / 2, (toY - fromY) / 2);

    }

    @Override
    public String toString() {
        return "[" +"x=" + x +", y=" + y +']';
    }
}