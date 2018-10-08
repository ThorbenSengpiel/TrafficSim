package de.trafficsim;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.TrackNetworkManager;
import de.trafficsim.logic.vehicles.VehicleManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TrafficSim extends Application {

    private GuiController guiController;

    private TrackNetworkManager trackNetworkManager;

    private VehicleManager vehicleManager;


    @Override
    public void start(Stage primaryStage) throws Exception{

        guiController = new GuiController();
        trackNetworkManager = new TrackNetworkManager();
        vehicleManager = new VehicleManager();



        buildGui(primaryStage);


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                mainLoop(now);
            }
        }.start();
    }

    private void buildGui(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/window.fxml"));

        loader.setController(guiController);
        Parent root = loader.load();
        primaryStage.setTitle("TrafficSim");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        guiController.start();
    }

    private void mainLoop(long now) {
        trackNetworkManager.update(now);
        vehicleManager.update(now);
        guiController.update(now);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
