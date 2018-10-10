package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;

public abstract class Street {
    protected Position position;

    protected List<Track> tracks;

    public final StreetType type;

    public Street(Position position, StreetType type) {
        this.position = position;
        this.type = type;
        tracks = new ArrayList<>();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position p) {
        position = p;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public abstract StreetView createView();
}
