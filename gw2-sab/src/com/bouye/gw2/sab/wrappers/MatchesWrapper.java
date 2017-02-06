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
import java.util.Map;
import java.util.Objects;

/**
 * Wraps several WvW matches.
 * @author Fabrice Bouyé
 */
public final class MatchesWrapper {

    private final Map<String, Match> matches;
    private final Map<Integer, World> worlds;

    public MatchesWrapper(Map<String, Match> matches, Map<Integer, World> worlds) {
        Objects.requireNonNull(matches);
        Objects.requireNonNull(worlds);
        this.matches = matches;
        this.worlds = worlds;
    }

    public Map<String, Match> getMatches() {
        return matches;
    }

    public Map<Integer, World> getWorlds() {
        return worlds;
    }
}
