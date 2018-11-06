package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;

public abstract class StreetView {
    protected Street street;

    private Hitbox hitBox;
    private Rectangle boundingBox;

    public StreetView(Street street, Hitbox hitBox) {
        this.street = street;
        this.hitBox = hitBox;

        boundingBox = hitBox.calcBoundingBox();
    }

    public void draw(AreaGraphicsContext agc) {
        draw(agc, street.getPosition());
    }
    public void drawOverVehicle(AreaGraphicsContext agc) {
        drawOverVehicle(agc, street.getPosition());
    }
    public abstract void draw(AreaGraphicsContext agc, Position center);
    public abstract void drawOverVehicle(AreaGraphicsContext agc, Position center);

    protected Position getPosition(Position offset, Position p) {
        return p.add(offset);
    }

    protected Position getPosition(Position offset, double x, double y) {
        return new Position(x + offset.x, y + offset.y);
    }

    protected Position getPositionOnCanvas(Position offset, AreaGraphicsContext agc, Position p) {
        return agc.areaToCanvas(p.add(offset));
    }

    protected Position getPositionOnCanvas(Position offset, AreaGraphicsContext agc, double x, double y) {
        return agc.areaToCanvas(new Position(x + offset.x, y + offset.y));
    }

    public void drawPreview(AreaGraphicsContext agc) {
        draw(agc, street.getPosition().snapToGrid(Area.GRID_SPACING));
    }

    public void drawTracks(AreaGraphicsContext agc) {
        for (Track track : street.getTracks()) {
            track.render(agc, street.getPosition());
        }
    }

    public boolean PointHit(Position p) {
        return hitBox.hit(p.sub(street.getPosition()));
    }

    public void drawBoundingBox(AreaGraphicsContext agc) {
        boundingBox.render(agc, street.getPosition());
    }

    public void drawHitBox(AreaGraphicsContext agc) {
        hitBox.draw(agc, street.getPosition());
    }

    public boolean isVisible(AreaGraphicsContext agc) {
        return boundingBox.in(agc.screen, street.getPosition());
    }

    public Street getStreet() {
        return street;
    }
}
