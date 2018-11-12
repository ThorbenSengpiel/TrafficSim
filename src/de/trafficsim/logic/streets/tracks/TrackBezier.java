package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

public class TrackBezier extends Track {

    private Position c0;
    private Position c1;

    public TrackBezier(Position from, Direction inDir, Position to, Direction outDir, double weight, Street street) {
        this(from, from.add(inDir.vector.scale(weight)), to.add(outDir.rotateClockWise().rotateClockWise().vector.scale(weight)), to, street);
        this.inDir = inDir;
        this.outDir = outDir;
    }

    public TrackBezier(Position from, Position c0, Position c1, Position to, Street street) {
        super(from, to, calcLength(from, c0, c1, to), street);
        this.c0 = c0;
        this.c1 = c1;
    }

    private static double calcLength(Position from, Position c0, Position c1, Position to) {
        double l = 0;
        Position last = calcPos(0, from, c0, c1, to);
        for (int i = 1; i <= 25; i++) {
            Position next = calcPos(i/25.0, from, c0, c1, to);
            l += last.distance(next);
            last = next;
        }
        return l;
    }

    @Override
    protected void renderTrack(AreaGraphicsContext agc) {
        agc.gc.beginPath();
        agc.gc.moveTo(from.x, from.y);
        agc.gc.bezierCurveTo(c0.x, c0.y, c1.x, c1.y, to.x, to.y);
        agc.gc.stroke();
        agc.setStroke(Color.FUCHSIA.deriveColor(0, 1, 1, 0.3));
        agc.gc.strokeLine(from.x, from.y, c0.x, c0.y);
        agc.gc.strokeLine(to.x, to.y, c1.x, c1.y);
    }

    @Override
    public Position getPosOnArea(double pos) {
        double value = pos/length;
        return calcPos(value, from, c0, c1, to).add(street.getPosition());
    }

    @Override
    public double getDirectionOnPos(double pos) {
        double value = pos/length;
        Position p0_0 = from.interpolate(c0, value);
        Position p0_1 = c0.interpolate(c1, value);
        Position p0_2 = c1.interpolate(to, value);
        Position p1_0 = p0_0.interpolate(p0_1, value);
        Position p1_1 = p0_1.interpolate(p0_2, value);
        return Math.toDegrees(p1_0.angleTo(p1_1));
    }

    private static Position calcPos(double value, Position from, Position c0, Position c1, Position to) {
        Position p0_0 = from.interpolate(c0, value);
        Position p0_1 = c0.interpolate(c1, value);
        Position p0_2 = c1.interpolate(to, value);
        Position p1_0 = p0_0.interpolate(p0_1, value);
        Position p1_1 = p0_1.interpolate(p0_2, value);
        return p1_0.interpolate(p1_1, value);
    }
}
