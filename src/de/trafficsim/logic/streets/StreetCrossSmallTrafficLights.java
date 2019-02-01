package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetCrossView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.network.TrafficLightManager;
import de.trafficsim.logic.streets.signs.TrafficLight;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackAndPosition;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.logic.streets.tracks.TrafficPriorityChecker;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

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
            track.setPriorityStopPoint(new TrafficPriorityChecker(track, 5, new TrackAndPosition(betweenTracks[(i+2) % 4][i], 13.75)));
        }

        for (Track inTrack : inTracks) {
            inTrack.createStopPoint(10, true);
        }

        TrafficLight trafficLight0 = new TrafficLight(new Position(-8, -20), Direction.SOUTH, inTracks[0]);
        TrafficLight trafficLight1 = new TrafficLight(new Position(20, -8), Direction.WEST, inTracks[1]);
        TrafficLight trafficLight2 = new TrafficLight(new Position(8, 20), Direction.NORTH, inTracks[2]);
        TrafficLight trafficLight3 = new TrafficLight(new Position(-20, 8), Direction.EAST, inTracks[3]);

        trafficLightManager = new TrafficLightManager(16, 2, 2, true, trafficLight0, trafficLight1, trafficLight2, trafficLight3);

        signList.add(trafficLight0);
        signList.add(trafficLight1);
        signList.add(trafficLight2);
        signList.add(trafficLight3);


    }

    double time = 0;

    @Override
    public void update(double delta) {
        trafficLightManager.update(delta);
    }

    @Override
    public StreetView createView() {
        return new StreetCrossView(this);
    }
}
