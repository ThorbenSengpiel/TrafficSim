package de.trafficsim.gui.graphics;

import de.trafficsim.gui.views.TrackRoundAboutView;
import de.trafficsim.gui.views.TrackView;
import de.trafficsim.logic.tracks.TrackRoundAbout;
import de.trafficsim.util.Util;
import de.trafficsim.util.geometry.Position;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class Area extends Canvas {


    private Position center;
    //Scale in meter per Pixel
    private double scale;
    public static int GRID_SPACING = 25;

    private TrackView dragged;
    private Position dragCurrentPos;
    private Position dragStartPos;
    private Position dragStartCenter;

    private List<TrackView> views;




    public Area() {
        super(200, 200);
        this.center = new Position(0, 0);
        this.scale = 1/* m/Pix */;

        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, 50, 50);

        /*setOnMouseMoved(event -> {
            System.out.println(event.getX() + " " + event.getY());
        });*/

        views = new ArrayList<>();
        views.add(new TrackRoundAboutView(new TrackRoundAbout(new Position(0, 0))));
        views.add(new TrackRoundAboutView(new TrackRoundAbout(new Position(50, 100))));
        for (int i = 0; i < 10; i++) {
            views.add(new TrackRoundAboutView(new TrackRoundAbout(new Position(Math.random() * 2000 - 1000, Math.random() * 2000 - 1000).snapToGrid(Area.GRID_SPACING))));
        }

        setOnMouseDragged(e -> {
            mouseDrag(e);
        });

        setOnMousePressed(e -> {
            mousePressed(e);
        });

        setOnMouseReleased(e -> {
            mouseReleased(e);
        });

        setOnScroll(e -> {
            scale *= 1-(e.getDeltaY() * 0.005);
            if (scale >= 5) {
                scale = 5;
            } else if(scale <= 0.001) {
                scale = 0.001;
            }
        });
    }


    private void mousePressed(MouseEvent e) {
        Position pos = agc.canvasToArea(new Position(e.getX(), e.getY()));
        TrackView hit = null;
        for (int i = views.size()-1; i >= 0; i--) {
            if (views.get(i).PointHit(pos)) {
                hit = views.get(i);
                break;
            }
        }
        pos = agc.canvasToAreaNoOffset(new Position(e.getX(), e.getY()));
        dragStartPos = pos;
        dragCurrentPos = pos;
        if (hit != null) {
            dragStartCenter = hit.getTrack().getPosition();
            dragged = hit;
        } else {
            dragStartCenter = center;
        }
    }

    private void mouseReleased(MouseEvent e) {
        dragCurrentPos = null;
        dragStartPos = null;
        dragStartCenter = null;
        if (dragged != null) {
            dragged.getTrack().setPosition(dragged.getTrack().getPosition().snapToGrid(GRID_SPACING));
            dragged = null;
        }
    }

    private void mouseDrag(MouseEvent e) {
        if (dragStartPos != null) {
            dragCurrentPos = agc.canvasToAreaNoOffset(new Position(e.getX(), e.getY()));
        }
        if (dragged == null) {
            center = dragStartCenter.sub(dragCurrentPos.sub(dragStartPos));
        } else {
            dragged.getTrack().setPosition(dragStartCenter.add(dragCurrentPos.sub(dragStartPos)));
        }
        //System.out.println(center);
    }


    AreaGraphicsContext agc;

    public void draw(long now) {
        agc = new AreaGraphicsContext(getGraphicsContext2D(), center, scale, getWidth(), getHeight());

        drawArea();
        agc.setTransparent(true);
        drawPreviewElement();
        agc.setTransparent(false);
        drawElements();
        //drawdrawBoundingBoxes();
        //drawdrawHitBoxes();
        drawOverlay();
    }

    private void drawPreviewElement() {
        if (dragged != null) {
            dragged.drawPreview(agc);
        }
    }

    private void drawArea() {
        agc.gc.clearRect(0, 0, getWidth(), getHeight());
        agc.gc.setFill(Color.color(0.4, 0.8, 0.3));
        agc.gc.setStroke(Color.gray(0.5, 0.2));
        agc.gc.fillRect(0, 0, getWidth(), getHeight());
        Position p1 = agc.areaToCanvas(new Position(0, 0));
        agc.gc.setFill(Color.LIGHTBLUE);
        agc.gc.setLineWidth(3);
        for (int x = ((int)agc.screen.from.x) / GRID_SPACING * GRID_SPACING; x <= agc.screen.to.x; x += GRID_SPACING) {
            Position p = agc.areaToCanvas(new Position(x, 0));
            agc.gc.strokeLine(p.x, 0, p.x, getHeight());
        }
        for (int y = ((int)agc.screen.from.y) / GRID_SPACING * GRID_SPACING; y <= agc.screen.to.y; y += GRID_SPACING) {
            Position p = agc.areaToCanvas(new Position(0, y));
            agc.gc.strokeLine(0, p.y, getWidth(), p.y);
        }
    }

    private void drawElements() {
        for (TrackView view : views) {
            if (view.isVisible(agc)) {
                view.draw(agc);
            }
        }
    }

    private void drawdrawBoundingBoxes() {
        agc.gc.setFill(Color.TRANSPARENT);
        agc.gc.setLineWidth(2);
        agc.gc.setStroke(Color.LIME);
        for (TrackView view : views) {
            if (view.isVisible(agc)) {
                view.drawBoundingBox(agc);
            }
        }
    }

    private void drawdrawHitBoxes() {
        agc.gc.setFill(Color.TRANSPARENT);
        agc.gc.setLineWidth(2);
        agc.gc.setStroke(Color.RED);
        for (TrackView view : views) {
            if (view.isVisible(agc)) {
                view.drawHitBox(agc);
            }
        }
    }

    private void drawOverlay() {
        agc.gc.setFill(Color.BLACK);
        agc.gc.setTextAlign(TextAlignment.LEFT);
        agc.gc.fillText(agc.screen.from.toString(), 10, 10);
        agc.gc.fillText(agc.screen.to.toString(), 10, 30);

        agc.gc.setStroke(Color.BLACK);
        agc.gc.setLineWidth(3);

        agc.gc.strokeLine(getWidth() - 25, getHeight() - 25, getWidth() - 25, getHeight() - 45);
        agc.gc.strokeLine(getWidth() - 125, getHeight() - 25, getWidth() - 125, getHeight() - 45);
        agc.gc.strokeLine(getWidth() - 125, getHeight() - 25, getWidth() - 25, getHeight() - 25);
        agc.gc.setTextAlign(TextAlignment.CENTER);
        agc.gc.fillText(Util.DOUBLE_FORMAT_0_0000.format(agc.scaleToArea(100)) + "m", getWidth() - 75, getHeight() - 35);


    }


}