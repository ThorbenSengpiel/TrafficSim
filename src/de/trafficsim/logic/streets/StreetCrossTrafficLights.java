package de.trafficsim.logic.streets;

import com.sun.org.apache.regexp.internal.RE;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.gui.views.StreetTestView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.signs.TrafficLight;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.geometry.Pos;

public class StreetCrossTrafficLights extends Street {

    public StreetCrossTrafficLights() {
        this(Position.ZERO);
    }

    TrafficLight trafficLight;

    public StreetCrossTrafficLights(Position position) {
        super(position, StreetType.CROSS_TRAFFICLIGHTS);

        Track inWest = addInTrack(new TrackStraight(new Position(-50, 2.5), new Position(-40, 2.5), this));
        Track outWest = addOutTrack(new TrackStraight(new Position(-40, -2.5), new Position(-50, -2.5), this));
        Track inEast = addInTrack(new TrackStraight(new Position(50, -2.5), new Position(40, -2.5), this));
        Track outEast = addOutTrack(new TrackStraight(new Position(40, 2.5), new Position(50, 2.5), this));

        Track inNorth = addInTrack(new TrackStraight(new Position(-2.5, -50), new Position(-2.5, -40), this));
        Track outNorth = addOutTrack(new TrackStraight(new Position(2.5, -40), new Position(2.5, -50), this));
        Track inSouth = addInTrack(new TrackStraight(new Position(2.5, 50), new Position(2.5, 40), this));
        Track outSouth = addOutTrack(new TrackStraight(new Position(-2.5, 40), new Position(-2.5, 50), this));

        Track wL = addBezierTrackToPos(inWest, new Position(-20, 0), inWest.getOutDir(), 5);
        Track wL1 = addTrackToPos(wL, new Position(-10, 0), wL.getOutDir());
        Track wS = addBezierTrackToPos(inWest, new Position(-20, 5), inWest.getOutDir(), 5);
        Track wS1 = addTrackToPos(wS, new Position(-10, 5), wS.getOutDir());
        Track wR = addBezierTrackToPos(inWest, new Position(-20, 10), inWest.getOutDir(), 5);

        Track eL = addBezierTrackToPos(inEast, new Position(20, 0), inEast.getOutDir(), 5);
        Track eL1 = addTrackToPos(eL, new Position(10, 0), eL.getOutDir());
        Track eS = addBezierTrackToPos(inEast, new Position(20, -5), inEast.getOutDir(), 5);
        Track eS1 = addTrackToPos(eS, new Position(10, -5), eS.getOutDir());
        Track eR = addBezierTrackToPos(inEast, new Position(20, -10), inEast.getOutDir(), 5);

        Track nL = addBezierTrackToPos(inNorth, new Position(0, -20), inNorth.getOutDir(), 5);
        Track nL1 = addTrackToPos(nL, new Position(0, -10), nL.getOutDir());
        Track nS = addBezierTrackToPos(inNorth, new Position(-5, -20), inNorth.getOutDir(), 5);
        Track nS1 = addTrackToPos(nS, new Position(-5, -10), nS.getOutDir());
        Track nR = addBezierTrackToPos(inNorth, new Position(-10, -20), inNorth.getOutDir(), 5);

        Track sL = addBezierTrackToPos(inSouth, new Position(0, 20), inSouth.getOutDir(), 5);
        Track sL1 = addTrackToPos(sL, new Position(0, 10), sL.getOutDir());
        Track sS = addBezierTrackToPos(inSouth, new Position(5, 20), inSouth.getOutDir(), 5);
        Track sS1 = addTrackToPos(sS, new Position(5, 10), sS.getOutDir());
        Track sR = addBezierTrackToPos(inSouth, new Position(10, 20), inSouth.getOutDir(), 5);

        Track wS2 = addTrackStraight(wS1, 25);
        Track eS2 = addTrackStraight(eS1, 25);
        Track nS2 = addTrackStraight(nS1, 25);
        Track sS2 = addTrackStraight(sS1, 25);

        Track eO = addTrackStraight(wS2, 10);
        Track eO1 = addBezierTrackBetween(eO, outEast, 5);

        Track wO = addTrackStraight(eS2, 10);
        Track wO1 = addBezierTrackBetween(wO, outWest, 5);

        Track nO = addTrackStraight(sS2, 10);
        Track nO1 = addBezierTrackBetween(nO, outNorth, 5);

        Track sO = addTrackStraight(nS2, 10);
        Track sO1 = addBezierTrackBetween(sO, outSouth, 5);


        addTrackBetween(nL1, eO);
        addTrackBetween(eL1, sO);
        addTrackBetween(sL1, wO);
        addTrackBetween(wL1, nO);

        addTrackBetween(sR, eO1);
        addTrackBetween(eR, nO1);
        addTrackBetween(nR, wO1);
        addTrackBetween(wR, sO1);


        trafficLight = new TrafficLight(new Position(25, 5));
        signList.add(trafficLight);
        //signList.add(new TrafficLight(new Position(25, -5)));

    }

    @Override
    public StreetView createView() {
        return new StreetTestView(this, new Hitbox(new Rectangle(Position.ZERO, 50, 15), new Rectangle(Position.ZERO, 15, 50)));
    }

    double time = 0;

    @Override
    public void update(double delta) {
        time += delta;
        if (time % 8 < 2) {
            trafficLight.setState(TrafficLight.State.RED);
        } else if (time % 8 < 4) {
            trafficLight.setState(TrafficLight.State.RED_YELLOW);
        } else if (time % 8 < 6) {
            trafficLight.setState(TrafficLight.State.GREEN);
        } else {
            trafficLight.setState(TrafficLight.State.YELLOW);
        }
    }
}

