package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetRoundAboutView;
import de.trafficsim.gui.views.StreetStraightView;
import de.trafficsim.gui.views.StreetView;

public enum StreetType {
    ROUNDABOUT("Roundabout"),
    STRAIGHT("Straight"),
    TEST_CROSS("TestCross"),
    PARKING_DECK("TestCross"),
    T_JUNCTION("TJunction");

    public final String name;


    StreetType(String name) {
        this.name = name;
    }
}
