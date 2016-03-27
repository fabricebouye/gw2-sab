/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.query.ImageCache;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class CurrencyListCellController extends SABControllerBase<CurrencyListCell> {

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
    public CurrencyListCellController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        currencyProperty().addListener((observable, oldValue, newValue) -> updateContent());
    }

    @Override
    protected void clearContent(final CurrencyListCell parent) {
        nameLabel.setText(null);
        amountLabel.setText(null);
        icon.setImage(null);
        infoTip.setText(null);
    }

    @Override
    protected void installContent(final CurrencyListCell parent) {
        final Optional<CurrencyWrapper> currency = Optional.ofNullable(getCurrency());
        currency.ifPresent(c -> {
            nameLabel.setText(c.getCurrency().getName());
            amountLabel.setText(String.valueOf(c.getCurrencyAmount().getValue()));
            c.getCurrency().getIcon().ifPresent(url -> {
                final Image image = ImageCache.INSTANCE.getImage(url.toExternalForm());
                icon.setImage(image);
            });
            infoTip.setText(c.getCurrency().getDescription());
        });
    }

    /**
     * The currency value on display.
     */
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
