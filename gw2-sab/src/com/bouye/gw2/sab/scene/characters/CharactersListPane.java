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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

/**
 * List characters from given account.
 * @author Fabrice Bouyé
 */
public final class CharactersListPane extends VBox {

    private final Optional<CharactersListPaneController> controller;

    /**
     * Creates a new instance.
     */
    public CharactersListPane() {
        super();
        setId("characterListPane");
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/characters/CharactersListPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/CharacterListPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }
    
    private final ObservableList<CharacterWrapper> characters = FXCollections.observableArrayList();

    public final ObservableList<CharacterWrapper> getCharacters() {
        return characters;
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
