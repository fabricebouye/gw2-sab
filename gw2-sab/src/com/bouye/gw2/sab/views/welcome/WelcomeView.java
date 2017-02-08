/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.views.welcome;

import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.util.Optional;
import javafx.scene.layout.BorderPane;

/**
 * The welcome view.
 * @author Fabrice Bouyé
 */
public final class WelcomeView extends BorderPane {

    /**
     * This node's controller.
     */
    private final Optional<WelcomeViewController> controller;

    /**
     * Creates a new instance.
     */
    public WelcomeView() {
        super();
        setId("welcomeView"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/views/welcome/WelcomeView.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
}
