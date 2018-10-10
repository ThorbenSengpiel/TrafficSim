package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.util.geometry.Position;
import javafx.scene.shape.ArcType;

public class TrackCurve extends Track {

    boolean curveDirection;

    public TrackCurve(Position from, Position to, boolean curveDirection) {
        super(from, to, Math.abs(from.x - to.x) * Math.PI / 2);
        if (Math.abs(from.x - to.x) != Math.abs(from.y - to.y)) {
            throw new RuntimeException("TrackCurve can only be 90°");
        }
        this.curveDirection = curveDirection;
    }


    @Override
    protected void renderTrack(AreaGraphicsContext agc, Position f, Position t) {
        int angle;

        double w = t.x-f.x;
        double h = t.y-f.y;


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
        Position center;
        if (curveDirection) {
            center = new Position(f.x, t.y);
        } else {
            center = new Position(t.x, f.y);
            angle += 180;
            angle %= 360;
        }

        w = Math.abs(w);
        h = Math.abs(h);

        agc.gc.strokeArc(center.x-w, center.y-h, w*2, h*2, angle, 90, ArcType.OPEN);
    }
}
