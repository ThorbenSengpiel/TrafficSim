package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public class TrackStraight extends Track {

    public TrackStraight(Position from, Position to, Street street) {
        super(from, to, from.distance(to),street);
        inDir = Direction.generateDirection(from,to);
        outDir = inDir;
        if (from.x != to.x && from.y != to.y) {
            throw new RuntimeException("TrackStraight can only be straight");
        }
    }

    @Override
    public void renderTrack(AreaGraphicsContext agc, Position f, Position t) {
        agc.gc.strokeLine(f.x, f.y, t.x, t.y);
        if (f.x == t.x) {
            agc.gc.strokeLine(f.x, f.y, t.x, t.y);
        } else {
            double middle = (t.x + f.x) / 2;
            agc.gc.strokeLine(middle-5, f.y, middle+5, t.y-5);
            agc.gc.strokeLine(middle-5, f.y, middle+5, t.y+5);
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
    public Position getDirectionOnPos(double currentPosInTrack) {
        return inDir.vector;
    }
}
