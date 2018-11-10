package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetStraight2Lane;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import de.trafficsim.util.geometry.Shape;

public class StreetStraight2LaneView extends StreetView {

    private double length;

    public StreetStraight2LaneView(StreetStraight2Lane street) {
        super(street, Hitbox.createLaneHitbox(street.getLength(), street.getRotation(), 10));
        length = street.getLength();
    }

    @Override
    public void draw(AreaGraphicsContext agc) {
        agc.draw2Lane(Position.ZERO, new Position(0, length));
    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}
