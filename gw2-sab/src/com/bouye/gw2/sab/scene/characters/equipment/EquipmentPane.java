/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.equipment;

import api.web.gw2.mapping.v2.characters.Character;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.FlowPane;

/**
 * Display the character's equipment, costume, miniature and glider (all at once unlike the game).
 * @author Fabrice Bouyé
 */
public final class EquipmentPane extends FlowPane {

    private final Optional<EquipmentPaneController> controller;

    /**
     * Creates a new instance.
     */
    public EquipmentPane() {
        super();
        setId("equipmentPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/characters/equipment/EquipmentPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/equipment/EquipmentPane.css"); // NOI18N.
        return url.toExternalForm();
    }

    private final ObjectProperty<Character> character = new SimpleObjectProperty<>(this, "character", null); // NOI18N.

    public final Character getCharacter() {
        return character.get();
    }

    public final void setCharacter(final Character value) {
        character.set(value);
    }

    public final ObjectProperty<Character> characterProperty() {
        return character;
    }
}
