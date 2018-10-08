package de.trafficsim.util.geometry;

import de.trafficsim.gui.graphics.AreaGraphicsContext;

public class Circle extends Shape {

    private double radius;

    public Circle(Position center, double radius) {
        super(center);
        this.radius = radius;
    }

    @Override
    public void render(AreaGraphicsContext agc, Position offset) {
        Position c = agc.areaToCanvas(center.add(offset));
        double r = agc.scaleToCanvas(radius);
        agc.gc.fillOval(c.x-r, c.y-r, 2*r, 2*r);
        agc.gc.strokeOval(c.x-r, c.y-r, 2*r, 2*r);
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