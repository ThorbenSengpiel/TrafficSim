package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

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

        agc.setFill(Color.GRAY);
        double radius = 5;
        agc.gc.beginPath();
        agc.gc.moveTo(r.x, r.y+radius);
        agc.gc.bezierCurveTo(r.x-radius*0.85, r.y+radius, b.x+radius, b.y-radius*0.85, b.x+radius, b.y);
        agc.gc.lineTo(b.x-radius, b.y);
        agc.gc.bezierCurveTo(b.x-radius, b.y-radius*0.85, l.x+radius*0.85, l.y+radius, l.x, l.y+radius);
        agc.gc.lineTo(l.x, l.y-radius);
        agc.gc.lineTo(r.x, -l.y-radius);

        agc.gc.closePath();
        agc.gc.fill();

        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_BORDER);
        Position ne = new Position( 12.5,  12.5);
        Position nw = new Position(-12.5,  12.5);
        radius = 7.5;
        agc.gc.strokeArc(nw.x-radius, nw.y-radius, radius*2, radius*2, 0, 90, ArcType.OPEN);
        agc.gc.strokeArc(ne.x-radius, ne.y-radius, radius*2, radius*2, 90, 90, ArcType.OPEN);
        agc.gc.strokeLine(l.x, l.y-5, r.x, r.y-5);
    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}
