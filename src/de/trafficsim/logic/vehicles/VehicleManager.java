package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.Pathfinder;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.StreetSpawn;

import java.util.ArrayList;
import java.util.List;

public class VehicleManager {

    private static VehicleManager instance;


    private List<Vehicle> vehicleList = new ArrayList<>();

    private double spawnPerSecond = 10;

    private double spawnCnt = 0;

    private VehicleManager() {

    }

    public void initialize() {

    }

    public void update(double delta) {
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
        for (int i = 0; i < (int)spawnCnt; i++) {
            spawnVehicle();
        }
        spawnCnt -= (int)spawnCnt;
    }


    public void addVehicle(Vehicle vehicle){
        vehicleList.add(vehicle);
        GuiController.getInstance().addVehicle(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicleList.remove(vehicle);
        vehicle.getCurrentTrack().getVehiclesOnTrack().remove(vehicle);
        GuiController.getInstance().removeVehicle(vehicle);
    }

    public static VehicleManager getInstance() {
        if (instance == null) {
            instance = new VehicleManager();
        }
        return instance;
    }

    public void spawnVehicle() {
        /*StreetSpawn spawn = StreetNetworkManager.getInstance().getRandomSpawn();
        addVehicle(new Vehicle(50, Pathfinder.getRandomPath(spawn.getStartTrack(), 20)));*/
        addVehicle(new Vehicle(50, Pathfinder.getPath(StreetNetworkManager.getInstance().getRandomSpawn().getStartTrack(), StreetNetworkManager.getInstance().getRandomSpawn().getEndTrack())));
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }
}
