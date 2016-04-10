/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import com.bouye.gw2.sab.SABControlBase;

/**
 * List characters from given account.
 * @author Fabrice Bouyé
 */
public final class CharactersListPane extends SABControlBase<CharactersListPaneController> {

    /**
     * Creates a new instance.
     */
    public CharactersListPane() {
        super("fxml/scene/characters/CharactersListPane.fxml"); // NOI18N.
    }
}
