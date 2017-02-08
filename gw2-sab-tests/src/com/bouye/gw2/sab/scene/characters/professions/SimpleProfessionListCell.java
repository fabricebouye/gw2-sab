/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.professions;

import api.web.gw2.mapping.v2.professions.Profession;
import javafx.scene.control.ListCell;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class SimpleProfessionListCell extends ListCell<Profession> {

    /**
     * Creates a new instance.
     */
    public SimpleProfessionListCell() {
    }

    @Override
    protected void updateItem(final Profession item, final boolean empty) {
        super.updateItem(item, empty);
        final String text = (empty || item == null) ? null : item.getName();
        setText(text);
    }
}
