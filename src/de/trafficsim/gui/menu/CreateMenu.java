package de.trafficsim.gui.menu;

import de.trafficsim.gui.GuiController;
import de.trafficsim.gui.graphics.Area;
import de.trafficsim.logic.network.StreetNetworkManager;
import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.StreetStraight2Lane;
import de.trafficsim.logic.streets.StreetTwoPositions;
import de.trafficsim.logic.streets.StreetType;
import de.trafficsim.util.geometry.Position;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.HashMap;

public class CreateMenu extends ContextMenu {

    private Area editor;
    private Position pos;

    public CreateMenu(Area editor) {
        this.editor = editor;
        HashMap<MenuCategory, CreateSubMenu> subMenus = new HashMap<>();

        MenuItem del = new MenuItem("Remove");
        getItems().add(del);
        del.setOnAction(event -> editor.removeStreetAt(pos));

        for (MenuCategory category : MenuCategory.values()) {
            CreateSubMenu subMenu = new CreateSubMenu(category);
            subMenus.put(category, subMenu);
            if (category.parentCategory != null) {
                subMenus.get(category.parentCategory).getItems().add(subMenu);
            } else {
                getItems().add(subMenu);
            }
        }

        for (StreetType street : StreetType.values()) {
            if (street.category != null) {
                subMenus.get(street.category).getItems().add(new CreateMenuItem(street));
            }
        }
    }

    public void show(Node anchor, double screenX, double screenY, Position pos) {
        super.show(anchor, screenX, screenY);
        this.pos = pos;
    }

    private void createStreet(StreetType streetType) {
        try {
            Street street = streetType.create();
            street.setPosition(pos);
            if (street instanceof StreetTwoPositions) {
                GuiController.getInstance().newEditableStreet((StreetTwoPositions) street);
            } else {
                StreetNetworkManager.getInstance().addStreet(street);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }


    public class CreateSubMenu extends Menu {
        public CreateSubMenu(MenuCategory category) {
            super(category.uiName);
        }
    }

    public class CreateMenuItem extends MenuItem {

        private StreetType streetType;

        public CreateMenuItem(StreetType streetType) {
            super(streetType.uiName);
            this.streetType = streetType;
            setOnAction(event -> {
                CreateMenu.this.createStreet(streetType);
            });

        }
    }
}
