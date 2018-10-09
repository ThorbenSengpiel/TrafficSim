package de.trafficsim.logic.streets.tracks;

import de.trafficsim.logic.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public abstract class Track {

    protected double length;

    protected List<Vehicle> vehiclesOnTrack;

    protected List<Track> inTracks;
    protected List<Track> outTracks;

    public Track() {
        vehiclesOnTrack = new ArrayList<>();
        inTracks = new ArrayList<>();
        outTracks = new ArrayList<>();
    }

    public void connectTo(Track track) {
        track.inTracks.add(this);
        outTracks.add(track);
    }

}
