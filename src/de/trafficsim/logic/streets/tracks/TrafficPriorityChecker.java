package de.trafficsim.logic.streets.tracks;

import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.util.Util;

import java.util.ArrayList;
import java.util.List;

import static de.trafficsim.logic.vehicles.Vehicle.MIN_DIST;

public class TrafficPriorityChecker {

    private Track track;
    private double stopPointPos;

    public TrafficPriorityChecker(Track track, double stopPointPos) {
        this.track = track;
        this.stopPointPos = stopPointPos;
    }

    public boolean checkFree(Vehicle vehicle) {
        if (track.getVehiclesOnTrack().contains(vehicle)) {
            if (track.getVehiclesOnTrack().size() > 1) {
                return false;
            }
        } else {
            if (track.getVehiclesOnTrack().size() > 0) {
                return false;
            }
        }
        //TODO weiter check / seitentracks checken ????
        Track nextTrack = track.getOutTrackList().get(0);
        double dist = nextTrack.getDistToNextVehicle(vehicle);
        if (dist > Util.VEHICLE_LENGTH + MIN_DIST) {
            double time;
            if (vehicle.getCurrentTrack() == track) {
                time = vehicle.getTimeForDist(track.length - vehicle.getCurrentPosInTrack());
            } else {
                time = vehicle.getTimeForDist(track.length + (vehicle.getCurrentTrack().length-vehicle.getCurrentPosInTrack()));
            }
            double lookDist = time*vehicle.maxVelocity;
            List<Vehicle> vehicles = checkBack(lookDist);
            double maxDist = 0;
            Vehicle maxVehicle = null;
            for (Vehicle v : vehicles) {
                double d = v.getVelocity()*time;
                if (d > maxDist) {
                    maxDist = d;
                    maxVehicle = v;
                }
            }
            if (maxVehicle == null) {
                return true;
            }

            if (maxDist < maxVehicle.distanceToTrack(track, maxDist)) {
                return true;
            }
            return false;
        }
        return false;

    }

    private List<Vehicle> checkBack(double maxCheckDist) {
        List<Vehicle> vehicleList = new ArrayList<>();
        List<Track> tracks = new ArrayList<>();
        for (Track t : track.getOutTrackList().get(0).getInTrackList()) {
            if (t != track) {
                tracks.add(t);
                checkBack(vehicleList, t, maxCheckDist);
            }
        }
        return vehicleList;
    }

    private void checkBack(List<Vehicle> foundVehicles, Track t, double dist) {
        checkIfOnTrack(foundVehicles, t, dist);
        if (t.length < dist) {
            for (Track t0 : t.getInTrackList()) {
                checkBack(foundVehicles, t0, dist-t.length);
            }
        }
    }

    private void checkIfOnTrack(List<Vehicle> foundVehicles, Track t, double dist) {
        for (Vehicle vehicle : t.getVehiclesOnTrack()) {
            if ((t.length - vehicle.getCurrentPosInTrack()) <= dist) {
                foundVehicles.add(vehicle);
            }
        }
    }



    public double getStopPointPos() {
        return stopPointPos;
    }

}
