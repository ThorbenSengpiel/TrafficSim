package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;

public class StreetCrossTrafficLightsView extends StreetView {

    public StreetCrossTrafficLightsView(Street street) {
        super(street, new Hitbox(new Rectangle(Position.ZERO, 50, 15), new Rectangle(Position.ZERO, 15, 50)));
    }

    @Override
    public void draw(AreaGraphicsContext agc) {
        agc.gc.beginPath();
        agc.gc.moveTo(50, -5);
        agc.gc.bezierCurveTo(45, -5, 40, -12.5, 35, -12.5);
        agc.gc.arcTo(7.5, -12.5, 7.5, -35, 12.5);
        agc.gc.lineTo(7.5, -35);
        agc.gc.bezierCurveTo(7.5, -40,5, -45,5, -50);


        agc.gc.lineTo(-5, -50);
        agc.gc.bezierCurveTo(-5, -45, -12.5,-40, -12.5,-35);
        agc.gc.arcTo(-12.5, -7.5, -35, -7.5, 12.5);
        agc.gc.lineTo(-35, -7.5);
        agc.gc.bezierCurveTo(-40,-7.5, -45,-5, -50, -5);


        agc.gc.lineTo(-50, 5);
        agc.gc.bezierCurveTo(-45, 5, -40, 12.5, -35, 12.5);
        agc.gc.arcTo(-7.5, 12.5, -7.5, 35, 12.5);
        agc.gc.lineTo(-7.5, 35);
        agc.gc.bezierCurveTo(-7.5, 40,-5, 45,-5, 50);


        agc.gc.lineTo(5, 50);
        agc.gc.bezierCurveTo(5, 45, 12.5,40, 12.5,35);
        agc.gc.arcTo(12.5, 7.5, 35, 7.5, 12.5);
        agc.gc.lineTo(35, 7.5);
        agc.gc.bezierCurveTo(40,7.5, 45,5, 50, 5);


        agc.gc.closePath();
        agc.setFill(AreaGraphicsContext.StreetVisuals.STREET.stroke);
        agc.gc.fill();

        drawQuarter(agc);
        agc.gc.rotate(90);
        drawQuarter(agc);
        agc.gc.rotate(90);
        drawQuarter(agc);
        agc.gc.rotate(90);
        drawQuarter(agc);
        agc.gc.rotate(90);
    }

    private void drawQuarter(AreaGraphicsContext agc) {
        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_BORDER);
        agc.gc.beginPath();
        agc.gc.moveTo(5, 50);
        agc.gc.bezierCurveTo(5, 45, 12.5,40, 12.5,35);
        agc.gc.arcTo(12.5, 7.5, 35, 7.5, 12.5);
        agc.gc.lineTo(35, 7.5);
        agc.gc.bezierCurveTo(40,7.5, 45,5, 50, 5);
        agc.gc.stroke();

        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_LINE);
        agc.gc.beginPath();
        agc.gc.moveTo(50, 0);
        agc.gc.bezierCurveTo(45, 0, 40, 2.5, 35, 2.5);
        agc.gc.lineTo(15, 2.5);
        agc.gc.lineTo(15, -7.5);
        agc.gc.lineTo(35, -7.5);
        agc.gc.stroke();

        agc.gc.strokeLine(15, -2.5, 35, -2.5);
        agc.gc.strokeArc(7.5, 2.5, 35, 35, 90+45, 45, ArcType.OPEN);

        agc.setFill(AreaGraphicsContext.StreetVisuals.STREET_LINE.stroke);
        agc.gc.setLineCap(StrokeLineCap.SQUARE);
        agc.gc.setLineWidth(0.5);
        agc.gc.strokeLine(20, -1.5, 25, -1.5);
        agc.gc.strokeLine(20, -1.5, 20, 0);
        agc.gc.fillPolygon(new double[] {20, 19, 21}, new double[] {1.5, -0.5, -0.5},3);
        agc.gc.strokeLine(21, -5, 25, -5);
        agc.gc.fillPolygon(new double[] {19, 21, 21}, new double[] {-5, -6, -4},3);
        agc.gc.strokeLine(20, -8.5, 25, -8.5);
        agc.gc.strokeLine(20, -8.5, 20, -10);
        agc.gc.fillPolygon(new double[] {20, 19, 21}, new double[] {-11.5, -9.5, -9.5},3);

        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_LINE_DASHED_SMALL);
        agc.gc.strokeLine(15, -2.5, -15, -2.5);
        agc.gc.strokeLine(15, -7.5, -12.5, -7.5);

        agc.gc.strokeArc(-7.5, -2.5, 35, 35, 90, 90, ArcType.OPEN);
        agc.gc.strokeArc(-2.5, 2.5, 25, 25, 90, 90, ArcType.OPEN);

        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_BORDER);

    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}
