package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetCrossTestView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.geometry.Position;

public class StreetTestCross extends Street {

    public StreetTestCross(Position position) {
        super(position, StreetType.TEST_CROSS);

        Track inWest = addTrack(new TrackStraight(new Position(-25, 2.5), new Position(-12.5, 2.5), this));
        Track outWest = addTrack(new TrackStraight(new Position(-12.5, -2.5), new Position(-25, -2.5), this));
        Track inEast = addTrack(new TrackStraight(new Position(25, -2.5), new Position(12.5, -2.5), this));
        Track outEast = addTrack(new TrackStraight(new Position(12.5, 2.5), new Position(25, 2.5), this));

        Track inNorth = addTrack(new TrackStraight(new Position(-2.5, -25), new Position(-2.5, -12.5), this));
        Track outNorth = addTrack(new TrackStraight(new Position(2.5, -12.5), new Position(2.5, -25), this));
        Track inSouth = addTrack(new TrackStraight(new Position(2.5, 25), new Position(2.5, 12.5), this));
        Track outSouth = addTrack(new TrackStraight(new Position(-2.5, 12.5), new Position(-2.5, 25), this));

        addTrackBetween(inWest, outNorth);
        addTrackBetween(inWest, outEast);
        addTrackBetween(inWest, outSouth);

        addTrackBetween(inEast, outNorth);
        addTrackBetween(inEast, outSouth);
        addTrackBetween(inEast, outWest);

        addTrackBetween(inNorth, outSouth);
        addTrackBetween(inNorth, outEast);
        addTrackBetween(inNorth, outWest);

        addTrackBetween(inSouth, outNorth);
        addTrackBetween(inSouth, outEast);
        addTrackBetween(inSouth, outWest);

        addTrackBetween(outNorth, inNorth);
        addTrackBetween(outEast, inEast);
        addTrackBetween(outSouth, inSouth);
        addTrackBetween(outWest, inWest);

    }

    @Override
    public StreetView createView() {
        return new StreetCrossTestView(this);
    }
}
