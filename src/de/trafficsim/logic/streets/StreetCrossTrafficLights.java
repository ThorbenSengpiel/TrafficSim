package de.trafficsim.logic.streets;

import com.sun.org.apache.regexp.internal.RE;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.gui.views.StreetCrossTrafficLightsView;
import de.trafficsim.gui.views.StreetTestView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.network.TrafficLightManager;
import de.trafficsim.logic.streets.signs.TrafficLight;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.logic.streets.tracks.TrafficPriorityChecker;
import de.trafficsim.logic.streets.tracks.TrafficPriorityCheckerRightOverLeft;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.geometry.Pos;

public class StreetCrossTrafficLights extends Street {

    public StreetCrossTrafficLights() {
        this(Position.ZERO);
    }

    private TrafficLightManager trafficLightManager;

    public StreetCrossTrafficLights(Position position) {
        super(position, StreetType.CROSS_TRAFFICLIGHTS);

        Track inWest = addInTrack(new TrackStraight(new Position(-50, 2.5), new Position(-47.5, 2.5), this));
        Track outWest = addOutTrack(new TrackStraight(new Position(-47.5, -2.5), new Position(-50, -2.5), this));
        Track inEast = addInTrack(new TrackStraight(new Position(50, -2.5), new Position(47.5, -2.5), this));
        Track outEast = addOutTrack(new TrackStraight(new Position(47.5, 2.5), new Position(50, 2.5), this));

        Track inNorth = addInTrack(new TrackStraight(new Position(-2.5, -50), new Position(-2.5, -47.5), this));
        Track outNorth = addOutTrack(new TrackStraight(new Position(2.5, -47.5), new Position(2.5, -50), this));
        Track inSouth = addInTrack(new TrackStraight(new Position(2.5, 50), new Position(2.5, 47.5), this));
        Track outSouth = addOutTrack(new TrackStraight(new Position(-2.5, 47.5), new Position(-2.5, 50), this));

        Track wL = addBezierTrackToPos(inWest, new Position(-35, 0), inWest.getOutDir(), 5);
        Track wL1 = addTrackToPos(wL, new Position(-10, 0), wL.getOutDir());
        Track wS = addBezierTrackToPos(inWest, new Position(-35, 5), inWest.getOutDir(), 5);
        Track wS1 = addTrackToPos(wS, new Position(-10, 5), wS.getOutDir());
        Track wR = addBezierTrackToPos(inWest, new Position(-35, 10), inWest.getOutDir(), 5);
        Track wR1 = addTrackToPos(wR, new Position(-20, 10), wR.getOutDir());

        Track eL = addBezierTrackToPos(inEast, new Position(35, 0), inEast.getOutDir(), 5);
        Track eL1 = addTrackToPos(eL, new Position(10, 0), eL.getOutDir());
        Track eS = addBezierTrackToPos(inEast, new Position(35, -5), inEast.getOutDir(), 5);
        Track eS1 = addTrackToPos(eS, new Position(10, -5), eS.getOutDir());
        Track eR = addBezierTrackToPos(inEast, new Position(35, -10), inEast.getOutDir(), 5);
        Track eR1 = addTrackToPos(eR, new Position(20, -10), eR.getOutDir());


        Track nL = addBezierTrackToPos(inNorth, new Position(0, -35), inNorth.getOutDir(), 5);
        Track nL1 = addTrackToPos(nL, new Position(0, -10), nL.getOutDir());
        Track nS = addBezierTrackToPos(inNorth, new Position(-5, -35), inNorth.getOutDir(), 5);
        Track nS1 = addTrackToPos(nS, new Position(-5, -10), nS.getOutDir());
        Track nR = addBezierTrackToPos(inNorth, new Position(-10, -35), inNorth.getOutDir(), 5);
        Track nR1 = addTrackToPos(nR, new Position(-10, -20), nR.getOutDir());


        Track sL = addBezierTrackToPos(inSouth, new Position(0, 35), inSouth.getOutDir(), 5);
        Track sL1 = addTrackToPos(sL, new Position(0, 10), sL.getOutDir());
        Track sS = addBezierTrackToPos(inSouth, new Position(5, 35), inSouth.getOutDir(), 5);
        Track sS1 = addTrackToPos(sS, new Position(5, 10), sS.getOutDir());
        Track sR = addBezierTrackToPos(inSouth, new Position(10, 35), inSouth.getOutDir(), 5);
        Track sR1 = addTrackToPos(sR, new Position(10, 20), sR.getOutDir());


        Track wS2 = addTrackStraight(wS1, 25);
        Track eS2 = addTrackStraight(eS1, 25);
        Track nS2 = addTrackStraight(nS1, 25);
        Track sS2 = addTrackStraight(sS1, 25);

        Track eO = addTrackStraight(wS2, 10);
        Track eO1 = addTrackStraight(eO, 10);
        Track eO2 = addBezierTrackBetween(eO1, outEast, 5);

        Track wO = addTrackStraight(eS2, 10);
        Track wO1 = addTrackStraight(wO, 10);
        Track wO2 = addBezierTrackBetween(wO1, outWest, 5);

        Track nO = addTrackStraight(sS2, 10);
        Track nO1 = addTrackStraight(nO, 10);
        Track nO2 = addBezierTrackBetween(nO1, outNorth, 5);

        Track sO = addTrackStraight(nS2, 10);
        Track sO1 = addTrackStraight(sO, 10);
        Track sO2 = addBezierTrackBetween(sO1, outSouth, 5);


        addTrackBetween(nL1, eO);
        addTrackBetween(eL1, sO);
        addTrackBetween(sL1, wO);
        addTrackBetween(wL1, nO);

        Track t = addTrackBetween(sR1, eO1);
        t.setPriorityStopPoint(new TrafficPriorityChecker(t, 10));
        t = addTrackBetween(eR1, nO1);
        t.setPriorityStopPoint(new TrafficPriorityChecker(t, 10));
        t = addTrackBetween(nR1, wO1);
        t.setPriorityStopPoint(new TrafficPriorityChecker(t, 10));
        t = addTrackBetween(wR1, sO1);
        t.setPriorityStopPoint(new TrafficPriorityChecker(t, 10));

        wL1.createStopPoint(21, true);
        wS1.createStopPoint(21, true);
        eL1.createStopPoint(21, true);
        eS1.createStopPoint(21, true);
        nL1.createStopPoint(21, true);
        nS1.createStopPoint(21, true);
        sL1.createStopPoint(21, true);
        sS1.createStopPoint(21, true);

        TrafficLight trafficLight0 = new TrafficLight(new Position(15, -2.5), Direction.EAST, eL1, eS1);
        TrafficLight trafficLight1 = new TrafficLight(new Position(2.5, 15), Direction.SOUTH, sL1, sS1);
        TrafficLight trafficLight2 = new TrafficLight(new Position(-15, 2.5), Direction.WEST, wL1, wS1);
        TrafficLight trafficLight3 = new TrafficLight(new Position(-2.5, -15), Direction.NORTH, nL1, nS1);

        trafficLightManager = new TrafficLightManager(16, 2, 2, false, trafficLight0, trafficLight1, trafficLight2, trafficLight3);

        signList.add(trafficLight0);
        signList.add(trafficLight1);
        signList.add(trafficLight2);
        signList.add(trafficLight3);
        //signList.add(new TrafficLight(new Position(25, -5)));

    }

    @Override
    public StreetView createView() {
        return new StreetCrossTrafficLightsView(this);
    }

    double time = 0;

    @Override
    public void update(double delta) {
        trafficLightManager.update(delta);
    }
}

