package de.trafficsim.logic.vehicles;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.List;

public class Vehicle {

    protected double velocity;
    protected List<Track> path;

    public Position getPosition(){
        return new Position(0,0);
    }
    public Position getDirection(){
        return new Position(1,0);
    }
}
