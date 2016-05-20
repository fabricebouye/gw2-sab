/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.pvp;

import api.web.gw2.mapping.v2.pvp.stats.Stat;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.VBox;

/**
 * Display the account and character inventory.
 * @author Fabrice Bouyé
 */
public final class PvPStatsPane extends VBox {

    /**
     * This node's controller.
     */
    private final Optional<PvPStatsPaneController> controller;

    /**
     * Creates a new instance.
     */
    public PvPStatsPane() {
        super();
        setId("pvpStatsPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/characters/pvp/PvPStatsPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/pvp/PvPStatsPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<Stat> stat = new SimpleObjectProperty<>(this, "stat", null);  // NOI18N.

    public void setStat(final Stat value) {
        stat.set(value);
    }

    public Stat getStat() {
        return stat.get();
    }

    public ObjectProperty<Stat> statProperty() {
        return stat;
    }
}
