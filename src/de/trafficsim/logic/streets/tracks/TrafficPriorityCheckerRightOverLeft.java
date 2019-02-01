package de.trafficsim.logic.streets.tracks;

import de.trafficsim.logic.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class TrafficPriorityCheckerRightOverLeft extends TrafficPriorityChecker {
    public TrafficPriorityCheckerRightOverLeft(Track track, double stopPointPos, TrackAndPosition... crossTrackPoints) {
        super(track, stopPointPos, crossTrackPoints);
    }

    @Override
    protected List<Vehicle> checkBack(Track start, double maxCheckDist) {
        List<Vehicle> vehicleList = new ArrayList<>();
        for (Track t : start.getOutTrackList().get(0).getInTrackList()) {
            if (t != start) {
                if (track instanceof TrackStraight) {
                    if(t.getInDir().isRightOf(track.getOutDir())) {
                        checkBack(vehicleList, t, maxCheckDist);
                    }
                } else {
                    checkBack(vehicleList, t, maxCheckDist);
                }
            }
        }
        return vehicleList;
    }
}
