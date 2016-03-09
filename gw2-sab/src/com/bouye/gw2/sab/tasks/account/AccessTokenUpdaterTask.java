/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.tasks.account;

import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.data.account.AccessToken;
import com.bouye.gw2.sab.demo.DemoSupport;
import com.bouye.gw2.sab.query.WebQuery;
import java.util.Optional;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * This task queries the v2/account endpoint to update access token informations.
 * @author Fabrice Bouyé
 */
public final class AccessTokenUpdaterTask extends Task<Void> {

    /**
     * Access tokens to update.
     */
    private final AccessToken[] accessTokens;

    /**
     * Creates a new instance.
     * @param accessTokens Access tokens to update.
     */
    public AccessTokenUpdaterTask(final AccessToken... accessTokens) {
        this.accessTokens = accessTokens;
    }

    @Override
    protected Void call() throws Exception {
        for (final AccessToken accessToken : accessTokens) {
            final String appKey = accessToken.getAppKey();
            final boolean isDemo = SABConstants.IS_DEMO || DemoSupport.INSTANCE.isDemoApplicationKey(appKey);
            final Optional<TokenInfo> tokenInfo = WebQuery.INSTANCE.queryTokenInfo(isDemo, appKey);
            final Optional<Account> account = WebQuery.INSTANCE.queryAccount(isDemo, appKey);
            // Update results on JavaFX application thread.
            Platform.runLater(() -> {
                tokenInfo.ifPresent(t -> accessToken.setTokenInfo(t));
                account.ifPresent(a -> {
                    accessToken.setAccount(a);
                    accessToken.setAccountName(a.getName());
                });
            });
        }
        return null;
    }
}
