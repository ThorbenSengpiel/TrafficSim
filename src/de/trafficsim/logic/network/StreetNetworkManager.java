package de.trafficsim.logic.network;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.streets.*;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;

public class StreetNetworkManager {

    private static StreetNetworkManager instance;

    private List<Street> streetList = new ArrayList<>();
    private List<StreetSpawn> streetSpawnList = new ArrayList<>();
    private boolean running = false;

    private StreetNetworkManager() {

    }

    public void update(double delta) {
      if (running){

      }

    }

    public void initialize(){
        /*Street p0 = new StreetParkingDeck(new Position(100, 50), Direction.NORTH);
        Street p1 = new StreetParkingDeck(new Position(100, 0), Direction.NORTH);
        Street p2 = new StreetParkingDeck(new Position(100, -50), Direction.NORTH);
        addStreet(p0, p1, p2);
        Street s = new StreetStraight(new Position(-100,-150),new Position(100,-150));*/

        /*Street s0 = new StreetStraight(new Position(-100,-100),new Position(100,-100));
        Street s1 = new StreetStraight(new Position(100,-100),new Position(100,100));
        Street s2 = new StreetStraight(new Position(100,100),new Position(-100,100));
        Street s3 = new StreetStraight(new Position(-100,100),new Position(-100,-100));



        addStreet(new StreetRoundAbout(new Position(0, -150), true));
        addStreet(s, s0, s1, s2, s3);*/
        //addStreet(new StreetStraight(new Position(-100,-150),new Position(100,-150)));


        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                addStreet(new StreetTestCross(new Position(x*50, y*50)));
            }
        }
        for (int i = -1; i <= 1; i++) {
            addStreet(new StreetParkingDeck(new Position(100, i*50), Direction.NORTH));
            addStreet(new StreetParkingDeck(new Position(i*50, 100), Direction.EAST));
            addStreet(new StreetParkingDeck(new Position(-100, i*50), Direction.SOUTH));
            addStreet(new StreetParkingDeck(new Position(i*50, -100), Direction.WEST));
        }

        /*for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                streets[x][y].outEast.connectOutToInOf(streets[(x+1)%3][y].inWest);
                streets[(x+1)%3][y].outWest.connectOutToInOf(streets[x][y].inEast);

                streets[x][y].outSouth.connectOutToInOf(streets[x][(y+1)%3].inNorth);
                streets[x][(y+1)%3].outNorth.connectOutToInOf(streets[x][y].inSouth);
            }
        }*/

        for (Street street : streetList) {
            street.disconnect();
            connectStreet(street);
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
            if (street instanceof StreetSpawn) {
                streetSpawnList.add((StreetSpawn) street);
            }
            GuiController.getInstance().addStreet(street);
        }
    }

    public void addStreet(Street street){
        streetList.add(street);
        if (street instanceof StreetSpawn) {
            streetSpawnList.add((StreetSpawn) street);
        }
        GuiController.getInstance().addStreet(street);
    }

    public void removeStreet(Street street) {
        street.disconnect();
        street.removeAllVehicles();
        streetList.remove(street);
        if (street instanceof StreetSpawn) {
            streetSpawnList.remove(street);
        }
        GuiController.getInstance().removeStreet(street);
    }

    public static StreetNetworkManager getInstance() {
        if (instance == null) {
            instance = new StreetNetworkManager();
        }
        return instance;
    }

    public StreetSpawn getRandomSpawn() {
        return streetSpawnList.get((int) (streetSpawnList.size() * Math.random()));
    }

    public List<Street> getStreetList() {
        return streetList;
    }

    public void connectStreet(Street streetConnect) {
        for (Street street : streetList) {
            if (street != streetConnect) {
                for (Track inTrack : street.getInTracks()) {
                    Position from = inTrack.getFrom().add(street.getPosition());
                    for (Track outTrack : streetConnect.getOutTracks()) {
                        Position to = outTrack.getTo().add(streetConnect.getPosition());
                        if (from.equals(to)) {
                            outTrack.connectOutToInOf(inTrack);
                        }
                    }
                }
                for (Track outTrack : street.getOutTracks()) {
                    Position to = outTrack.getTo().add(street.getPosition());
                    for (Track inTrack : streetConnect.getInTracks()) {
                        Position from = inTrack.getFrom().add(streetConnect.getPosition());
                        if (from.equals(to)) {
                            outTrack.connectOutToInOf(inTrack);
                        }
                    }
                }
            }
        }
    }

    public List<Track> createRandomPath(){
        return Pathfinder.getRandomPath(streetList.get((int)Math.random()*streetList.size()).getTracks().get(0),3);
    }

    public void deleteAllStreets(){
      this.streetList.clear();
      this.streetSpawnList.clear();
    }

  public void start() {
      this.running = true;
  }

  public void pause(){
      this.running = false;
  }

  public void stop(){
      this.running = false;
  }
}
