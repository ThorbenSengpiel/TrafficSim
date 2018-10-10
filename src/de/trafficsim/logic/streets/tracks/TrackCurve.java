package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import javafx.scene.shape.ArcType;

public class TrackCurve extends Track {

    private boolean curveDirection;
    private Position center;

    public TrackCurve(Position from, Position to, boolean curveDirection, Street street) {
        super(from, to, Math.abs(from.x - to.x) * Math.PI / 2,street);
        if (Math.abs(from.x - to.x) != Math.abs(from.y - to.y)) {
            throw new RuntimeException("TrackCurve can only be 90°");
        }
        this.curveDirection = curveDirection;
        if (curveDirection) {
            center = new Position(from.x, to.y);
        } else {
            center = new Position(to.x, from.y);
        }
    }


    @Override
    public Position getPosOnArea(double pos) {
        return new Position(0,0);
    }

    @Override
    public Position getDirectionOnPos(double currentPosInTrack) {
        return null;
    }

    @Override
    protected void renderTrack(AreaGraphicsContext agc, Position f, Position t) {
        int angle;

        double w = t.x-f.x;
        double h = t.y-f.y;

        Position c = agc.areaToCanvas(center);

        if (w < 0 && h < 0) {
            angle = 180;
        } else if (w > 0 && h < 0) {
            angle = 270;
        } else if (w > 0 && h > 0) {
            angle = 0;
        } else if (w < 0 && h > 0) {
            angle = 90;
        } else {
            angle = 0;
        }

        if (!curveDirection) {
            angle += 180;
            angle %= 360;
        }

        w = Math.abs(w);
        h = Math.abs(h);

        agc.gc.strokeArc(c.x-w, c.y-h, w*2, h*2, angle, 90, ArcType.OPEN);
    }
}
