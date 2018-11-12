package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.transform.Rotate;

public class StreetCurveView extends StreetView {
    private Position l = new Position(-12.5, 0);
    private Position b = new Position(0, 12.5);

    public StreetCurveView(Street street) {
        super(street, createHitbox(street.getRotation()));
    }

    private static Hitbox createHitbox(Direction rotation) {
        //Assume having a curved street with an ingoing car at the bottom which exits the curved street on the left
        Rectangle in;
        Rectangle out; //the direction is orientated on out
        return new Hitbox(new Rectangle(Position.ZERO, 25, 5));
/*
        switch (rotation) {
            case NORTH:
                break;
            case EAST:
                break;
            case SOUTH:
                break;
            case WEST:
                break;
            case ZERO:
                break;
        }

        if (rotation == Direction.NORTH){
            in = new Rectangle(Position.ZERO, 25, 5);
        }else{

        }*/
    }


    @Override
    public void draw(AreaGraphicsContext agc) {
        //draw tails
        agc.draw2Lane(new Position(12.2, 0),  new Position(25.1, 0));   //left
        agc.draw2Lane(new Position(0, 12.2),  new Position(0, 25.1));   //bottom

        agc.draw2LaneCurve(b, l, getStreet().getRotation());
    }

    @Override
    public void drawOverVehicle(AreaGraphicsContext agc) {

    }
}
