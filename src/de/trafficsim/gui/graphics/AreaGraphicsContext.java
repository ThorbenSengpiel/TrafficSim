package de.trafficsim.gui.graphics;

import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class AreaGraphicsContext {

    public final GraphicsContext gc;
    public final Position center;
    public final double scale;
    private Position canvasCenter;
    public final Rectangle screen;

    private boolean transparent;

    public AreaGraphicsContext(GraphicsContext gc, Position center, double scale, double width, double height) {
        this.gc = gc;
        this.center = center;
        this.scale = scale;

        double halfScreenWidth = (width * scale) / 2;
        double halfScreenHeight = (height * scale) / 2;

        canvasCenter = new Position(width/2, height/2);

        screen = new Rectangle(new Position(center.x - halfScreenWidth, center.y - halfScreenHeight), new Position(center.x + halfScreenWidth, center.y + halfScreenHeight));
    }

    public Position areaToCanvas(Position p) {
        return p.sub(center).scale(1/scale).add(canvasCenter);
    }

    public double areaToCanvasX(double x) {
        return ((x - center.x) / scale) + canvasCenter.x;
    }



    public double scaleToCanvas(double s) {
        return s / scale;
    }

    public Position canvasToArea(Position p) {
        return p.sub(canvasCenter).scale(scale).add(center);
    }

    public Position canvasToAreaNoOffset(Position p) {
        return p.sub(canvasCenter).scale(scale);
    }

    public double scaleToArea(double s) {
        return s * scale;
    }

    public void setStroke(Color color) {
        if (transparent) {
            gc.setStroke(color.deriveColor(0, 1, 1, 0.5));
        } else {
            gc.setStroke(color);
        }
    }

    public void setStroke(StreetVisuals visuals) {
        setStroke(visuals.stroke);
        gc.setLineWidth(scaleToCanvas(visuals.width));
        if (visuals.dashed != null) {
            gc.setLineDashes(scaleToCanvas(StreetVisuals.STREET_LINE.width * visuals.dashed));
        } else {
            gc.setLineDashes(null);
        }
    }

    public void draw2Lane(Position from, Position to) {
        Position f = areaToCanvas(from);
        Position t = areaToCanvas(to);

        double w = scaleToCanvas(StreetVisuals.STREET2LANE.width / 2);

        setStroke(StreetVisuals.STREET2LANE);
        gc.setLineCap(StrokeLineCap.BUTT);
        gc.strokeLine(f.x, f.y, t.x, t.y);
        setStroke(StreetVisuals.STREET_LINE_DASHED);
        gc.strokeLine(f.x, f.y, t.x, t.y);
        setStroke(StreetVisuals.STREET_BORDER);
        if (from.y == to.y) {
            gc.strokeLine(f.x, f.y+w, t.x, t.y+w);
            gc.strokeLine(f.x, f.y-w, t.x, t.y-w);
        } else {
            gc.strokeLine(f.x+w, f.y, t.x+w, t.y);
            gc.strokeLine(f.x-w, f.y, t.x-w, t.y);
        }
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public void setFill(Color color) {
        if (transparent) {
            gc.setFill(color.deriveColor(0, 1, 1, 0.5));
        } else {
            gc.setFill(color);
        }
    }

    public enum StreetVisuals {
        STREET(Color.GRAY, 5, null),
        STREET2LANE(Color.GRAY, 10, null),
        STREET_LINE(Color.WHITE, 0.2, null),
        STREET_LINE_DASHED(Color.WHITE, 0.2, 10.0),
        STREET_BORDER(Color.BLACK, 0.2, null);

        public final Color stroke;
        public final double width;
        public final Double dashed;

        StreetVisuals(Color stroke, double width, Double dashed) {
            this.stroke = stroke;
            this.width = width;
            this.dashed = dashed;
        }
    }
}