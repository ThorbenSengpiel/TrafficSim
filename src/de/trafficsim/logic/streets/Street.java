package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackCurve;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;

public abstract class Street {
    protected Position position;

    private List<Track> tracks;

    private List<Track> inTracks;

    private List<Track> outTracks;
    public final StreetType type;

    public Street(Position position, StreetType type) {
        this.position = position;
        this.type = type;
        tracks = new ArrayList<>();
        inTracks = new ArrayList<>();
        outTracks = new ArrayList<>();

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

    protected Track addInOutTrack(Track track) {
        inTracks.add(track);
        outTracks.add(track);
        return addTrack(track);
    }

    protected Track addInTrack(Track track) {
        inTracks.add(track);
        return addTrack(track);
    }

    protected Track addOutTrack(Track track) {
        outTracks.add(track);
        return addTrack(track);
    }

    protected Track addTrack(Track track) {
        tracks.add(track);
        return track;
    }

    protected void addTracks(Track... tracks) {
        for (Track track : tracks) {
            addTrack(track);
        }
    }

    protected Track addTrackBetween(Track from, Track to) {
        Track track;
        if (from.getOutDir().isHorizontal() ^ to.getInDir().isHorizontal()) {
            track = new TrackCurve(from.getTo(), to.getFrom(), from.getOutDir(), this);
        } else {
            track = new TrackStraight(from.getTo(), to.getFrom(), this);
        }
        from.connectOutToInOf(track); track.connectOutToInOf(to);
        return addTrack(track);
    }

    public void disconnect() {
        for (Track track : inTracks) {
            track.disconnectAllIngoing();
        }
        for (Track track : outTracks) {
            track.disconnectAllOutgoing();
        }
    }

    public abstract StreetView createView();

    public List<Track> getInTracks() {
        return inTracks;
    }

    public List<Track> getOutTracks() {
        return outTracks;
    }
}
