/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import api.web.gw2.mapping.v2.currencies.Currency;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class WalletPaneController extends SABControllerBase<WalletPane> {

    @FXML
    private ListView<CurrencyWrapper> walletListView;
    @FXML
    private TextField searchField;

    /**
     * Creates a new instance.
     */
    public WalletPaneController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        walletListView.setCellFactory(listView -> new CurrencyListCell());
        walletListView.setItems(filteredCurrencies);
        searchField.textProperty().addListener(searchTextInvalidationListener);
    }

    @Override
    public void dispose() {
        try {
            searchField.textProperty().removeListener(searchTextInvalidationListener);
        } finally {
            super.dispose();
        }
    }

    @Override
    protected void uninstallNode(final WalletPane parent) {
        currencies.set(null);
    }

    @Override
    protected void installNode(final WalletPane parent) {
        currencies.set(parent.getCurrencies());
    }

    @Override
    protected void updateUI() {
    }

    /**
     * Called whenever the search text is invalidated.
     */
    private final InvalidationListener searchTextInvalidationListener = observable -> applySearchFilter();

    private final ListProperty<CurrencyWrapper> currencies = new SimpleListProperty<>(this, "currencies"); // NOI18N.
    private final FilteredList<CurrencyWrapper> filteredCurrencies = new FilteredList<>(currencies);

    /**
     * Apply filter from the search box.
     */
    private void applySearchFilter() {
        final String searchValue = searchField.getText();
        Predicate<CurrencyWrapper> predicate = null;
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            final String criteria = searchValue.trim().toLowerCase();
            predicate = wrapper -> {
                final Currency currency = wrapper.getCurrency();
                final CurrencyAmount currencyAmount = wrapper.getCurrencyAmount();
                boolean result = false;
                result |= currency.getName().toLowerCase().contains(criteria);
                final int amount = (currencyAmount == null) ? 0 : currencyAmount.getValue();
                result |= String.valueOf(amount).startsWith(criteria);
                return result;
            };
        }
        filteredCurrencies.setPredicate(predicate);
    }
}
