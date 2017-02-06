/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild.treasury;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.TreasuryWrapper;
import java.net.URL;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

/**
 * Display a guild's treasury.
 * @author Fabrice Bouyé
 */
public final class TreasuryPane extends VBox {

    /**
     * This node's controller.
     */
    private final Optional<TreasuryPaneController> controller;

    /**
     * Creates a new instance.
     */
    public TreasuryPane() {
        super();
        setId("treasuryPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/guild/treasury/TreasuryPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/guild/treasury/TreasuryPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObservableList<TreasuryWrapper> treasury = FXCollections.observableArrayList();

    public final ObservableList<TreasuryWrapper> getTreasury() {
        return treasury;
    }
}
