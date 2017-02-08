/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import api.web.gw2.mapping.v2.items.Item;
import com.bouye.gw2.sab.query.ImageCache;
import com.bouye.gw2.sab.wrappers.TreasuryWrapper;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Fabrice Bouyé
 */
public final class Slot extends StackPane {

    private enum Flavor {
        ITEM,
        SKIN,
        CHARACTER_INVENTORY,
        ACCOUNT_INVENTORY,
        TREASURY;
    }
    private final Object value;

    /**
     * Creates a new instance.
     */
    private Slot(final Item item) {
        super();
        value = item;
        init(Flavor.ITEM, item);
    }

    /**
     * Creates a new instance.
     */
    private Slot(final Skin skin) {
        super();
        value = skin;
        init(Flavor.SKIN, skin);
    }

    /**
     * Creates a new instance.
     */
    private Slot(final TreasuryWrapper treasuryWrapper) {
        super();
        value = treasuryWrapper;
        init(Flavor.SKIN, treasuryWrapper);
    }

    private void init(final Flavor flavor, final Object value) {
        setUserData(value);
        switch (flavor) {
            case ITEM:
                Optional.of((Item) value)
                        .ifPresent(this::initItem);
                break;
            case SKIN:
                Optional.of((Skin) value)
                        .ifPresent(this::initSkin);
                break;
            case CHARACTER_INVENTORY:
                break;
            case ACCOUNT_INVENTORY:
                break;
            case TREASURY:
                Optional.of((TreasuryWrapper) value)
                        .ifPresent(this::initTreasury);
                break;
        }
    }

    private void initItem(final Item item) {
        item.getIcon().ifPresent(url -> {
            final Image image = ImageCache.INSTANCE.getImage(url.toExternalForm());
        });
    }

    private void initSkin(final Skin skin) {
    }

    private void initTreasury(final TreasuryWrapper treasuryWrapper) {
        Optional.of(treasuryWrapper.getItem())
                .ifPresent(this::initItem);
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    public static Slot of(final Item item) {
        return new Slot(item);
    }

    public static Slot of(final Skin skin) {
        return new Slot(skin);
    }

    public static Slot of(final TreasuryWrapper treasuryWrapper) {
        return new Slot(treasuryWrapper);
    }

    /**
     * If {@code true} the slot will display a colored border indicating the rarity of the object contained in the cell.
     */
    private final BooleanProperty showRarity = new SimpleBooleanProperty(this, "showRarity", false); // NOI18N.

    public final void setShowRarity(final boolean value) {
        showRarity.set(value);
    }

    public final boolean isShowRarity() {
        return showRarity.get();
    }

    public final BooleanProperty showRarityProperty() {
        return showRarity;
    }
}
