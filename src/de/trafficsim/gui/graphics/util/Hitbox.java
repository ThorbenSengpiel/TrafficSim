package de.trafficsim.gui.graphics.util;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import de.trafficsim.util.geometry.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hitbox {
    private List<Shape> shapes;
    private List<Shape> shapesSub;

    public Hitbox(Shape... shapes) {
        this.shapes = new ArrayList<>();
        this.shapesSub = new ArrayList<>();
        this.shapes.addAll(Arrays.asList(shapes));
    }

    public Hitbox add(Shape s) {
        shapes.add(s);
        return this;
    }

    public Hitbox addSub(Shape s) {
        shapesSub.add(s);
        return this;
    }


    public Rectangle calcBoundingBox() {

        //Position min = new Position(Double.);
        Rectangle boundingBox = shapes.get(0).getBoundingBox();
        for (int i = 1; i < shapes.size(); i++) {
            boundingBox = boundingBox.getBoundingRect(shapes.get(i).getBoundingBox());
        }
        return boundingBox;
    }

    public void draw(AreaGraphicsContext agc) {
        for (Shape shape : shapes) {
            shape.render(agc);
        }
        for (Shape shape : shapesSub) {
            shape.render(agc);
        }
    }

    public boolean hit(Position p) {
        for (Shape shape : shapes) {
            if (shape.hit(p)) {
                for (Shape shapeSub : shapesSub) {
                    if (shapeSub.hit(p)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static Hitbox createLaneHitbox(Position from, Position to, double laneWidth) {
        Position f;
        Position t;
        if (from.y == to.y) {
            f = new Position(from.x, from.y - (laneWidth/2));
            t = new Position(to.x, to.y + (laneWidth/2));
        } else {
            f = new Position(from.x - (laneWidth/2), from.y);
            t = new Position(to.x + (laneWidth/2), to.y);
        }
        return new Hitbox(new Rectangle(f, t));
    }

    public static Hitbox createLaneHitbox(double length, Direction direction, double laneWidth) {
        Position f = new Position(laneWidth / 2, 0).rotate(direction);
        Position t = new Position(-laneWidth / 2, -length).rotate(direction);
        return new Hitbox(new Rectangle(f, t));
    }
}
