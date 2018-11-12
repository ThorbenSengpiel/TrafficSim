package de.trafficsim.logic.streets;

import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.gui.views.StreetTestView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackBezier;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;

public class StreetTestBezier extends Street {


    public StreetTestBezier() {
        this(Position.ZERO);
    }

    public StreetTestBezier(Position position) {
        super(position, StreetType.TEST_BEZIER);
        Track a = addInTrack(new TrackBezier(new Position(-25, 2.5), new Position(-5, 2.5), new Position(-20, 25), new Position(0, 25), this));
        Track b = addOutTrack(new TrackBezier(new Position(0, 25), new Position(0, 0), new Position(25, -10), new Position(25, 2.5), this));
        a.connectOutToInOf(b);

    }

    @Override
    public StreetView createView() {
        return new StreetTestView(this, new Hitbox(new Rectangle(Position.ZERO, 25, 25)));
    }
}
