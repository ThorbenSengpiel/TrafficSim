package de.trafficsim.logic.streets.signs;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.util.geometry.Position;
import javafx.scene.paint.Color;

public class TrafficLight extends Sign {



    public enum State {RED, YELLOW, GREEN, RED_YELLOW}
    private State state;

    public TrafficLight(Position position) {
        super(position, SignType.TRAFFIC_LIGHT);
    }

    @Override
    public void render(AreaGraphicsContext agc) {
        agc.setFill(Color.GRAY);
        agc.gc.fillRect(position.x-1.5, position.y - 4.25, 3, 8.5);
        agc.gc.setFill((state == State.RED || state == State.RED_YELLOW) ? Color.RED : Color.DARKRED);
        agc.gc.fillOval(position.x-1, position.y-1-2.5, 2, 2);
        agc.gc.setFill((state == State.YELLOW || state == State.RED_YELLOW)? Color.YELLOW : Color.DARKGOLDENROD);
        agc.gc.fillOval(position.x-1, position.y-1, 2, 2);
        agc.gc.setFill(state == State.GREEN ? Color.LIME : Color.DARKGREEN);
        agc.gc.fillOval(position.x-1, position.y-1+2.5, 2, 2);
    }



    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
