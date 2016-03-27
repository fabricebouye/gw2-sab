/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import com.bouye.gw2.sab.SABControlBase;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.util.LinkedList;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
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

    /**
     * The currencies in the wallet of the current account.
     */
    private final ListProperty<CurrencyWrapper> currencies = new SimpleListProperty<>(this, "currencies", FXCollections.observableList(new LinkedList())); // NOI18N.

    public final ObservableList<CurrencyWrapper> getCurrencies() {
        return currencies;
    }

    public final void setCurrencies(final ObservableList<CurrencyWrapper> value) {
        currencies.setValue(value);
    }

    public final ListProperty<CurrencyWrapper> currenciesProperty() {
        return currencies;
    }
}
