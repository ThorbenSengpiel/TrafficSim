package de.trafficsim.logic.streets.signs;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

/**
 * Abstract Class representing a generic Sign.
 */
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

    /**
     * Method to render the Sign. Has to be overwritten
     * @param agc - GraphicsContext to use for rendering
     */
    public abstract void render(AreaGraphicsContext agc);
}
