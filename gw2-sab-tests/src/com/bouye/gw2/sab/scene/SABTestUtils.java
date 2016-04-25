/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene;

import api.web.gw2.mapping.core.JsonpSAXMarshaller;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.session.Session;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test support class.
 * @author Fabrice Bouyé
 */
public enum SABTestUtils {
    INSTANCE;

    private final Properties settings = new Properties();

    private SABTestUtils() {
        final File file = new File("settings.properties"); // NOI18N.
        if (file.exists() && file.canRead()) {
            try (final InputStream input = new FileInputStream(file)) {
                settings.load(input);
            } catch (IOException ex) {
                Logger.getLogger(SABTestUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Gets the test session object.
     * @return A {@code Session} instance, never {@code null}.
     */
    public Session getTestSession() {
        final String appKey = settings.getProperty("app.key"); // NOI18N.
        final Session result = new Session(appKey);
        Optional<TokenInfo> tokenInfo = Optional.empty();
        if (!settings.containsKey("token.info")) { // NOI18N.
            tokenInfo = WebQuery.INSTANCE.queryTokenInfo(appKey);
        } else {
            final String jsonTokenInfo = settings.getProperty("token.info"); // NOI18N.
            try (final InputStream input = new ByteArrayInputStream(jsonTokenInfo.getBytes(StandardCharsets.UTF_8))) {
                tokenInfo = Optional.of(new JsonpSAXMarshaller().loadObject(TokenInfo.class, input));
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        tokenInfo.ifPresent(ti -> result.setTokenInfo(ti));
        Optional<Account> account = Optional.empty();
        if (!settings.containsKey("account.info")) { // NOI18N.
            account = WebQuery.INSTANCE.queryAccount(appKey);
        } else {
            final String jsonAccount = settings.getProperty("account.info"); // NOI18N.
            try (final InputStream input = new ByteArrayInputStream(jsonAccount.getBytes(StandardCharsets.UTF_8))) {
                account = Optional.of(new JsonpSAXMarshaller().loadObject(Account.class, input));
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        account.ifPresent(a -> result.setAccount(a));
        return result;
    }
}
