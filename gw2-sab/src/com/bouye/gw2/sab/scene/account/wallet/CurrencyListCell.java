/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import com.bouye.gw2.sab.SABListCellBase;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;

/**
 *
 * @author Fabrice
 */
public final class CurrencyListCell extends SABListCellBase<CurrencyWrapper, CurrencyListCellController>{
   /**
     * Creates a new instance.
     */
    public CurrencyListCell() throws NullPointerException {
        super("fxml/scene/account/wallet/CurrencyListCell.fxml"); // NOI18N.
        getStyleClass().add("currency-list-cell"); // NOI18N.
    }

    @Override
    protected void updateController(final CurrencyListCellController controller, final CurrencyWrapper item) {
        controller.setCurrency(item);
    }        
}
