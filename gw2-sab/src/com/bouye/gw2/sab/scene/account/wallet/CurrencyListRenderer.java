/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.HBox;

/**
 * List cell for the currency list.
 * @author Fabrice Bouyé
 */
public final class CurrencyListRenderer extends HBox {

    private final Optional<CurrencyListRendererController> controller;

    /**
     * Creates a new instance.
     */
    public CurrencyListRenderer() throws NullPointerException {
        super();
        setId("currencyListRenderer"); // NOI18N.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/wallet/CurrencyListRenderer.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }    
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/wallet/CurrencyListRenderer.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<CurrencyWrapper> currency = new SimpleObjectProperty<>(this, "currency", null); // NOI18N.

    public final CurrencyWrapper getCurrency() {
        return currency.get();
    }

    public final void setCurrency(final CurrencyWrapper value) {
        currency.set(value);
    }

    public final ObjectProperty<CurrencyWrapper> currencyProperty() {
        return currency;
    }
}
