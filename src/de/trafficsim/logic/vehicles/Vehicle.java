package de.trafficsim.logic.vehicles;

import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.Util;
import de.trafficsim.util.geometry.Position;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class Vehicle {
    protected double MIN_DIST = 10;
    protected int LOOKAHEAD_LIMIT = 1;

    protected double velocity = 1.0;
    protected double currentPosInTrack = 0;

    private final double maxAcceleration = 7; // m/s²
    private final double maxDeceleration = 20; // m/s²

    private final double maxVelocity = 13.88888888888889; // m/s

    protected Track currentTrack;

    protected List<Track> path;
    private int currentTrackNumber;

    private boolean active = true;
    public double color = 0;

    public Double getLookAheadDist(double lookDistance){
        System.out.println("Lookahead Calc for" + getCurrentTrack() + " Pos =" + getCurrentPosInTrack() + "Length of Track=" + getCurrentTrack().getLength());
        List<Vehicle> vehicles = currentTrack.getVehiclesOnTrack();
        Double minDist = Double.POSITIVE_INFINITY;
        boolean vehFound = false;
        if (currentTrack.hasStopPoint()) {
            if (currentTrack.isStopPointEnabled()) {
                double delta = currentTrack.getStopPointPosition() - currentPosInTrack;
                System.out.println("Stop Point Delta =" + delta + "Min Dist =" + minDist);
                if(delta > 0){
                    minDist = (minDist > delta ? delta : minDist);
                    vehFound = true;
                }
            }
        }
        for (Vehicle vehicle : vehicles) {
            if (vehicle != this){
                double delta = vehicle.getCurrentPosInTrack() - currentPosInTrack;
                System.out.println("Delta =" + delta + "Min Dist =" + minDist);
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
            visited.add(actTrack);
            while(!stack.empty()){
                //List of all Tracks that weren't already checked and so would have been put into the Hashmap
                List<Track> remaining = actTrack.getOutTrackList().stream().filter(e -> !visited.contains(e)).collect(Collectors.toList());
                //If there is no Element in the List. The Last Track was Part of a Spawn or didn't yield to another Track which
                //hasn't been checked
                // Hell Lot of Debug
                /*
                System.out.println("----------------");
                System.out.println("Outside");
                System.out.println("Actual Track =" +actTrack + " Length =" + actTrack.getLength());
                System.out.println("Visited =" + Arrays.toString(visited.toArray()));
                System.out.println("Stack =" + Arrays.toString(stack.toArray()));
                System.out.println("Remaining = " + Arrays.toString(remaining.toArray()));
                System.out.println("Accumulator =" +accumulator);
                System.out.println("MinDist =" + minDist);
                System.out.println("----------------");
                */
                if(remaining.isEmpty()){
                    //pop the Element of the Stack and decrement the accumulator because the distance was previously added
                    Track formertrack = stack.pop();
                    if (!stack.empty()) {
                        accumulator -= formertrack.getLength();
                        actTrack = stack.peek();
                        System.out.println("<-Backtracking to " + actTrack + "Accumulator now =" + accumulator + "->");
                    } else{
                        accumulator -= getCurrentTrack().getLength() - currentPosInTrack;
                        System.out.println("<-Backtracking to Root Accumulator now =" + accumulator + "->");
                    }
                } else {
                    //Dive one Track deeper into the hierarchy
                    actTrack = remaining.get(0);
                    visited.add(actTrack);
                    stack.push(actTrack);

                    //Another Hell Lot of Debug
                    /*
                    System.out.println("-------------");
                    System.out.println("ActTrackInside =" + actTrack + " Length =" + actTrack.getLength());
                    System.out.println("Stack =" + Arrays.toString(stack.toArray()));
                    System.out.println("Visited =" + Arrays.toString(visited.toArray()));
                    System.out.println("Accumulator =" + accumulator);
                    System.out.println("-------------");
                    */

                    //is the distance to this track still less than the Minimal Distance? If not
                    //there is no need to check this track
                    if (accumulator < lookDistance){
                        System.out.println("<-- Checking Track " + actTrack + "--->");
                        for (Vehicle vehicle : actTrack.getVehiclesOnTrack()) {
                            double distOfVehicleInTrack = vehicle.getCurrentPosInTrack();
                            if (distOfVehicleInTrack + accumulator < minDist){
                                minDist = distOfVehicleInTrack + accumulator;
                            }
                            //Found something. No need to dive deeper into the tree
                            vehFound = true;
                        }
                        if (actTrack.hasStopPoint()) {
                            if (actTrack.isStopPointEnabled()) {
                                double distOfStopPointInTrack = actTrack.getStopPointPosition();
                                if (distOfStopPointInTrack + accumulator < minDist){
                                    minDist = distOfStopPointInTrack + accumulator;
                                }
                                //Found something. No need to dive deeper into the tree
                                vehFound = true;
                            }
                        }
                        if (vehFound){
                            //Immediately pop this node of the stack. Thereby prevent deeper diving into Tree
                            // because the actual Node already was added to visited and therefore wont be visited;
                            System.out.println("Vehicle Found on " + actTrack);
                            Track formerTrack = stack.pop();
                            accumulator -= formerTrack.getLength();
                            actTrack = stack.peek();
                            vehFound = false;
                        }
                        accumulator += actTrack.getLength();

                    } else {
                        //Accumulator Exceed
                        System.out.println("Dist exceed. Don't Check this one");
                        Track formerTrack = stack.pop();
                        System.out.println("Popped " + formerTrack);
                        actTrack = stack.peek();

                    }


                }
            }

        }
        return minDist;
    }

    public Vehicle(double velocity, Track track){
        this.velocity = velocity;
        switchTrack(track);
        color = Math.random();
    }

    public Vehicle(double velocity, List<Track> path){
        this.velocity = velocity;
        switchTrack(path.get(0));
        this.path = path;
        this.color = Math.random();
    }

    private void accelerate(double delta, double value) {
        velocity += delta * maxAcceleration * value;
        if (velocity >= maxVelocity) {
            velocity = maxVelocity;
        }
    }

    private void brake(double delta, double value) {
        velocity -= delta * maxDeceleration * value;
        if (velocity < 0) {
            velocity = 0;
        }
    }

    private double brakeDistance() {
        return (velocity * velocity) / (2 * maxDeceleration);
    }

    public void move(double delta) {
        if (path != null) {
            double brakeDist = brakeDistance();
            double dist = getLookAheadDist(MIN_DIST + brakeDist);
            System.out.println("Dist =" + dist);
            if (velocity * delta + MIN_DIST + brakeDist < dist) {
                accelerate(delta, 1);
            } else {
                brake(delta, 1);
            }
            double newPositionInCurrentTrack = currentPosInTrack + velocity * delta;
            if (currentTrack.getLength() < newPositionInCurrentTrack) {
                currentTrackNumber++;

                if (currentTrackNumber < path.size() && currentTrack.getOutTrackList().size() > 0) {
                    Track nextTrack = path.get(currentTrackNumber);
                    double distanceInNewTrack = newPositionInCurrentTrack - currentTrack.getLength();
                    currentPosInTrack = distanceInNewTrack;
                    switchTrack(nextTrack);
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
                    switchTrack(nextTrack);
                } else {
                    active = false;
                }
            } else {
                currentPosInTrack = newPositionInCurrentTrack;
            }
        }
    }

    private void switchTrack(Track newTrack) {
        if (currentTrack != null) {
            currentTrack.removeVehicle(this);
        }
        newTrack.addVehicle(this);
        currentTrack = newTrack;
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

    @Override
    public String toString() {
        return "T:" + currentTrack.id + "P: " + Util.DOUBLE_FORMAT_0_00.format(currentPosInTrack) + " V:" + Util.DOUBLE_FORMAT_0_00.format(velocity);
    }
}
