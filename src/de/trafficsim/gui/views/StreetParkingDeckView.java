package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;

public class StreetParkingDeckView extends StreetView {

    private List<Position> cars = new ArrayList<>();
    private List<Double> colors = new ArrayList<>();
    private boolean vertical;
    
    public StreetParkingDeckView(Street street) {
        super(street, new Hitbox(new Rectangle(Position.ZERO, 25, 25)));
        vertical = street.getRotation().isVertical();
        for (int y = 0; y < 8; y++) {
            if (Math.random() < 0.5) {
                cars.add(new Position(15, (y * 6.25) - 21.875));
                colors.add(Math.random());
            }
            if (Math.random() < 0.5) {
                cars.add(new Position(-15, (y * 6.25) - 21.875));
                colors.add(Math.random());
            }
        }
    }

    @Override
    public void draw(AreaGraphicsContext agc) {
        agc.draw2Lane(new Position(-25, 0), Position.ZERO);
    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {
        agc.setFill(AreaGraphicsContext.StreetVisuals.STREET.stroke);
        double w = 20;
        double h = 25;
        agc.gc.fillRect(-w, -h, w*2, h*2);


        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_LINE);
        double dist = 6.25;
        double start = dist*3;
        for (int i = 0; i < 7; i++) {
            agc.gc.strokeLine(+w/2,  + (i*dist)-start, +w,  + (i*dist)-start);
            agc.gc.strokeLine(-w,  + (i*dist)-start, -w/2,  + (i*dist)-start);
        }

        agc.setStroke(AreaGraphicsContext.StreetVisuals.STREET_BORDER);
        agc.gc.strokeRect(-w, -h, w*2, h*2);

        agc.draw2Lane(new Position(0, 10), new Position(0, -10));
        agc.gc.strokeLine(-h/5, -h/2.5, +h/5, -h/2.5);
        agc.gc.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(1, Color.gray(0, 0)), new Stop(0, Color.gray(0, 0.3))));
        agc.gc.fillRect(-h/5, -h/2.5, h/2.5, h/1.25);


        double size = 4;
        for (int i = 0; i < cars.size(); i++) {
            agc.setFill(Color.hsb(colors.get(i)*360, 1, 1, 1));
            Position pos = cars.get(i);
            if (vertical) {
                agc.gc.fillRoundRect(pos.x-size, pos.y-(size/2), size*2, size, size / 2, size / 2);
            } else {
                agc.gc.fillRoundRect(pos.x-(size/2), pos.y-size, size, size*2, size / 2, size / 2);
            }
        }
    }
}
