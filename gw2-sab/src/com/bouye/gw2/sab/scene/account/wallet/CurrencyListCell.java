/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * List cell for the currency list.
 * @author Fabrice Bouyé
 */
public final class CurrencyListCell extends ListCell<CurrencyWrapper> {

    private final CurrencyListRenderer renderer = new CurrencyListRenderer();

    /**
     * Creates a new instance.
     */
    public CurrencyListCell() throws NullPointerException {
        super();
        setId("currencyListCell"); // NOI18N.
        renderer.currencyProperty().bind(itemProperty());
    }

    @Override
    protected void updateItem(final CurrencyWrapper item, final boolean empty) {
        super.updateItem(item, empty);
        final String text = null;
        setText(text);
        final Node graphic = (empty || item == null) ? null : renderer;
        setGraphic(graphic);
    }
}
