/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import api.web.gw2.mapping.v2.currencies.Currency;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.query.ImageCache;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import api.web.gw2.mapping.v2.account.wallet.AccountCurrencyAmount;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class CurrencyListRendererController extends SABControllerBase<CurrencyListRenderer> {

    @FXML
    private Label nameLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private ImageView icon;
    @FXML
    private Tooltip infoTip;

    /**
     * Creates a new instance.
     */
    public CurrencyListRendererController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }

    /**
     * Called whenever observed values are invalidated.
     */
    private final InvalidationListener valueInvalidationListener = observable -> updateUI();

    @Override
    protected void uninstallNode(final CurrencyListRenderer parent) {
        parent.currencyProperty().removeListener(valueInvalidationListener);
    }

    @Override
    protected void installNode(final CurrencyListRenderer parent) {
        parent.currencyProperty().addListener(valueInvalidationListener);
    }

    @Override
    protected void updateUI() {
        final Optional<CurrencyListRenderer> parent = parentNode();
        final CurrencyWrapper wrapper = parent.isPresent() ? parent.get().getCurrency() : null;
        if (wrapper == null) {
            nameLabel.setText(null);
            amountLabel.setText(null);
            icon.setImage(null);
            infoTip.setText(null);
        } else {
            final Currency currency = wrapper.getCurrency();
            final AccountCurrencyAmount currencyAmount = wrapper.getCurrencyAmount();
            nameLabel.setText(currency.getName());
            final int amount = (currencyAmount == null) ? 0 : currencyAmount.getValue();
            amountLabel.setText(String.valueOf(amount));
            currency.getIcon().ifPresent(url -> {
                final Image image = ImageCache.INSTANCE.getImage(url.toExternalForm());
                icon.setImage(image);
            });
            infoTip.setText(currency.getDescription());
        }
    }
}
