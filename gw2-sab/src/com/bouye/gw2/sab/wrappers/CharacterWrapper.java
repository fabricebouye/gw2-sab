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
 * <br>Issues with character data as of 2016/04:
 * <ul>
 * <li>The endpoint does not provide a way to get minimalistic character information (ie: just name, sex, race, profession, level, nothing else). 
 * In list and tables we would only need those data.</li>
 * <li>There's a large amount of optional data that depends heavily on the permissions of the app-key used.</li>
 * <li>Full character data is quite heavy and may take some time to return and parse.</li>
 * </ul>
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
