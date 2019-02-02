package de.trafficsim.logic.streets.signs;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public abstract class Sign {

    protected Position position;
    protected Direction rotation;

    protected Sign(Position position) {
        this.position = position;
        this.rotation = Direction.NORTH;
    }

    protected Sign(Position position, Direction rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public abstract void render(AreaGraphicsContext agc);
}
