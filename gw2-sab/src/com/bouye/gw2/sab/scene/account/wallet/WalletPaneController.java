/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.core.JsonpUtils;
import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import api.web.gw2.mapping.v2.currencies.Currency;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

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
        session = Bindings.select(nodeProperty(), "session"); // NOI18N.
        session.addListener(sessionInvalidationListener);
    }

    @Override
    public void dispose() {
        try {
            searchField.textProperty().removeListener(searchTextInvalidationListener);
            session.removeListener(sessionInvalidationListener);
            session.dispose();
            if (walletQueryService != null) {
                walletQueryService.cancel();
            }
        } finally {
            super.dispose();
        }
    }

    /**
     * Called whenever the search text is invalidated.
     */
    private final InvalidationListener searchTextInvalidationListener = observable -> applySearchFilter();

    /**
     * Called whenever the session is invalidated.
     */
    private final InvalidationListener sessionInvalidationListener = observable -> updateContent();

    private Binding<Session> session;

    private final ObservableList<CurrencyWrapper> currencies = FXCollections.observableList(new LinkedList());
    private final FilteredList<CurrencyWrapper> filteredCurrencies = new FilteredList<>(currencies);

    /**
     * Apply filter from the search box.
     */
    private void applySearchFilter() {
        final String searchValue = searchField.getText();
        Predicate<CurrencyWrapper> predicate = null;
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            final String criteria = searchValue.trim().toLowerCase();
            predicate = currency -> currency.getCurrency().getName().toLowerCase().contains(criteria);
        }
        filteredCurrencies.setPredicate(predicate);
    }

    /**
     * Service to query wallet for current account.
     */
    private ScheduledService<List<CurrencyWrapper>> walletQueryService;

    @Override
    protected void clearContent(final WalletPane parent) {
        if (walletQueryService != null) {
            walletQueryService.cancel();
        }
    }

    @Override
    protected void installContent(final WalletPane parent) {
        final Session session = this.session.getValue();
        // No need to start service.
        if (session == null) {
            return;
        }
        updateWalletAsync();
    }

    private void updateWalletAsync() {
        // Service lazy initialization.
        if (walletQueryService == null) {
            final ScheduledService<List<CurrencyWrapper>> service = new ScheduledService<List<CurrencyWrapper>>() {
                @Override
                protected Task<List<CurrencyWrapper>> createTask() {
                    return new WalletQueryTask();
                }
            };
            // @todo Get wait time from settings.
            service.setPeriod(Duration.minutes(5));
            service.setRestartOnFailure(true);
            service.setOnSucceeded(workerStateEvent -> {
                final List<CurrencyWrapper> result = (List<CurrencyWrapper>) workerStateEvent.getSource().getValue();
                final int selection = walletListView.getSelectionModel().getSelectedIndex();
                currencies.setAll(result);
                walletListView.getSelectionModel().select(selection);
            });
            walletQueryService = service;
        }
        addAndStartService(walletQueryService, "WalletPaneController::updateWalletAsync");
    }

    private class WalletQueryTask extends Task<List<CurrencyWrapper>> {

        public WalletQueryTask() {
        }

        @Override
        protected List<CurrencyWrapper> call() throws Exception {
            System.out.println("Wallet update!");
            List<CurrencyWrapper> result = Collections.EMPTY_LIST;
            final Session session = WalletPaneController.this.session.getValue();
            if (session != null) {
                // Get wallet amounts on this account.
                final List<CurrencyAmount> ammounts = JsonpContext.SAX.loadObjectArray(CurrencyAmount.class, new URL("https://api.guildwars2.com/v2/account/wallet?access_token=" + session.getAppKey()))
                        .stream()
                        .collect(Collectors.toList());
                // Extract currency ids.
                final int[] currencyIds = ammounts.stream()
                        .mapToInt(ammount -> ammount.getId())
                        .toArray();
                // Get all currencies in wallet.
                final Map<Integer, Currency> currencies = JsonpContext.SAX.loadObjectArray(Currency.class, new URL("https://api.guildwars2.com/v2/currencies?ids=" + JsonpUtils.INSTANCE.idsToParameter(currencyIds)))
                        .stream()
                        .collect(Collectors.toMap(currency -> currency.getId(), Function.identity()));
                // Wrap into single object.
                result = ammounts.stream()
                        .map(ammount -> {
                            final Currency currency = currencies.get(ammount.getId());
                            return new CurrencyWrapper(currency, ammount);
                        })
                        .collect(Collectors.toList());
                result.sort((v1, v2) -> v1.getCurrency().getOrder() - v2.getCurrency().getOrder());
            }
            return result;
        }

    }
}
