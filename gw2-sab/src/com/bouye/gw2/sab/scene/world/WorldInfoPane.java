/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.world;

import com.bouye.gw2.sab.scene.SABControlBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

/**
 * Displays world info.
 * @author Fabrice Bouyé
 */
public final class WorldInfoPane extends SABControlBase<WorldInfoPaneController> {

    /**
     * Creates a new empty instance.
     */
    public WorldInfoPane() {
        super("fxml/scene/world/WorldInfoPane.fxml"); // NOI18N.
        getStyleClass().add("world-info-pane"); // NOI18N.
    }
    
    private final ReadOnlyIntegerWrapper worldId = new ReadOnlyIntegerWrapper(this, "worldId", -1); // NOI18N.

    public final int getWorldId() {
        return worldId.get();
    }
    
    public final void setWorldId(final int value) {
        final int v = (value < -1) ? -1 : value;
        worldId.set(v);
    }
    
    public final ReadOnlyIntegerProperty worldIdProperty() {
        return worldId.getReadOnlyProperty();
    }
}
