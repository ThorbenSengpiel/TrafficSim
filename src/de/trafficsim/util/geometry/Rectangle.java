package de.trafficsim.util.geometry;

import de.trafficsim.gui.graphics.AreaGraphicsContext;

public class Rectangle extends Shape {
    public Position from;
    public Position to;

    public Rectangle(Position from, Position to) {
        super(new Position((to.x - from.x) / 2, (to.y - from.y) / 2));
        this.from = from;
        this.to = to;
    }

    public Rectangle(Position center, double xRadius, double yRadius) {
        super(center);
        from = new Position(center.x - xRadius, center.y - yRadius);
        to = new Position(center.x + xRadius, center.y + yRadius);
    }


    public boolean in(Rectangle r) {
        if (to.x >= r.from.x && from.x <= r.to.x) {
            if (to.y >= r.from.y && from.y <= r.to.y) {
                return true;
            }
        }
        return false;
    }

    public boolean in(Rectangle r, Position offset) {
        Rectangle rect = new Rectangle(r.from.sub(offset), r.to.sub(offset));
        if (to.x >= rect.from.x && from.x <= rect.to.x) {
            if (to.y >= rect.from.y && from.y <= rect.to.y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(AreaGraphicsContext agc, Position offset) {
        Position f = agc.areaToCanvas(from.add(offset));
        Position t = agc.areaToCanvas(to.add(offset));
        agc.gc.fillRect(f.x, f.y, t.x - f.x, t.y - f.y);
        agc.gc.strokeRect(f.x, f.y, t.x - f.x, t.y - f.y);
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(from, to);
    }

    @Override
    public boolean hit(Position p) {
        if(p.x >= from.x && p.x <= to.x) {
            if(p.y >= from.y && p.y <= to.y) {
                return true;
            }
        }
        return false;
    }
}