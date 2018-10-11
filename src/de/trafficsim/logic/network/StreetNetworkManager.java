package de.trafficsim.logic.network;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetRoundAbout;
import de.trafficsim.logic.streets.StreetStraight;
import de.trafficsim.logic.streets.StreetTestCross;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;

public class StreetNetworkManager {

    private static StreetNetworkManager instance;

    private List<Street> streetList = new ArrayList<>();


    public void update(double delta) {

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


        addStreet(new StreetRoundAbout(new Position(0, -150), true));
        addStreet(s0, s1, s2, s3);
        addStreet(new StreetStraight(new Position(-100,-150),new Position(100,-150)));

        addStreet(new StreetTestCross(Position.ZERO));
    }

    public void addStreet(Street... streets){
        for (Street street : streets) {
            streetList.add(street);
            GuiController.getInstance().addStreet(street);
        }
    }

    public void addStreet(Street street){
        streetList.add(street);
        GuiController.getInstance().addStreet(street);
    }

    public static StreetNetworkManager getInstance() {
        if (instance == null) {
            instance = new StreetNetworkManager();
        }
        return instance;
    }

    public List<Street> getStreetList() {
        return streetList;
    }
}
