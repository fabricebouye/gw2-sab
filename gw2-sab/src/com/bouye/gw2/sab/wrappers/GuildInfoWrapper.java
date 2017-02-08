/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.guild.id.Guild;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Wraps a guild an related information.
 * @author Fabrice Bouyé
 */
public final class GuildInfoWrapper {

    private final boolean isGuildLeader;

    public GuildInfoWrapper(final boolean isGuildLeader) {
        this.isGuildLeader = isGuildLeader;
    }

    /**
     * Indicates if current account is a guild leader.
     * @return A {@code boolean}.
     */
    public boolean isGuildLeader() {
        return isGuildLeader;
    }

    private final ObjectProperty<Guild> guild = new SimpleObjectProperty<>(this, "guild"); // NOI18N.

    public Guild getGuild() {
        return guild.get();
    }

    public void setGuild(final Guild value) {
        guild.set(value);
    }

    public ObjectProperty<Guild> guildProperty() {
        return guild;
    }
}
