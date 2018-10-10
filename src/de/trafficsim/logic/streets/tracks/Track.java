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
    protected List<Vehicle> vehiclesOnTrack = new ArrayList<>();

    protected List<Track> inTrackList = new ArrayList<>();

    protected List<Track> outTrackList = new ArrayList<>();

    public Track(Position from, Position to, double length) {
        this.from = from;
        this.to = to;
        this.length = length;
    }

    public void connectOutToInOf(Track track) {
        track.inTrackList.add(this);
        outTrackList.add(track);
    }


    /**
     * Rendert Grundlegene Tackvisualisierung (Connection Punkte)
     * @param agc
     * @param offset
     */
    public void render(AreaGraphicsContext agc, Position offset) {
        Position f = agc.areaToCanvas(from.add(offset));
        Position t = agc.areaToCanvas(to.add(offset));
        agc.gc.setLineWidth(1.5);
        if (inTrackList.size() < 1) {
            agc.gc.setStroke(Color.LIME);
            agc.gc.strokeOval(f.x - 5, f.y - 5, 10, 10);
        }
        if (outTrackList.size() < 1) {
            agc.gc.setStroke(Color.RED);
            agc.gc.strokeOval(t.x - 5, t.y - 5, 10, 10);
        }


        agc.gc.setStroke(Color.YELLOW);
        if (inTrackList.size() > 0) {
            agc.gc.strokeLine(f.x - 7, f.y - 7, f.x + 7, f.y + 7);
            agc.gc.strokeLine(f.x - 7, f.y + 7, f.x + 7, f.y - 7);
        }

        agc.gc.setStroke(Color.CYAN);

        renderTrack(agc, f, t);
    }

    /**
     * Track Spezifische Visualisierung
     * @param agc
     * @param f
     * @param t
     */
    protected abstract void renderTrack(AreaGraphicsContext agc, Position f, Position t);
}
