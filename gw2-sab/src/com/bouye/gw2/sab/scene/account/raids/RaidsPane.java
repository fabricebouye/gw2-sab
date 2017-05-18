/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.raids;

import com.bouye.gw2.sab.scene.account.bank.*;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.net.URL;
import java.util.Optional;
import javafx.scene.layout.VBox;
import com.bouye.gw2.sab.wrappers.RaidsWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Display raid info.
 * @author Fabrice Bouyé
 */
public final class RaidsPane extends VBox {

    /**
     * This node's controller.
     */
    private final Optional<BankPaneController> controller;

    /**
     * Creates a new instance.
     */
    public RaidsPane() {
        super();
        setId("raidsPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/raids/RaidsPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/raids/RaidsPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<RaidsWrapper> raids = new SimpleObjectProperty<>(this, "raids", null);

    public final RaidsWrapper getRaids() {
        return raids.get();
    }

    public final void setRaids(final RaidsWrapper value) {
        raids.set(value);
    }

    public final ObjectProperty<RaidsWrapper> raidsProperty() {
        return raids;
    }
}
