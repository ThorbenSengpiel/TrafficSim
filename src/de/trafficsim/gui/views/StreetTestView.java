package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;

public class StreetTestView extends StreetView {


    public StreetTestView(Street street, Hitbox hitBox) {
        super(street, hitBox);
    }

    @Override
    public void draw(AreaGraphicsContext agc) {

    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}
