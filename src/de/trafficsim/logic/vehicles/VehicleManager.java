package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;

import java.util.ArrayList;
import java.util.List;

public class VehicleManager {

    private static VehicleManager instance;


    List<Vehicle> vehicleList = new ArrayList<>();


    public void initialize() {
        addVehicle(new Vehicle(27.55, StreetNetworkManager.getInstance().getStreetList().get(1).getTracks().get(0)));
    }

    public void update(double delta) {
        for (Vehicle vehicle : vehicleList) {
            vehicle.move(delta);
        }
    }

    public void addVehicle(Vehicle vehicle){
        vehicleList.add(vehicle);
        GuiController.getInstance().addVehicle(vehicle);
    }

    public static VehicleManager getInstance() {
        if (instance == null) {
            instance = new VehicleManager();
        }
        return instance;
    }
}
