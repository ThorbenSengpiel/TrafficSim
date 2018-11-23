package de.trafficsim.logic.streets.tracks;

import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.util.Util;

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
        double dist = nextTrack.getDistToNextObstacle(vehicle);
        if (dist > Util.VEHICLE_LENGTH + MIN_DIST) {
            return true;
        }
        return false;

    }




    public double getStopPointPos() {
        return stopPointPos;
    }

}
