package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetCrossView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.network.TrafficLightManager;
import de.trafficsim.logic.streets.signs.TrafficLight;
import de.trafficsim.logic.streets.tracks.*;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

import java.util.List;

public class StreetCrossSmallTrafficLights extends Street {

    public StreetCrossSmallTrafficLights() {
        this(Position.ZERO);
    }

    private TrafficLightManager trafficLightManager;

    public StreetCrossSmallTrafficLights(Position position) {
        super(position, StreetType.CROSS_TRAFFICLIGHTS_SMALL);

        Track[] inTracks = new Track[4];
        Track[] outTracks = new Track[4];

        //create in- and outgoing tracks
        inTracks[0] = addInTrack(new TrackStraight(new Position(-2.5, -25), new Position(-2.5, -12.5), this));
        inTracks[1] = addInTrack(new TrackStraight(new Position(25, -2.5), new Position(12.5, -2.5), this));
        inTracks[2] = addInTrack(new TrackStraight(new Position(2.5, 25), new Position(2.5, 12.5), this));
        inTracks[3] = addInTrack(new TrackStraight(new Position(-25, 2.5), new Position(-12.5, 2.5), this));

        outTracks[0] = addOutTrack(new TrackStraight(new Position(2.5, -12.5), new Position(2.5, -25), this));
        outTracks[1] = addOutTrack(new TrackStraight(new Position(12.5, 2.5), new Position(25, 2.5), this));
        outTracks[2] = addOutTrack(new TrackStraight(new Position(-2.5, 12.5), new Position(-2.5, 25), this));
        outTracks[3] = addOutTrack(new TrackStraight(new Position(-12.5, -2.5), new Position(-25, -2.5), this));

        Track[][] betweenTracks = new Track[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i != j) {
                    betweenTracks[i][j] = addTrackBetween(inTracks[i], outTracks[j]);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            Track track = betweenTracks[i][(i + 1) % 4];
            track.setPriorityStopPoint(new TrafficPriorityCheckerTrafficLights(track, 5, new TrackAndPosition(betweenTracks[(i+2) % 4][i], 13.75)));
        }

        for (Track[] tracks : betweenTracks) {
            for (Track track : tracks) {
                if (track != null) {
                    track.createStopPoint(5, true);
                }
            }
        }

        TrafficLight trafficLight0 = new TrafficLight(new Position(-8, -20), Direction.SOUTH, betweenTracks[0]);
        TrafficLight trafficLight1 = new TrafficLight(new Position(20, -8), Direction.WEST, betweenTracks[1]);
        TrafficLight trafficLight2 = new TrafficLight(new Position(8, 20), Direction.NORTH, betweenTracks[2]);
        TrafficLight trafficLight3 = new TrafficLight(new Position(-20, 8), Direction.EAST, betweenTracks[3]);

        trafficLightManager = new TrafficLightManager(16, 2, 2, true, trafficLight0, trafficLight1, trafficLight2, trafficLight3);

        signList.add(trafficLight0);
        signList.add(trafficLight1);
        signList.add(trafficLight2);
        signList.add(trafficLight3);

        stoppedCountForDeadLock = 4;

    }

    double time = 0;

    @Override
    public void update(double delta) {
        trafficLightManager.update(delta);
    }

    @Override
    protected void extraChecks(List<List<TrafficPriorityChecker>> groups, List<TrafficPriorityChecker> waiting) {
        System.out.println("deadlock");
        if (groups.size() == 2) {
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
                }
            }
        }
    }

    @Override
    public StreetView createView() {
        return new StreetCrossView(this);
    }
}
