/*
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.masteries;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.AccountMasteriesWrapper;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;

/**
 * Display the masteries from the account.
 * @author Fabrice Bouyé
 */
public final class MasteriesPane extends BorderPane {

    /**
     * This node's controller.
     */
    private final Optional<MasteriesPaneController> controller;

    /**
     * Creates a new instance.
     */
    public MasteriesPane() {
        super();
        setId("masteriesPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/masteries/MasteriesPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/masteries/MasteriesPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<AccountMasteriesWrapper> masteries = new SimpleObjectProperty<>(this, "masteries", null); // NOI18N.

    public final AccountMasteriesWrapper getMasteries() {
        return masteries.get();
    }

    public final void setMasteries(final AccountMasteriesWrapper value) {
        masteries.set(value);
    }

    public final ObjectProperty<AccountMasteriesWrapper> masteriesProperty() {
        return masteries;
    }
}
