package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class Track {

    protected double length;

    protected Position from;

    protected Position to;
    protected List<Vehicle> vehiclesOnTrack;

    protected List<Track> inTracks;

    protected List<Track> outTracks;

    public Track(Position from, Position to, double length) {
        this.from = from;
        this.to = to;
        this.length = length;
        vehiclesOnTrack = new ArrayList<>();
        inTracks = new ArrayList<>();
        outTracks = new ArrayList<>();
    }

    public void connectOutToInOf(Track track) {
        track.inTracks.add(this);
        outTracks.add(track);
    }

    public void render(AreaGraphicsContext agc, Position offset) {
        Position f = agc.areaToCanvas(from.add(offset));
        Position t = agc.areaToCanvas(to.add(offset));
        if (inTracks.size() > 0) {
            agc.gc.strokeOval(f.x - 5, f.y - 5, 10, 10);
        }
        renderTrack(agc, f, t);
    }
    protected abstract void renderTrack(AreaGraphicsContext agc, Position f, Position t);
}
