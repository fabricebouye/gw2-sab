/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.worlds;

import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.MatchWrapper;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.GridPane;

/**
 * Displays world info.
 * @author Fabrice Bouyé
 */
public final class WorldInfoPane extends GridPane {

    private final Optional<WorldInfoPaneController> controller;

    /**
     * Creates a new empty instance.
     */
    public WorldInfoPane() {
        super();
        setId("worldInfoPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/worlds/WorldInfoPane.fxml", this); // NOI18N.                
    }

    //    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/worlds/WorldInfoPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<World> world = new SimpleObjectProperty<>(this, "world", null); // NOI18N.   

    public final World getWorld() {
        return world.get();
    }

    public final void setWorld(final World value) {
        world.set(value);
    }

    public final ObjectProperty<World> worldProperty() {
        return world;
    }

    private final ObjectProperty<MatchWrapper> match = new SimpleObjectProperty<>(this, "match", null); // NOI18N.   

    public final MatchWrapper getMatch() {
        return match.get();
    }

    public final void setMatch(final MatchWrapper value) {
        match.set(value);
    }

    public final ObjectProperty<MatchWrapper> matchProperty() {
        return match;
    }
}
