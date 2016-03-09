/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.demo;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.worlds.World;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides support for demo mode.
 * @author Fabrice Bouyé
 */
public enum DemoSupport {
    /**
     * The unique instance of this class.
     */
    INSTANCE;

    /**
     * Pattern of the demo app-key.
     */
    private static final String APPLICATION_KEY_PATTERN = "X{8}-X{4}-X{4}-X{4}-X{20}-X{4}-X{4}-X{4}-X{12}"; // NOI18N.

    /**
     * Test if given app-key is a demo key.
     * @param value App-key to test, may be {@code null}.
     * @return {@code True} if test succeeds, {@code false} otherwise.
     */
    public boolean isDemoApplicationKey(final String value) {
        return (value == null) ? false : value.matches(APPLICATION_KEY_PATTERN);
    }

    /**
     * Load demo token info.
     * @return A {@code TokenInfo} instance, may be {@code null}.
     */
    public TokenInfo loadTokenInfo() {
        try {
            final URL url = getClass().getResource("v2/tokeninfo.json"); // NOI18N.
            return JsonpContext.SAX.loadObject(TokenInfo.class, url);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(DemoSupport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Load demo account.
     * @return An {@code Account} instance, may be {@code null}.
     */
    public Account loadAccount() {
        try {
            final URL url = getClass().getResource("v2/account.json"); // NOI18N.
            return JsonpContext.SAX.loadObject(Account.class, url);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(DemoSupport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Load demo world.
     * @return An {@code World} instance, may be {@code null}.
     */
    public World loadWorld() {
        try {
            final URL url = getClass().getResource("v2/world.json"); // NOI18N.
            return JsonpContext.SAX.loadObject(World.class, url);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(DemoSupport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
