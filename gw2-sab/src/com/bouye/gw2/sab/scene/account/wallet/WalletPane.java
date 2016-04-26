/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.net.URL;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

/**
 * Displays the wallet.
 * @author Fabrice Bouyé
 */
public final class WalletPane extends VBox {

    private final Optional<WalletPaneController> controller;

    /**
     * Creates a new instance.
     */
    public WalletPane() throws NullPointerException {
        super();
        setId("walletPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/wallet/WalletPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/wallet/WalletPane.css"); // NOI18N.
        return url.toExternalForm();
    }

    private final ObservableList<CurrencyWrapper> currencies = FXCollections.observableArrayList();

    public final ObservableList<CurrencyWrapper> getCurrencies() {
        return currencies;
    }
}
