package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.signs.Sign;
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

    public void drawI(AreaGraphicsContext agc) {
        translate(agc);
        agc.gc.rotate(-street.getRotation().angle+90);
        draw(agc);
        agc.gc.rotate(street.getRotation().angle-90);
        translateBack(agc);
    }
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
    public abstract void draw(AreaGraphicsContext agc);
    public abstract void drawOverVehicle(AreaGraphicsContext agc);

    public void drawPreview(AreaGraphicsContext agc) {
        Position offset = street.getPosition().snapToGrid(Area.GRID_SPACING);
        agc.gc.translate(offset.x, offset.y);
        agc.gc.rotate(-street.getRotation().angle+90);
        draw(agc);
        drawOverVehicle(agc);
        agc.gc.rotate(street.getRotation().angle-90);
        agc.gc.translate(-offset.x, -offset.y);
    }

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

    public boolean PointHit(Position p) {
        return hitBox.hit(p.sub(street.getPosition()));
    }

    public void drawBoundingBox(AreaGraphicsContext agc) {
        translate(agc);
        boundingBox.render(agc);
        translateBack(agc);
    }

    public void drawHitBox(AreaGraphicsContext agc) {
        translate(agc);
        hitBox.draw(agc);
        translateBack(agc);
    }

    public boolean isVisible(AreaGraphicsContext agc) {
        return boundingBox.in(agc.screen, street.getPosition());
    }

    public Street getStreet() {
        return street;
    }

    private void translate(AreaGraphicsContext agc) {
        agc.gc.translate(street.getPosition().x, street.getPosition().y);
    }

    private void translateBack(AreaGraphicsContext agc) {
        agc.gc.translate(-street.getPosition().x, -street.getPosition().y);
    }

}
