/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.wvw.matches.Match;
import com.bouye.gw2.sab.scene.SABControlBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Standalone WvW summary pane control.
 * @author Fabrice Bouyé
 */
public final class WvwSummaryPane extends SABControlBase<WvwSummaryPaneController> {

    /**
     * Creates a new instance.
     */
    public WvwSummaryPane() {
        super("fxml/scene/wvw/WvwSummaryPane.fxml"); // NOI18N.
        setId("wvwSummaryPane");
        getStyleClass().add("wvw-summary-pane"); // NOI18N.
    }

    private final ObjectProperty<Match> match = new SimpleObjectProperty<>(this, "match", null); // NOI18N.

    public final Match getMatch() {
        return match.get();
    }

    public final void setMatch(final Match value) {
        match.set(value);
    }

    public final ObjectProperty<Match> matchProperty() {
        return match;
    }

    private final ObservableList<World> worlds = FXCollections.observableArrayList();

    public final ObservableList<World> getWorlds() {
        return worlds;
    }
}
