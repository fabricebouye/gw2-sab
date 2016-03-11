/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild;

import com.bouye.gw2.sab.SabControlBase;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * Displays guild info.
 * @author Fabrice Bouyé
 */
public final class GuildInfoPane extends SabControlBase<GuildInfoPaneController> {

    /**
     * Creates a new empty instance.
     */
    public GuildInfoPane() {
        super("fxml/scene/guild/GuildInfoPane.fxml"); // NOI18N.
        getStyleClass().add("guild-info-pane"); // NOI18N.
    }

    private final ReadOnlyStringWrapper guildId = new ReadOnlyStringWrapper(this, "guildId", null); // NOI18N.

    public final String getGuildId() {
        return guildId.get();
    }

    public final void setGuildId(final String value) {
        final String v = (value == null || value.trim().isEmpty()) ? null : value.trim();
        guildId.set(v);
    }

    public final ReadOnlyStringProperty guildIdProperty() {
        return guildId.getReadOnlyProperty();
    }
}
