package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.StreetRoundAbout;
import de.trafficsim.util.geometry.Circle;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.paint.Color;

public class StreetRoundAboutView extends StreetView {

    private static final double radius = 11.18033988749895;
    private static final double streetWidth = 5;
    private static final double lineWidth = 0.2;

    public StreetRoundAboutView(StreetRoundAbout street) {
        super(street, new Hitbox(new Rectangle(Position.ZERO, 25, 6), new Rectangle(Position.ZERO, 6, 25), new Circle(new Position(), radius+(streetWidth/2))).addSub(new Circle(new Position(), radius-(streetWidth/2))));
    }

    @Override
    public void draw(AreaGraphicsContext agc) {
        Position c = Position.ZERO;

        double r = radius;

        double rIn = radius - (streetWidth/2);
        double rOut = radius + (streetWidth/2);


        agc.setStroke(Color.BLACK);
        agc.gc.setLineWidth(lineWidth);
        agc.gc.strokeOval(c.x-rIn, c.y-rIn, 2*rIn, 2*rIn);
        agc.gc.strokeOval(c.x-rOut, c.y-rOut, 2*rOut, 2*rOut);

        agc.setFill(AreaGraphicsContext.StreetVisuals.STREET.stroke);


        drawQuarter(agc);
        agc.gc.rotate(90);
        drawQuarter(agc);
        agc.gc.rotate(90);
        drawQuarter(agc);
        agc.gc.rotate(90);
        drawQuarter(agc);
        agc.gc.rotate(90);

        agc.setStroke(Color.GRAY);
        agc.gc.setLineWidth(streetWidth-0.18);
        agc.gc.strokeOval(c.x-r, c.y-r, 2*r, 2*r);

    }

    private void drawQuarter(AreaGraphicsContext agc) {
        double p = 11.45;

        agc.setFill(AreaGraphicsContext.StreetVisuals.STREET.stroke);
        agc.gc.beginPath();
        agc.gc.moveTo(25, -5);
        agc.gc.bezierCurveTo(25, -5, p+2.5, -5, p-0.1, -7.5);
        agc.gc.lineTo(p-0.1, 7.5);
        agc.gc.bezierCurveTo(p+2.5, 5, 25, 5, 25, 5);
        agc.gc.closePath();
        agc.gc.fill();


        agc.gc.beginPath();
        agc.gc.moveTo(25, -5);
        agc.gc.bezierCurveTo(25, -5, p+2.5, -5, p, -7.5);
        agc.gc.stroke();

        agc.gc.beginPath();
        agc.gc.moveTo(p, 7.5);
        agc.gc.bezierCurveTo(p+2.5, 5, 25, 5, 25, 5);
        agc.gc.stroke();

        agc.setFill(Color.color(0.4, 0.8, 0.3));
        agc.gc.beginPath();
        agc.gc.moveTo(20, 0);
        agc.gc.lineTo(14, -1.5);
        agc.gc.lineTo(14, 1.5);
        agc.gc.lineTo(20, 0);
        agc.gc.fill();
        agc.gc.stroke();

    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}

