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
            for (Street street : streetList) {
                street.update(delta);
            }
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
                addStreet(new StreetCross(new Position(x*50, y*50)));
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
    }

    public void addStreet(Street... streets){
        for (Street street : streets) {
            addStreet(street);
        }
    }

    public void addStreet(Street street){
        connectStreet(street);
        streetList.add(street);
        if (street instanceof StreetSpawn) {
            streetSpawnList.add((StreetSpawn) street);
        }
        GuiController.getInstance().addStreet(street);
        changed = true;
    }

    public void removeStreet(Street street) {
        disconnectStreet(street);
        street.removeAllVehicles();
        streetList.remove(street);
        if (street instanceof StreetSpawn) {
            streetSpawnList.remove(street);
        }
        GuiController.getInstance().removeStreet(street);
        changed = true;
    }

    public static StreetNetworkManager getInstance() {
        if (instance == null) {
            instance = new StreetNetworkManager();
        }
        return instance;
    }

    public Track getRandomSpawn() {
        List<StreetSpawn> spawns = new ArrayList<>();
        spawns.addAll(streetSpawnList);
        int cnt = spawns.size();
        for (int i = 0; i < cnt; i++) {
            StreetSpawn spawn = spawns.get((int) (spawns.size() * Math.random()));
            spawns.remove(spawn);
            if (spawn.getStartTrack().isFree()) {
                return spawn.getStartTrack();
            }
        }
        return null;
    }

    public Track getRandomEnd() {
        return streetSpawnList.get((int) (streetSpawnList.size() * Math.random())).getEndTrack();
    }

    public List<Street> getStreetList() {
        return streetList;
    }

    private void connectStreet(Street streetConnect) {
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

    private void disconnectStreet(Street street) {
        for (Track track : street.getInTracks()) {
            track.disconnectAllIngoing();
        }
        for (Track track : street.getOutTracks()) {
            track.disconnectAllOutgoing();
        }
    }

    public void deleteAllStreets(){
        for (int i = streetList.size()-1; i >= 0; i--){
            removeStreet(streetList.get(i));
        }
    }

    public void start() {
    running = true;
    }

    public void pause(){
    running = false;
    }

    public void stop(){
        running = false;
    }

    public void reset() {
        deleteAllStreets();
        running = false;
    }

    public String export() {
        StringBuilder sb = new StringBuilder();
        for (Street street : streetList) {
            sb.append(street.export());
        }
        return sb.toString();
    }

    public void importFile(List<String> strings) {
        for (String line : strings) {
            System.out.println(line);
            String[] values = line.split(";");
            Street street = null;
            try {
                street = StreetType.valueOf(values[0]).create();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            street.setPosition(new Position(Double.parseDouble(values[1]), Double.parseDouble(values[2])));
            //TODO schlechte Lösung aber läuft erstmal
            switch (Direction.valueOf(values[3])) {
                case WEST:
                    street = street.createRotated();
                case SOUTH:
                    street = street.createRotated();
                case EAST:
                    street = street.createRotated();
            }
            addStreet(street);
        }
    }

    private boolean changed = true;
    private int trackCount;

    public int getTrackCount() {
        if (changed) {
            trackCount = 0;
            for (Street street : streetList) {
                for (Track track : street.getTracks()) {
                    trackCount++;
                }
            }
            changed = false;
        }
        return trackCount;
    }
}
