package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetSpawn;

import java.util.ArrayList;
import java.util.List;

public class VehicleManager {

    private static VehicleManager instance;


    private List<Vehicle> vehicleList = new ArrayList<>();

    private double spawnPerSecond = 50;

    private double spawnCnt = 0;

    public void initialize() {
        /*addVehicle(new Vehicle(50, StreetNetworkManager.getInstance().getStreetList().get(1).getTracks().get(0)));
        addVehicle(new Vehicle(50, StreetNetworkManager.getInstance().getStreetList().get(0).getTracks().get(0)));
        addVehicle(new Vehicle(20, StreetNetworkManager.getInstance().getStreetList().get(StreetNetworkManager.getInstance().getStreetList().size()-1).getTracks().get(0)));
        for (int i = 0; i < 50; i++) {
            addVehicle(new Vehicle(5+ Math.random() * 50, StreetNetworkManager.getInstance().getStreetList().get((int) (StreetNetworkManager.getInstance().getStreetList().size() * Math.random())).getTracks().get(0)));
        }*/
    }

    public void update(double delta) {
        long t0 = System.nanoTime();
        List<Vehicle> inactive = new ArrayList<>();
        for (Vehicle vehicle : vehicleList) {
            vehicle.move(delta);
            if (!vehicle.isActive()) {
                inactive.add(vehicle);
            }
        }
        long t1 = System.nanoTime();
        for (Vehicle vehicle : inactive) {
            removeVehicle(vehicle);
        }
        spawnCnt += spawnPerSecond * delta;
        for (int i = 0; i < (int)spawnCnt; i++) {
            spawnVehicle();
        }
        spawnCnt -= (int)spawnCnt;
        long t2 = System.nanoTime();
        //System.out.println("Cars update: " + t(t2-t0) + ": " + t(t1-t0) + " " + t(t2-t1));
    }

    private String t(long t) {
        return (t / 1000)+"";
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
        StreetSpawn spawn = StreetNetworkManager.getInstance().getRandomSpawn();
        addVehicle(new Vehicle(30, spawn.getStartTrack()));
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }
}
