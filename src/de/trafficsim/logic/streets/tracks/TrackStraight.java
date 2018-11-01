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
    public void renderTrack(AreaGraphicsContext agc, Position f, Position t, Position offset) {
        agc.gc.strokeLine(f.x, f.y, t.x, t.y);
        Position middle = new Position((f.x + t.x) / 2, (f.y + t.y) / 2);
        switch (inDir) {
            case NORTH:
                agc.gc.strokeLine(middle.x, middle.y-5, middle.x + 5, middle.y+5);
                agc.gc.strokeLine(middle.x, middle.y-5, middle.x - 5, middle.y+5);
                break;
            case EAST:
                agc.gc.strokeLine(middle.x+5, middle.y, middle.x-5, middle.y+5);
                agc.gc.strokeLine(middle.x+5, middle.y, middle.x-5, middle.y-5);
                break;
            case SOUTH:
                agc.gc.strokeLine(middle.x, middle.y+5, middle.x + 5, middle.y-5);
                agc.gc.strokeLine(middle.x, middle.y+5, middle.x - 5, middle.y-5);
                break;
            case WEST:
                agc.gc.strokeLine(middle.x-5, middle.y, middle.x+5, middle.y+5);
                agc.gc.strokeLine(middle.x-5, middle.y, middle.x+5, middle.y-5);
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
