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

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public static Direction generateDirectionStraight(Position from, Position to) {
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

    public static Direction generateDirectionCurve(Position from, Position to, Direction inDirection) {
        switch (inDirection) {
            case NORTH:
            case SOUTH:
                if (from.x > to.x) {
                        return WEST;
                } else {
                        return EAST;
                }
            case EAST:
            case WEST:
                if (from.y > to.y) {
                        return NORTH;
                } else {
                        return SOUTH;
                }
            default:
                return ZERO;
        }
    }
}