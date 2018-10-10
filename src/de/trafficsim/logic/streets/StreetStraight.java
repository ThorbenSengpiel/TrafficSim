package de.trafficsim.logic.streets;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.geometry.Position;

public class StreetStraight extends Street {

    private Position from;
    private Position to;

    public StreetStraight(Position from, Position to) {
        super(new Position((from.x + to.x)/2, (from.y + to.y)/2));
        tracks.add(new TrackStraight(from, to));
        this.from = from;
        this.to = to;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }
}
