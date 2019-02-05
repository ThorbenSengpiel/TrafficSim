package de.trafficsim.logic.streets;

import de.trafficsim.gui.menu.MenuCategory;

public enum StreetType {
    STRAIGHT_2_LANE("Straight", MenuCategory.LANES, StreetStraight2Lane.class),
    CURVE("Curve", MenuCategory.LANES, StreetCurve.class),

    CROSS("Cross", MenuCategory.JUNCTIONS, StreetCross.class),
    ROUNDABOUT("Roundabout", MenuCategory.JUNCTIONS, StreetRoundAbout.class),
    T_JUNCTION("T Junction", MenuCategory.JUNCTIONS, StreetTJunction.class),

    PARKING_DECK("Parking Deck", MenuCategory.SPAWNS, StreetParkingDeck.class),

    CROSS_TRAFFICLIGHTS("Trafficlight", MenuCategory.TRAFFIC_LIGHTS, StreetCrossTrafficLights.class),
    CROSS_TRAFFICLIGHTS_SMALL("Trafficlight Small", MenuCategory.TRAFFIC_LIGHTS, StreetCrossSmallTrafficLights.class);

    public final String uiName;
    public final Class clazz;
    public final MenuCategory category;

    StreetType(String uiName, MenuCategory category, Class<? extends Street> clazz) {
        this.uiName = uiName;
        this.category = category;
        this.clazz = clazz;
    }

    public Street create() throws IllegalAccessException, InstantiationException {
        return (Street) clazz.newInstance();
    }
}
