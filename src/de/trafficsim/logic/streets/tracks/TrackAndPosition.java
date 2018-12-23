package de.trafficsim.logic.streets.tracks;

public class TrackAndPosition {
    private Track track;
    private double position;

    public TrackAndPosition(Track track, double position) {
        this.track = track;
        this.position = position;
    }

    public Track getTrack() {
        return track;
    }

    public double getPosition() {
        return position;
    }
}
