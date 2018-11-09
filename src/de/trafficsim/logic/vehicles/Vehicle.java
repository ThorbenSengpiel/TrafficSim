package de.trafficsim.logic.vehicles;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.List;

public class Vehicle {

    protected double velocity = 1.0;
    protected double currentPosInTrack = 0;

    protected Track currentTrack;

    protected List<Track> path;
    private int currentTrackNumber;

    private boolean active = true;
    public double color = 0;

    public Vehicle(double velocity, Track track){
        this.velocity = velocity;
        this.currentTrack = track;
        color = Math.random();
    }

    public Vehicle(double velocity, List<Track> path){
        this.velocity = velocity;
        this.currentTrack = path.get(0);
        this.path = path;
        this.color = Math.random();
    }

    public void move(double delta) {
        if (path != null) {
            double newPositionInCurrentTrack = currentPosInTrack + velocity * delta;
            if (currentTrack.getLength() < newPositionInCurrentTrack) {
                currentTrackNumber++;

                if (currentTrackNumber < path.size() && currentTrack.getOutTrackList().size() > 0) {
                    Track nextTrack = path.get(currentTrackNumber);
                    double distanceInNewTrack = newPositionInCurrentTrack - currentTrack.getLength();
                    currentPosInTrack = distanceInNewTrack;
                    currentTrack.removeVehicle(this);
                    nextTrack.addVehicle(this);
                    currentTrack = nextTrack;
                } else {
                    active = false;
                }
            } else {
                currentPosInTrack = newPositionInCurrentTrack;
            }
        } else {
            double newPositionInCurrentTrack = currentPosInTrack + velocity * delta;
            if (currentTrack.getLength() < newPositionInCurrentTrack) {
                if (currentTrack.getOutTrackList().size() > 0) {
                    Track nextTrack = currentTrack.getOutTrackList().get((int) (Math.random() * currentTrack.getOutTrackList().size()));
                    double distanceInNewTrack = newPositionInCurrentTrack - currentTrack.getLength();
                    currentPosInTrack = distanceInNewTrack;
                    currentTrack.removeVehicle(this);
                    nextTrack.addVehicle(this);
                    currentTrack = nextTrack;
                } else {
                    active = false;
                }
            } else {
                currentPosInTrack = newPositionInCurrentTrack;
            }
        }
    }
    public boolean isActive() {
        return active;
    }

    public Position getPosition() {
        return currentTrack.getPosOnArea(currentPosInTrack);
    }

    public double getDirection() {
        return currentTrack.getDirectionOnPos(currentPosInTrack);
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public List<Track> getPath() {
        return path;
    }
}
