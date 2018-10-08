package de.trafficsim;

import de.trafficsim.gui.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TrafficSim extends Application {

    private WindowController controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/window.fxml"));
        controller = new WindowController();

        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.setTitle("TrafficSim");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
