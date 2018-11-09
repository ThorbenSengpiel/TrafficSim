package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetParkingDeckView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

public class StreetParkingDeck extends StreetSpawn {

    private Track start;
    private Track end;

    public StreetParkingDeck() {
        this(Position.ZERO, Direction.NORTH);
    }


    public StreetParkingDeck(Position position, Direction rotation) {
        super(position, StreetType.PARKING_DECK, rotation);
        start = addOutTrack(new TrackStraight(createPosition(0, -2.5), createPosition(-25, -2.5), this));
        end = addInTrack(new TrackStraight(createPosition(-25, 2.5), createPosition(0, 2.5), this));
    }

    @Override
    public StreetView createView() {
        return new StreetParkingDeckView(this);
    }

    @Override
    public Street createRotated() {
        return new StreetParkingDeck(position, rotation.rotateClockWise());
    }

    @Override
    public Track getStartTrack() {
        return start;
    }

    @Override
    public Track getEndTrack() {
        return end;
    }
}
