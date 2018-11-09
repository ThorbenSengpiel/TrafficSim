package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;

public class TJunctionView extends StreetView {
    public TJunctionView(Street street) {
        super(street, new Hitbox(new Rectangle(Position.ZERO, 25, 25)));
    }

    @Override
    public void draw(AreaGraphicsContext agc) {

    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}
