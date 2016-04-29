/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import api.web.gw2.mapping.v2.currencies.Currency;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfoPermission;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.CurrencyWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestWalletPane extends Application {

    @Override
    public void start(Stage primaryStage) throws NullPointerException, IOException {
        final WalletPane walletPane = new WalletPane();
        final StackPane root = new StackPane();
        root.setBackground(Background.EMPTY);
        root.getChildren().add(walletPane);
        final Scene scene = new Scene(root);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestWalletPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
//        ScenicView.show(root);
        loadTestAsync(walletPane);
    }

    /**
    * Loads the test in a background service.
    * @param walletPane The target pane.
    */
    private void loadTestAsync(final WalletPane walletPane) {
        final ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        final List<CurrencyWrapper> wallet = (SABConstants.INSTANCE.isOffline()) ? doLocalTest() : doRemoteTest();
                        if (!wallet.isEmpty()) {
                            Platform.runLater(() -> walletPane.getCurrencies().setAll(wallet));
                        }
                        return null;
                    }
                };
            }
        };
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getLogger(TestWalletPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.setPeriod(Duration.minutes(5));
        service.setRestartOnFailure(true);
        service.start();
    }

    /**
     * Do a remote test.
     * @return A {@code List<CurrencyWrapper>}, never {@code null}, might be empty.
     */
    private List<CurrencyWrapper> doRemoteTest() {
        List<Currency> currencies = Collections.EMPTY_LIST;
        Map<Integer, CurrencyAmount> wallet = Collections.EMPTY_MAP;
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        if (session.getTokenInfo().getPermissions().contains(TokenInfoPermission.WALLET)) {
            currencies = WebQuery.INSTANCE.queryCurrencies();
            wallet = WebQuery.INSTANCE.queryWallet(session.getAppKey())
                    .stream()
                    .collect(Collectors.toMap(CurrencyAmount::getId, Function.identity()));
        }
        return wrapCurrencies(currencies, wallet);
    }

    /**
     * Do a local test.
     * @return A {@code List<CurrencyWrapper>}, never {@code null}, might be empty.
     * @throws IOException In case of IO errors.
     */
    private List<CurrencyWrapper> doLocalTest() throws IOException {
        final Optional<URL> currenciesURL = Optional.ofNullable(getClass().getResource("currencies.json"));  // NOI18N.
        final List<Currency> currencies = (!currenciesURL.isPresent()) ? Collections.EMPTY_LIST : JsonpContext.SAX.loadObjectArray(Currency.class, currenciesURL.get())
                .stream()
                .collect(Collectors.toList());
        final Optional<URL> walletURL = Optional.ofNullable(getClass().getResource("wallet.json"));  // NOI18N.
        final Map<Integer, CurrencyAmount> wallet = (!walletURL.isPresent()) ? Collections.EMPTY_MAP : JsonpContext.SAX.loadObjectArray(CurrencyAmount.class, walletURL.get())
                .stream()
                .collect(Collectors.toMap(CurrencyAmount::getId, Function.identity()));
        return wrapCurrencies(currencies, wallet);
    }

    /**
     * Wrap into single object.
     * @param currencies The currencies.
     * @param wallet The wallet.
     * @return A {@code List<CurrencyWrapper>}, never {@code null}, might be empty.
     */
    private List<CurrencyWrapper> wrapCurrencies(final List<Currency> currencies, final Map<Integer, CurrencyAmount> wallet) {
        final List<CurrencyWrapper> result = currencies.stream()
                .map(currency -> {
                    final CurrencyAmount amount = wallet.get(currency.getId());
                    return new CurrencyWrapper(currency, amount);
                })
                .collect(Collectors.toList());
        result.sort((v1, v2) -> v1.getCurrency().getOrder() - v2.getCurrency().getOrder());
        return Collections.unmodifiableList(result);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
