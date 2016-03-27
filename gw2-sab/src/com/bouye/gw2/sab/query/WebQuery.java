/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.core.PageResult;
import api.web.gw2.mapping.v1.guilddetails.GuildDetails;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.guild.id.log.LogEvent;
import api.web.gw2.mapping.v2.guild.id.members.Member;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.wvw.matches.Match;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.demo.DemoSupport;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    public String encodeURLParameter(final String value) throws UnsupportedEncodingException {
        String result = URLEncoder.encode(value, "utf-8"); // NOI18N.
        result = result.replaceAll("\\+", "%20"); // NOI18N.
        return result;
    }
    
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
    public String idsToString(final int... ids) {
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

    private <T> PageResult<T> pageWebQuery(final Class<T> targetClass, final String path) {
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "arrayWebQuery " + path); // NOI18N.
        PageResult<T> result = PageResult.EMPTY;
        try {
            final URL url = new URL(path);
            result = JsonpContext.SAX.loadPage(targetClass, url);;
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

    public List<World> queryWorlds(final boolean demo, final int... ids) {
        List<World> result = Collections.EMPTY_LIST;
        if (demo) {
            result = DemoSupport.INSTANCE.loadWorlds(ids);
        } else {
            final String path = String.format("https://api.guildwars2.com/v2/worlds?lang=%s&ids=%s", getLanguageCode(), idsToString(ids)); // NOI18N.
            result = arrayWebQuery(World.class, path);
        }
        return result;
    }

    public List<GuildDetails> queryGuildDetails(final boolean demo, final String... ids) {
        // V1 endpoint: can only query one guild at a time.
        List<GuildDetails> result = Collections.EMPTY_LIST;
        if (demo) {
            result = DemoSupport.INSTANCE.loadGuilds(ids);
        } else {
            result = new ArrayList(ids.length);
            for (final String id : ids) {
                final String path = String.format("https://api.guildwars2.com/v1/guild_details.php?guild_id=%s", id); // NOI18N.
                final Optional<GuildDetails> value = objectWebQuery(GuildDetails.class, path);
                if (value.isPresent()) {
                    result.add(value.get());
                }
            }
            result = Collections.unmodifiableList(result);
        }
        return result;
    }

    public List<Member> queryGuildMembers(final boolean demo, final String appKey, final String id) {
        List<Member> result = Collections.EMPTY_LIST;
        if (demo) {
            result = DemoSupport.INSTANCE.loadGuildRoster(id);
        } else {
            final String path = String.format("https://api.guildwars2.com/v2/guild/%s/members?access_token=%s", id, appKey); // NOI18N.
            result = arrayWebQuery(Member.class, path);
        }
        return result;
    }

    public List<LogEvent> queryGuildLogs(final boolean demo, final String appKey, final String id) {
        List<LogEvent> result = Collections.EMPTY_LIST;
        if (demo) {
            result = DemoSupport.INSTANCE.loadGuildLogs(id);
        } else {
            final String path = String.format("https://api.guildwars2.com/v2/guild/%s/log?access_token=%s", id, appKey); // NOI18N.
            result = arrayWebQuery(LogEvent.class, path);
        }
        return result;
    }
    
    public Optional<Match> queryWvwMatch(final boolean demo, final int id) {
        Optional<Match> result = Optional.empty();
        if (demo) {

        } else {
            final String path = String.format("https://api.guildwars2.com/v2/wvw/matches?world=%d", id); // NOI18N.
            result = objectWebQuery(Match.class, path);
        }
        return result;
    }
}
