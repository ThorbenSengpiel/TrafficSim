package de.trafficsim.util;

import de.trafficsim.util.geometry.Position;

public enum Direction {
    NORTH(new Position(0, -1),0),
    EAST(new Position(1, 0),90),
    SOUTH(new Position(0, 1),180),
    WEST(new Position(-1, 0),270),
    ZERO(new Position(0,0),0);

    public final Position vector;
    public final double angle;

    Direction(Position vector, double angle) {
        this.vector = vector;
        this.angle = angle;
    }

    public static Direction generateDirection(Position from,Position to) {
        if (from.y < to.y) {
            return SOUTH;
        }
        if (from.y > to.y) {
            return NORTH;
        }
        if (from.x < to.x) {
            return EAST;
        }
        if (from.x > to.x) {
            return WEST;
        }
        return ZERO;
    }
}
