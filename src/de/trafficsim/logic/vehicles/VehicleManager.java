package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.Path;
import de.trafficsim.logic.network.Pathfinder;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetStraight2Lane;
import de.trafficsim.logic.streets.tracks.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class, which manages the Vehicles in the Street Network. Implemented as a singleton
 */
public class VehicleManager {

    private static VehicleManager instance;


    private List<Vehicle> vehicleList = new ArrayList<>();

    //Spawnrate
    private double spawnPerSecond = 1;

    private double spawnCnt = 0;
    private boolean running = false;

    private VehicleManager() {

    }

    /**
     * Get an instance of the Vehicle Manager
     * @return Vehicle Manager instance
     */
    public static VehicleManager getInstance() {
        if (instance == null) {
            instance = new VehicleManager();
        }
        return instance;
    }

    public void initialize() {

    }

    /**
     * Update each Vehicle managed by the Vehicle Manager
     * @param delta - Time since last tick
     */
    public void update(double delta) {
        if (running) {
            List<Vehicle> inactive = new ArrayList<>();
            /**
             * Move each vehicle
             */
            for (Vehicle vehicle : vehicleList) {
                vehicle.move(delta);
                if (!vehicle.isActive()) {
                    inactive.add(vehicle);
                }
            }
            /**
             * If there are inactive vehicles removed them
             */
            for (Vehicle vehicle : inactive) {
                removeVehicle(vehicle);
            }

            //Vehicles to spawn
            spawnCnt += spawnPerSecond * delta;
            for (int i = 0; i < (int) spawnCnt; i++) {
                spawnVehicle();
            }
            spawnCnt -= (int) spawnCnt;
        }
    }

    /**
     * Add a vehicle to the managed pool
     * @param vehicle - Vehicle to be added
     */
    public void addVehicle(Vehicle vehicle) {
        vehicleList.add(vehicle);
        GuiController.getInstance().addVehicle(vehicle);
    }

    /**
     * Remove a vehicle from the managed pool
     * @param vehicle - Vehicle to be removed
     */
    public void removeVehicle(Vehicle vehicle) {
        vehicleList.remove(vehicle);
        vehicle.getCurrentTrack().getVehiclesOnTrack().remove(vehicle);
        GuiController.getInstance().removeVehicle(vehicle);
    }

    /**
     * Spawn a vehicle at a random position if possible
     */
    public void spawnVehicle() {
        /*StreetSpawn spawn = StreetNetworkManager.getInstance().getRandomSpawn();
        addVehicle(new Vehicle(50, Pathfinder.getRandomPath(spawn.getStartTrack(), 20)));*/
        Track spawn = StreetNetworkManager.getInstance().getRandomSpawn();
        //Amount of intermediate Points
        int intermediatePointCount = (int) (Math.random() * 2);
        List<Street> intermediateStreets = new ArrayList<>();
        //If there should be at least one intermediate point
        if (intermediatePointCount > 0){
            List<Street> allStreets = StreetNetworkManager.getInstance().getStreetList();
            //Only Select Straights
            List<Street> allStraights = allStreets.stream().filter(street -> street instanceof StreetStraight2Lane).collect(Collectors.toList());
            if (!allStraights.isEmpty()){
                //Randomly pick the intermediate points
                for (int i = 0; i < intermediatePointCount; i++) {
                    intermediateStreets.add(allStraights.get((int)(Math.random()*allStraights.size())));
                }
            }
        }
        // Determine a random destination end
        Track destination = StreetNetworkManager.getInstance().getRandomEnd();
        if (spawn != null) {
            Path path = Pathfinder.getPath(spawn, intermediateStreets,destination);
            if (path != null) {
                addVehicle(new Vehicle(0,path));
            } else {
                System.out.println("no Path found between " + spawn + " and " + destination);
            }
        }
    }


    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    /**
     * Delete all vehicles from the managed pool
     */
    public void deleteAllVehicle() {
        for (int i = vehicleList.size() - 1; i >= 0; i--) {
            removeVehicle(vehicleList.get(i));
        }
    }

    /**
     * Start the Vehicle Manager
     */
    public void start() {
        running = true;
    }

    /**
     * Pause the Vehicle Manager
     */
    public void pause() {
        running = false;
    }

    /**
     * Stop the Vehicle Manager
     */
    public void stop() {
        running = false;
        deleteAllVehicle();
    }

    /**
     * Reset the Vehicle Manager
     */
    public void reset() {
        stop();
    }

    /**
     * Set the Spawn Rate
     * @param spawnPerSecond - Spawn Rate to be set
     */
    public void setSpawnPerSecond(double spawnPerSecond) {
        this.spawnPerSecond = spawnPerSecond;
    }

    public double getSpawnPerSecond() {
        return spawnPerSecond;
    }
}
