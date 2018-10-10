package de.trafficsim.logic.vehicles;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.List;

public class Vehicle {

    protected double velocity = 1.0;
    protected double currentPosInTrack = 0;
    protected Track currentTrack;
    protected List<Track> path;

    public Vehicle(double velocity,Track track){
        this.velocity = velocity;
        this.currentTrack = track;
    }
    public Position getPosition(){
        return currentTrack.getPosOnArea(currentPosInTrack);
    }
    public Position getDirection(){
        return new Position(1,0);
    }
    public void move(double delta){
        double newPositionInCurrentTrack = currentPosInTrack+velocity * delta;
        if (currentTrack.getLength()< newPositionInCurrentTrack){
            Track nextTrack = currentTrack.getOutTrackList().get(0);
            double distanceInNewTrack = newPositionInCurrentTrack-currentTrack.getLength();
            currentPosInTrack = distanceInNewTrack;
            currentTrack = nextTrack;
        } else {
            currentPosInTrack = newPositionInCurrentTrack;
        }
    }

}
