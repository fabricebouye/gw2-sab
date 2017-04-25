/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.pvp;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.VBox;
import api.web.gw2.mapping.v2.pvp.stats.PvpStat;

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
     * Display options.
     * @author Fabrice Bouyé
     */
    public enum ResultType {
        WINS, TOTAL_GAMES;
    }

    /**
     * Creates a new instance.
     */
    public PvPStatsPane() {
        super();
        setId("pvpStatsPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/pvp/PvPStatsPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/pvp/PvPStatsPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<PvpStat> stat = new SimpleObjectProperty<>(this, "stat", null);  // NOI18N.

    public void setStat(final PvpStat value) {
        stat.set(value);
    }

    public PvpStat getStat() {
        return stat.get();
    }

    public ObjectProperty<PvpStat> statProperty() {
        return stat;
    }

    private final ReadOnlyObjectWrapper<ResultType> display = new ReadOnlyObjectWrapper<>(this, "display", ResultType.WINS);

    public final void setDisplay(final ResultType value) {
        display.set(value == null ? ResultType.WINS : value);
    }

    public final ResultType getDisplay() {
        return display.get();
    }

    public final ReadOnlyObjectProperty<ResultType> displayProperty() {
        return display.getReadOnlyProperty();
    }
}
