package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetRoundAboutView;
import de.trafficsim.gui.views.StreetStraightView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.geometry.Position;

import java.lang.reflect.InvocationTargetException;

public class StreetStraight extends Street {

    private Position from;
    private Position to;

    public StreetStraight(Position from, Position to) {
        super(new Position((from.x + to.x)/2, (from.y + to.y)/2), StreetType.STRAIGHT);
        addInOutTrack(new TrackStraight(from.sub(position), to.sub(position),this));
        this.from = from;
        this.to = to;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    @Override
    public StreetView createView() {
        return new StreetStraightView(this);
    }
}
