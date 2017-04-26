/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.items.Item;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import api.web.gw2.mapping.v2.guild.id.treasury.GuildTreasury;
import api.web.gw2.mapping.v2.guild.upgrades.GuildUpgrade;

/**
 * Wraps a guild treasury.
 * @author Fabrice Bouyé
 */
public final class TreasuryWrapper {

    /**
     * The treasury descriptor.
     */
    private final GuildTreasury treasury;
    /**
     * The item descriptor, may be {@code null}.
     */
    private final Item item;
    /**
     * The upgrades descriptors.
     */
    private final List<GuildUpgrade> upgrades;

    /**
     * Creates a new instance.
     * @param treasury The treasury descriptor.
     * @param item The item descriptor, may be {@code null}.
     * @param upgrades The upgrades descriptors, may be empty or contain {@code null} values.
     * @throws NullPointerException If {@code treasury} is {@code null}.
     */
    public TreasuryWrapper(final GuildTreasury treasury, final Item item, final GuildUpgrade... upgrades) throws NullPointerException {
        Objects.requireNonNull(treasury);
        this.treasury = treasury;
        this.item = item;
        this.upgrades = Collections.unmodifiableList(Arrays.asList(upgrades));
    }

    public GuildTreasury getTreasury() {
        return treasury;
    }

    public Item getItem() {
        return item;
    }

    public List<GuildUpgrade> getUpgrades() {
        return upgrades;
    }
}
