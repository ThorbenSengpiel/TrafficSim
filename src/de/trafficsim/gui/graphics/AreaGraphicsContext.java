package de.trafficsim.gui.graphics;

import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;

/**
 * Class witch incorporates a ordinary Graphics Context and contains additional
 * methods to calculate Scene coordinates from model coordinates and vice versa. Therefore contains information
 * about the scaling and offset
 */
public class AreaGraphicsContext {

    //Graphics context used for drawing
    public final GraphicsContext gc;
    //Offsets and scaling
    public final Position center;
    public final double scale;
    public Position canvasCenter;
    public final Rectangle screen;

    //Flag to specify whether the area should be rendered transperent
    private boolean transparent;

    private boolean fancyGraphics;


    public AreaGraphicsContext(GraphicsContext gc, Position center, double scale, double width, double height) {
        this.gc = gc;
        this.center = center;
        this.scale = scale;

        double halfScreenWidth = (width * scale) / 2;
        double halfScreenHeight = (height * scale) / 2;

        canvasCenter = new Position(width/2, height/2);

        screen = new Rectangle(new Position(center.x - halfScreenWidth, center.y - halfScreenHeight), new Position(center.x + halfScreenWidth, center.y + halfScreenHeight));
    }

    /**
     * Calculates Canvas coordinate from a model coordinate
     * @param p - Position in the model
     * @return Position on the canvas
     */
    public Position areaToCanvas(Position p) {
        return p.sub(center).scale(1/scale).add(canvasCenter);
    }

    /**
     * Calculates x Canvas coordinate from a x-model coordinate
     * @param x
     * @return
     */
    public double areaToCanvasX(double x) {
        return ((x - center.x) / scale) + canvasCenter.x;
    }


    /**
     * Scales a distance to the canvas
     * @param s - Distance unscaled
     * @return scaled distance
     */
    public double scaleToCanvas(double s) {
        return s / scale;
    }

    /**
     * Convert a canvas coordinate to a area coordinate
     * @param p - Position on the canvas
     * @return Position in the model
     */
    public Position canvasToArea(Position p) {
        return p.sub(canvasCenter).scale(scale).add(center);
    }

    /**
     * Convert a canvas coordinate to a area coordinate without taking the offset in account
     * @param p - Position on the canvas
     * @return Position in the model without offset
     */
    public Position canvasToAreaNoOffset(Position p) {
        return p.sub(canvasCenter).scale(scale);
    }

    /**
     * Scales a distance on the canvas to the area
     * @param s - Distance on the canvas
     * @return Scaled Distance on the Area
     */
    public double scaleToArea(double s) {
        return s * scale;
    }

    /**
     * Sets the stroke Color
     * @param color - Color in which the Area Graphics context should draw
     */
    public void setStroke(Color color) {
        if (transparent) {
            gc.setStroke(color.deriveColor(0, 1, 1, 0.5));
        } else {
            gc.setStroke(color);
        }
    }

    /**
     * Sets the Stroke Style
     * @param visuals - Style in which the stroke should be drawn
     */
    public void setStroke(StreetVisuals visuals) {
        setStroke(visuals.stroke);
        gc.setLineWidth(visuals.width);
        if (visuals.dashed != null) {
            gc.setLineDashes(StreetVisuals.STREET_LINE.width * visuals.dashed);
        } else {
            gc.setLineDashes(null);
        }
    }

    /**
     * Draw a lane between two points
     * @param from - Point from which should be drawn
     * @param to - Point to which the line should be drawn
     */
    public void draw2Lane(Position from, Position to) {
        double w = StreetVisuals.STREET2LANE.width / 2;

        setStroke(StreetVisuals.STREET2LANE);
        gc.setLineCap(StrokeLineCap.BUTT);
        gc.strokeLine(from.x, from.y, to.x, to.y);
        setStroke(StreetVisuals.STREET_LINE_DASHED);
        gc.strokeLine(from.x, from.y, to.x, to.y);
        setStroke(StreetVisuals.STREET_BORDER);
        if (from.y == to.y) {
            gc.strokeLine(from.x, from.y+w, to.x, to.y+w);
            gc.strokeLine(from.x, from.y-w, to.x, to.y-w);
        } else {
            gc.strokeLine(from.x+w, from.y, to.x+w, to.y);
            gc.strokeLine(from.x-w, from.y, to.x-w, to.y);
        }
    }

    /**
     * Draw a curve on the canvas
     * @param center - Center of the Arc
     * @param radius - Radius of the Arc
     * @param inDir - Begin direction
     */
    public void draw2LaneCurve(Position center, double radius, Direction inDir) {
        setStroke(StreetVisuals.STREET2LANE);
        gc.setLineCap(StrokeLineCap.BUTT);
        gc.strokeArc(center.x-radius, center.y-radius, radius*2, radius*2, inDir.angle , 90, ArcType.OPEN);

        setStroke(StreetVisuals.STREET_LINE_DASHED);
        gc.setLineCap(StrokeLineCap.BUTT);
        gc.strokeArc(center.x-radius, center.y-radius, radius*2, radius*2, inDir.angle , 90, ArcType.OPEN);

        setStroke(StreetVisuals.STREET_BORDER);
        gc.setLineCap(StrokeLineCap.BUTT);
        radius -= 5;
        gc.strokeArc(center.x-radius, center.y-radius, radius*2, radius*2, inDir.angle , 90, ArcType.OPEN);
        radius += 10;
        gc.strokeArc(center.x-radius, center.y-radius, radius*2, radius*2, inDir.angle , 90, ArcType.OPEN);

    }

    /**
     * Sets the transparent flag
     * @param transparent - value to which the flag should be set
     */
    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    /**
     * Sets the fill color
     * @param color - Color uses for filling
     */
    public void setFill(Color color) {
        if (transparent) {
            gc.setFill(color.deriveColor(0, 1, 1, 0.5));
        } else {
            gc.setFill(color);
        }
    }

    /**
     * Sets an Effect
     * @param effect - Effect to be used
     */
    public void setEffect(Effect effect) {
        if (fancyGraphics) {
            gc.setEffect(effect);
        }
    }

    /**
     * Enum to represent StreetStyle. Including Color, line width and dash style
     */
    public enum StreetVisuals {
        STREET(Color.GRAY, 5, null),
        STREET2LANE(Color.GRAY, 10, null),
        STREET_LINE(Color.WHITE, 0.2, null),
        STREET_LINE_DASHED(Color.WHITE, 0.2, 10.0),
        STREET_LINE_DASHED_SMALL(Color.WHITE, 0.1, 5.0),
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

    /**
     * Check whether fancy graphics is enabled
     * @return boolean representing whether fancy graphics option is enabled
     */
    public boolean isFancyGraphics() {
        return fancyGraphics;
    }

    /**
     * Set the fancy graphics option to a given value
     * @param fancyGraphics - boolean representing the value to which it should be set
     */
    public void setFancyGraphics(boolean fancyGraphics) {
        this.fancyGraphics = fancyGraphics;
    }
}