/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.pvp.seasons;

import api.web.gw2.mapping.v2.pvp.seasons.Season;
import com.bouye.gw2.sab.SABControlBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Displays a PvP season.
 * @author Fabrice Bouyé
 */
public final class SeasonPane extends SABControlBase<SeasonPaneController> {

    /**
     * Creates a new instance.
     */
    public SeasonPane() {
        super("SeasonPane.fxml");  // NOI18N.
    }

    private final ObjectProperty<Season> season = new SimpleObjectProperty<>(this, "season", null); // NOI18N.

    public final Season getSeason() {
        return season.get();
    }

    public final void setSeason(final Season value) {
        season.set(value);
    }

    public final ObjectProperty<Season> seasonProperty() {
        return season;
    }

    public final void selectDivision(final int index) throws IndexOutOfBoundsException {
        getController().ifPresent(c -> c.selectDivision(index));
    }
}
