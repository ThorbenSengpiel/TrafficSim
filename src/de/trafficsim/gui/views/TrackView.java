package de.trafficsim.gui.views;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.gui.graphics.AreaGraphicsContext;
import de.trafficsim.gui.graphics.util.Hitbox;
import de.trafficsim.logic.tracks.Track;
import de.trafficsim.util.geometry.Position;
import de.trafficsim.util.geometry.Rectangle;

public abstract class TrackView {
    protected Track track;

    private Hitbox hitBox;
    private Rectangle boundingBox;

    public TrackView(Track track, Hitbox hitBox) {
        this.track = track;
        this.hitBox = hitBox;

        boundingBox = hitBox.calcBoundingBox();
    }

    public void draw(AreaGraphicsContext agc) {
        draw(agc, track.getPosition());
    }
    public abstract void draw(AreaGraphicsContext agc, Position center);


    public void drawPreview(AreaGraphicsContext agc) {
        draw(agc, track.getPosition().snapToGrid(Area.GRID_SPACING));
    }

    public abstract void drawPaths(AreaGraphicsContext agc);

    public boolean PointHit(Position p) {
        return hitBox.hit(p.sub(track.getPosition()));
    }

    public void drawBoundingBox(AreaGraphicsContext agc) {
        boundingBox.render(agc, track.getPosition());
    }

    public void drawHitBox(AreaGraphicsContext agc) {
        hitBox.draw(agc, track.getPosition());
    }

    public boolean isVisible(AreaGraphicsContext agc) {
        return boundingBox.in(agc.screen, track.getPosition());
    }

    public Track getTrack() {
        return track;
    }
}
