package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.StreetRoundAbout;
import de.trafficsim.util.geometry.Circle;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

public class StreetRoundAboutView extends StreetView {

    private static final double radius = 50;
    private static final double streetWidth = 10;
    private static final double lineWidth = 0.2;

    public StreetRoundAboutView(StreetRoundAbout street) {
        super(street, new Hitbox(new Circle(new Position(), radius+(streetWidth/2))).addSub(new Circle(new Position(), radius-(streetWidth/2))));
    }

    @Override
    public void draw(AreaGraphicsContext agc, Position center) {
        Position c = agc.areaToCanvas(center);

        double r = agc.scaleToCanvas(radius);

        double rIn = agc.scaleToCanvas(radius - (streetWidth/2));
        double rOut = agc.scaleToCanvas(radius + (streetWidth/2));

        agc.setStroke(Color.GRAY);
        agc.gc.setLineWidth(agc.scaleToCanvas(streetWidth));
        agc.gc.strokeOval(c.x-r, c.y-r, 2*r, 2*r);

        agc.setStroke(Color.BLACK);
        agc.gc.setLineWidth(agc.scaleToCanvas(lineWidth));
        agc.gc.strokeOval(c.x-rIn, c.y-rIn, 2*rIn, 2*rIn);
        agc.gc.strokeOval(c.x-rOut, c.y-rOut, 2*rOut, 2*rOut);

        agc.setStroke(Color.WHITE);
        agc.gc.setLineWidth(agc.scaleToCanvas(lineWidth));
        agc.gc.setLineDashes(agc.scaleToCanvas(lineWidth*3*Math.PI));
        agc.gc.strokeOval(c.x-r, c.y-r, 2*r, 2*r);
        agc.gc.setLineDashes(null);
    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc, Position center) {

    }
}

