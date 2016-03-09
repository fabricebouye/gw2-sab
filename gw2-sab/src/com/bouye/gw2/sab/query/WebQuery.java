/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.demo.DemoSupport;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized class for web-queries.
 * @author Fabrice Bouyé
 */
public enum WebQuery {
    INSTANCE;

    /**
     * Return the language code to be used when doing queries that return localized values.
     * <br>If the user's current language is not supported, the default locale will be used instead.
     * @return A {@code String} instance, never {@code null}.
     */
    private String getLanguageCode() {
        final Set<String> supportedLanguages = SABConstants.INSTANCE.getSupportedWebApiLanguages();
        final String currentLanguage = Locale.getDefault().getLanguage();
        return supportedLanguages.contains(currentLanguage) ? currentLanguage : SABConstants.DEFAULT_WEBAPI_LANGUAGE;
    }

    /**
     * Convert given {@code int} ids into a {@code String} for the query.
     * @param ids the ids.
     * @return A {@code String}, never {@code null}.
     * <br>Contains {@code "all"} no id provided.
     */
    private String idsToString(final int... ids) {
        String result = "all"; // NOI18N.
        if (ids.length > 0) {
            final StringBuilder builder = new StringBuilder();
            for (final int id : ids) {
                builder.append(id);
                builder.append(','); // NOI18N.
            }
            // Remove last comma.
            builder.replace(builder.length() - 1, builder.length(), ""); // NOI18N.
            result = builder.toString();
        }
        return result;
    }

    /**
     * Convert given {@code String} ids into a {@code String} for the query.
     * @param ids the ids.
     * @return A {@code String}, never {@code null}.
     * <br>Contains {@code "all"} no id provided.
     */
    public String idsToString(final String... ids) {
        String result = "all"; // NOI18N.
        if (ids.length > 0) {
            final StringBuilder builder = new StringBuilder();
            for (final String id : ids) {
                builder.append(id);
                builder.append(','); // NOI18N.
            }
            // Remove last comma.
            builder.replace(builder.length() - 1, builder.length(), ""); // NOI18N.
            result = builder.toString();
        }
        return result;
    }

    /**
     * Do a simply web query that returns a simple object.
     * @param <T> The type to use.
     * @param targetClass The target class.
     * @param path The path to query.
     * @return An {@code Optional<T>} instance, never {@code null}.
     */
    private <T> Optional<T> objectWebQuery(final Class<T> targetClass, final String path) {
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "objectWebQuery " + path); // NOI18N.
        Optional<T> result = Optional.empty();
        try {
            final URL url = new URL(path);
            final T value = JsonpContext.SAX.loadObject(targetClass, url);
            result = Optional.ofNullable(value);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    /**
     * Do a simple web query that returns a list of object.
     * @param <T> The type to use.
     * @param targetClass The target class.
     * @param path The path to query.
     * @return A {@code List<T>} instance, never {@code null}.
     */
    private <T> List<T> arrayWebQuery(final Class<T> targetClass, final String path) {
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "arrayWebQuery " + path); // NOI18N.
        List<T> result = Collections.EMPTY_LIST;
        try {
            final URL url = new URL(path);
            final Collection<T> value = JsonpContext.SAX.loadObjectArray(targetClass, url);
            result = new ArrayList<>(value);
            result = Collections.unmodifiableList(result);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    public Optional<TokenInfo> queryTokenInfo(final boolean demo, final String appKey) {
        Optional<TokenInfo> result = Optional.empty();
        if (demo) {
            result = Optional.ofNullable(DemoSupport.INSTANCE.loadTokenInfo());
        } else {
            final String path = String.format("https://api.guildwars2.com/v2/tokeninfo?access_token=%s", appKey); // NOI18N.
            result = objectWebQuery(TokenInfo.class, path);
        }
        return result;
    }

    public Optional<Account> queryAccount(final boolean demo, final String appKey) {
        Optional<Account> result = Optional.empty();
        if (demo) {
            result = Optional.ofNullable(DemoSupport.INSTANCE.loadAccount());
        } else {
            final String path = String.format("https://api.guildwars2.com/v2/account?access_token=%s", appKey); // NOI18N.
            result = objectWebQuery(Account.class, path);
        }
        return result;
    }

    public List<World> queryWorldIds(final boolean demo, final int... ids) {
        List<World> result = Collections.EMPTY_LIST;
        if (demo) {
            result = new LinkedList();
            result.add(DemoSupport.INSTANCE.loadWorld());
            result = Collections.unmodifiableList(result);
        } else {
            final String path = String.format("https://api.guildwars2.com/v2/worlds?lang=%s&ids=%s", getLanguageCode(), idsToString(ids)); // NOI18N.
            result = arrayWebQuery(World.class, path);
        }
        return result;
    }
}
