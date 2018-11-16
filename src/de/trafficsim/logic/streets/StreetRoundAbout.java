package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetRoundAboutView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackBezier;
import de.trafficsim.logic.streets.tracks.TrackCurve;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

import java.lang.reflect.InvocationTargetException;

public class StreetRoundAbout extends Street {

    public StreetRoundAbout() {
        this(Position.ZERO, true);
    }

    public StreetRoundAbout(Position position, boolean right) {
        super(position, StreetType.ROUNDABOUT);

        Track inWest = addInTrack(new TrackBezier(new Position(-25, 2.5), new Position(-25, 2.5), Direction.EAST, new Position(-10, 5), new Position(-12.5, 2.5), Direction.SOUTH, this));
        Track outWest = addOutTrack(new TrackBezier(new Position(-10, -5), new Position(-12.5, -2.5), Direction.SOUTH, new Position(-25, -2.5), new Position(-25, -2.5), Direction.WEST, this));

        Track inEast = addInTrack(new TrackBezier(new Position(25, -2.5), new Position(25, -2.5), Direction.WEST, new Position(10, -5), new Position(12.5, -2.5), Direction.NORTH, this));
        Track outEast = addOutTrack(new TrackBezier(new Position(10, 5), new Position(12.5, 2.5), Direction.NORTH, new Position(25, 2.5), new Position(25, 2.5), Direction.EAST, this));

        Track inNorth = addInTrack(new TrackBezier(new Position(-2.5, -25), new Position(-2.5, -25), Direction.SOUTH, new Position(-5, -10), new Position(-2.5, -12.5), Direction.WEST, this));
        Track outNorth = addOutTrack(new TrackBezier(new Position(5, -10), new Position(2.5, -12.5), Direction.WEST, new Position(2.5, -25), new Position(2.5, -25), Direction.NORTH, this));

        Track inSouth = addInTrack(new TrackBezier(new Position(2.5, 25), new Position(2.5, 25), Direction.NORTH, new Position(5, 10),  new Position(2.5, 12.5),Direction.EAST, this));
        Track outSouth = addOutTrack(new TrackBezier(new Position(-5, 10), new Position(-2.5, 12.5), Direction.EAST, new Position(-2.5, 25), new Position(-2.5, 25), Direction.SOUTH, this));

        Track w0 = addTrack(new TrackBezier(new Position(-10, -5), new Position(-11.25, -2.5), Direction.SOUTH, new Position(-10, 5), new Position(-11.25, 2.5), Direction.SOUTH, this));
        Track w1 = addTrack(new TrackBezier(new Position(-10, 5), new Position(-8.75, 7.5), Direction.SOUTH, new Position(-5, 10), new Position(-7.5, 8.75), Direction.EAST, this));

        Track n0 = addTrack(new TrackBezier(new Position(-10, -5).rotate(Direction.EAST), new Position(-11.25, -2.5).rotate(Direction.EAST), Direction.WEST, new Position(-10, 5).rotate(Direction.EAST), new Position(-11.25, 2.5).rotate(Direction.EAST), Direction.WEST, this));
        Track n1 = addTrack(new TrackBezier(new Position(-10, 5).rotate(Direction.EAST), new Position(-8.75, 7.5).rotate(Direction.EAST), Direction.WEST, new Position(-5, 10).rotate(Direction.EAST), new Position(-7.5, 8.75).rotate(Direction.EAST), Direction.SOUTH, this));

        Track e0 = addTrack(new TrackBezier(new Position(-10, -5).rotate(Direction.SOUTH), new Position(-11.25, -2.5).rotate(Direction.SOUTH), Direction.NORTH, new Position(-10, 5).rotate(Direction.SOUTH), new Position(-11.25, 2.5).rotate(Direction.SOUTH), Direction.NORTH, this));
        Track e1 = addTrack(new TrackBezier(new Position(-10, 5).rotate(Direction.SOUTH), new Position(-8.75, 7.5).rotate(Direction.SOUTH), Direction.NORTH, new Position(-5, 10).rotate(Direction.SOUTH), new Position(-7.5, 8.75).rotate(Direction.SOUTH), Direction.WEST, this));

        Track s0 = addTrack(new TrackBezier(new Position(-10, -5).rotate(Direction.WEST), new Position(-11.25, -2.5).rotate(Direction.WEST), Direction.EAST, new Position(-10, 5).rotate(Direction.WEST), new Position(-11.25, 2.5).rotate(Direction.WEST), Direction.EAST, this));
        Track s1 = addTrack(new TrackBezier(new Position(-10, 5).rotate(Direction.WEST), new Position(-8.75, 7.5).rotate(Direction.WEST), Direction.EAST, new Position(-5, 10).rotate(Direction.WEST), new Position(-7.5, 8.75).rotate(Direction.WEST), Direction.NORTH, this));

        w0.connectOutToInOf(w1);
        w1.connectOutToInOf(s0);
        inWest.connectOutToInOf(w1);
        n1.connectOutToInOf(outWest);

        s0.connectOutToInOf(s1);
        s1.connectOutToInOf(e0);
        inSouth.connectOutToInOf(s1);
        w1.connectOutToInOf(outSouth);

        e0.connectOutToInOf(e1);
        e1.connectOutToInOf(n0);
        inEast.connectOutToInOf(e1);
        s1.connectOutToInOf(outEast);

        n0.connectOutToInOf(n1);
        n1.connectOutToInOf(w0);
        inNorth.connectOutToInOf(n1);
        e1.connectOutToInOf(outNorth);


    }

    @Override
    public StreetView createView() {
        return new StreetRoundAboutView(this);
    }
}
