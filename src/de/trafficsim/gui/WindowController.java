package de.trafficsim.gui;

import de.trafficsim.gui.graphics.Area;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class WindowController {

    @FXML
    AnchorPane paneCanvas;

    private Area area;

    public void start() {
        area = new Area();
        paneCanvas.getChildren().add(area);
        area.widthProperty().bind(paneCanvas.widthProperty().subtract(20));
        area.heightProperty().bind(paneCanvas.heightProperty().subtract(20));
    }


}
