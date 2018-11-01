package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class Track {
    protected final Street street;
    protected Direction inDir;
    protected Direction outDir;

    protected double length;

    protected Position from;

    protected Position to;
    protected List<Vehicle> vehiclesOnTrack = new ArrayList<>();

    protected List<Track> inTrackList = new ArrayList<>();

    protected List<Track> outTrackList = new ArrayList<>();

    public Track(Position from, Position to, double length, Street street) {
        this.from = from;
        this.to = to;
        this.length = length;
        this.street = street;
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

        if (vehiclesOnTrack.size() > 0) {
            agc.gc.setStroke(Color.LIME);
            agc.gc.strokeOval(f.x - 5, f.y - 5, 10, 10);
        } else {
            agc.gc.setStroke(Color.CYAN.deriveColor(0, 1, 1, 0.2));
        }

        renderTrack(agc, f, t, offset);
    }

    /**
     * Track Spezifische Visualisierung
     * @param agc
     * @param f
     * @param t
     * @param offset
     */
    protected abstract void renderTrack(AreaGraphicsContext agc, Position f, Position t, Position offset);
    public abstract Position getPosOnArea(double pos);
    public double getLength() {
        return length;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public List<Track> getInTrackList() {
        return inTrackList;
    }

    public List<Track> getOutTrackList() {
        return outTrackList;
    }

    public abstract double getDirectionOnPos(double currentPosInTrack);

    public void removeVehicle(Vehicle vehicle) {
        vehiclesOnTrack.remove(vehicle);
    }

    public void addVehicle(Vehicle vehicle) {
        vehiclesOnTrack.add(vehicle);
    }

    public Direction getOutDir() {
        return outDir;
    }

    public Direction getInDir() {
        return inDir;
    }
}
