package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetStraight2LaneView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public class StreetStraight2Lane extends StreetTwoPositions {

    private double length;

    public StreetStraight2Lane() {
        this(Position.ZERO, 25, Direction.NORTH);
    }

    public StreetStraight2Lane(Position position, double length, Direction direction) {
        super(position, StreetType.STRAIGHT_2_LANE, direction);
        this.length = length;
        addInOutTrack(new TrackStraight(new Position(-2.5, 0).rotate(direction), new Position(-2.5, length).rotate(direction), this));
        addInOutTrack(new TrackStraight(new Position(2.5, length).rotate(direction), new Position(2.5, 0).rotate(direction), this));

    }

    @Override
    public StreetView createView() {
        return new StreetStraight2LaneView(this);
    }

    @Override
    public Street createRotated() {
        return new StreetStraight2Lane(position, length, rotation.rotateClockWise());
    }

    public double getLength() {
        return length;
    }


    @Override
    public StreetTwoPositions createChanged(Position pos2nd) {
        Direction direction;
        Position dir = position.sub(pos2nd);
        if (Math.abs(dir.x) > Math.abs(dir.y)) {
            if (dir.x > 0) {
                direction = Direction.EAST;
            } else {
                direction = Direction.WEST;
            }
        } else {
            if (dir.y > 0) {
                direction = Direction.SOUTH;
            } else {
                direction = Direction.NORTH;
            }
        }
        double length;
        if (direction.isHorizontal()) {
            length = Math.abs(dir.x);
        } else {
            length = Math.abs(dir.y);
        }
        return new StreetStraight2Lane(position, length, direction);
    }
}
