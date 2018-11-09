package de.trafficsim.logic.vehicles;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.List;

public class Vehicle {

    protected double velocity = 1.0;
    protected double currentPosInTrack = 0;

    protected Track currentTrack;

    protected List<Track> path;
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
        this.color = 1;
    }

    public void move(double delta) {
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

    public void drivePath(double delta) {
        double newPositionInCurrentTrack = currentPosInTrack + velocity * delta;
        if (path.get(0).getLength() < newPositionInCurrentTrack) {
            if (path.size() > 1) {
                double distanceInNewTrack = newPositionInCurrentTrack - path.get(0).getLength();
                currentPosInTrack = distanceInNewTrack;
                path.get(0).removeVehicle(this);
                path.get(1).addVehicle(this);
                path.remove(0);
            } else {
                active = false;
            }
        } else {
            currentPosInTrack = newPositionInCurrentTrack;
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
}
