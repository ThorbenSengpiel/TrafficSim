package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import javafx.scene.shape.ArcType;

public class TrackCurve extends Track {

    private Position center;
    private double radius;
    private boolean isRight;

    public void calcCenter(){
        if(inDir == Direction.EAST || inDir == Direction.WEST){
            center = new Position(from.x, to.y);
        } else {
            center = new Position(to.x,from.y);
        }
    }
    public TrackCurve(Position from, Position to, Direction inDir, Street street) {
        super(from, to, Math.abs(from.x - to.x) * Math.PI / 2,street);
        this.inDir = inDir;
        this.outDir = Direction.generateDirectionCurve(from, to, inDir);

        if (Math.abs(from.x - to.x) != Math.abs(from.y - to.y)) {
            throw new RuntimeException("TrackCurve can only be 90°");
        }

        radius = Math.abs(from.x - to.x);
        isRight = inDir.isLeftOf(outDir);
        calcCenter();
    }


    @Override
    public Position getPosOnArea(double pos) {
        double offsetRad;
        double rad;
        if (isRight) {
            offsetRad = Math.toRadians(inDir.angle+90);
            rad = -pos / radius + offsetRad;
        } else {
            offsetRad = Math.toRadians(inDir.angle-90);
            rad = pos / radius + offsetRad;
        }

        double x = Math.cos(rad) * radius;
        double y = -Math.sin(rad) * radius;
        return new Position(x,y).add(center).add(street.getPosition());
    }

    @Override
    public double getDirectionOnPos(double currentPosInTrack) {
        if (isRight) {
            return Math.toDegrees(currentPosInTrack/radius) - inDir.angle;
        } else {
            return -Math.toDegrees(currentPosInTrack/radius) - inDir.angle;
        }
    }

    @Override
    protected void renderTrack(AreaGraphicsContext agc) {
        agc.gc.strokeArc(center.x - radius, center.y - radius, radius*2, radius*2, isRight ? inDir.angle : inDir.angle-90, 90, ArcType.OPEN);

    }
}
