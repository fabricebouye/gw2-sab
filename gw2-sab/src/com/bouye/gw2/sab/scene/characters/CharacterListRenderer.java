/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.CharacterWrapper;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.GridPane;

/**
 * Renders characters in list views.
 * @author Fabrice Bouyé
 */
public final class CharacterListRenderer extends GridPane {

    private final Optional<CharacterListRendererController> controller;

    /**
     * Creates a new instance.
     */
    public CharacterListRenderer() {
        super();
        setId("characterListRenderer"); // NOI18N.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/characters/CharacterListRenderer.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }    
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/CharacterListRenderer.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<CharacterWrapper> character = new SimpleObjectProperty<>(this, "character", null);

    public final void setCharacter(final CharacterWrapper value) {
        character.set(value);
    }

    public final CharacterWrapper getCharacter() {
        return character.get();
    }

    public final ObjectProperty<CharacterWrapper> characterProperty() {
        return character;
    }

    private final ObjectProperty<Consumer<CharacterWrapper>> onSelect = new SimpleObjectProperty<>(this, "onSelect", null); // NOI18N.

    public final Consumer<CharacterWrapper> getOnSelect() {
        return onSelect.get();
    }

    public final void setOnSelect(final Consumer<CharacterWrapper> value) {
        onSelect.set(value);
    }

    public final ObjectProperty<Consumer<CharacterWrapper>> onSelectProperty() {
        return onSelect;
    }
}
