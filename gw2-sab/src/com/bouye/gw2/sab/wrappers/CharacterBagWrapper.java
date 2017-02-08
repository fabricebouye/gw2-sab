/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.characters.inventory.InventoryBag;
import api.web.gw2.mapping.v2.items.Item;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Wraps a character's inventory bag.
 * @author Fabrice Bouyé
 */
public final class CharacterBagWrapper {

    private final InventoryBag bag;
    private final Item item;
    private final List<CharacterInventoryWrapper> content;

    public CharacterBagWrapper(final InventoryBag bag, final Item item, CharacterInventoryWrapper... content) throws NullPointerException {
        Objects.requireNonNull(bag);
        this.bag = bag;
        this.item = item;
        this.content = Collections.unmodifiableList(Arrays.asList(content));
    }

    public InventoryBag getBag() {
        return bag;
    }

    public Item getItem() {
        return item;
    }

    public List<CharacterInventoryWrapper> getContent() {
        return content;
    }        
}
