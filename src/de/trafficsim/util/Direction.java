package de.trafficsim.util;

import de.trafficsim.util.geometry.Position;

public enum Direction {
    NORTH(new Position(0, -1)),
    EAST(new Position(1, 0)),
    SOUTH(new Position(0, 1)),
    WEST(new Position(-1, 0));

    public final Position vector;

    Direction(Position vector) {
        this.vector = vector;
    }
}
