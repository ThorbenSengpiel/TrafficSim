package de.trafficsim.logic.vehicles;

import de.trafficsim.gui.GuiController;
import de.trafficsim.logic.network.Path;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.tracks.Track;

import java.util.ArrayList;
import java.util.List;

public class StaticVehicle extends Vehicle {

    public StaticVehicle(double position, int trackID) {
        super(0, new Path(getTrack(trackID)));
        currentPosInTrack = position;

    }

    private static Track getTrack(int trackID) {
        for (Street street : StreetNetworkManager.getInstance().getStreetList()) {
            for (Track track : street.getTracks()) {
                if (track.id == trackID) {
                    return track;
                }
            }
        }
        return null;
    }

    @Override
    public void move(double delta) {

    }
}
