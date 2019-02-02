package de.trafficsim.logic.streets.tracks;

import de.trafficsim.logic.streets.StreetCross;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.util.Util;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static de.trafficsim.logic.vehicles.Vehicle.MIN_DIST;

public class TrafficPriorityCheckerTrafficLights extends TrafficPriorityChecker {
    public TrafficPriorityCheckerTrafficLights(Track track, double stopPointPos, TrackAndPosition... crossTrackPoints) {
        super(track, stopPointPos, crossTrackPoints);
    }

    @Override
    protected List<Vehicle> checkBack(Track start, double maxCheckDist) {
        List<Vehicle> vehicleList = new ArrayList<>();
        for (Track t : start.getOutTrackList().get(0).getInTrackList()) {
            if (t != start) {
                if (!(t instanceof TrackStraight)) {
                    checkBack(vehicleList, t, maxCheckDist);
                }
            }
        }
        return vehicleList;
    }
}
