/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABListCellBase;
import com.bouye.gw2.sab.wrappers.CharacterWrapper;
import java.net.URL;

/**
 * List cell for the characters list.
 * @author Fabrice Bouyé
 */
public final class CharacterListCell extends SABListCellBase<CharacterWrapper, CharacterListCellController> {

    /**
     * Creates a new empty instance.
     */
    public CharacterListCell() {
        super("fxml/scene/characters/CharacterListCell.fxml"); // NOI18N.
        getStyleClass().add("character-list-cell"); // NOI18N.
    }

    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/CharacterListCell.css"); // NOI18N.
        return url.toExternalForm();
    }
}
