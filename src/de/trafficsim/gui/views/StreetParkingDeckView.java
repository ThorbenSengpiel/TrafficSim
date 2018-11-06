package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;

public class StreetParkingDeckView extends StreetView {

    public StreetParkingDeckView(Street street) {
        super(street, new Hitbox(new Rectangle(Position.ZERO, 25, 25)));
    }

    @Override
    public void draw(AreaGraphicsContext agc, Position center) {
        agc.draw2Lane(getPosition(center, new Position(-25, 0).rotate(street.getRotation())), getPosition(center, 0, 0));
    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc, Position center) {
        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_BORDER);
        agc.setFill(AreaGraphicsContext.StreetVisuals.STREET.stroke);
        Position p = getPositionOnCanvas(center, agc, Position.ZERO);
        double w = agc.scaleToCanvas(20);
        double h = agc.scaleToCanvas(25);
        if (street.getRotation().isHorizontal()) {
            double t = w;
            w = h;
            h = t;
        }
        agc.gc.fillRect(p.x-w, p.y-h, w*2, h*2);
        agc.gc.strokeRect(p.x-w, p.y-h, w*2, h*2);
    }
}
