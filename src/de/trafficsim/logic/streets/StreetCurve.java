package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetCurveView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public class StreetCurve extends Street {
    public StreetCurve() {
        this(Position.ZERO, Direction.NORTH);
    }

    public StreetCurve(Position position, Direction rotation) {
        super(position, StreetType.CURVE, rotation);

        Track inLeft;
        Track outLeft;
        Track inBottom;
        Track outBottom;

        //create in- and outgoing tracks
        inLeft = addInTrack(new TrackStraight(createPosition(25, -2.5), createPosition(12.5, -2.5), this));
        outLeft = addOutTrack(new TrackStraight(createPosition(12.5, 2.5), createPosition(25, 2.5), this));
        inBottom = addInTrack(new TrackStraight(createPosition(2.5, 25), createPosition(2.5, 12.5), this));
        outBottom = addOutTrack(new TrackStraight(createPosition(-2.5, 12.5), createPosition(-2.5, 25), this));

        //create tracks inbetween in- and outgoing tracks
        addTrackBetween(inLeft, outBottom);
        addTrackBetween(inBottom, outLeft);
    }

    @Override
    public StreetView createView() {
        return new StreetCurveView(this);
    }

    @Override
    public Street createRotated() {
        return new StreetCurve(position, rotation.rotateClockWise());
    }
}
