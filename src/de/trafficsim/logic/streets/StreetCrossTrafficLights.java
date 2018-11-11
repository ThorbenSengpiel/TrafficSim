package de.trafficsim.logic.streets;

import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.gui.views.StreetTestView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;

public class StreetCrossTrafficLights extends Street {

    public StreetCrossTrafficLights() {
        this(Position.ZERO);
    }

    public StreetCrossTrafficLights(Position position) {
        super(position, StreetType.CROSS_TRAFFICLIGHTS);

        Track inWest = addInTrack(new TrackStraight(new Position(-50, 2.5), new Position(-40, 2.5), this));
        Track outWest = addOutTrack(new TrackStraight(new Position(-40, -2.5), new Position(-50, -2.5), this));
        Track inEast = addInTrack(new TrackStraight(new Position(50, -2.5), new Position(40, -2.5), this));
        Track outEast = addOutTrack(new TrackStraight(new Position(40, 2.5), new Position(50, 2.5), this));

        Track inNorth = addInTrack(new TrackStraight(new Position(-2.5, -50), new Position(-2.5, -40), this));
        Track outNorth = addOutTrack(new TrackStraight(new Position(2.5, -40), new Position(2.5, -50), this));
        Track inSouth = addInTrack(new TrackStraight(new Position(2.5, 50), new Position(2.5, 40), this));
        Track outSouth = addOutTrack(new TrackStraight(new Position(-2.5, 40), new Position(-2.5, 50), this));
        
    }

    @Override
    public StreetView createView() {
        return new StreetTestView(this, new Hitbox(new Rectangle(Position.ZERO, 50, 15), new Rectangle(Position.ZERO, 15, 50)));
    }
}
