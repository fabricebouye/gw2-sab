/* 
 * Copyright (C) 2016 Fabrice Bouyé
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
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * List cell for the currency list.
 * @author Fabrice Bouyé
 */
public final class CurrencyListCell extends ListCell<CurrencyWrapper> {

    private final Optional<CurrencyListCellController> controller;
    private final Node node;

    /**
     * Creates a new instance.
     */
    public CurrencyListCell() throws NullPointerException {
        super();
        setId("currencyListCell"); // NOI18N.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/wallet/CurrencyListCell.fxml", this); // NOI18N.
        node = getGraphic();
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }    
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/wallet/CurrencyListCell.css"); // NOI18N.
        return url.toExternalForm();
    }

    @Override
    protected void updateItem(CurrencyWrapper item, boolean empty) {
        super.updateItem(item, empty);
        final String text = null;
        Node graphic = null;
        if (!empty && item != null) {
            graphic = node;
        }
        setText(text);
        setGraphic(graphic);
    }
}
