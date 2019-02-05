package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.signs.Sign;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;
import javafx.scene.paint.Color;

/**
 * Class representing the visual representation of a street
 */
public abstract class StreetView {
    protected Street street;

    private Hitbox hitBox;
    private Rectangle boundingBox;

    /**
     * Constructor
     * @param street - Street which the View represents
     * @param hitBox - Hitbox for the street
     */
    public StreetView(Street street, Hitbox hitBox) {
        this.street = street;
        this.hitBox = hitBox;

        boundingBox = hitBox.calcBoundingBox();
    }

    /**
     * Function called when trying to draw a Street. Uses abstract draw method inside after performing transformation
     * @param agc - Graphics context used for rendering
     */
    public void drawI(AreaGraphicsContext agc) {
        translate(agc);
        agc.gc.rotate(-street.getRotation().angle+90);
        draw(agc);
        agc.gc.rotate(street.getRotation().angle-90);
        translateBack(agc);
    }

    /**
     * Used to draw parking vehicles on the Street for Parking Decks
     * @param agc - Graphics context used for rendering
     */
    public void drawOverVehicleI(AreaGraphicsContext agc) {
        translate(agc);
        agc.gc.rotate(-street.getRotation().angle+90);
        drawOverVehicle(agc);
        for (Sign sign : street.getSignList()) {
            sign.render(agc);
        }
        agc.gc.rotate(street.getRotation().angle-90);
        translateBack(agc);
    }

    /**
     * Renders the street
     * @param agc - Graphics Context used for rendering
     */
    public abstract void draw(AreaGraphicsContext agc);

    /**
     * Renders the street with parking vehicles on top of it
     * @param agc - Graphics Context used for rendering
     */
    public abstract void drawOverVehicle(AreaGraphicsContext agc);

    /**
     * Draws a preview of the street. Used when dragging a street
     * @param agc - Graphics Context used for rendering
     */
    public void drawPreview(AreaGraphicsContext agc) {
        Position offset = street.getPosition().snapToGrid(Area.GRID_SPACING);
        agc.gc.translate(offset.x, offset.y);
        agc.gc.rotate(-street.getRotation().angle+90);
        draw(agc);
        drawOverVehicle(agc);
        agc.gc.rotate(street.getRotation().angle-90);
        agc.gc.translate(-offset.x, -offset.y);
    }

    /**
     * Draws the tracks of the street
     * @param agc - Graphics Context used for rendering
     * @param onlySelected - Flag representing whether only selected Tracks should be drawn
     */
    public void drawTracks(AreaGraphicsContext agc, boolean onlySelected) {
        translate(agc);
        for (Track track : street.getTracks()) {
            if (onlySelected) {
                if (track.isSelected()) {
                    track.render(agc);
                }
            } else {
                track.render(agc);
            }
        }
        translateBack(agc);
    }

    /**
     * Checks whether a point on the canvas lies in the hitbox of the street
     * @param p - Position on the canvas
     * @return Boolean representing whether the Position lies in the hitbox of the street
     */
    public boolean PointHit(Position p) {
        return hitBox.hit(p.sub(street.getPosition()));
    }

    /**
     * Draws the bounding Box of the street
     * @param agc - Graphics Context used for rendering
     */
    public void drawBoundingBox(AreaGraphicsContext agc) {
        translate(agc);
        boundingBox.render(agc);
        translateBack(agc);
    }

    /**
     * Draws the hitbox of the street
     * @param agc - Graphics Context used for rendering
     */
    public void drawHitBox(AreaGraphicsContext agc) {
        translate(agc);
        hitBox.draw(agc);
        translateBack(agc);
    }

    /**
     * Check whether the Street is currently visible
     * @param agc - Graphics context to check
     * @return Boolean representing whether it is visible
     */
    public boolean isVisible(AreaGraphicsContext agc) {
        return boundingBox.in(agc.screen, street.getPosition());
    }

    /**
     * Getter for the Street
     * @return Street
     */
    public Street getStreet() {
        return street;
    }

    /**
     * Translate to the position of the street
     * @param agc
     */
    private void translate(AreaGraphicsContext agc) {
        agc.gc.translate(street.getPosition().x, street.getPosition().y);
    }

    /**
     * Translate back from the position of the street
     * @param agc
     */
    private void translateBack(AreaGraphicsContext agc) {
        agc.gc.translate(-street.getPosition().x, -street.getPosition().y);
    }

}
