package de.trafficsim.logic.vehicles;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Vehicle {
    protected double MIN_DIST = 10;
    protected int LOOKAHEAD_LIMIT = 1;

    protected double velocity = 1.0;
    protected double currentPosInTrack = 0;

    protected Track currentTrack;

    protected List<Track> path;
    private int currentTrackNumber;

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
        // if an vehicle was already found on the last Track there is no need to check the upcoming Tracks
        // because their distance has to be higher
        if (!vehFound){
            Track actTrack = getCurrentTrack();
            double accumulator = actTrack.getLength() -currentPosInTrack;
            Stack<Track> stack = new Stack<>();
            stack.push(actTrack);
            List<Track> visited = new ArrayList<>();
            while(!stack.empty()){
                //List of all Tracks that weren't already checked and so would have been put into the Hashmap
                List<Track> remaining = actTrack.getOutTrackList().stream().filter(e -> !visited.contains(e)).collect(Collectors.toList());
                //If there is no Element in the List. The Last Track was Part of a Spawn or didn't yield to another Track which
                //hasn't been checked
                if(remaining.isEmpty()){
                    //pop the Element of the Stack and decrement the accumulator because the distance was previously added
                    Track formertrack = stack.pop();
                    accumulator -= accumulator;
                } else {
                    // if this is not the first Track the full length of the last Track has to be added
                    if (actTrack != getCurrentTrack()){
                        accumulator += actTrack.getLength();
                    }
                    //is the distance to this track still less than the Minimal Distance? If not
                    //there is no need to check this track
                    if (accumulator < MIN_DIST){
                        //Dive one Track deeper into the hierarchy
                        actTrack = remaining.get(0);
                        visited.add(actTrack);
                        stack.push(actTrack);
                        for (Vehicle vehicle : actTrack.getVehiclesOnTrack()) {
                            double distOfVehicleInTrack = vehicle.getCurrentPosInTrack();
                            if (distOfVehicleInTrack + accumulator < minDist){
                                minDist = distOfVehicleInTrack + accumulator;
                            }
                            //Found something. No need to dive deeper into the tree
                            vehFound = true;
                        }
                        if (vehFound){
                            //Immediately pop this node of the stack. Thereby prevent deeper diving into Tree
                            // because the actual Node already was added to visited and therefore wont be visited;
                            stack.pop();
                        }

                    }


                }
                Track nextTrack = remaining.get(0);
            }

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
