package de.trafficsim.gui.graphics;

import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
}