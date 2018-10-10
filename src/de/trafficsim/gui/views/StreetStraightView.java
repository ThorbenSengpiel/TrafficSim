package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetStraight;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import de.trafficsim.util.geometry.Shape;
import javafx.scene.paint.Color;

public class StreetStraightView extends StreetView {

    private static double width = 5;
    private boolean horizontal;
    private double length;

    public StreetStraightView(StreetStraight street) {
        super(street, new Hitbox(calcHitbox(street.getFrom().sub(street.getPosition()), street.getTo().sub(street.getPosition()))));
        horizontal = street.getTo().y == street.getFrom().y;
        length = street.getFrom().distance(street.getTo());
    }

    private static Shape calcHitbox(Position from, Position to) {
        Position f;
        Position t;
        if (from.y == to.y) {
            f = new Position(from.x, from.y - (width/2));
            t = new Position(to.x, to.y + (width/2));
        } else {
            f = new Position(from.x - (width/2), from.y);
            t = new Position(to.x + (width/2), to.y);
        }
        return new Rectangle(f, t);
    }

    @Override
    public void draw(AreaGraphicsContext agc, Position center) {
        Position c = agc.areaToCanvas(center);

        double l;
        double w;

        if (horizontal) {
            l = agc.scaleToCanvas(length/2);
            w = agc.scaleToCanvas(width/2);
        } else {
            w = agc.scaleToCanvas(length/2);
            l = agc.scaleToCanvas(width/2);
        }

        agc.setFill(Color.GRAY);
        agc.gc.fillRect(c.x-l, c.y-w, 2*l, 2*w);

        agc.setStroke(Color.BLACK);
        agc.gc.setLineWidth(agc.scaleToCanvas(0.2));
        if (horizontal) {
            agc.gc.strokeLine(c.x-l, c.y+w, c.x+l, c.y+w);
            agc.gc.strokeLine(c.x-l, c.y-w, c.x+l, c.y-w);
        } else {
            agc.gc.strokeLine(c.x-l, c.y-w, c.x-l, c.y+w);
            agc.gc.strokeLine(c.x+l, c.y-w, c.x+l, c.y+w);
        }

    }
}
