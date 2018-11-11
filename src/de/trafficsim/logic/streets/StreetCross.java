package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetCrossView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.geometry.Position;

public class StreetCross extends Street {

    //todo var's moven
    public Track inWest;
    public Track outWest;
    public Track inEast;
    public Track outEast;

    public Track inNorth;
    public Track outNorth;
    public Track inSouth;
    public Track outSouth;

    public StreetCross() {
        this(Position.ZERO);
    }

    public StreetCross(Position position) {
        super(position, StreetType.CROSS);

        //create in- and outgoing tracks
        inWest = addInTrack(new TrackStraight(new Position(-25, 2.5), new Position(-12.5, 2.5), this));
        outWest = addOutTrack(new TrackStraight(new Position(-12.5, -2.5), new Position(-25, -2.5), this));
        inEast = addInTrack(new TrackStraight(new Position(25, -2.5), new Position(12.5, -2.5), this));
        outEast = addOutTrack(new TrackStraight(new Position(12.5, 2.5), new Position(25, 2.5), this));

        inNorth = addInTrack(new TrackStraight(new Position(-2.5, -25), new Position(-2.5, -12.5), this));
        outNorth = addOutTrack(new TrackStraight(new Position(2.5, -12.5), new Position(2.5, -25), this));
        inSouth = addInTrack(new TrackStraight(new Position(2.5, 25), new Position(2.5, 12.5), this));
        outSouth = addOutTrack(new TrackStraight(new Position(-2.5, 12.5), new Position(-2.5, 25), this));

        //create tracks inbetween in- and outgoing tracks
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

        /*addTrackBetween(outNorth, inNorth);
        addTrackBetween(outEast, inEast);
        addTrackBetween(outSouth, inSouth);
        addTrackBetween(outWest, inWest);*/

    }

    @Override
    public StreetView createView() {
        return new StreetCrossView(this);
    }
}
