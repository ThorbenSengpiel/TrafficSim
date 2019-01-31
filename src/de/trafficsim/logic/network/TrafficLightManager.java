package de.trafficsim.logic.network;

import de.trafficsim.logic.streets.signs.TrafficLight;

public class TrafficLightManager {

    private final TrafficLight[] trafficLights;

    private double time;
    private double greenTime;
    private double yellowTime;
    private double switchingTime;

    private double tSwitch;
    private double tRedYellow;
    private double tGreen;
    private double tYellow;

    private int currentTrafficLight;

    boolean grouped;
    int size;

    public TrafficLightManager(double greenTime, double yellowTime, double switchingTime, boolean grouped, TrafficLight... trafficLights) {
        this.trafficLights = trafficLights;
        this.greenTime = greenTime;
        this.yellowTime = yellowTime;
        this.switchingTime = switchingTime;
        this.grouped = grouped;
        size = grouped ? trafficLights.length/2 : trafficLights.length;
        tSwitch = switchingTime;
        tRedYellow = tSwitch + yellowTime;
        tGreen = tRedYellow + greenTime;
        tYellow = tGreen + yellowTime;

    }

    public void update(double delta) {
        time += delta;
        if (time <= tSwitch) {
            trafficLights[currentTrafficLight].setState(TrafficLight.RED);
            if (grouped) {
                trafficLights[currentTrafficLight+size].setState(TrafficLight.RED);
            }
        } else if (time <= tRedYellow) {
            trafficLights[currentTrafficLight].setState(TrafficLight.RED_YELLOW);
            if (grouped) {
                trafficLights[currentTrafficLight+size].setState(TrafficLight.RED_YELLOW);
            }
        } else if (time <= tGreen) {
            trafficLights[currentTrafficLight].setState(TrafficLight.GREEN);
            if (grouped) {
                trafficLights[currentTrafficLight+size].setState(TrafficLight.GREEN);
            }
        } else if (time <= tYellow) {
            trafficLights[currentTrafficLight].setState(TrafficLight.YELLOW);
            if (grouped) {
                trafficLights[currentTrafficLight+size].setState(TrafficLight.YELLOW);
            }
        } else {
            trafficLights[currentTrafficLight].setState(TrafficLight.RED);
            if (grouped) {
                trafficLights[currentTrafficLight+size].setState(TrafficLight.RED);
            }
            currentTrafficLight++;
            currentTrafficLight %= size;
            time -= tYellow;
        }
    }

}
