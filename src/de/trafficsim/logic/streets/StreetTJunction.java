package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetView;
import de.trafficsim.gui.views.StreetTJunctionView;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetType;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackAndPosition;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.logic.streets.tracks.TrafficPriorityChecker;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public class StreetTJunction extends Street {

    public StreetTJunction() {
        this(Position.ZERO, Direction.NORTH);
    }

    public StreetTJunction(Position position, Direction rotation) {
        super(position, StreetType.T_JUNCTION, rotation);

        Track inLeft;
        Track outLeft;
        Track inRight;
        Track outRight;
        Track inBottom;
        Track outBottom;

        //create in- and outgoing tracks
        inRight = addInTrack(new TrackStraight(createPosition(-25, 2.5), createPosition(-12.5, 2.5), this));
        outRight = addOutTrack(new TrackStraight(createPosition(-12.5, -2.5), createPosition(-25, -2.5), this));
        inLeft = addInTrack(new TrackStraight(createPosition(25, -2.5), createPosition(12.5, -2.5), this));
        outLeft = addOutTrack(new TrackStraight(createPosition(12.5, 2.5), createPosition(25, 2.5), this));
        inBottom = addInTrack(new TrackStraight(createPosition(2.5, 25), createPosition(2.5, 12.5), this));
        outBottom = addOutTrack(new TrackStraight(createPosition(-2.5, 12.5), createPosition(-2.5, 25), this));

        Track rightToLeft = addTrackBetween(inRight, outLeft);
        Track rightToBottom = addTrackBetween(inRight, outBottom);

        Track leftToRight = addTrackBetween(inLeft, outRight);
        Track leftToBottom = addTrackBetween(inLeft, outBottom);

        Track bottomToRight = addTrackBetween(inBottom, outRight);
        Track bottomToLeft  = addTrackBetween(inBottom, outLeft);


        bottomToRight.setPriorityStopPoint(new TrafficPriorityChecker(bottomToRight, 5, new TrackAndPosition(leftToBottom, 14.75)));
        leftToBottom.setPriorityStopPoint(new TrafficPriorityChecker(leftToBottom, 5, new TrackAndPosition(rightToLeft, 13.75)));
        rightToLeft.setPriorityStopPoint(new TrafficPriorityChecker(rightToLeft, 5, new TrackAndPosition(bottomToRight, 11)));
    }

    @Override
    public StreetView createView() {
        return new StreetTJunctionView(this);
    }

    @Override
    public Street createRotated() {
        return new StreetTJunction(position, rotation.rotateClockWise());
    }
}
