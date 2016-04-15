/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.commerce.exchange;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.commerce.exchange.ExchangeRate;
import com.bouye.gw2.sab.scene.SABControllerBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class GemExchangePaneController extends SABControllerBase<GemExchangePane> {

    private static final int TEST_COINS_QUANTITY = 10000;
    private static final int TEST_GEMS_QUANTITY = 10000;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }

    public ScheduledService<Void> updateService;

    private final Duration updatePeriod = Duration.minutes(1);

    private void start() {
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
        addAndStartService(updateService, "GemExchangePaneController::updateAsync");
    }

    private void stop() {
        if (updateService != null) {
            updateService.cancel();
        }
    }

    /**
     * FXML Controller class
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
}
