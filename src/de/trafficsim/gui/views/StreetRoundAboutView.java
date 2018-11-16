package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.StreetRoundAbout;
import de.trafficsim.util.geometry.Circle;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

public class StreetRoundAboutView extends StreetView {

    private static final double radius = 11.18033988749895;
    private static final double streetWidth = 5;
    private static final double lineWidth = 0.2;

    public StreetRoundAboutView(StreetRoundAbout street) {
        super(street, new Hitbox(new Circle(new Position(), radius+(streetWidth/2))).addSub(new Circle(new Position(), radius-(streetWidth/2))));
    }

    @Override
    public void draw(AreaGraphicsContext agc) {
        Position c = Position.ZERO;

        double r = radius;

        double rIn = radius - (streetWidth/2);
        double rOut = radius + (streetWidth/2);

        agc.setStroke(Color.GRAY);
        agc.gc.setLineWidth(streetWidth);
        agc.gc.strokeOval(c.x-r, c.y-r, 2*r, 2*r);

        agc.setStroke(Color.BLACK);
        agc.gc.setLineWidth(lineWidth);
        agc.gc.strokeOval(c.x-rIn, c.y-rIn, 2*rIn, 2*rIn);
        agc.gc.strokeOval(c.x-rOut, c.y-rOut, 2*rOut, 2*rOut);

    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}

