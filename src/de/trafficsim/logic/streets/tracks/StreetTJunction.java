package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.views.StreetView;
import de.trafficsim.gui.views.TJunctionView;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetType;
import de.trafficsim.util.geometry.Position;

public class StreetTJunction extends Street {

    public StreetTJunction(Position position, StreetType type) {
        super(position, StreetType.T_JUNCTION);

        Track inLeft;
        Track outLeft;
        Track inRight;
        Track outRight;
        Track inBottom;
        Track outBottom;

        //create in- and outgoing tracks
        inLeft = addInTrack(new TrackStraight(new Position(-25, 2.5), new Position(-12.5, 2.5), this));
        outLeft = addOutTrack(new TrackStraight(new Position(-12.5, -2.5), new Position(-25, -2.5), this));
        inRight = addInTrack(new TrackStraight(new Position(25, -2.5), new Position(12.5, -2.5), this));
        outRight = addOutTrack(new TrackStraight(new Position(12.5, 2.5), new Position(25, 2.5), this));
        inBottom = addInTrack(new TrackStraight(new Position(2.5, 25), new Position(2.5, 12.5), this));
        outBottom = addOutTrack(new TrackStraight(new Position(-2.5, 12.5), new Position(-2.5, 25), this));

        //create tracks inbetween in- and outgoing tracks
        addTrackBetween(inLeft, outRight);
        addTrackBetween(inLeft, outBottom);

        addTrackBetween(inRight, outLeft);
        addTrackBetween(inRight, outBottom);

        addTrackBetween(inBottom, outRight);
        addTrackBetween(inBottom, outLeft);
    }

    @Override
    public StreetView createView() {
        return new TJunctionView(this);
    }

    @Override
    public Street createRotated() {
        return this;
    }
}