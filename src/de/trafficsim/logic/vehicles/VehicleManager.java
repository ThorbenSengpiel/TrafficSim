package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.StreetSpawn;

import java.util.ArrayList;
import java.util.List;

public class VehicleManager {

    private static VehicleManager instance;


    private List<Vehicle> vehicleListNormal = new ArrayList<>();
    private List<Vehicle> vehicleListNeu = new ArrayList<>();

    private double spawnPerSecond = 1;
    private double spawnCnt = 0;


    public void initialize() {
        for (int i = 0; i < 50; i++) {
            addVehicle(new Vehicle(5+ Math.random() * 50, StreetNetworkManager.getInstance().getStreetList().get((int) (StreetNetworkManager.getInstance().getStreetList().size() * Math.random())).getTracks().get(0)));
        }
        for (int i = 0; i < 100; i++) {
            Vehicle vehicle = new Vehicle(15, StreetNetworkManager.getInstance().creatRandomPath());
            vehicleListNeu.add(vehicle);
            GuiController.getInstance().addVehicle(vehicle);
        }
    }

    public void update(double delta) {
        List<Vehicle> inactive = new ArrayList<>();
        System.out.println("neue Vehicle: "+vehicleListNeu.size());
        System.out.println("alte Vehicle: "+vehicleListNormal.size());
        for (Vehicle vehicle : vehicleListNormal) {
            vehicle.move(delta);
            if (!vehicle.isActive()) {
                inactive.add(vehicle);
            }
        }
        for (Vehicle vehicle : vehicleListNeu) {
            vehicle.drivePath(delta);
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
        vehicleListNormal.add(vehicle);
        GuiController.getInstance().addVehicle(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicleListNormal.remove(vehicle);
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
        StreetSpawn spawn = StreetNetworkManager.getInstance().getRandomSpawn();
        addVehicle(new Vehicle(5+ Math.random() * 50, spawn.getTracks()));
    }
}
