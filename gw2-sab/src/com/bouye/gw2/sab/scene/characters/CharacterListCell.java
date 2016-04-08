/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import com.bouye.gw2.sab.SABListCellBase;

/**
 * List cell for the characters list.
 * @author Fabrice Bouyé
 */
public final class CharacterListCell extends SABListCellBase<String, CharacterListCellController> {

    /**
     * Creates a new empty instance.
     */
    public CharacterListCell() {
        super("fxml/scene/characters/CharacterListCell.fxml"); // NOI18N.
        getStyleClass().add("character-list-cell"); // NOI18N.
    }
}
