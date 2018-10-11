package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;

public class StreetCrossTestView extends StreetView {
    public StreetCrossTestView(Street street) {
        super(street, new Hitbox(new Rectangle(Position.ZERO, 5, 25), new Rectangle(Position.ZERO, 25, 5)));
    }

    @Override
    public void draw(AreaGraphicsContext agc, Position center) {

    }
}
