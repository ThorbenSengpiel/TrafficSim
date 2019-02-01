package de.trafficsim.logic.streets.tracks;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.logic.network.Path;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetCross;
import de.trafficsim.logic.streets.StreetRoundAbout;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.util.Util;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.trafficsim.logic.vehicles.Vehicle.MIN_DIST;

public class TrafficPriorityChecker {

    protected Track track;
    protected double stopPointPos;

    protected Vehicle letThroughVehicle;

    protected List<TrackAndPosition> crossTracks;


    public TrafficPriorityChecker(Track track, double stopPointPos, TrackAndPosition... crossTrackPoints) {
        this.track = track;
        this.stopPointPos = stopPointPos;
        crossTracks = Arrays.asList(crossTrackPoints);
    }

    public boolean checkFree(Vehicle vehicle) {
        if (vehicle == letThroughVehicle) {
            return true;
        }
        vehicle.debug = new ArrayList<>();
        //Check if Track is free
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
        //check if next track is free
        if (dist > Util.VEHICLE_LENGTH + MIN_DIST) {
            double time;
            if (vehicle.getCurrentTrack() == track) {
                //Todo sollten die autos weiter fahren? track + extra distance auf next track damit kreuzung komplett frei
                time = vehicle.getTimeForDist(track.length - vehicle.getCurrentPosInTrack());
            } else {
                time = vehicle.getTimeForDist(track.length + (vehicle.getCurrentTrack().length-vehicle.getCurrentPosInTrack()));
            }
            double lookDist = (time*vehicle.maxVelocity + MIN_DIST) * (track.getStreet() instanceof StreetCross ? 2 : 1);
            //TODO Maybe Revert
            List<Vehicle> vehicles = (track.getStreet() instanceof StreetRoundAbout ? checkBackWithTrack(track,lookDist) : checkBack(track, lookDist));

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
                    List<Vehicle> crossVehicles = checkBackCross(crossTrack, lookDist);
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
                    if (!crossOK){
                        return false;
                    }
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

    protected List<Vehicle> checkBack(Track start, double maxCheckDist) {
        List<Vehicle> vehicleList = new ArrayList<>();
        for (Track t : start.getOutTrackList().get(0).getInTrackList()) {
            if (t != start) {
                checkBack(vehicleList, t, maxCheckDist);
            }
        }
        return vehicleList;
    }

    private List<Vehicle> checkBackCross(Track start, double maxCheckDist) {
        List<Vehicle> vehicleList = new ArrayList<>();
        checkBack(vehicleList, start, maxCheckDist);
        return vehicleList;
    }

    public void checkBack(List<Vehicle> foundVehicles, Track t, double dist) {
        checkIfOnTrack(foundVehicles, t, dist);
        if (t.length < dist) {
            for (Track t0 : t.getInTrackList()) {
                checkBack(foundVehicles, t0, dist-t.length);
            }
        }
    }

    public List<Vehicle> checkBackWithTrack(Track toLookTrack, double maxCheckDist){
        List<Vehicle> foundVehicles = checkBack(toLookTrack, maxCheckDist);
        System.out.println("Found Vehicles for CheckBackOnTrack " + toLookTrack + " = " + Arrays.toString(foundVehicles.toArray()));
        List<Vehicle> vehicleColliding = foundVehicles.stream().filter(new collidesWithPath(toLookTrack)).collect(Collectors.toList());
        System.out.println(" --> " + Arrays.toString(vehicleColliding.toArray()));
        return vehicleColliding;
    }

    class collidesWithPath implements Predicate<Vehicle>{
        private List<Track> toCheckTracks;


        public collidesWithPath(Track toCheckTrack) {
            toCheckTracks = new ArrayList<>();
            List<Track> formerTracks = toCheckTrack.getOutTrackList().get(0).getInTrackList();
            if (formerTracks == null || formerTracks.isEmpty()) {
                toCheckTracks.add(toCheckTrack);
            } else{
                toCheckTracks.addAll(formerTracks.get(0).getOutTrackList());
            }
            if (toCheckTrack.street instanceof StreetRoundAbout){
                System.out.println("FromTrack = " + toCheckTrack);
                System.out.println("ToCheck= " + Arrays.toString(toCheckTracks.toArray()));
                System.out.println("Former =" + formerTracks.get(0));
            }
        }
        @Override
        public boolean test(Vehicle vehicle) {
            List<Track> path = vehicle.getPath();
            for (Track track : path.subList(vehicle.getCurrentTrackNumber(),(vehicle.getCurrentTrackNumber()+4 > path.size() ? path.size(): vehicle.getCurrentTrackNumber()+4))) {
                if (toCheckTracks.stream().anyMatch(x -> x == track)){
                    return true;
                }
            }
            return false;
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


    public Vehicle getCurrentVehicle() {
        Vehicle current = null;
        for (Vehicle vehicle : track.getVehiclesOnTrack()) {
            double pos = vehicle.getCurrentPosInTrack();
            if (pos <= stopPointPos) {
                if (current == null) {
                    current = vehicle;
                } else {
                    if (pos > current.getCurrentPosInTrack()) {
                        current = vehicle;
                    }
                }
            }
        }
        if (current == null) {
            if(!track.getInTrackList().isEmpty()){
                for (Vehicle vehicle : track.getInTrackList().get(0).getVehiclesOnTrack()) {
                    double pos = vehicle.getCurrentPosInTrack();
                    if (vehicle.getNextTrack() == track) {
                        if (current == null) {
                            current = vehicle;
                        } else {
                            if (pos > current.getCurrentPosInTrack()) {
                                current = vehicle;
                            }
                        }
                    }
                }
            }
        }

        return current;
    }

    public void letThrough() {
        letThroughVehicle = getCurrentVehicle();
    }

    public void clearLetThroughs() {
        if (letThroughVehicle != null) {
            if (letThroughVehicle.getCurrentTrack() != track && letThroughVehicle.getCurrentTrack() != track.getInTrackList().get(0)) {
                letThroughVehicle = null;
            }
        }
    }

    public boolean isDeadLockFree() {
        if (track.getVehiclesOnTrack().contains(getCurrentVehicle())) {
            if (track.getVehiclesOnTrack().size() > 1) {
                return false;
            }
        } else {
            if (track.getVehiclesOnTrack().size() > 0) {
                return false;
            }
        }
        Track nextTrack = track.getOutTrackList().get(0);
        if (!nextTrack.isAreaFree(0, Util.VEHICLE_LENGTH + MIN_DIST)) {
            return false;
        }
        for (Track t : nextTrack.getInTrackList()) {
            if (!t.isAreaFree(t.length - (Util.VEHICLE_LENGTH + MIN_DIST), t.length)) {
                return false;
            }
        }
        return true;
    }
}
