package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;
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
        isRight = inDir.isRightOf(outDir);
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
    protected void renderTrack(AreaGraphicsContext agc, Position f, Position t, Position offset) {


        //agc.gc.strokeLine(f.x, f.y, t.x, t.y);

        Position c = agc.areaToCanvas(center.add(offset));
        double r = agc.scaleToCanvas(radius);

        agc.gc.strokeArc(c.x - r, c.y - r, r*2, r*2, isRight ? inDir.angle : inDir.angle-90, 90, ArcType.OPEN);


        /*int angle;

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

        agc.gc.strokeArc(c.x-w, c.y-h, w*2, h*2, angle, 90, ArcType.OPEN);*/
        if (vehiclesOnTrack.size() > 0) {
            agc.setStroke(Color.RED);
            agc.gc.strokeLine(f.x, f.y, f.x+inDir.vector.x * 40, f.y+inDir.vector.y * 40);
            agc.gc.strokeLine(t.x, t.y, t.x+outDir.vector.x * 40, t.y+outDir.vector.y * 40);
        }
    }
}
