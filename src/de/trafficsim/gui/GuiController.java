package de.trafficsim.gui;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetTwoPositions;
import de.trafficsim.logic.vehicles.StaticVehicle;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.logic.vehicles.VehicleManager;
import de.trafficsim.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

/**
 * Class used to implement the GUI Functionality. Is a singleton.
 */
public class GuiController {

    private static GuiController instance;

    /*
    Gui Elements Specified via FXML. Injected by the JavaFX Framework
     */
    @FXML
    private AnchorPane paneCanvas;

    @FXML
    private CheckBox checkShowTracks;

    @FXML
    private CheckBox checkShowBoundingBox;

    @FXML
    private CheckBox checkShowHitBox;

    @FXML
    private Button startButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    @FXML
    private CheckBox checkShowVehicleInfo;

    @FXML
    private CheckBox checkShowTrackInfo;

    @FXML
    private Slider spawnSlider;

    @FXML
    private TextField spawnTextField;

    @FXML
    private Slider speedSlider;

    @FXML
    private Label speedLabel;

    //Speed Factor to scale the time
    private double speedFactor = 1;

    //Objects controlling the Simulation
    private Area area;
    private VehicleManager vehicleManager;
    private StreetNetworkManager streetNetworkManager;

    private Stage primaryStage;

    private GuiController() {

    }

    /**
     * Function which returns an instance of the GUI- Controller and ensures that there is only one
     * instance
     * @return GUIController instance
     */
    public static GuiController getInstance() {
        if (instance == null) {
            instance = new GuiController();
        }
        return instance;
    }

    /**
     * Start the simulation. Called by the JavaFX Framework
     * @param primaryStage - Stage on which the Scene should be rendered. Representing the window
     */
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        //Initialize Manager and Area
        area = new Area();
        vehicleManager = VehicleManager.getInstance();
        streetNetworkManager = StreetNetworkManager.getInstance();
        paneCanvas.getChildren().add(area);

        //Register Event Listener
        checkShowTracks.setOnAction(event -> area.setShowTracks(checkShowTracks.isSelected()));
        checkShowVehicleInfo.setOnAction(event -> area.setShowVehicleInfo(checkShowVehicleInfo.isSelected()));
        checkShowTrackInfo.setOnAction(event -> area.setShowTrackInfo(checkShowTrackInfo.isSelected()));
        checkShowBoundingBox.setOnAction(event -> area.setShowBoundingBox(checkShowBoundingBox.isSelected()));
        checkShowHitBox.setOnAction(event -> area.setShowHitBox(checkShowHitBox.isSelected()));

        stopButton.setDisable(true);
        pauseButton.setDisable(true);

        startButton.setOnAction(event -> {
            startButton.setDisable(true);
            stopButton.setDisable(false);
            pauseButton.setDisable(false);
            startModules();
        });
        stopButton.setOnAction(event -> {
            startButton.setDisable(false);
            stopButton.setDisable(true);
            pauseButton.setDisable(true);
            stopModules();
        });
        pauseButton.setOnAction(event -> {
            startButton.setDisable(false);
            stopButton.setDisable(false);
            pauseButton.setDisable(true);
            pauseModules();
        });

        //set up the cars/sec widgets
        spawnSlider.setValue(vehicleManager.getSpawnPerSecond()*60);
        spawnTextField.setText(Util.DOUBLE_FORMAT_0_00.format(spawnSlider.getValue()));
        //change slider value
        spawnSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            vehicleManager.setSpawnPerSecond(newValue.doubleValue()/60.0);
            spawnTextField.setText(Util.DOUBLE_FORMAT_0_00.format(vehicleManager.getSpawnPerSecond()*60));
        });
        //change textField value
        spawnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double value = Double.parseDouble(newValue);
                vehicleManager.setSpawnPerSecond(value/60.0);
                spawnSlider.setValue(value);
            }catch (NumberFormatException e){
            }
        });
        //set up speed widgets
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            speedFactor = (float) Math.pow(2, Math.round(newValue.doubleValue()));
            if (speedFactor < 0.2) {
                speedFactor = 0;
            }
            speedLabel.setText(speedFactor+"x");
        });
    }

    /**
     * Reset Simulation
     * @param e - ActionEvent
     */
    @FXML
    private void createNew(ActionEvent e) {
        reset();
    }

    /**
     * Load a previously saved StreetNetwork from a file
     * @param e
     */
    @FXML
    private void openFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();

        //Only Select valid files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Traffic Sim files (*.trafficsim)", "*.trafficsim");
        fileChooser.getExtensionFilters().add(extFilter);

        //Change in Directory
        fileChooser.setInitialDirectory(new File("./data/"));

        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try {
                List<String> strings = Files.readAllLines(file.toPath());
                reset();
                streetNetworkManager.importFile(strings);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Save the current Street network to a file
     * @param e - ActionEvent
     */
    @FXML
    private void saveFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Traffic Sim files (*.trafficsim)", "*.trafficsim");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(new File("./data/"));

        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                PrintWriter printWriter = new PrintWriter(file.getPath());
                printWriter.write(streetNetworkManager.export());
                printWriter.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Reset the simulation
     */
    private void reset() {
        //Reset the buttons
        startButton.setDisable(false);
        stopButton.setDisable(true);
        pauseButton.setDisable(true);
        streetNetworkManager.reset();
        vehicleManager.reset();
        area.reset();
    }

    /**
     * Update the simulation
     * @param delta - Time since the last Tick
     */
    public void update(double delta) {
        area.draw(delta);
    }

    /**
     * Add a Street to the managed pool
     * @param street - Street to be added
     */
    public void addStreet(Street street) {
        area.addStreet(street);
    }

    /**
     * Remove a Street from the managed pool
     * @param street - Street to be removed
     */
    public void removeStreet(Street street) {
        area.removeStreet(street);
    }

    /**
     * Add a vehicle to the managed pool
     * @param vehicle - Vehicle to be added
     */
    public void addVehicle(Vehicle vehicle) {
        area.addVehicle(vehicle);
    }

    /**
     * Remove a vehicle from the managed pool
     * @param vehicle - Vehicle to be removed
     */
    public void removeVehicle(Vehicle vehicle) {
        area.removeVehicle(vehicle);
    }

    /**
     * Start the simulation objects
     */
    public void startModules() {
        streetNetworkManager.start();
        vehicleManager.start();
        area.start();
    }

    /**
     * Stop the simulation objects
     */
    public void stopModules() {
        streetNetworkManager.stop();
        vehicleManager.stop();
        area.stop();
    }

    /**
     * Pause the simulation objects
     */
    public void pauseModules() {
        streetNetworkManager.pause();
        vehicleManager.pause();
        area.pause();
    }

    /**
     * Pass the key event to the area
     * @param event - Key Event to be passed
     */
    public void keyPressed(KeyEvent event) {
        area.keyPressed(event);
    }

    //TODO Comment
    /**
     *
     * @param street
     */
    public void newEditableStreet(StreetTwoPositions street) {
        area.newEditableStreet(street);
    }

    /**
     * Getter for the speed factor
     * @return Double representing the speed factor
     */
    public double getSpeedFactor() {
        return speedFactor;
    }

}
