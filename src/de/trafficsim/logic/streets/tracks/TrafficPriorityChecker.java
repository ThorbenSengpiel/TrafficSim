package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.util.Util;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.trafficsim.logic.vehicles.Vehicle.MIN_DIST;

public class TrafficPriorityChecker {

    private Track track;
    private double stopPointPos;

    private Vehicle currentVehicle;
    private Vehicle letThroughVehicle;

    private List<TrackAndPosition> crossTracks;


    public TrafficPriorityChecker(Track track, double stopPointPos, TrackAndPosition... crossTrackPoints) {
        this.track = track;
        this.stopPointPos = stopPointPos;
        crossTracks = Arrays.asList(crossTrackPoints);
    }

    public boolean checkFree(Vehicle vehicle) {
        if (vehicle == letThroughVehicle) {
            return true;
        }
        currentVehicle = vehicle;
        vehicle.debug = new ArrayList<>();
        if (track.getVehiclesOnTrack().contains(vehicle)) {
            if (track.getVehiclesOnTrack().size() > 1) {
                vehicle.debugPoint = this;
                for (Vehicle debugV : track.getVehiclesOnTrack()) {
                    vehicle.debug.add(new Pair<>(debugV, Color.LIME));
                }
                return false;
            }
        } else {
            if (track.getVehiclesOnTrack().size() > 0) {
                vehicle.debugPoint = this;
                for (Vehicle debugV : track.getVehiclesOnTrack()) {
                    vehicle.debug.add(new Pair<>(debugV, Color.LIME));
                }
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
            double lookDist = time*vehicle.maxVelocity*2;
            List<Vehicle> vehicles = checkBack(track, lookDist);

            boolean ok = true;
            for (Vehicle v : vehicles) {
                double distToNextTrack = v.distanceToTrack(v.getCurrentTrack(), nextTrack, lookDist) - Util.VEHICLE_LENGTH * 2 ;
                double d = v.maxVelocity*time;
                if (distToNextTrack < Util.VEHICLE_LENGTH*2 || d > distToNextTrack) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                vehicle.debugPoint = this;
                for (Vehicle debugV : vehicles) {
                    vehicle.debug.add(new Pair<>(debugV, Color.BLACK));
                }
                for (TrackAndPosition crossTrackAndPos : crossTracks) {
                    Track crossTrack = crossTrackAndPos.getTrack();
                    double pos = crossTrackAndPos.getPosition();
                    boolean free = true;
                    for (Vehicle v : crossTrack.getVehiclesOnTrack()) {
                        vehicle.debug.add(new Pair<>(v, Color.MEDIUMPURPLE));
                        if (v.getCurrentPosInTrack() < pos + Util.VEHICLE_LENGTH) {
                            free = false;
                        }
                    }
                    if (!free) {
                        return false;
                    }
                    List<Vehicle> crossVehicles = checkBack(crossTrack, lookDist);
                    for (Vehicle debugV : crossVehicles) {
                        vehicle.debug.add(new Pair<>(debugV, Color.ORANGE));
                    }


                    boolean crossOK = true;
                    for (Vehicle v : crossVehicles) {
                        double distToNextTack = (v.distanceToTrack(v.getCurrentTrack(), crossTrack, lookDist) + pos) - Util.VEHICLE_LENGTH;
                        double d = v.maxVelocity*time;
                        if (d > distToNextTack) {
                            crossOK = false;
                            break;
                        }
                    }
                    return crossOK;
                }
                return true;
            } else {
                vehicle.debugPoint = this;
                for (Vehicle debugV : vehicles) {
                    vehicle.debug.add(new Pair<>(debugV, Color.LIGHTBLUE));
                }
                return false;
            }

        } else {
            vehicle.debugPoint = this;
            vehicle.debugColor = Color.RED;
            for (Vehicle debugV : nextTrack.getVehiclesOnTrack()) {
                vehicle.debug.add(new Pair<>(debugV, Color.RED));
            }
            return false;
        }

    }

    private List<Vehicle> checkBack(Track start ,double maxCheckDist) {
        List<Vehicle> vehicleList = new ArrayList<>();
        for (Track t : start.getOutTrackList().get(0).getInTrackList()) {
            if (t != start) {
                //TODO ?????????????
                if (track.getOutDir().isRightOf(t.getInDir())) {
                    checkBack(vehicleList, t, maxCheckDist);
                }
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

    public Track getTrack() {
        return track;
    }

    public List<TrackAndPosition> getCrossTracks() {
        return crossTracks;
    }

    public void begin() {
        currentVehicle = null;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public void letThrough() {
        letThroughVehicle = currentVehicle;
    }

    public void clearLetThroughs() {
        if (letThroughVehicle != null) {
            if (letThroughVehicle.getCurrentTrack() != track && letThroughVehicle.getCurrentTrack() != track.getInTrackList().get(0)) {
                letThroughVehicle = null;
            }
        }
    }
}
