/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.commerce.exchange;

import api.web.gw2.mapping.core.CoinAmount;
import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.commerce.exchange.ExchangeRate;
import api.web.gw2.mapping.v2.commerce.exchange.ExchangeResource;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.text.IntegerOnlyFilter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class GemExchangePaneController extends SABControllerBase<GemExchangePane> {

    private static final int TEST_COINS_QUANTITY = 10000;
    private static final int TEST_GEMS_QUANTITY = 10000;

    @FXML
    private VBox coinsBox;
    @FXML
    private VBox gemsBox;

    @FXML
    private Spinner<Integer> gemsToConvertSpinner;
    @FXML
    private TextFlow gemsConvertedLabel;

    @FXML
    private Spinner<Integer> coinsToConvertSpinner;
    @FXML
    private TextFlow coinsConvertedLabel;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        coinsBox.getChildren().setAll(
                IntStream.of(250, 100, 50, 10, 1)
                .mapToObj(CoinAmount::ofGold)
                .map(coinAmount -> {
                    final Pane pane = new Pane();
                    pane.getStyleClass().add("exchange-content"); // NOI18N.
                    pane.setUserData(coinAmount);
                    return pane;
                })
                .collect(Collectors.toList()));
        gemsBox.getChildren().addAll(0, IntStream.of(2000, 1200, 800, 400)
                .mapToObj(gemQuantity -> {
                    final Pane pane = new Pane();
                    pane.getStyleClass().add("exchange-content"); // NOI18N.
                    pane.setUserData(gemQuantity);
                    return pane;
                })
                .collect(Collectors.toList()));
        gemsToConvertSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        gemsToConvertSpinner.getEditor().setTextFormatter(new TextFormatter<>(new IntegerOnlyFilter(-1, Integer.MAX_VALUE)));
        gemsToConvertSpinner.valueProperty().addListener(observable -> requestExchangeRate(ExchangeResource.GEMS));
        coinsToConvertSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        coinsToConvertSpinner.getEditor().setTextFormatter(new TextFormatter<>(new IntegerOnlyFilter(0, Integer.MAX_VALUE)));
        coinsToConvertSpinner.valueProperty().addListener(observable -> requestExchangeRate(ExchangeResource.COINS));
    }

    public ScheduledService<Void> updateService;

    private final Duration updatePeriod = Duration.minutes(1);

    private void startUpdaterService() {
        if (updateService == null) {
            updateService = new ScheduledService<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new ExchangeRateUpdateTask();
                }
            };
            updateService.setRestartOnFailure(true);
            updateService.setPeriod(updatePeriod);
        }
        addAndStartService(updateService, "GemExchangePaneController::startUpdaterService");
    }

    private void stopUpdaterService() {
        if (updateService != null) {
            updateService.cancel();
        }
    }

    /**
     * Exchange rate updater task.
     * @author Fabrice Bouyé
     */
    private final class ExchangeRateUpdateTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            final String gemQuery = String.format("https://api.guildwars2.com/v2/commerce/exchange/gems?quantity=%d", TEST_COINS_QUANTITY); // NOI18N.
            final String coinQuery = String.format("https://api.guildwars2.com/v2/commerce/exchange/coins?quantity=%d", TEST_GEMS_QUANTITY); // NOI18N.
            final ExchangeRate gemExchangeRate = JsonpContext.SAX.loadObject(ExchangeRate.class, new URL(gemQuery));
            final ExchangeRate coinExchangeRate = JsonpContext.SAX.loadObject(ExchangeRate.class, new URL(coinQuery));
            return null;
        }
    }

    private Service<ExchangeRate> coinsToGemsConversionService;
    private Service<ExchangeRate> gemsToCoinsConversionService;

    private void requestExchangeRate(final ExchangeResource type) {
    }
}
