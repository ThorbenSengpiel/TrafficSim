package de.trafficsim;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.StreetNetworkManager;
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

    private StreetNetworkManager streetNetworkManager;

    private VehicleManager vehicleManager;

    long lastNow = 0;


    @Override
    public void start(Stage primaryStage) throws Exception{

        guiController = GuiController.getInstance();
        streetNetworkManager = StreetNetworkManager.getInstance();
        vehicleManager = VehicleManager.getInstance();



        buildGui(primaryStage);

        streetNetworkManager.initialize();

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
        if (lastNow == 0) {
            lastNow = now;
        } else {
            double delta = ((now-lastNow) / 1000) / 1000000.0;
            lastNow = now;
            streetNetworkManager.update(delta);
            vehicleManager.update(delta);
            guiController.update(delta);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
