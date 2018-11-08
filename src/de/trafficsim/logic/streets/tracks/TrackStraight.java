package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public class TrackStraight extends Track {

    public TrackStraight(Position from, Position to, Street street) {
        super(from, to, from.distance(to),street);
        inDir = Direction.generateDirectionStraight(from,to);
        outDir = inDir;
        if (from.x != to.x && from.y != to.y) {
            throw new RuntimeException("TrackStraight can only be straight");
        }
    }

    @Override
    public void renderTrack(AreaGraphicsContext agc) {
        agc.gc.strokeLine(from.x, from.y, to.x, to.y);
        Position middle = new Position((from.x + to.x) / 2, (from.y + to.y) / 2);
        double a = 2.5;
        switch (inDir) {
            case NORTH:
                agc.gc.strokeLine(middle.x, middle.y-a, middle.x + a, middle.y+a);
                agc.gc.strokeLine(middle.x, middle.y-a, middle.x - a, middle.y+a);
                break;
            case EAST:
                agc.gc.strokeLine(middle.x+a, middle.y, middle.x-a, middle.y+a);
                agc.gc.strokeLine(middle.x+a, middle.y, middle.x-a, middle.y-a);
                break;
            case SOUTH:
                agc.gc.strokeLine(middle.x, middle.y+a, middle.x + a, middle.y-a);
                agc.gc.strokeLine(middle.x, middle.y+a, middle.x - a, middle.y-a);
                break;
            case WEST:
                agc.gc.strokeLine(middle.x-a, middle.y, middle.x+a, middle.y+a);
                agc.gc.strokeLine(middle.x-a, middle.y, middle.x+a, middle.y-a);
                break;
            default:
                break;
        }
    }

    @Override
    public Position getPosOnArea(double pos) {
        if (inDir == Direction.NORTH || inDir == Direction.SOUTH ) {
            return new Position(from.x,from.y + inDir.vector.y * pos).add(street.getPosition());
        } else {
            return new Position(from.x + inDir.vector.x * pos,from.y).add(street.getPosition());
        }
    }

    @Override
    public double getDirectionOnPos(double currentPosInTrack) {
        return -inDir.angle;
    }
}
