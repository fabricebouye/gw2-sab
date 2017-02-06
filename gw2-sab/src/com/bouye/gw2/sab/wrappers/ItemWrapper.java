/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.skins.Skin;
import java.util.Objects;

/**
 * Wraps an item.
 * @author Fabrice Bouyé
 */
public final class ItemWrapper {

    private final Item item;
    private final Skin skin;

    /**
     * Creates a new instance.
     * @param item The item.
     * @param skin The skin, may be {@code null}.
     * @throws NullPointerException If {@code item} is {@code null}.
     */
    public ItemWrapper(final Item item, final Skin skin) throws NullPointerException {
        Objects.requireNonNull(item);
        this.item = item;
        this.skin = skin;
    }

    public Item getItem() {
        return item;
    }

    public Skin getStkin() {
        return skin;
    }
}
