package de.trafficsim.logic.streets.signs;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.util.geometry.Position;

public abstract class Sign {

    protected Position position;
    public final SignType type;

    protected Sign(Position position, SignType type) {
        this.position = position;
        this.type = type;
    }

    public abstract void render(AreaGraphicsContext agc);
}
