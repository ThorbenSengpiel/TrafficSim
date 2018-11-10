package de.trafficsim.logic.streets;

import de.trafficsim.gui.menu.MenuCategory;

public enum StreetType {
    ROUNDABOUT("Roundabout", MenuCategory.LANES, StreetRoundAbout.class),
    STRAIGHT("Straight", MenuCategory.LANES, StreetStraight.class),
    STRAIGHT_2_LANE("Straight 2 Lane", MenuCategory.LANES, StreetStraight2Lane.class),

    TEST_CROSS("TestCross", MenuCategory.JUNCTIONS, StreetTestCross.class),
    T_JUNCTION("T Junction", MenuCategory.JUNCTIONS, StreetTJunction.class),

    PARKING_DECK("TestCross", MenuCategory.SPAWNS, StreetParkingDeck.class);

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
