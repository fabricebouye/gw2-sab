/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import com.bouye.gw2.sab.SABControlBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Displays the wallet.
 * @author Fabrice Bouyé
 */
public final class WalletPane extends SABControlBase<WalletPaneController> {

    /**
     * Creates a new instance.
     */
    public WalletPane() throws NullPointerException {
        super("fxml/scene/account/wallet/WalletPane.fxml"); // NOI18N.
        getStyleClass().add("wallet-pane"); // NOI18N.
    }

    private final ObservableList<CurrencyAmount> currencies = FXCollections.observableArrayList();

    public final ObservableList<CurrencyAmount> getCurrencies() {
        return currencies;
    }
}
