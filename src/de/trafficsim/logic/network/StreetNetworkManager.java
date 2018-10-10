package de.trafficsim.logic.network;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetStraight;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;

public class StreetNetworkManager {
    private List<Street> streetList = new ArrayList<>();

    private GuiController guiController;
    public StreetNetworkManager(GuiController guicon){
        this.guiController = guicon;
    }

    public void update(long now) {

    }

    public void initialize(){
        this.addStreet(new StreetStraight(new Position(100,100),new Position(500,100)));
        this.addStreet(new StreetStraight(new Position(100,300),new Position(500,300)));
        this.addStreet(new StreetStraight(new Position(100,500),new Position(500,500)));
        this.addStreet(new StreetStraight(new Position(100,700),new Position(500,700)));
    }

    public void addStreet(Street street){
        this.streetList.add(street);
        this.guiController.addStreet(street);
    }

}
