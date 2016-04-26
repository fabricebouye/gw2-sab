/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.equipment;

import api.web.gw2.mapping.v2.characters.Character;
import com.bouye.gw2.sab.scene.SABControllerBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;

/**
 * FXML Controller class.
 * @author Fabrice Bouyé
 */
public final class EquipmentPaneController extends SABControllerBase<EquipmentPane> {

    /**
     * Creates a new instance.
     */
    public EquipmentPaneController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }

    @Override
    protected void uninstallNode(final EquipmentPane node) {
        node.characterProperty().removeListener(characterChangeListener);
    }

    @Override
    protected void installNode(final EquipmentPane node) {
        node.characterProperty().addListener(characterChangeListener);
    }

    @Override
    protected void updateUI() {
        updateEquipment();
        updateCostume();
        updateGlider();
        updateMiniature();
        updateStats();
    }

    private void updateEquipment() {
    }

    private void updateCostume() {
    }

    private void updateGlider() {
    }

    private void updateMiniature() {
    }

    private void updateStats() {
    }

    ////////////////////////////////////////////////////////////////////////////
    private final ChangeListener<Character> characterChangeListener = (observable, oldValue, newValue) -> updateStats();
}
