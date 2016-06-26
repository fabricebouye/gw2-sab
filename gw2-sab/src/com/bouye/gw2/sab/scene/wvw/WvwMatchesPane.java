/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.MatchesWrapper;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * Summary of all WvW matches for a given region.
 * @author Fabrice Bouyé
 */
public final class WvwMatchesPane extends VBox {

    private final Optional<WvwMatchesPaneController> controller;

    public WvwMatchesPane() {
        setId("wvwMatchesPane"); // NOI18N.     
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/wvw/WvwMatchesPane.fxml", this); // NOI18N.                
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    private final ObjectProperty<MatchesWrapper> matches = new SimpleObjectProperty<>(this, "matches", null); // NOI18N.

    public final MatchesWrapper getMatches() {
        return matches.get();
    }

    public final void setMatches(final MatchesWrapper value) {
        matches.set(value);
    }

    public final ObjectProperty<MatchesWrapper> matchesProperty() {
        return matches;
    }
}
