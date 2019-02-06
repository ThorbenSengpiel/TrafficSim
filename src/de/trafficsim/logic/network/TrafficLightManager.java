package de.trafficsim.logic.network;

import de.trafficsim.logic.streets.signs.TrafficLight;

/**
 * Manager controlling the Trafficlights of a given Street
 */
public class TrafficLightManager {

    private final TrafficLight[] trafficLights;

    private double time;

    //Duration of the Traffic Light phases
    private double tSwitch;
    private double tRedYellow;
    private double tGreen;
    private double tYellow;

    private int currentTrafficLight;

    boolean grouped;
    int size;

    public TrafficLightManager(double greenTime, double yellowTime, double switchingTime, boolean grouped, TrafficLight... trafficLights) {
        this.trafficLights = trafficLights;
        this.grouped = grouped;
        size = grouped ? trafficLights.length/2 : trafficLights.length;
        tSwitch = switchingTime;
        tRedYellow = tSwitch + yellowTime;
        tGreen = tRedYellow + greenTime;
        tYellow = tGreen + yellowTime;

    }

    /**
     * Update the state of the TrafficLight
     * @param delta - Delta since last tick
     */
    public void update(double delta) {
        time += delta;
        //Switch between the Trafficlight phases and the different Trafficlights
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
