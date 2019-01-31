package de.trafficsim.logic.streets.signs;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public abstract class Sign {

    protected Position position;
    protected Direction rotation;
    public final SignType type;

    protected Sign(Position position, SignType type) {
        this.position = position;
        this.rotation = Direction.NORTH;
        this.type = type;
    }

    protected Sign(Position position, Direction rotation, SignType type) {
        this.position = position;
        this.rotation = rotation;
        this.type = type;
    }

    public abstract void render(AreaGraphicsContext agc);
}
