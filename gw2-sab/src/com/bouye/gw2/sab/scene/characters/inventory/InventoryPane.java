/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.inventory;

import api.web.gw2.mapping.v2.account.inventory.SharedInventory;
import api.web.gw2.mapping.v2.characters.inventory.InventoryBag;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

/**
 * Display the account and character inventory.
 * @author Fabrice Bouyé
 */
public final class InventoryPane extends VBox {

    /**
     * This node's controller.
     */
    private final Optional<InventoryPaneController> controller;

    /**
     * Creates a new instance.
     */
    public InventoryPane() {
        super();
        setId("inventoryPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/characters/inventory/InventoryPane.fxml", this); // NOI18N.
    }
    
//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }

    private final ObservableList<SharedInventory> sharedInventory = FXCollections.observableArrayList();

    /**
     * Gets the shared inventory.
     * @return An {@code ObservableList<SharedInventory>} instance, never {@code null}.
     */
    public final ObservableList<SharedInventory> getSharedInventory() {
        return sharedInventory;
    }

    private final ObservableList<InventoryBag> characterInventory = FXCollections.observableArrayList();

    /**
     * Gets the character inventory.
     * @return An {@code ObservableList<InventoryBag>} instance, never {@code null}.
     */
    public ObservableList<InventoryBag> getCharacterInventory() {
        return characterInventory;
    }

    /**
     * If {@code true} the inventory will display a colored border indicating the rarity of the object contained in the cell.
     */
    private final BooleanProperty showRarity = new SimpleBooleanProperty(this, "showRarity", false); // NOI18N.

    public final void setShowRarity(final boolean value) {
        showRarity.set(value);
    }

    public final boolean isShowRarity() {
        return showRarity.get();
    }

    public final BooleanProperty showRarityProperty() {
        return showRarity;
    }

    /**
     * Allows to configure the inventory display.
     */
    private final ReadOnlyObjectWrapper<InventoryDisplay> display = new ReadOnlyObjectWrapper<>(this, "display", InventoryDisplay.BAGS_SHOWN); // NOI18N.

    public final void setDisplay(final InventoryDisplay value) {
        display.set(value == null ? InventoryDisplay.BAGS_SHOWN : value);
    }

    public final InventoryDisplay getDisplay() {
        return display.get();
    }

    public final ReadOnlyObjectProperty<InventoryDisplay> displayProperty() {
        return display.getReadOnlyProperty();
    }
}
