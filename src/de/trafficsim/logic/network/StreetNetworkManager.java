package de.trafficsim.logic.network;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetRoundAbout;
import de.trafficsim.logic.streets.StreetStraight;
import de.trafficsim.logic.streets.StreetTestCross;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;

public class StreetNetworkManager {

    private static StreetNetworkManager instance;

    private List<Street> streetList = new ArrayList<>();


    public void update(double delta) {

    }

    public void initialize(){
        Street s = new StreetStraight(new Position(-100,-150),new Position(100,-150));

        Street s0 = new StreetStraight(new Position(-100,-100),new Position(100,-100));
        Street s1 = new StreetStraight(new Position(100,-100),new Position(100,100));
        Street s2 = new StreetStraight(new Position(100,100),new Position(-100,100));
        Street s3 = new StreetStraight(new Position(-100,100),new Position(-100,-100));


        s0.getTracks().get(0).connectOutToInOf(s1.getTracks().get(0));
        s1.getTracks().get(0).connectOutToInOf(s2.getTracks().get(0));
        s2.getTracks().get(0).connectOutToInOf(s3.getTracks().get(0));
        s3.getTracks().get(0).connectOutToInOf(s0.getTracks().get(0));


        addStreet(new StreetRoundAbout(new Position(0, -150), true));
        addStreet(s, s0, s1, s2, s3);
        //addStreet(new StreetStraight(new Position(-100,-150),new Position(100,-150)));

        StreetTestCross[][] streets = new StreetTestCross[3][3];

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                streets[x+1][y+1] = new StreetTestCross(new Position(x*50, y*50));
                addStreet(streets[x+1][y+1]);
            }
        }

        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                streets[x][y].outEast.connectOutToInOf(streets[(x+1)%3][y].inWest);
                streets[(x+1)%3][y].outWest.connectOutToInOf(streets[x][y].inEast);

                streets[x][y].outSouth.connectOutToInOf(streets[x][(y+1)%3].inNorth);
                streets[x][(y+1)%3].outNorth.connectOutToInOf(streets[x][y].inSouth);
            }
        }

        int cnt = 0;
        for (Street street : streetList) {
            for (Track track : street.getTracks()) {
                cnt++;
            }
        }
        System.out.println("TrackCount = " + cnt);
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
