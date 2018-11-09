package de.trafficsim.logic.vehicles;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Vehicle {
    protected double MIN_DIST = 10;
    protected int LOOKAHEAD_LIMIT = 1;

    protected double velocity = 1.0;
    protected double currentPosInTrack = 0;

    protected Track currentTrack;

    protected List<Track> path;

    private boolean active = true;
    public double color = 0;

    public boolean checkCollision(){
        if (getLookAheadDist() < MIN_DIST);
    }

    public Double getLookAheadDist(){
        List<Vehicle> vehicles = currentTrack.getVehiclesOnTrack();
        Double minDist = Double.POSITIVE_INFINITY;
        boolean vehFound = false;
        for (Vehicle vehicle : vehicles) {
            if (vehicle != this){
                double delta = vehicle.getCurrentPosInTrack() - currentPosInTrack;
                if(delta > 0){
                    minDist = (minDist > delta ? delta : minDist);
                    vehFound = true;
                }
            }
        }
        if (!vehFound){
            Track actTrack = getCurrentTrack();
            double accumulator = actTrack.getLength() -currentPosInTrack;
            Stack<Track> stack = new Stack<>();
            stack.push(actTrack);
            List<Track> visited = new ArrayList<>();

        }
        return minDist;
    }

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

    public double getCurrentPosInTrack() {
        return currentPosInTrack;
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
