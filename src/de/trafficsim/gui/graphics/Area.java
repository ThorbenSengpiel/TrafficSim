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
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the Simulation Area on which the vehicles and streets are rendered
 */
public class Area extends Pane {

    //Canvas containing the moving entities
    private Canvas foregound;
    //Canvas containing static entities such as streets
    private Canvas background;

    //Position to Track the x and y offset
    private Position center;
    //Scale in meter per Pixel
    private double scale;
    public static int GRID_SPACING = 25;

    //Variables used for Drag&Drop
    private boolean editingNew;
    private StreetTwoPositions editable;
    private StreetView dragged;
    private Position dragCurrentPos;
    private Position dragStartPos;
    private Position dragStartCenter;

    //List of the StreetViews to be rendered and the vehicles
    private List<StreetView> streetViewList = new ArrayList<>();
    private List<Vehicle> vehicleList = new ArrayList<>();


    //Boolean tracking the checkbox values
    private boolean showTracks = false;
    private boolean showHitBox = false;
    private boolean showBoundingBox = false;
    private boolean showFancyGraphics = false;

    private boolean running = false;

    //Right Click Menu
    private CreateMenu createMenu;

    private Vehicle selectedVehicle = null;

    private static DropShadow shadow = new DropShadow(1.4, Color.BLACK);

    public Area() {
        super();

        //Init the Pane
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);

        //Init the background
        background = new Canvas(200, 200);
        background.widthProperty().bind(widthProperty().subtract(20));
        background.heightProperty().bind(heightProperty().subtract(20));
        getChildren().add(background);

        //Init the foreground
        foregound = new Canvas(200, 200);
        foregound.widthProperty().bind(widthProperty().subtract(20));
        foregound.heightProperty().bind(heightProperty().subtract(20));
        widthProperty().addListener(observable -> setBgDirty());
        heightProperty().addListener(observable -> setBgDirty());

        getChildren().add(foregound);


        this.center = new Position(0, 0);
        this.scale = 1/* m/Pixel */;

        //Register Event Listener
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
            setBgDirty();
        });

        createMenu = new CreateMenu(this);

        setOnContextMenuRequested(event -> {
            if (!running) {
                Position pos = new Position(event.getX(), event.getY());
                createMenu.show(this, event.getScreenX(), event.getScreenY(), agcFG.canvasToArea(pos).snapToGrid(GRID_SPACING));
            }
        });
    }

    /**
     * Function to handle movement with dragging an item or offsetting the canvas
     * @param e - Event
     */
    private void mouseMoved(MouseEvent e) {
        if (editingNew) {
            if (dragStartPos != null) {
                dragCurrentPos = agcFG.canvasToAreaNoOffset(new Position(e.getX(), e.getY()));
                dragCurrentPos = agcFG.canvasToArea(new Position(e.getX(), e.getY()));
            }
            if (editable != null) {
                editable = editable.createChanged(dragStartCenter.add(dragCurrentPos.sub(dragStartPos)).snapToGrid(GRID_SPACING));
                dragged = editable.createView();
            }
        }
    }

    /**
     * Function to handle letting go of a mouse button. Thereby revealing Vehicle information when clicked on a vehicle
     * @param e - MouseEvent
     */
    private void mouseClicked(MouseEvent e) {
        createMenu.hide();
        Position pos = agcFG.canvasToArea(new Position(e.getX(), e.getY()));
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getPosition().distance(pos) <= 4) {
                selectedVehicle = vehicle;
            }
        }
    }

    /**
     * React to pressing the 'r' Key on the keyboard by rotating the dragged Street
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        if (dragged != null) {
            if (e.getCode() == KeyCode.R) {
                Street street = dragged.getStreet();
                Street rotated = street.createRotated();
                dragged = rotated.createView();
            }
        }
    }

    /**
     * Function to handle a mouse pressed event
     * @param e
     */
    private void mousePressed(MouseEvent e) {
        //Calculate the effective mouse position
        Position pos = agcFG.canvasToArea(new Position(e.getX(), e.getY()));
        StreetView hit = null;
        //If the simulation is stopped and no entity is being dragged
        if (!running && !editingNew) {
            //Get the hit enemy
            hit = getStreetAt(pos);
        }
        pos = agcFG.canvasToAreaNoOffset(new Position(e.getX(), e.getY()));
        //Begin dragging
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

    /**
     * Get the street at a specific position, if there is one
     * @param pos - Position on the screen to check for
     * @return StreetView of the Street in the model
     */
    private StreetView getStreetAt(Position pos) {
        for (int i = streetViewList.size()-1; i >= 0; i--) {
            if (streetViewList.get(i).PointHit(pos)) {
                return streetViewList.get(i);
            }
        }
        return null;
    }

    /**
     * Reacting to global mouse button release event. Thereby stop dragging a dragged street and fix them in place
     * @param e - MouseEvent
     */
    private void mouseReleased(MouseEvent e) {
        dragCurrentPos = null;
        dragStartPos = null;
        dragStartCenter = null;
        if (dragged != null) {
            if (editingNew) {
                editingNew = false;
                editable = null;
            } else {
                //Fix the street in place
                dragged.getStreet().setPosition(dragged.getStreet().getPosition().snapToGrid(GRID_SPACING));
            }
            StreetNetworkManager.getInstance().addStreet(dragged.getStreet());
            dragged = null;
        }
    }

    /**
     * Function to handle dragging the mouse. Thereby update the position of a dragged object or the position of the canvas
     * @param e - MouseEvent
     */
    private void mouseDrag(MouseEvent e) {
        if (dragStartPos != null) {
            dragCurrentPos = agcFG.canvasToAreaNoOffset(new Position(e.getX(), e.getY()));
        }
        if (dragged == null) {
            center = dragStartCenter.sub(dragCurrentPos.sub(dragStartPos));
        } else {
            dragged.getStreet().setPosition(dragStartCenter.add(dragCurrentPos.sub(dragStartPos)));
        }
        setBgDirty();
    }


    //Graphic Contexts for the canvas
    private AreaGraphicsContext agcFG;
    private AreaGraphicsContext agcBG;
    //Dirty Flag to check whether the background has to be drawn
    private boolean bgDirtyFlag = true;

    //Draw the Scene
    public void draw(double delta) {
        agcFG = new AreaGraphicsContext(foregound.getGraphicsContext2D(), center, scale, getWidth(), getHeight());
        agcFG.setFancyGraphics(showFancyGraphics);

        agcBG = new AreaGraphicsContext(background.getGraphicsContext2D(), center, scale, getWidth(), getHeight());

        //Check which streets are currently visible
        ArrayList<StreetView> visibleStreetViews = new ArrayList<>();
        for (StreetView view : streetViewList) {
            if (view.isVisible(agcFG)) {
                visibleStreetViews.add(view);
            }
        }

        agcFG.gc.clearRect(0, 0, getWidth(), getHeight());

        agcFG.gc.save();
        //Offset the canvas
        agcFG.gc.translate(agcFG.canvasCenter.x, agcFG.canvasCenter.y);
        agcFG.gc.scale(1/agcFG.scale, 1/agcFG.scale);
        agcFG.gc.translate(-agcFG.center.x, -agcFG.center.y);

        //Only redraw background if it has to be drawn again
        if (bgDirtyFlag) {
            agcBG.gc.clearRect(0, 0, getWidth(), getHeight());
            drawArea(agcBG);
            agcBG.gc.save();
            //Offset background
            agcBG.gc.translate(agcFG.canvasCenter.x, agcFG.canvasCenter.y);
            agcBG.gc.scale(1/agcFG.scale, 1/agcFG.scale);
            agcBG.gc.translate(-agcFG.center.x, -agcFG.center.y);

            drawStreets(agcBG, visibleStreetViews);

            //Draw debug information if set
            if (showBoundingBox) {
                drawBoundingBoxes(agcBG, visibleStreetViews);
            }
            if (showHitBox) {
                drawHitBoxes(agcBG, visibleStreetViews);
            }
            agcBG.gc.restore();
            bgDirtyFlag = false;
        }

        //Draw the foreground Objects
        drawVehicles(agcFG);
        drawStreetsOverVehicles(agcFG, visibleStreetViews);
        drawPreviewElement(agcFG);
        drawTracks(agcFG, visibleStreetViews);

        agcFG.gc.restore();
        drawOverlay(agcFG, delta);
    }

    /**
     * Function to draw the vehicles
     * @param agc - Graphics Context which should be used to draw them
     */
    private void drawVehicles(AreaGraphicsContext agc) {
        agc.setEffect(shadow);
        for (Vehicle vehicle : vehicleList) {
            Position position = vehicle.getPosition();
            //Transform the canvas
            agc.gc.translate(position.x, position.y);
            double rot = vehicle.getDirection();
            agc.gc.rotate(rot);

            vehicle.draw(agc, vehicle == selectedVehicle);

            //Revert Transformation
            agc.gc.rotate(-rot);
            agc.gc.translate(-position.x, -position.y);
        }
        agc.setEffect(null);
        //Draw Debug for each selected Vehicle
        if (selectedVehicle != null && selectedVehicle.debug != null) {
            for (Pair<Vehicle, Color> pair : selectedVehicle.debug) {
                agc.setStroke(pair.getValue());
                agc.gc.setLineWidth(1);
                Position p1 = pair.getKey().getPosition();
                Position posOnArea = selectedVehicle.debugPoint.getTrack().getPosOnArea(selectedVehicle.debugPoint.getStopPointPos());
                agc.gc.strokeLine(posOnArea.x, posOnArea.y, p1.x, p1.y);
            }
            selectedVehicle.debug = null;
            selectedVehicle.debugPoint = null;
        }
    }

    /**
     * Draw a shadow of the dragged Element snapped to the grid as a preview
     * @param agc - Graphics Context to use for drawing
     */
    private void drawPreviewElement(AreaGraphicsContext agc) {
        if (dragged != null) {
            agc.setTransparent(true);
            dragged.drawPreview(agc);
            agc.setTransparent(false);
            dragged.drawI(agc);
            dragged.drawOverVehicleI(agc);
            dragged.drawTracks(agc, false);
        }
    }

    /**
     * Draw the grid
     * @param agc - Graphics Context to use for Drawing
     */
    private void drawArea(AreaGraphicsContext agc) {
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

    /**
     * Draw All streets on the screen
     * @param agc - Graphics context to use for drawing
     * @param streetViews - All StreetViews that should be drawn
     */
    private void drawStreets(AreaGraphicsContext agc, List<StreetView> streetViews) {
        for (StreetView view : streetViews) {
            view.drawI(agc);
        }
    }

    //TODO Add Comment
    /**
     *
     * @param agc
     * @param streetViews
     */
    private void drawStreetsOverVehicles(AreaGraphicsContext agc, List<StreetView> streetViews) {
        for (StreetView view : streetViews) {
            view.drawOverVehicleI(agc);
        }
    }

    /**
     * Draw All Tracks of the given Street View
     * @param agc - Graphics context to use for drawing
     * @param streetViews - All StreetViews that should be drawn
     */
    private void drawTracks(AreaGraphicsContext agc, List<StreetView> streetViews) {
        // If there is a selected vehicle, then highlight its path
        if (selectedVehicle != null) {
            for (Track track : selectedVehicle.getPath()) {
                if (!track.isSelected()) {
                    track.select();
                }
            }
        }
        for (StreetView view : streetViews) {
            view.drawTracks(agc, !showTracks);
        }
    }

    /**
     * Draw the bounding Boxes for debug
     * @param agc - Graphics context to use for drawing
     * @param streetViews - All StreetViews for which the Boundingboxes should be drawn
     */
    private void drawBoundingBoxes(AreaGraphicsContext agc, List<StreetView> streetViews) {
        agc.gc.setFill(Color.TRANSPARENT);
        agc.gc.setLineWidth(2*agc.scale);
        agc.gc.setStroke(Color.LIME);
        for (StreetView view : streetViews) {
            view.drawBoundingBox(agc);
        }
    }

    /**
     * Draw the hit Boxes for debug
     * @param agc - Graphics context to use for drawing
     * @param streetViews - All StreetViews for which the Hitboxes should be drawn
     */
    private void drawHitBoxes(AreaGraphicsContext agc, List<StreetView> streetViews) {
        agc.gc.setFill(Color.TRANSPARENT);
        agc.gc.setLineWidth(2*agc.scale);
        agc.gc.setStroke(Color.RED);
        for (StreetView view : streetViews) {
            view.drawHitBox(agc);
        }
    }

    private double[] fps = new double[60];
    int current = 0;

    /**
     * Calculate the Fps for the actual delta.
     * @param delta - Time since the last Tick
     * @return Average fps over the last 60 Ticks
     */
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

    /**
     * Function to draw the Overlay with more Information for Debug and other purposes
     * @param agc - Graphics Context to use for drawing
     * @param delta - Time since the last Tick
     */
    private void drawOverlay(AreaGraphicsContext agc, double delta) {

        //Setup Fonts
        agc.gc.setFont(new Font(agc.gc.getFont().getFamily(), 15));
        agc.gc.setFill(Color.BLACK);
        agc.gc.setTextAlign(TextAlignment.LEFT);

        //Draw Information about the Fps, Track Count and cars
        agc.gc.fillText(Util.DOUBLE_FORMAT_0_0000.format(calcFps(delta))+" fps", 10, 30);
        agc.gc.fillText(StreetNetworkManager.getInstance().getTrackCount() +" Tracks", 10, 50);
        agc.gc.fillText(VehicleManager.getInstance().getVehicleList().size()+" cars", 10, 70);


        agc.gc.setStroke(Color.BLACK);
        agc.gc.setLineWidth(3);

        //Render Scale
        agc.gc.strokeLine(getWidth() - 25, getHeight() - 25, getWidth() - 25, getHeight() - 45);
        agc.gc.strokeLine(getWidth() - 125, getHeight() - 25, getWidth() - 125, getHeight() - 45);
        agc.gc.strokeLine(getWidth() - 125, getHeight() - 25, getWidth() - 25, getHeight() - 25);
        agc.gc.setTextAlign(TextAlignment.CENTER);
        agc.gc.fillText(Util.DOUBLE_FORMAT_0_0000.format(agc.scaleToArea(100)) + "m", getWidth() - 75, getHeight() - 35);

        //If ShowTrackInfo flag is set, then display bonus Track information
        if (showTrackInfo) {
            for (StreetView streetView : streetViewList) {
                agc.gc.setFont(new Font(agc.gc.getFont().getFamily(), 15));
                for (Track track : streetView.getStreet().getTracks()) {
                    if (track.isFree()) {
                        agc.setFill(Color.BLACK);
                    } else {
                        agc.setFill(Color.DARKGREEN);
                    }
                    Position pos = agc.areaToCanvas(streetView.getStreet().getPosition().add(track.getFrom().getCenterBetween(track.getTo())));
                    //Render the debug information
                    agc.gc.fillText(track + "", pos.x, pos.y+10);
                }
                agc.gc.setFont(new Font(agc.gc.getFont().getFamily(), 20));
                agc.gc.setFill(Color.RED);
                Position pos = agc.areaToCanvas(streetView.getStreet().getPosition());
                //Render street information
                agc.gc.fillText(streetView.getStreet() + "", pos.x, pos.y+10);
            }
        }
        agc.setFill(Color.WHITE);
        //Render vehicle info if flag is set
        if (showVehicleInfo) {
            for (Vehicle vehicle : vehicleList) {
                Position pos = agc.areaToCanvas(vehicle.getPosition());
                agc.gc.fillText(vehicle + "", pos.x, pos.y+10);
            }
        }

    }

    //Functions to set flags
    public void setShowTracks(boolean selected) {
        showTracks = selected;
    }

    public void setShowBoundingBox(boolean selected) {
        showBoundingBox = selected;
        setBgDirty();
    }

    public void setShowHitBox(boolean selected) {
        showHitBox = selected;
        setBgDirty();
    }

    /**
     private StreetView getView(Street street) {
     for (StreetView streetView : streetViewList) {
     if (streetView.getStreet() == street) {
     return streetView;
     }
     }
     return null;
     }
     **/

    private boolean showTrackInfo;

    public void setShowTrackInfo(boolean selected) {
        showTrackInfo = selected;
    }

    private boolean showVehicleInfo;

    public void setShowVehicleInfo(boolean selected) {
        showVehicleInfo = selected;
    }

    public void setBgDirty() {
        bgDirtyFlag = true;
    }

    /**
     * Add a street to be managed
     * @param street - Street to be added
     */
    public void addStreet(Street street) {
        if (dragged != null && street == dragged.getStreet()) {
            streetViewList.add(dragged);
        } else {
            streetViewList.add(street.createView());
        }
        setBgDirty();
    }
    /**
     * Remove a street from the managed pool
     * @param street - Street to be added
     */
    public void removeStreet(Street street) {
        for (StreetView streetView : streetViewList) {
            if (streetView.getStreet() == street) {
                streetViewList.remove(streetView);
                break;
            }
        }
        setBgDirty();
    }
    //TODO check if needed

    /**
     * Add a vehicle to the managed pool
     * @param vehicle - Vehicle to be added
     */
    public void addVehicle(Vehicle vehicle) {
        vehicleList.add(vehicle);
    }

    /**
     * Remove a vehicle from the managed pool
     * @param vehicle - Vehicle to be removed
     */
    public void removeVehicle(Vehicle vehicle) {
        vehicleList.remove(vehicle);
        if (vehicle == selectedVehicle) {
            selectedVehicle = null;
        }
    }

    /**
     * Start the simulation
     */
    public void start() {
        running = true;
    }

    /**
     * Stop the simulation
     */
    public void stop() {
        running = false;
    }

    /**
     * Pause the simulation
     */
    public void pause() {
        running = true;
    }

    /**
     * Reset the simulation
     */
    public void reset() {
        stop();
    }

    /**
     * Remove the street at a specified position if there is one
     * @param pos - Position which should be checked
     */
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
}