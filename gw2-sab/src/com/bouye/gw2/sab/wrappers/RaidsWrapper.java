/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.raids.Raid;
import java.util.Objects;
import java.util.Set;

/**
 * Wraps raids info.
 * @author Fabrice Bouyé
 */
public final class RaidsWrapper {

    private final Set<Raid> raids;
    private final Set<String> encounterIds;

    /**
     * Creates a new instance.
     * @param raids Definitions of raids.
     * @param encounterIds Encounters validated by the account.
     * @throws NullPointerException If {@code raids} or {@code encouterIds} is {@code null}.
     */
    public RaidsWrapper(final Set<Raid> raids, final Set<String> encounterIds) throws NullPointerException {
        Objects.requireNonNull(raids);
        Objects.requireNonNull(encounterIds);
        this.raids = raids;
        this.encounterIds = encounterIds;
    }

    public Set<Raid> getRaids() {
        return raids;
    }

    public Set<String> getEncounterIds() {
        return encounterIds;
    }
}
