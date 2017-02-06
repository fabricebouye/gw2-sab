/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.commerce.exchange;

import com.bouye.gw2.sab.scene.SABControlBase;

/**
 * Displays the exchange rate of gems.
 * @author Fabrice Bouyé
 */
public final class GemExchangePane extends SABControlBase<GemExchangePaneController> {

    /**
     * Creates a new instance.
     */
    public GemExchangePane() {
        super("fxml/scene/commerce/exchange/GemExchangePane.fxml"); // NOI18N.
        setId("gemExchangePane"); // NOI18N.
        getStyleClass().add("gem-exchange-pane"); // NOI18N.
    }
}
