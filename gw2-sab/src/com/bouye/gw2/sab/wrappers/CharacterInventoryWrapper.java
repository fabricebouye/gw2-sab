/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.characters.inventory.Inventory;
import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.skins.Skin;
import java.util.Objects;

/**
 * Wraps a character's inventory object.
 * @author Fabrice Bouyé
 */
public final class CharacterInventoryWrapper {

    /**
     * The character inventory descriptor.
     */
    private final Inventory inventory;
    /**
     * The item descriptor.
     */
    private final Item item;
    /**
     * The skin descriptor.
     */
    private final Skin skin;

    /**
     * Creates a new instance.
     * @param inventory The character inventory descriptor.
     * @param item The item descriptor, may be {@code null}.
     * @param skin The skin descriptor, may be {@code null}.
     * @throws NullPointerException If {@code inventory} is {@code null}.
     */
    public CharacterInventoryWrapper(final Inventory inventory, final Item item, final Skin skin) throws NullPointerException {
        Objects.requireNonNull(inventory);
        this.inventory = inventory;
        this.item = item;
        this.skin = skin;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Item getItem() {
        return item;
    }

    public Skin getSkin() {
        return skin;
    }
}
