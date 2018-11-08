package de.trafficsim.util.geometry;

import de.trafficsim.gui.graphics.AreaGraphicsContext;

public class Circle extends Shape {

    private double radius;

    public Circle(Position center, double radius) {
        super(center);
        this.radius = radius;
    }

    @Override
    public void render(AreaGraphicsContext agc) {
        agc.gc.fillOval(center.x-radius, center.y-radius, 2*radius, 2*radius);
        agc.gc.strokeOval(center.x-radius, center.y-radius, 2*radius, 2*radius);
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(center, radius, radius);
    }

    @Override
    public boolean hit(Position p) {
        return p.distance(center) <= radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}