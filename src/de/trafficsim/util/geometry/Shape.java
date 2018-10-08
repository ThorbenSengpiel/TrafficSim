package de.trafficsim.util.geometry;

import de.trafficsim.gui.graphics.AreaGraphicsContext;

public abstract class Shape {
    protected Position center;

    public Shape(Position center) {
        this.center = center;
    }

    public abstract void render(AreaGraphicsContext agc, Position offset);

    public abstract Rectangle getBoundingBox();

    public Position getCenter() {
        return center;
    }

    public void setCenter(Position center) {
        this.center = center;
    }

    public abstract boolean hit(Position p);
}
