/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.characters.Character;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Wraps a character.
 * @author Fabrice Bouyé
 */
public final class CharacterWrapper {

    private final String name;

    public CharacterWrapper(final String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
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
