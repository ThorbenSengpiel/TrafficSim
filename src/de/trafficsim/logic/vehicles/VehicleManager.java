package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.Path;
import de.trafficsim.logic.network.Pathfinder;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetStraight;
import de.trafficsim.logic.streets.StreetStraight2Lane;
import de.trafficsim.logic.streets.tracks.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleManager {

    private static VehicleManager instance;


    private List<Vehicle> vehicleList = new ArrayList<>();

    private double spawnPerSecond = 1;

    private double spawnCnt = 0;
    private boolean running = false;

    private VehicleManager() {

    }

    public static VehicleManager getInstance() {
        if (instance == null) {
            instance = new VehicleManager();
        }
        return instance;
    }

    public void initialize() {

    }

    public void update(double delta) {
        if (running) {
            List<Vehicle> inactive = new ArrayList<>();
            for (Vehicle vehicle : vehicleList) {
                vehicle.move(delta);
                if (!vehicle.isActive()) {
                    inactive.add(vehicle);
                }
            }
            for (Vehicle vehicle : inactive) {
                removeVehicle(vehicle);
            }

            spawnCnt += spawnPerSecond * delta;
            for (int i = 0; i < (int) spawnCnt; i++) {
                spawnVehicle();
            }
            spawnCnt -= (int) spawnCnt;
        }
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleList.add(vehicle);
        GuiController.getInstance().addVehicle(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicleList.remove(vehicle);
        vehicle.getCurrentTrack().getVehiclesOnTrack().remove(vehicle);
        GuiController.getInstance().removeVehicle(vehicle);
    }

    public void spawnVehicle() {
        /*StreetSpawn spawn = StreetNetworkManager.getInstance().getRandomSpawn();
        addVehicle(new Vehicle(50, Pathfinder.getRandomPath(spawn.getStartTrack(), 20)));*/
        Track spawn = StreetNetworkManager.getInstance().getRandomSpawn();
        List<Street> allStreets = StreetNetworkManager.getInstance().getStreetList();
        List<Street> allStraights = allStreets.stream().filter(street -> street instanceof StreetStraight2Lane).collect(Collectors.toList());
        List<Street> intermediateStreets = new ArrayList<>();
        if (!allStraights.isEmpty()){
            for (int i = 0; i < 2; i++) {
                intermediateStreets.add(allStraights.get((int)(Math.random()*allStraights.size())));
            }
        }
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

    public void deleteAllVehicle() {
        for (int i = vehicleList.size() - 1; i >= 0; i--) {
            removeVehicle(vehicleList.get(i));
        }
    }

    public void start() {
        running = true;
    }

    public void pause() {
        running = false;
    }

    public void stop() {
        running = false;
        deleteAllVehicle();
    }

    public void reset() {
        stop();
    }

    public void setSpawnPerSecond(double spawnPerSecond) {
        this.spawnPerSecond = spawnPerSecond;
    }

    public double getSpawnPerSecond() {
        return spawnPerSecond;
    }
}
