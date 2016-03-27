/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
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
        currencies.bind(Bindings.select(nodeProperty(), "currencies"));
        walletListView.setItems(filteredCurrencies);
        searchField.textProperty().addListener(observable -> {
            final String searchValue = searchField.getText();
            Predicate<CurrencyWrapper> predicate = null;
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                final String criteria = searchValue.trim().toLowerCase();
                predicate = currency -> currency.getCurrency().getName().toLowerCase().contains(criteria);
            }
            filteredCurrencies.setPredicate(predicate);
        });
    }

    private final ListProperty<CurrencyWrapper> currencies = new SimpleListProperty<>(FXCollections.observableList(new LinkedList()));
    private final FilteredList<CurrencyWrapper> filteredCurrencies = new FilteredList<>(currencies);
}
