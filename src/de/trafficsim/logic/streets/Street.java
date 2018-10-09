package de.trafficsim.logic.streets;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;

public abstract class Street {
    protected Position position;

    protected List<Track> tracks;

    public Street(Position position) {
        this.position = position;
        tracks = new ArrayList<>();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position p) {
        position = p;
    }
}
