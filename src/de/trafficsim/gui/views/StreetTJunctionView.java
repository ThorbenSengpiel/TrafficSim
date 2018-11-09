package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;

public class StreetTJunctionView extends StreetView {
    public StreetTJunctionView(Street street) {
        super(street, new Hitbox(new Rectangle(Position.ZERO, 25, 5), new Rectangle(new Position(0, 10), 5, 15)));
    }

    @Override
    public void draw(AreaGraphicsContext agc) {
        Position l = new Position(-12.5, 0);
        Position r = new Position(12.5, 0);
        Position b = new Position(0, 12.5);

        //draw tails
        agc.draw2Lane(new Position(12.2, 0),  new Position(25.1, 0));   //left
        agc.draw2Lane(new Position(-12.2, 0), new Position(-25.1, 0));  //right
        agc.draw2Lane(new Position(0, 12.2),  new Position(0, 25.1));   //bottom

        //todo Leon...

    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}
