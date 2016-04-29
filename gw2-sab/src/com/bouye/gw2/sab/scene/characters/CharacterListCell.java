/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import com.bouye.gw2.sab.wrappers.CharacterWrapper;
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

    private final CharacterListRenderer renderer = new CharacterListRenderer();

    /**
     * Creates a new empty instance.
     */
    public CharacterListCell() {
        renderer.characterProperty().bind(itemProperty());
        renderer.onSelectProperty().bind(onSelectProperty());
    }

    @Override
    protected void updateItem(final CharacterWrapper item, final boolean empty) {
        super.updateItem(item, empty);
        final String text = null;
        setText(text);
        final Node graphic = (empty || item == null) ? null : renderer;
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
