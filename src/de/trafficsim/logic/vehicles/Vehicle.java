package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.network.Path;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.Util;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

import java.util.*;

public class Vehicle {
    public static final double CAR_SIZE = 3;
    private static final double MIN_DIST_SIDEWAY = CAR_SIZE*2;
    private static final double VEHICLE_LENGTH = CAR_SIZE*2+2;
    protected double MIN_DIST = CAR_SIZE + 2;
    protected int LOOKAHEAD_LIMIT = 1;

    protected double velocity = 1.0;
    protected double currentPosInTrack = 0;

    private final double maxAcceleration = 7; // m/s²
    private final double maxDeceleration = 10; // m/s²

    private final double maxVelocity = Util.kmhToMs(50); // m/s

    protected Track currentTrack;

    protected Path path;
    private int currentTrackNumber;

    private boolean active = true;
    public double color;

    public Vehicle(double velocity, Path path){
        this.velocity = velocity;
        switchTrack(path.get(0));
        this.path = path;
        this.color = Math.random();
    }

    public double getLookAheadDist(double lookDistance){
        List<Vehicle> vehicles = currentTrack.getVehiclesOnTrack();
        double minDist = Double.POSITIVE_INFINITY;
        boolean vehFound = false;
        //Calculate dist to StopPoint on CurrTrack
        if (currentTrack.hasStopPoint()) {
            if (currentTrack.isStopPointEnabled()) {
                double delta = currentTrack.getStopPointPosition() - currentPosInTrack;
                System.out.println("Stop Point Delta = " + delta + " Min Dist = " + minDist);
                if(delta > 0){
                    minDist = (minDist > delta ? delta : minDist);
                    vehFound = true;
                }
            }
        }
        //Calculate dist to vehicles on Curr Track
        for (Vehicle vehicle : vehicles) {
            if (vehicle != this){
                double delta = vehicle.getCurrentPosInTrack() - VEHICLE_LENGTH/2 - currentPosInTrack ;
                //System.out.println("Delta =" + delta + "Min Dist =" + minDist);
                if(delta > -VEHICLE_LENGTH /2 && delta <= 0){
                    minDist = 0;
                    //System.out.println("Normally this shouldn't happen");
                }
                if(delta > 0){
                    minDist = (minDist > delta ? delta : minDist);
                    vehFound = true;
                }
            }
        }
        //Look at neighbour Tracks
        if(currentTrackNumber + 1 < path.size()){
            for (Track track : currentTrack.getOutTrackList()) {
                Track nextTrack = path.get(currentTrackNumber+1);
                if(track != nextTrack){
                    //track.select();
                    if (!track.getVehiclesOnTrack().isEmpty()){
                        double minDistInOtherTrack = Double.POSITIVE_INFINITY;
                        for (Vehicle vehicle : track.getVehiclesOnTrack()) {
                            minDistInOtherTrack = (vehicle.currentPosInTrack - VEHICLE_LENGTH/2 < minDistInOtherTrack ? vehicle.currentPosInTrack : minDistInOtherTrack);
                        }
                        Position posOnPath = nextTrack.getPosOnArea(minDistInOtherTrack);
                        Position posOffPath = track.getPosOnArea(minDistInOtherTrack);
                        double dist = posOnPath.distance(posOffPath);
                        //System.out.println("Dist Sideways First Track= " +dist +"Vehicles ="+this);
                        if(dist < MIN_DIST_SIDEWAY){
                            vehFound = true;
                            minDist = currentTrack.getLength()-currentPosInTrack;
                        }
                    }
                }
            }
        }
        if (!vehFound){
            double accumulator = currentTrack.getLength() - currentPosInTrack;
            for (int i = currentTrackNumber + 1; i < path.size() && accumulator < lookDistance && !vehFound ; i++) {
                Track actTrack = path.get(i);
                for (Vehicle vehicle : actTrack.getVehiclesOnTrack()) {
                    double distOfVehicleInTrack = vehicle.getCurrentPosInTrack() - VEHICLE_LENGTH/2;
                    if (distOfVehicleInTrack + accumulator < minDist){
                        minDist = distOfVehicleInTrack + accumulator;
                    }
                    //Found something. No need to check following Tracks
                    vehFound = true;
                }
                if (actTrack.hasStopPoint()) {
                    if (actTrack.isStopPointEnabled()) {
                        double distOfStopPointInTrack = actTrack.getStopPointPosition();
                        if (distOfStopPointInTrack + accumulator < minDist){
                            minDist = distOfStopPointInTrack + accumulator;
                        }
                        //Found something. No need to check following Tracks
                        vehFound = true;
                    }
                }
                if(!vehFound){
                    if (i+1 < path.size()){
                        Track OutTrackInPath = path.get(i+1);
                        for (Track track : actTrack.getOutTrackList()) {
                            if(track != OutTrackInPath){
                                //track.select();
                                if (!track.getVehiclesOnTrack().isEmpty()){
                                    double minDistInOtherTrack = Double.POSITIVE_INFINITY;
                                    for (Vehicle vehicle : track.getVehiclesOnTrack()) {
                                        minDistInOtherTrack = (vehicle.currentPosInTrack < minDistInOtherTrack ? vehicle.currentPosInTrack : minDistInOtherTrack);
                                    }
                                    Position posOnPath = OutTrackInPath.getPosOnArea(minDistInOtherTrack);
                                    Position posOffPath = track.getPosOnArea(minDistInOtherTrack);
                                    double dist = posOnPath.distance(posOffPath);
                                    //System.out.println("Dist Sideways = " +dist +"Vehicles ="+this);
                                    if(dist < MIN_DIST_SIDEWAY){
                                        vehFound = true;
                                        minDist=accumulator+actTrack.getLength();
                                    }
                                }
                            }
                        }
                    }
                }
                accumulator += actTrack.getLength();
            }
        }
        //System.out.println("Min Dist =" + minDist);
        return minDist;
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
        double brakeDist = brakeDistance();
        double dist = getLookAheadDist(VEHICLE_LENGTH + brakeDist + 5);
        //System.out.println("Dist =" + dist);
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
    }

    protected void switchTrack(Track newTrack) {
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
        return "T:" + currentTrack.id + " P: " + Util.DOUBLE_FORMAT_0_00.format(currentPosInTrack) + " V:" + Util.DOUBLE_FORMAT_0_00.format(velocity);
    }



    public void draw(AreaGraphicsContext agc, boolean selected) {
        agc.setFill(Color.hsb(color*360, 1, 1, 1));
        agc.gc.fillRoundRect(-CAR_SIZE, -(CAR_SIZE /2), CAR_SIZE *2, CAR_SIZE, CAR_SIZE / 2, CAR_SIZE / 2);
        if (selected) {
            agc.gc.setLineWidth(3*agc.scale);
            agc.setStroke(Color.WHITE);
            agc.gc.strokeRoundRect(-CAR_SIZE, -(CAR_SIZE /2), CAR_SIZE *2, CAR_SIZE, CAR_SIZE / 2, CAR_SIZE / 2);
            double lookRadius = VEHICLE_LENGTH + brakeDistance() + 5;
            agc.gc.setLineWidth(agc.scale*1.5);
            agc.setStroke(Color.WHITE);
            agc.gc.strokeOval(-lookRadius, -lookRadius, lookRadius*2, lookRadius*2);


            double brakeDist = brakeDistance();
            agc.setStroke(Color.FUCHSIA);
            agc.gc.strokeOval(-brakeDist, -brakeDist, brakeDist*2, brakeDist*2);
        }
    }
}
