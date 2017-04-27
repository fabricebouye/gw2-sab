/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene;

import api.web.gw2.mapping.v2.items.ItemRarity;
import api.web.gw2.mapping.v2.skins.SkinRarity;
import com.bouye.gw2.sab.text.LabelUtils;
import java.util.Objects;
import java.util.stream.Stream;
import javafx.css.PseudoClass;
import javafx.scene.Node;

/**
 * Utility class for UI styling.
 * @author Fabrice Bouyé
 */
public enum SABUIUtils {
    INSTANCE;

    /**
     * Apply given item rarity style on the given node.
     * @param node The node.
     * @param itemRarity The item rarity.
     * @throws NullPointerException If {@code node} is {@code null}.
     */
    public void updateRarityStyle(final Node node, final ItemRarity itemRarity) throws NullPointerException {
        Objects.requireNonNull(node);
        Stream.of(ItemRarity.values())
                .filter(rarity -> rarity != ItemRarity.UNKNOWN)
                .forEach(rarity -> {
                    final PseudoClass rarityPseudoClass = LabelUtils.INSTANCE.toPseudoClass(rarity);
                    node.pseudoClassStateChanged(rarityPseudoClass, rarity == itemRarity);
                });
    }

    /**
     * Apply given skin rarity style on the given node.
     * @param node The node.
     * @param skinRarity The skin rarity.
     * @throws NullPointerException If {@code node} is {@code null}.
     */
    public void updateRarityStyle(final Node node, final SkinRarity skinRarity) {
        Objects.requireNonNull(node);
        Stream.of(SkinRarity.values())
                .filter(rarity -> rarity != SkinRarity.UNKNOWN)
                .forEach(rarity -> {
                    final PseudoClass rarityPseudoClass = LabelUtils.INSTANCE.toPseudoClass(rarity);
                    node.pseudoClassStateChanged(rarityPseudoClass, rarity == skinRarity);
                });
    }
}
