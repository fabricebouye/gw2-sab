/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.tasks.account;

import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.query.WebQuery;
import java.util.Optional;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * This task queries the v2/account and v2/tokeninfo endpoints to update session informations.
 * @author Fabrice Bouyé
 */
public final class SessionUpdaterTask extends Task<Void> {

    /**
     * Sessions to update.
     */
    private final Session[] sessions;

    /**
     * Creates a new instance.
     * @param sessions Sessions to update.
     */
    public SessionUpdaterTask(final Session... sessions) {
        this.sessions = sessions;
    }

    @Override
    protected Void call() throws Exception {
        for (final Session session : sessions) {
            final String appKey = session.getAppKey();
            final Optional<TokenInfo> tokenInfo = WebQuery.INSTANCE.queryTokenInfo(appKey);
            final Optional<Account> account = WebQuery.INSTANCE.queryAccount(appKey);
            // Update results on JavaFX application thread.
            Platform.runLater(() -> {
                tokenInfo.ifPresent(t -> session.setTokenInfo(t));
                account.ifPresent(a -> {
                    session.setAccount(a);
                    session.setAccountName(a.getName());
                });
            });
        }
        return null;
    }
}
