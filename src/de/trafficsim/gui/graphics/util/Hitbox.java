package de.trafficsim.gui.graphics.util;

import de.trafficsim.gui.graphics.AreaGraphicsContext;
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

    public void draw(AreaGraphicsContext agc, Position offset) {
        for (Shape shape : shapes) {
            shape.render(agc, offset);
        }
        for (Shape shape : shapesSub) {
            shape.render(agc, offset);
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
}
