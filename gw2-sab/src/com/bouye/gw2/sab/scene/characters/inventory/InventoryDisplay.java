/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.inventory;

/**
 * Display the account and character inventory.
 * @author Fabrice Bouyé
 */
public enum InventoryDisplay {
    /**
     * Each separate bags are shown.
     */
    BAGS_SHOWN,
    /**
     * Bags content is fused into a single display.
     */
    BAGS_HIDDEN,
    /**
     * Character bags content is fused into a single display. Shared account inventories are displayed separately.
     */
    BAGS_HIDDEN_SEPARATED;
}
