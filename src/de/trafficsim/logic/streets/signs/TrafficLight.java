package de.trafficsim.logic.streets.signs;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

public class TrafficLight extends Sign {


    public final static int RED = 0;
    public final static int RED_YELLOW = 1;
    public final static int GREEN = 2;
    public final static int YELLOW = 3;

    private int state = RED;

    Track[] tracks;

    public TrafficLight(Position position, Direction rotation, Track... tracks) {
        super(position, rotation, SignType.TRAFFIC_LIGHT);
        this.tracks = tracks;
    }

    @Override
    public void render(AreaGraphicsContext agc) {
        agc.gc.translate(position.x, position.y);
        agc.gc.rotate(rotation.angle-90);
        agc.setFill(Color.DIMGRAY);
        agc.gc.fillRect(-1.5, -4.25, 3, 8.5);
        agc.gc.setFill((state == RED || state == RED_YELLOW) ? Color.RED : Color.RED.deriveColor(0, 1, 0.3, 1));
        agc.gc.fillOval(-1, -1-2.5, 2, 2);
        agc.gc.setFill((state == YELLOW || state == RED_YELLOW)? Color.YELLOW : Color.YELLOW.deriveColor(0, 1, 0.3, 1));
        agc.gc.fillOval(-1, -1, 2, 2);
        agc.gc.setFill(state == GREEN ? Color.LIME : Color.LIME.deriveColor(0, 1, 0.3, 1));
        agc.gc.fillOval(-1, -1+2.5, 2, 2);
        agc.gc.rotate(-rotation.angle+90);
        agc.gc.translate(-position.x, -position.y);
    }



    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (state >= 0 && state < 4) {
            this.state = state;
        } else {
            this.state = 0;
        }
        for (Track track : tracks) {
            track.enableStopPoint(state != GREEN);
        }
    }
}
