/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.materials;

import api.web.gw2.mapping.v2.materials.MaterialStorage;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import api.web.gw2.mapping.v2.account.materials.AccountMaterial;

/**
 * Display the materials from the account vault.
 * @author Fabrice Bouyé
 */
public final class MaterialsPane extends VBox {

    /**
     * This node's controller.
     */
    private final Optional<MaterialsPaneController> controller;

    /**
     * Creates a new instance.
     */
    public MaterialsPane() {
        super();
        setId("materialsPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/materials/MaterialsPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/materials/MaterialsPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObservableList<MaterialStorage> materialStorage = FXCollections.observableArrayList();

    /**
     * Gets the material storage categories.
     * @return An {@code ObservableList<MaterialStorage>} instance, never {@code null}.
     */
    public final ObservableList<MaterialStorage> getMaterialStorage() {
        return materialStorage;
    }

    private final ObservableList<AccountMaterial> materials = FXCollections.observableArrayList();

    /**
     * Gets the account's materials.
     * @return An {@code ObservableList<AccountMaterial>} instance, never {@code null}.
     */
    public final ObservableList<AccountMaterial> getMaterials() {
        return materials;
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
