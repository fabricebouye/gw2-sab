/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.wvw.matches.Match;
import java.util.List;
import java.util.Objects;

/**
 * Wraps a WvW match.
 * @author Fabrice Bouyé
 */
public final class MatchWrapper {

    private final Match match;
    private final List<World> worlds;

    public MatchWrapper(final Match match, final List<World> worlds) {
        Objects.requireNonNull(match);
        Objects.requireNonNull(worlds);
        this.match = match;
        this.worlds = worlds;
    }

    public Match getMatch() {
        return match;
    }

    public List<World> getWorlds() {
        return worlds;
    }
}
