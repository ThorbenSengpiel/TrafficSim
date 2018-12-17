package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.network.Path;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrafficPriorityChecker;
import de.trafficsim.util.Util;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

import java.util.*;

import static de.trafficsim.util.Util.CAR_SIZE;
import static de.trafficsim.util.Util.VEHICLE_LENGTH;

public class Vehicle {
    private static final double MIN_DIST_SIDEWAY = CAR_SIZE*2;
    public static final double MIN_DIST = CAR_SIZE + 2;
    protected int LOOKAHEAD_LIMIT = 1;

    protected double velocity = 1.0;
    protected double currentPosInTrack = 0;

    private final double maxAcceleration = 7; // m/s²
    private final double maxDeceleration = 10; // m/s²

    public final double maxVelocity = Util.kmhToMs(50); // m/s

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

    public double getLookAheadDist(double position, double lookDistance){
        List<Vehicle> vehicles = currentTrack.getVehiclesOnTrack();
        double minDist = Double.POSITIVE_INFINITY;
        boolean obstacleFound = false;
        //Calculate dist to StopPoint on CurrTrack
        if (currentTrack.hasStopPoint()) {
            if (currentTrack.isStopPointEnabled()) {
                double delta = currentTrack.getStopPointPosition() - position;
                System.out.println("Stop Point Delta = " + delta + " Min Dist = " + minDist);
                if(delta > 0){
                    minDist = (minDist > delta ? delta : minDist);
                    obstacleFound = true;
                }
            }
        }
        if (currentTrack.hasPriorityStopPoint()) {
            TrafficPriorityChecker checker = currentTrack.getPriorityStopPoint();
            if (!checker.checkFree(this)) {
                double delta = checker.getStopPointPos() - position;
                System.out.println("Prio Stop Point Delta = " + delta + " Min Dist = " + minDist);
                if(delta > 0){
                    minDist = (minDist > delta ? delta : minDist);
                    obstacleFound = true;
                }
            }
        }
        //Calculate dist to vehicles on Curr Track
        for (Vehicle vehicle : vehicles) {
            if (vehicle != this){
                double delta = vehicle.getCurrentPosInTrack() - VEHICLE_LENGTH/2 - position ;
                //System.out.println("Delta =" + delta + "Min Dist =" + minDist);
                if(delta > -VEHICLE_LENGTH /2 && delta <= 0){
                    minDist = 0;
                    //System.out.println("Normally this shouldn't happen");
                }
                if(delta > 0){
                    minDist = (minDist > delta ? delta : minDist);
                    obstacleFound = true;
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
                            obstacleFound = true;
                            minDist = currentTrack.getLength()-position;
                        }
                    }
                }
            }
        }
        if (!obstacleFound){
            double accumulator = currentTrack.getLength() - position;
            for (int i = currentTrackNumber + 1; i < path.size() && accumulator < lookDistance && !obstacleFound ; i++) {
                Track actTrack = path.get(i);

                if (actTrack.hasPriorityStopPoint()) {
                    TrafficPriorityChecker checker = actTrack.getPriorityStopPoint();
                    if (!checker.checkFree(this)) {
                        double distOfStopPointInTrack = checker.getStopPointPos();
                        if (distOfStopPointInTrack + accumulator < minDist){
                            minDist = distOfStopPointInTrack + accumulator;
                        }
                        //Found something. No need to check following Tracks
                        obstacleFound = true;
                    }
                }

                if (actTrack.hasStopPoint()) {
                    if (actTrack.isStopPointEnabled()) {
                        double distOfStopPointInTrack = actTrack.getStopPointPosition();
                        if (distOfStopPointInTrack + accumulator < minDist){
                            minDist = distOfStopPointInTrack + accumulator;
                        }
                        //Found something. No need to check following Tracks
                        obstacleFound = true;
                    }
                }

                for (Vehicle vehicle : actTrack.getVehiclesOnTrack()) {
                    double distOfVehicleInTrack = vehicle.getCurrentPosInTrack() - VEHICLE_LENGTH/2;
                    if (distOfVehicleInTrack + accumulator < minDist){
                        minDist = distOfVehicleInTrack + accumulator;
                    }
                    //Found something. No need to check following Tracks
                    obstacleFound = true;
                }

                if(!obstacleFound){
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
                                        obstacleFound = true;
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

    private double accelerationDistance() {
        return (maxVelocity*maxVelocity - velocity*velocity) / (2 * maxAcceleration);
    }

    private double accelerationTime() {
        return (maxVelocity-velocity) / maxAcceleration;
    }

    public double getTimeForDist(double distance) {
        double accDist = accelerationDistance();
        double accTime = accelerationTime();
        if (distance < accDist ) {
            return accTime;
        } else {
            double remaningDist = distance - accDist;
            return accTime+(remaningDist/maxVelocity);
        }
    }

    public void move(double delta) {
        double brakeDist = brakeDistance();
        double dist = getLookAheadDist(currentPosInTrack, VEHICLE_LENGTH + brakeDist + 5);
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

    public int getCurrentTrackNumber() {
        return currentTrackNumber;
    }

    public double getVelocity() {
        return velocity;
    }

    public double distanceToTrack(Track target, double maxDist) {
        double dist = 0;
        for (int i = currentTrackNumber; i < path.size(); i++) {
            Track track = path.get(currentTrackNumber);
            if (track == target) {
                return dist;
            }
            if (i == currentTrackNumber) {
                dist += track.getLength()-currentPosInTrack;
            } else {
                dist += track.getLength();
            }
            if (dist > maxDist) {
                return Double.POSITIVE_INFINITY;
            }
        }
        return dist;
    }
}
