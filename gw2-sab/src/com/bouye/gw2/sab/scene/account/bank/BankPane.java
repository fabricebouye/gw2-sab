/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.bank;

import api.web.gw2.mapping.v2.account.bank.BankSlot;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

/**
 * Display the bank from the account vault.
 * @author Fabrice Bouyé
 */
public final class BankPane extends VBox {

    /**
     * This node's controller.
     */
    private final Optional<BankPaneController> controller;

    /**
     * Creates a new instance.
     */
    public BankPane() {
        super();
        setId("bankPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/bank/BankPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    private final ObservableList<BankSlot> slots = FXCollections.observableArrayList();

    /**
     * Gets the shared inventory.
     * @return An {@code ObservableList<BankSlot>} instance, never {@code null}.
     */
    public final ObservableList<BankSlot> getSlots() {
        return slots;
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
}
