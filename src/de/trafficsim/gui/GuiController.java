package de.trafficsim.gui;

import de.trafficsim.gui.graphics.Area;
import de.trafficsim.logic.streets.Street;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;

public class GuiController {

    @FXML
    AnchorPane paneCanvas;

    @FXML
    CheckBox checkShowTracks;

    @FXML
    CheckBox checkShowBoundingBox;

    @FXML
    CheckBox checkShowHitBox;


    private Area area;

    public void start() {
        area = new Area();
        paneCanvas.getChildren().add(area);
        area.widthProperty().bind(paneCanvas.widthProperty().subtract(20));
        area.heightProperty().bind(paneCanvas.heightProperty().subtract(20));

        checkShowTracks.setOnAction(event -> area.setShowTracks(checkShowTracks.isSelected()));
        checkShowBoundingBox.setOnAction(event -> area.setShowBoundingBox(checkShowBoundingBox.isSelected()));
        checkShowHitBox.setOnAction(event -> area.setShowHitBox(checkShowHitBox.isSelected()));
    }


    public void update(long now) {
        area.draw(now);
    }

    public void addStreet(Street street) {

    }

    public void removeStreet(Street street) {

    }
}
