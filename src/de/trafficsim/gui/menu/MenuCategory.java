package de.trafficsim.gui.menu;

public enum MenuCategory {

    LANES("Lanes", null),
    JUNCTIONS("Junctions", null),
    TRAFFIC_LIGHTS("Trafficlights", null),
    SPAWNS("Spawns", null);

    public final String uiName;
    public final MenuCategory parentCategory;

    MenuCategory(String uiName, MenuCategory parentCategory) {
        this.uiName = uiName;
        this.parentCategory = parentCategory;
    }
}
