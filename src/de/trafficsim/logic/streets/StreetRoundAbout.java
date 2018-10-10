package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetRoundAboutView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackCurve;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

import java.lang.reflect.InvocationTargetException;

public class StreetRoundAbout extends Street {
    public StreetRoundAbout(Position position) {
        super(position, StreetType.ROUNDABOUT);

        Track r0 = new TrackCurve(new Position(-50, 0), new Position(0, -50), Direction.NORTH,this);
        Track r1 = new TrackCurve(new Position(0, -50), new Position(50, 0), Direction.EAST,this);
        Track r2 = new TrackCurve(new Position(50, 0), new Position(0, 50), Direction.SOUTH,this);
        Track r3 = new TrackCurve(new Position(0, 50), new Position(-50, 0), Direction.WEST,this);

        r0.connectOutToInOf(r1);
        r1.connectOutToInOf(r2);
        r2.connectOutToInOf(r3);
        r3.connectOutToInOf(r0);

        tracks.add(r0);
        tracks.add(r1);
        tracks.add(r2);
        tracks.add(r3);
    }

    @Override
    public StreetView createView() {
        return new StreetRoundAboutView(this);
    }
}
