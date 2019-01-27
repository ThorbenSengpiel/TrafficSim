package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.signs.Sign;
import de.trafficsim.logic.streets.tracks.*;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.logic.vehicles.VehicleManager;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Street {
    protected Position position;

    private List<Track> tracks;

    private List<Track> inTracks;
    private List<Track> outTracks;

    public final StreetType type;

    protected final Direction rotation;

    protected List<Sign> signList = new ArrayList<>();

    private List<TrafficPriorityChecker> prioStopPoints = new ArrayList<>();

    private List<List<TrafficPriorityChecker>> prioStopPointGroups = new ArrayList<>();

    protected int stoppedCountForDeadLock = 0;

    public Street(Position position, StreetType type, Direction rotation) {
        this.rotation = rotation;
        this.position = position;
        this.type = type;
        tracks = new ArrayList<>();
        inTracks = new ArrayList<>();
        outTracks = new ArrayList<>();

    }

    public Street(Position position, StreetType type) {
        this(position, type, Direction.NORTH);
    }

    protected Position createPosition(double x, double y) {
        return new Position(x, y).rotate(rotation);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position p) {
        position = p;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    protected Track addInOutTrack(Track track) {
        inTracks.add(track);
        outTracks.add(track);
        return addTrack(track);
    }

    protected Track addInTrack(Track track) {
        inTracks.add(track);
        return addTrack(track);
    }

    protected Track addOutTrack(Track track) {
        outTracks.add(track);
        return addTrack(track);
    }

    protected Track addTrack(Track track) {
        tracks.add(track);
        return track;
    }

    protected void addTracks(Track... tracks) {
        for (Track track : tracks) {
            addTrack(track);
        }
    }

    protected Track addTrackBetween(Track from, Track to) {
        Track track;
        if (from.getOutDir().isHorizontal() ^ to.getInDir().isHorizontal()) {
            track = new TrackCurve(from.getTo(), to.getFrom(), from.getOutDir(), this);
        } else {
            track = new TrackStraight(from.getTo(), to.getFrom(), this);
        }
        from.connectOutToInOf(track); track.connectOutToInOf(to);
        return addTrack(track);
    }

    protected Track addTrackToPos(Track from, Position to, Direction outDir) {
        Track track;
        if (from.getOutDir().isHorizontal() ^ outDir.isHorizontal()) {
            track = new TrackCurve(from.getTo(), to, from.getOutDir(), this);
        } else {
            track = new TrackStraight(from.getTo(), to, this);
        }
        from.connectOutToInOf(track);
        return addTrack(track);
    }

    protected Track addBezierTrackBetween(Track from, Track to, double weight) {
        Track track = new TrackBezier(from.getTo(), from.getOutDir(), to.getFrom(), to.getInDir(), weight, this);
        from.connectOutToInOf(track); track.connectOutToInOf(to);
        return addTrack(track);
    }

    protected Track addBezierTrackToPos(Track from, Position to, Direction outDir, double weight) {
        Track track = new TrackBezier(from.getTo(), from.getOutDir(), to, outDir, weight, this);
        from.connectOutToInOf(track);
        return addTrack(track);
    }

    protected Track addTrackStraight(Track from, double length) {
        Track track = new TrackStraight(from.getTo(), from.getTo().add(from.getOutDir().vector.scale(length)), this);
        from.connectOutToInOf(track);
        return addTrack(track);
    }

    public abstract StreetView createView();

    public List<Track> getInTracks() {
        return inTracks;
    }

    public List<Track> getOutTracks() {
        return outTracks;
    }

    public void removeAllVehicles() {
        List<Vehicle> toRemove = new ArrayList<>();
        for (Track track : getTracks()) {
            toRemove.addAll(track.getVehiclesOnTrack());
        }
        VehicleManager vehicleManager = VehicleManager.getInstance();
        for (Vehicle vehicle : toRemove) {
            vehicleManager.removeVehicle(vehicle);
        }
    }

    public Street createRotated() {
        return this;
    }

    public Direction getRotation() {
        return rotation;
    }

    public String export() {
        return type + ";"+position.x+";"+position.y+";"+rotation;
    }

    public void update(double delta) {

    }

    public List<Sign> getSignList() {
        return signList;
    }

    @Override
    public String toString() {
        String s = "";
        if (debugLetThrough != null) {
            s+=debugLetThrough.getTrack().id;
        }
        //return type + " " + position + " " + rotation + " deadlock " + s + " " + deadlockCount;
        return s + " " + deadlockCount + " " + deadlockCount2;
    }

    public void addPriorityStopPoint(TrafficPriorityChecker priorityStopPoint) {
        prioStopPoints.add(priorityStopPoint);
        if (!prioStopPointGroups.isEmpty()) {
            for (List<TrafficPriorityChecker> group : prioStopPointGroups) {
                if (priorityStopPoint.getTrack().getInTrackList().size() > 0) {
                    if (group.get(0).getTrack().getInTrackList().get(0) == priorityStopPoint.getTrack().getInTrackList().get(0)) {
                        group.add(priorityStopPoint);
                        return;
                    }
                }
            }

        }
        List<TrafficPriorityChecker> list = new ArrayList<>();
        list.add(priorityStopPoint);
        prioStopPointGroups.add(list);
    }

    //TODO löschen
    /*public void begin() {
        for (TrafficPriorityChecker prioStopPoint : prioStopPoints) {
            prioStopPoint.begin();
        }
    }*/

    public void solveDeadLocks() {
        if (prioStopPoints.isEmpty()) {
            return;
        }
        List<List<TrafficPriorityChecker>> groups = new ArrayList<>(prioStopPointGroups);
        List<TrafficPriorityChecker> waiting = new ArrayList<>();

        for (TrafficPriorityChecker prioStopPoint : prioStopPoints) {
            Vehicle currentVehicle = prioStopPoint.getCurrentVehicle();
            if (currentVehicle != null) {
                if(currentVehicle.getVelocity() <= 0) {
                    waiting.add(prioStopPoint);
                    for (List<TrafficPriorityChecker> group : groups) {
                        if (group.contains(prioStopPoint)) {
                            groups.remove(group);
                            break;
                        }
                    }
                } else {
                    return;
                }
            }
        }
        deadlockCount = groups.size();
        if (groups.size() == 0) {
            TrafficPriorityChecker selected = waiting.get((int) (Math.random() * waiting.size()));
            //debugLetThrough = selected;
            if (selected.isDeadLockFree()) {
                selected.letThrough();
            }
        } else if (this instanceof StreetCross && groups.size() == 1) {
            List<TrafficPriorityChecker> group = groups.get(0);
            for (TrafficPriorityChecker trafficPriorityChecker : group) {
                if (!trafficPriorityChecker.getTrack().isFree()) {
                    return;
                }
            }
            //check empty side for incoming vehicles
            List<Vehicle> found = new ArrayList<>();
            group.get(0).checkBack(found, group.get(0).getTrack().getInTrackList().get(0), 100);
            found = found.stream().filter(v -> v.getVelocity() >= 2).collect(Collectors.toList());
            if (found.isEmpty()) {
                TrafficPriorityChecker selected = waiting.get((int) (Math.random() * waiting.size()));
                if (selected.isDeadLockFree()) {
                    selected.letThrough();
                }
            }
            deadlockCount2 = found.size();
        } else if (this instanceof StreetCross && groups.size() == 2) {
            TrafficPriorityChecker a = null;
            TrafficPriorityChecker b = null;
            for (TrafficPriorityChecker trafficPriorityChecker : waiting) {
                if (trafficPriorityChecker.getTrack() instanceof TrackCurve) {
                    a = trafficPriorityChecker;
                }
            }
            waiting.remove(a);
            for (TrafficPriorityChecker trafficPriorityChecker : waiting) {
                if (trafficPriorityChecker.getTrack() instanceof TrackCurve) {
                    b = trafficPriorityChecker;
                }
            }

            if (b != null) {
                if (a.getTrack().getInDir().rotateClockWise().rotateClockWise().equals(b.getTrack().getInDir())) {
                    a.letThrough();
                    b.letThrough();
                    System.out.println("IST DAS 1 2er DEADLOCK.... DAS ICH DEN NOCH ERLEBEN DARF");
                }
            }

            System.out.println(waiting.size());

        }
    }
    TrafficPriorityChecker debugLetThrough;
    int deadlockCount;
    int deadlockCount2;


}
