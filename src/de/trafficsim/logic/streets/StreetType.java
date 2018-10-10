package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetRoundAboutView;
import de.trafficsim.gui.views.StreetStraightView;
import de.trafficsim.gui.views.StreetView;

public enum StreetType {
    ROUNDABOUT("Roundabout"),
    STRAIGHT("Straight");

    public final String name;


    StreetType(String name) {
        this.name = name;
    }
}