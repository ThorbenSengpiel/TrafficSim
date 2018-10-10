package de.trafficsim.logic.network;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetRoundAbout;
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
        Street s0 = new StreetStraight(new Position(-100,-100),new Position(100,-100));
        Street s1 = new StreetStraight(new Position(100,-100),new Position(100,100));
        Street s2 = new StreetStraight(new Position(100,100),new Position(-100,100));
        Street s3 = new StreetStraight(new Position(-100,100),new Position(-100,-100));


        s0.getTracks().get(0).connectOutToInOf(s1.getTracks().get(0));
        s1.getTracks().get(0).connectOutToInOf(s2.getTracks().get(0));
        s2.getTracks().get(0).connectOutToInOf(s3.getTracks().get(0));
        s3.getTracks().get(0).connectOutToInOf(s0.getTracks().get(0));


        this.addStreet(new StreetRoundAbout(new Position(0, 0)));
        this.addStreet(s0, s1, s2, s3);
        addStreet(new StreetStraight(new Position(-100,-150),new Position(100,-150)));
    }

    public void addStreet(Street... streets){
        for (Street street : streets) {
            this.streetList.add(street);
            this.guiController.addStreet(street);
        }
    }

    public void addStreet(Street street){
        this.streetList.add(street);
        this.guiController.addStreet(street);
    }

}
