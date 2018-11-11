package de.trafficsim.util.geometry;

import de.trafficsim.util.Direction;
import de.trafficsim.util.Util;
import javafx.geometry.Pos;

import java.util.Objects;

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

        return new Position((toX + fromX) / 2, (toY + fromY) / 2);

    }

    public Position rotate(Direction dir) {
        switch (dir) {
            case NORTH:
                return this;
            case EAST:
                return new Position(-y, x);
            case SOUTH:
                return new Position(-x, -y);
            case WEST:
                return new Position(y, -x);
            case ZERO:
                return Position.ZERO;
        }
        return this;
    }

    public Position interpolate(Position p, double value) {
        double xDist = p.x-x;
        double yDist = p.y-y;
        return new Position(x + xDist*value, y + yDist*value);
    }

    public double angleTo(Position p) {
        return Math.atan2(x - p.x, p.y - y)+Math.PI/2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.x, x) == 0 && Double.compare(position.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "[" +"x=" + x +", y=" + y +']';
    }
}