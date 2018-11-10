package de.trafficsim.logic.streets;

import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public abstract class StreetTwoPositions extends Street {


    public StreetTwoPositions(Position position, StreetType type, Direction rotation) {
        super(position, type, rotation);
    }

    public StreetTwoPositions(Position position, StreetType type) {
        super(position, type);
    }



    public abstract StreetTwoPositions createChanged(Position pos2nd);
}

