/*
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.masteries;

import api.web.gw2.mapping.v2.masteries.Mastery;
import api.web.gw2.mapping.v2.masteries.MasteryRegion;
import javafx.scene.control.TreeCell;

/**
 * Mastery tree cell.
 * @author Fabrice Bouyé
 */
final class MasteryTreeCell extends TreeCell {

    @Override
    protected void updateItem(final Object item, final boolean empty) {
        super.updateItem(item, empty);
        String text = null;
        if (!empty) {
            if (item instanceof Mastery) {
                final Mastery mastery = (Mastery) item;
                text = mastery.getName();
            } else if (item instanceof MasteryRegion) {
                final MasteryRegion region = (MasteryRegion) item;
                text = region.name();
            }
        }
        setText(text);
    }
}
