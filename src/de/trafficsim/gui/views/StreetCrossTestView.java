package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class StreetCrossTestView extends StreetView {
    public StreetCrossTestView(Street street) {
        super(street, new Hitbox(new Rectangle(Position.ZERO, 5, 25), new Rectangle(Position.ZERO, 25, 5)));
    }

    @Override
    public void draw(AreaGraphicsContext agc) {
        Position n = new Position(0, -12.5);
        Position e = new Position(12.5, 0);
        Position s = new Position(0, 12.5);
        Position w = new Position(-12.5, 0);

        agc.draw2Lane(new Position(12.2, 0),  new Position(25.1, 0));
        agc.draw2Lane(new Position(-12.2, 0), new Position(-25.1, 0));
        agc.draw2Lane(new Position(0, 12.2),  new Position(0, 25.1));
        agc.draw2Lane(new Position(0, 12.2),  new Position(0, -25.1));

        agc.setFill(Color.GRAY);
        double r = 5;
        agc.gc.beginPath();
        agc.gc.moveTo(n.x+r, n.y);
        agc.gc.bezierCurveTo(n.x+r, n.y+r*0.85, e.x-r*0.85, e.y-r, e.x, e.y-r);
        agc.gc.lineTo(e.x, e.y+r);
        agc.gc.bezierCurveTo(e.x-r*0.85, e.y+r, s.x+r, s.y-r*0.85, s.x+r, s.y);
        agc.gc.lineTo(s.x-r, s.y);
        agc.gc.bezierCurveTo(s.x-r, s.y-r*0.85, w.x+r*0.85, w.y+r, w.x, w.y+r);
        agc.gc.lineTo(w.x, w.y-r);
        agc.gc.bezierCurveTo(w.x+r*0.85, w.y-r, n.x-r, n.y+r*0.85, n.x-r, n.y);

        agc.gc.closePath();
        agc.gc.fill();

        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_BORDER);
        Position ne = new Position( 12.5,  12.5);
        Position se = new Position( 12.5, -12.5);
        Position nw = new Position(-12.5,  12.5);
        Position sw = new Position(-12.5, -12.5);
        r = 7.5;
        agc.gc.strokeArc(nw.x-r, nw.y-r, r*2, r*2, 0, 90, ArcType.OPEN);
        agc.gc.strokeArc(ne.x-r, ne.y-r, r*2, r*2, 90, 90, ArcType.OPEN);
        agc.gc.strokeArc(se.x-r, se.y-r, r*2, r*2, 180, 90, ArcType.OPEN);
        agc.gc.strokeArc(sw.x-r, sw.y-r, r*2, r*2, 270, 90, ArcType.OPEN);
    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}
