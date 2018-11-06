package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public abstract class StreetSpawn extends Street {

    public StreetSpawn(Position position, StreetType type, Direction rotation) {
        super(position, type, rotation);
    }

    public abstract Track getStartTrack();

    public abstract Track getEndTrack();
}
