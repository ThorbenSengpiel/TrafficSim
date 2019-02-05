package de.trafficsim.logic.network;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.streets.*;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.util.Direction;
import de.trafficsim.util.geometry.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for the streets. Implemented as a singleton
 */
public class StreetNetworkManager {

    private static StreetNetworkManager instance;
    //List of all managed streets
    private List<Street> streetList = new ArrayList<>();
    //List of the possible spawns
    private List<StreetSpawn> streetSpawnList = new ArrayList<>();
    private boolean running = false;

    private StreetNetworkManager() {

    }

    /**
     * Update each streets with the delta since last Tick
     * @param delta - Time since last Tick
     */
    public void update(double delta) {
        if (running){
            for (Street street : streetList) {
                //TODO löschen
                //street.begin();
                street.update(delta);
            }
        }

    }

    /**
     * Initialize the streetnetwork with a default map
     */
    public void initialize(){

        int size = 3;

        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                addStreet(new StreetCross(new Position(x*50, y*50)));
            }
        }
        for (int i = -halfSize; i <= halfSize; i++) {
            addStreet(new StreetParkingDeck(new Position(halfSize*50+50, i*50), Direction.NORTH));
            addStreet(new StreetParkingDeck(new Position(i*50, halfSize*50+50), Direction.EAST));
            addStreet(new StreetParkingDeck(new Position(-halfSize*50-50, i*50), Direction.SOUTH));
            addStreet(new StreetParkingDeck(new Position(i*50, -halfSize*50-50), Direction.WEST));
        }
    }

    /**
     * Add a variable amount of streets to the managed pool
     * @param streets - Streets to be managed
     */
    public void addStreet(Street... streets){
        for (Street street : streets) {
            addStreet(street);
        }
    }

    /**
     * Add a street to the managed pool
     * @param street - Street to be added
     */
    public void addStreet(Street street){
        connectStreet(street);
        streetList.add(street);
        //If it is a Spawn then add it to the list of possible Spawns
        if (street instanceof StreetSpawn) {
            streetSpawnList.add((StreetSpawn) street);
        }
        GuiController.getInstance().addStreet(street);
        changed = true;
    }

    /**
     * Remove a street from the managed pool
     * @param street - Street to be removed
     */
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

    /**
     * Get the instance of the StreetNetworkManager
     * @return Instance of the StreetNetworkManager
     */
    public static StreetNetworkManager getInstance() {
        if (instance == null) {
            instance = new StreetNetworkManager();
        }
        return instance;
    }

    /**
     * Returns a random free Track if there is one
     * @return Random free Track
     */
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

    /**
     * Returns a random end Track
     * @return Random end Track
     */
    public Track getRandomEnd() {
        return streetSpawnList.get((int) (streetSpawnList.size() * Math.random())).getEndTrack();
    }

    /**
     * Getter for the list of all streets
     * @return List of all streets
     */
    public List<Street> getStreetList() {
        return streetList;
    }

    /**
     * Connect a street with the street network
     * @param streetConnect
     */
    private void connectStreet(Street streetConnect) {
        // Check for each street in the managed pool
        for (Street street : streetList) {
            if (street != streetConnect) {
                //Check if an ingoing track collides with an outgoing track of the to add Street
                for (Track inTrack : street.getInTracks()) {
                    Position from = inTrack.getFrom().add(street.getPosition());
                    for (Track outTrack : streetConnect.getOutTracks()) {
                        Position to = outTrack.getTo().add(streetConnect.getPosition());
                        if (from.equals(to)) {
                            outTrack.connectOutToInOf(inTrack);
                        }
                    }
                }
                //Check if an outgoing track collides with an ingoing track of the to add Street
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

    /**
     * Disconnect the Street from the Streetnetwork
     * @param street - Street to be removed from the Streetnetwork
     */
    private void disconnectStreet(Street street) {
        for (Track track : street.getInTracks()) {
            track.disconnectAllIngoing();
        }
        for (Track track : street.getOutTracks()) {
            track.disconnectAllOutgoing();
        }
    }

    /**
     * Remove all Streets in the managed Pool
     */
    public void deleteAllStreets(){
        for (int i = streetList.size()-1; i >= 0; i--){
            removeStreet(streetList.get(i));
        }
    }

    /**
     * Start the manager
     */
    public void start() {
    running = true;
    }

    /**
     * Pause the manager
     */
    public void pause(){
    running = false;
    }

    /**
     * Stop the manager
     */
    public void stop(){
        running = false;
    }

    /**
     * Reset the the StreetNetwork
     */
    public void reset() {
        deleteAllStreets();
        running = false;
    }

    /**
     * Export the streetnetwork into a commaseparated format
     * @return String - Representing the streetnetwork
     */
    public String export() {
        StringBuilder sb = new StringBuilder();
        for (Street street : streetList) {
            sb.append(street.export()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Set Up the Streetnetwork by reading a List of String, which correspond to lines in a .trafficsim data
     * @param strings - List of Strings (Content of .trafficsim)
     */
    public void importFile(List<String> strings) {
        //Interpret each line
        for (String line : strings) {
            //System.out.println(line);
            String[] values = line.split(";");
            Street street = null;
            try {
                //Read the StreetTyüe
                street = StreetType.valueOf(values[0]).create();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            //Set the Position of the Street
            street.setPosition(new Position(Double.parseDouble(values[1]), Double.parseDouble(values[2])));
            if (street instanceof StreetTwoPositions) {
                if (values.length <= 4) {
                    continue;
                }
                //Read street Length
                street = ((StreetTwoPositions) street).createChanged(new Position(Double.parseDouble(values[4]), Double.parseDouble(values[5])));
            } else {
                //Rotate Streets
                //TODO schlechte Lösung aber läuft erstmal
                switch (Direction.valueOf(values[3])) {
                    case WEST:
                        street = street.createRotated();
                    case SOUTH:
                        street = street.createRotated();
                    case EAST:
                        street = street.createRotated();
                }
            }

            //Add the street to the manager
            addStreet(street);
        }
    }

    private boolean changed = true;
    private int trackCount;

    /**
     * Return the amount of Tracks in the StreetNetwork
     * @return
     */
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

    /**
     * Solve Deadlocks for each street
     * @param scaledDelta
     */
    public void solveDeadLocks(double scaledDelta) {
        for (Street street : streetList) {
            street.solveDeadLocks();
        }
    }
}
