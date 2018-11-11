package de.trafficsim.gui;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetTwoPositions;
import de.trafficsim.logic.vehicles.Vehicle;
import de.trafficsim.logic.vehicles.VehicleManager;
import de.trafficsim.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

public class GuiController {

    private static GuiController instance;

    @FXML
    AnchorPane paneCanvas;

    @FXML
    CheckBox checkShowTracks;

    @FXML
    CheckBox checkShowBoundingBox;

    @FXML
    CheckBox checkShowHitBox;

    @FXML
    Button addCarButton;

    @FXML
    Button startButton;

    @FXML
    Button pauseButton;

    @FXML
    Button stopButton;

    @FXML
    CheckBox checkShowFancyGraphics;

    @FXML
    CheckBox checkShowVehicleInfo;

    @FXML
    CheckBox checkShowTrackInfo;

    @FXML
    Slider spawnSlider;

    @FXML
    TextField spawnTextField;

    @FXML
    Slider speedSlider;

    @FXML
    Label speedLabel;

    private float speedFactor;
    private Area area;
    private VehicleManager vehicleManager;
    private StreetNetworkManager streetNetworkManager;

    private Stage primaryStage;

    private GuiController() {

    }

    public static GuiController getInstance() {
        if (instance == null) {
            instance = new GuiController();
        }
        return instance;
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        area = new Area();
        vehicleManager = VehicleManager.getInstance();
        streetNetworkManager = StreetNetworkManager.getInstance();
        paneCanvas.getChildren().add(area);
        area.widthProperty().bind(paneCanvas.widthProperty().subtract(20));
        area.heightProperty().bind(paneCanvas.heightProperty().subtract(20));

        checkShowTracks.setOnAction(event -> area.setShowTracks(checkShowTracks.isSelected()));
        checkShowVehicleInfo.setOnAction(event -> area.setShowVehicleInfo(checkShowVehicleInfo.isSelected()));
        checkShowTrackInfo.setOnAction(event -> area.setShowTrackInfo(checkShowTrackInfo.isSelected()));
        checkShowBoundingBox.setOnAction(event -> area.setShowBoundingBox(checkShowBoundingBox.isSelected()));
        checkShowHitBox.setOnAction(event -> area.setShowHitBox(checkShowHitBox.isSelected()));
        addCarButton.setOnAction(event -> vehicleManager.spawnVehicle());
        checkShowFancyGraphics.setOnAction(event -> area.setFancyGraphics(checkShowFancyGraphics.isSelected()));
        startButton.setOnAction(event -> startModules());
        stopButton.setOnAction(event -> stopModules());
        pauseButton.setOnAction(event -> pauseModules());

        //set up the cars/sec widgets
        spawnSlider.setValue(vehicleManager.getSpawnPerSecond());
        spawnTextField.setText(Util.DOUBLE_FORMAT_0_00.format(spawnSlider.getValue()));
        //change slider value
        spawnSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            vehicleManager.setSpawnPerSecond(newValue.doubleValue());
            spawnTextField.setText(Util.DOUBLE_FORMAT_0_00.format(vehicleManager.getSpawnPerSecond()));
        });
        //change textField value
        spawnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double value = Double.parseDouble(newValue);
                vehicleManager.setSpawnPerSecond(value);
                spawnSlider.setValue(value);
            }catch (NumberFormatException e){
            }
        });
        //set up speed widgets
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            speedFactor = (float) Math.pow(2, Math.round(newValue.doubleValue()));
            speedLabel.setText(speedFactor+"x");
        });

    }

    @FXML
    private void createNew(ActionEvent e) {
        reset();
    }

    @FXML
    private void openFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Traffic Sim files (*.trafficsim)", "*.trafficsim");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(new File("./data/"));

        //Show save file dialog
        try {
            List<String> strings = Files.readAllLines(fileChooser.showOpenDialog(primaryStage).toPath());
            reset();
            streetNetworkManager.importFile(strings);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    @FXML
    private void saveFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Traffic Sim files (*.trafficsim)", "*.trafficsim");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(new File("./data/"));

        //Show save file dialog
        try {
            PrintWriter printWriter = new PrintWriter(fileChooser.showSaveDialog(primaryStage).getPath());
            printWriter.write(streetNetworkManager.export());
            printWriter.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    private void reset() {
        streetNetworkManager.reset();
        vehicleManager.reset();
        area.reset();
    }

    public void update(double delta) {
        area.draw(delta);
    }

    public void addStreet(Street street) {
        area.addStreet(street);
    }

    public void removeStreet(Street street) {
        area.removeStreet(street);
    }

    public void addVehicle(Vehicle vehicle) {
        area.addVehicle(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        area.removeVehicle(vehicle);
    }

    public void startModules() {
        streetNetworkManager.start();
        vehicleManager.start();
        area.start();
    }

    public void stopModules() {
        streetNetworkManager.stop();
        vehicleManager.stop();
        area.stop();
    }

    public void pauseModules() {
        streetNetworkManager.pause();
        vehicleManager.pause();
        area.pause();
    }

    public void keyPressed(KeyEvent event) {
        area.keyPressed(event);
    }

    public void newEditableStreet(StreetTwoPositions street) {
        area.newEditableStreet(street);
    }

    public float getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
    }
}
