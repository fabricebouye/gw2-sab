/* 
 * Copyright (C) 2016 Fabrice Bouyé
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
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * List cell for the characters list.
 * @author Fabrice Bouyé
 */
public final class CharacterListCell extends ListCell<CharacterWrapper> {

    private final Optional<CharacterListCellController> controller;
    private final Node node;

    /**
     * Creates a new empty instance.
     */
    public CharacterListCell() {
        super();
        setId("characterListCell"); // NOI18N.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/characters/CharacterListCell.fxml", this); // NOI18N.
        node = getGraphic();
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }    
    
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/CharacterListCell.css"); // NOI18N.
        return url.toExternalForm();
    }

    @Override
    protected void updateItem(CharacterWrapper item, boolean empty) {
        super.updateItem(item, empty);
        final String text = null;
        Node graphic = null;
        if (!empty && item != null) {
            graphic = node;
        }
        setText(text);
        setGraphic(graphic);
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
