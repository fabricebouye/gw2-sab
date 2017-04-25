/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.pvp.seasons;

import com.bouye.gw2.sab.scene.SABControlBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import api.web.gw2.mapping.v2.pvp.seasons.PvpSeason;

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

    private final ObjectProperty<PvpSeason> season = new SimpleObjectProperty<>(this, "season", null); // NOI18N.

    public final PvpSeason getSeason() {
        return season.get();
    }

    public final void setSeason(final PvpSeason value) {
        season.set(value);
    }

    public final ObjectProperty<PvpSeason> seasonProperty() {
        return season;
    }

    public final void selectDivision(final int index) throws IndexOutOfBoundsException {
        getController().ifPresent(c -> c.selectDivision(index));
    }
}
