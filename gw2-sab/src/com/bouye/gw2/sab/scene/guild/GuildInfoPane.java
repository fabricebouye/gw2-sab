/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild;

import com.bouye.gw2.sab.scene.SABControlBase;
import com.bouye.gw2.sab.wrappers.GuildInfoWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Displays guild info.
 * @author Fabrice Bouyé
 */
public final class GuildInfoPane extends SABControlBase<GuildInfoPaneController> {

    /**
     * Creates a new empty instance.
     */
    public GuildInfoPane() {
        super("fxml/scene/guild/GuildInfoPane.fxml"); // NOI18N.
        getStyleClass().add("guild-info-pane"); // NOI18N.
    }

    private final ObjectProperty<GuildInfoWrapper> guild = new SimpleObjectProperty<>(this, "guild", null);

    public final GuildInfoWrapper getGuild() {
        return guild.get();
    }

    public final void setGuild(GuildInfoWrapper value) {
        guild.set(value);
    }

    public ObjectProperty<GuildInfoWrapper> guildProperty() {
        return guild;
    }
}
