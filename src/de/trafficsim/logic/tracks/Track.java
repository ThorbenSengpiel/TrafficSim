package de.trafficsim.logic.tracks;

import de.trafficsim.util.geometry.Position;

public abstract class Track {
    protected Position position;

    public Track(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position p) {
        position = p;
    }
}
