package de.trafficsim.gui.graphics;

import de.trafficsim.gui.menu.CreateMenu;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetTwoPositions;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.logic.vehicles.VehicleManager;
import de.trafficsim.util.Util;
import de.trafficsim.util.geometry.Position;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    private boolean editingNew;
    private StreetTwoPositions editable;
    private StreetView dragged;
    private Position dragCurrentPos;
    private Position dragStartPos;
    private Position dragStartCenter;

    private List<StreetView> streetViewList = new ArrayList<>();
    private List<Vehicle> vehicleList = new ArrayList<>();


    private boolean showTracks = false;
    private boolean showHitBox = false;
    private boolean showBoundingBox = false;
    private boolean showFancyGraphics = false;

    private boolean running = false;

    private CreateMenu createMenu;

    private Vehicle selectedVehicle = null;

    private static DropShadow shadow = new DropShadow(1.4, Color.BLACK);

    public Area() {
        super(200, 200);
        this.center = new Position(0, 0);
        this.scale = 1/* m/Pixel */;

        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, 50, 50);

        /*setOnMouseMoved(event -> {
            System.out.println(event.getX() + " " + event.getY());
        });*/

        setOnMouseDragged(this::mouseDrag);

        setOnMouseMoved(this::mouseMoved);

        setOnMousePressed(this::mousePressed);

        setOnMouseReleased(this::mouseReleased);

        setOnMouseClicked(this::mouseClicked);

        setOnScroll(e -> {
            scale *= 1-(e.getDeltaY() * 0.005);
            if (scale >= 5) {
                scale = 5;
            } else if(scale <= 0.001) {
                scale = 0.001;
            }
        });

        createMenu = new CreateMenu(this);

        setOnContextMenuRequested(event -> {
            Position pos = new Position(event.getX(), event.getY());
            createMenu.show(this, event.getScreenX(), event.getScreenY(), agc.canvasToArea(pos).snapToGrid(GRID_SPACING));
        });
    }

    private void mouseMoved(MouseEvent e) {
        if (editingNew) {
            if (dragStartPos != null) {
                dragCurrentPos = agc.canvasToAreaNoOffset(new Position(e.getX(), e.getY()));
                dragCurrentPos = agc.canvasToArea(new Position(e.getX(), e.getY()));
            }
            if (editable != null) {
                editable = editable.createChanged(dragStartCenter.add(dragCurrentPos.sub(dragStartPos)).snapToGrid(GRID_SPACING));
                dragged = editable.createView();
            }
        }
    }

    private void mouseClicked(MouseEvent e) {
        createMenu.hide();
        Position pos = agc.canvasToArea(new Position(e.getX(), e.getY()));
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getPosition().distance(pos) <= 4) {
                selectedVehicle = vehicle;
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (dragged != null) {
            if (e.getCode() == KeyCode.R) {
                Street street = dragged.getStreet();
                StreetNetworkManager.getInstance().removeStreet(street);
                Street rotated = street.createRotated();
                StreetNetworkManager.getInstance().addStreet(rotated);
                dragged = getView(rotated);
            }
        }
    }


    private void mousePressed(MouseEvent e) {
        Position pos = agc.canvasToArea(new Position(e.getX(), e.getY()));
        StreetView hit = null;
        if (!running && !editingNew) {
            hit = getStreetAt(pos);
        }
        pos = agc.canvasToAreaNoOffset(new Position(e.getX(), e.getY()));
        dragStartPos = pos;
        dragCurrentPos = pos;
        if (hit != null) {
            dragStartCenter = hit.getStreet().getPosition();
            dragged = hit;
            StreetNetworkManager.getInstance().removeStreet(hit.getStreet());
        } else {
            dragStartCenter = center;
        }
    }

    private StreetView getStreetAt(Position pos) {
        for (int i = streetViewList.size()-1; i >= 0; i--) {
            if (streetViewList.get(i).PointHit(pos)) {
                return streetViewList.get(i);
            }
        }
        return null;
    }

    private void mouseReleased(MouseEvent e) {
        dragCurrentPos = null;
        dragStartPos = null;
        dragStartCenter = null;
        if (dragged != null) {
            if (editingNew) {
                editingNew = false;
                editable = null;
            } else {
                dragged.getStreet().setPosition(dragged.getStreet().getPosition().snapToGrid(GRID_SPACING));
            }
            dragDropConnect(dragged);
            dragged = null;
        }
    }

    private void dragDropConnect(StreetView dragged) {
        StreetNetworkManager.getInstance().addStreet(dragged.getStreet());
    }

    private void mouseDrag(MouseEvent e) {
        if (dragStartPos != null) {
            dragCurrentPos = agc.canvasToAreaNoOffset(new Position(e.getX(), e.getY()));
        }
        if (dragged == null) {
            center = dragStartCenter.sub(dragCurrentPos.sub(dragStartPos));
        } else {
            dragged.getStreet().setPosition(dragStartCenter.add(dragCurrentPos.sub(dragStartPos)));
        }
        //System.out.println(center);
    }


    AreaGraphicsContext agc;

    public void draw(double delta) {
        agc = new AreaGraphicsContext(getGraphicsContext2D(), center, scale, getWidth(), getHeight());

        agc.setFancyGraphics(showFancyGraphics);


        ArrayList<StreetView> visibleStreetViews = new ArrayList<>();
        for (StreetView view : streetViewList) {
            if (view.isVisible(agc)) {
                visibleStreetViews.add(view);
            }
        }

        drawArea();
        agc.gc.save();
        agc.gc.translate(agc.canvasCenter.x, agc.canvasCenter.y);
        agc.gc.scale(1/agc.scale, 1/agc.scale);
        agc.gc.translate(-agc.center.x, -agc.center.y);


        drawStreets(visibleStreetViews);
        drawVehicles();
        drawStreetsOverVehicles(visibleStreetViews);
        drawPreviewElement();
        drawTracks(visibleStreetViews);
        if (showBoundingBox) {
            drawBoundingBoxes(visibleStreetViews);
        }
        if (showHitBox) {
            drawHitBoxes(visibleStreetViews);
        }
        agc.gc.restore();
        drawOverlay(delta);

    }

    private void drawVehicles() {
        //double size = agc.scaleToCanvas(4);
        agc.setEffect(shadow);
        double size = 4;
        agc.setStroke(Color.WHITE);
        agc.gc.setLineWidth(3*agc.scale);
        for (Vehicle vehicle : vehicleList) {
            //agc.setFill(Color.color(0.8, 0.2, 0.2));
            //agc.setFill(Color.color((vehicle.color & 0b1)>0?1:0, (vehicle.color & 0b10)>0?1:0, (vehicle.color & 0b100)>0?1:0).deriveColor(0, 0.8, 0.8, 1));
            agc.setFill(Color.hsb(vehicle.color*360, 1, 1, 1));
            Position position = vehicle.getPosition();
            agc.gc.translate(position.x, position.y);
            double rot = vehicle.getDirection();
            agc.gc.rotate(rot);

            //agc.gc.drawImage(img, -size, -(size/2), size*2, size);
            agc.gc.fillRoundRect(-size, -(size/2), size*2, size, size / 2, size / 2);
            if (vehicle == selectedVehicle) {
                agc.gc.strokeRoundRect(-size, -(size/2), size*2, size, size / 2, size / 2);
            }
            //agc.gc.fillRect(-size, -(size/2), size*2, size);
            /*agc.setStroke(Color.MAGENTA);
            agc.setFill(Color.WHITE);
            agc.gc.fillRect(size-(size / 5), -(size/2), size / 5, size / 5 );
            agc.gc.setLineWidth(agc.scaleToCanvas(1));
            agc.gc.strokeLine(0, 0, agc.scaleToCanvas(10), 0);*/
            agc.gc.rotate(-rot);
            agc.gc.translate(-position.x, -position.y);
        }
        agc.setEffect(null);
    }


    private void drawPreviewElement() {
        if (dragged != null) {
            agc.setTransparent(true);
            dragged.drawPreview(agc);
            agc.setTransparent(false);
            dragged.drawI(agc);
            dragged.drawOverVehicleI(agc);
            dragged.drawTracks(agc, false);
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

    private void drawStreets(List<StreetView> streetViews) {
        for (StreetView view : streetViews) {
            view.drawI(agc);
        }
    }

    private void drawStreetsOverVehicles(List<StreetView> streetViews) {
        for (StreetView view : streetViews) {
            view.drawOverVehicleI(agc);
        }
    }

    private void drawTracks(List<StreetView> streetViews) {
        if (selectedVehicle != null) {
            for (Track track : selectedVehicle.getPath()) {
                track.select();
            }
        }
        for (StreetView view : streetViews) {
            view.drawTracks(agc, !showTracks);
        }

    }

    private void drawBoundingBoxes(List<StreetView> streetViews) {
        agc.gc.setFill(Color.TRANSPARENT);
        agc.gc.setLineWidth(2*agc.scale);
        agc.gc.setStroke(Color.LIME);
        for (StreetView view : streetViews) {
            view.drawBoundingBox(agc);
        }
    }

    private void drawHitBoxes(List<StreetView> streetViews) {
        agc.gc.setFill(Color.TRANSPARENT);
        agc.gc.setLineWidth(2*agc.scale);
        agc.gc.setStroke(Color.RED);
        for (StreetView view : streetViews) {
            view.drawHitBox(agc);
        }
    }

    private double[] fps = new double[60];
    int current = 0;
    private double calcFps(double delta) {
        fps[current] = 1/delta;
        current++;
        current %= fps.length;
        double avg = 0;
        for (int i = 0; i < fps.length; i++) {
            avg += fps[i];
        }
        return avg / fps.length;
    }

    private void drawOverlay(double delta) {
        agc.gc.setFill(Color.BLACK);
        agc.gc.setTextAlign(TextAlignment.LEFT);
        agc.gc.fillText(agc.screen.from.toString(), 10, 10);
        agc.gc.fillText(agc.screen.to.toString(), 10, 30);

        agc.gc.fillText(Util.DOUBLE_FORMAT_0_0000.format(calcFps(delta))+" fps", 10, 50);
        agc.gc.fillText(VehicleManager.getInstance().getVehicleList().size()+" cars", 10, 70);


        agc.gc.setStroke(Color.BLACK);
        agc.gc.setLineWidth(3);

        agc.gc.strokeLine(getWidth() - 25, getHeight() - 25, getWidth() - 25, getHeight() - 45);
        agc.gc.strokeLine(getWidth() - 125, getHeight() - 25, getWidth() - 125, getHeight() - 45);
        agc.gc.strokeLine(getWidth() - 125, getHeight() - 25, getWidth() - 25, getHeight() - 25);
        agc.gc.setTextAlign(TextAlignment.CENTER);
        agc.gc.fillText(Util.DOUBLE_FORMAT_0_0000.format(agc.scaleToArea(100)) + "m", getWidth() - 75, getHeight() - 35);


    }


    public void setShowTracks(boolean selected) {
        showTracks = selected;
    }

    public void setShowBoundingBox(boolean selected) {
        showBoundingBox = selected;
    }

    public void setShowHitBox(boolean selected) {
        showHitBox = selected;
    }

    public void addStreet(Street street) {
        if (dragged != null && street == dragged.getStreet()) {
            streetViewList.add(dragged);
        } else {
            streetViewList.add(street.createView());
        }
    }

    public void removeStreet(Street street) {
        for (StreetView streetView : streetViewList) {
            if (streetView.getStreet() == street) {
                streetViewList.remove(streetView);
                break;
            }
        }
    }

    private StreetView getView(Street street) {
        for (StreetView streetView : streetViewList) {
            if (streetView.getStreet() == street) {
                return streetView;
            }
        }
        return null;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleList.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicleList.remove(vehicle);
        if (vehicle == selectedVehicle) {
            selectedVehicle = null;
        }
    }

    public void start() {
        running = true;
    }
    public void stop() {
        running = false;
    }
    public void pause() {
        running = true;
    }

    public void reset() {
        stop();
    }

    public void removeStreetAt(Position pos) {
        StreetView street = getStreetAt(pos);
        if (street != null) {
            StreetNetworkManager.getInstance().removeStreet(street.getStreet());
        }
    }

    public void newEditableStreet(StreetTwoPositions street) {
        Position pos = street.getPosition();
        editable = street;
        editingNew = true;
        dragStartPos = pos;
        dragCurrentPos = pos;
        dragStartCenter = pos;
        dragged = street.createView();
    }

    public void setFancyGraphics(boolean selected) {
        showFancyGraphics = selected;
    }
}