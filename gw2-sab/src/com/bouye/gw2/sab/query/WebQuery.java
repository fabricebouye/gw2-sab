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
import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import api.web.gw2.mapping.v2.characters.Character;
import api.web.gw2.mapping.v2.currencies.Currency;
import api.web.gw2.mapping.v2.files.File;
import api.web.gw2.mapping.v2.guild.id.log.LogEvent;
import api.web.gw2.mapping.v2.guild.id.members.Member;
import api.web.gw2.mapping.v2.guild.id.treasury.Treasury;
import api.web.gw2.mapping.v2.guild.upgrades.Upgrade;
import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.professions.Profession;
import api.web.gw2.mapping.v2.pvp.stats.Stat;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
            result = Arrays.stream(ids)
                    .mapToObj(value -> String.valueOf(value))
                    .collect(Collectors.joining(",")); // NOI18N.
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
            result = Arrays.stream(ids)
                    .collect(Collectors.joining(",")); // NOI18N.
        }
        return result;
    }

    /**
     * Do a simply web query that returns a simple object.
     * @param <T> The type to use.
     * @param targetClass The target class.
     * @param query The query.
     * @return An {@code Optional<T>} instance, never {@code null}.
     */
    private <T> Optional<T> objectWebQuery(final Class<T> targetClass, final String query) {
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "objectWebQuery " + query); // NOI18N.
        Optional<T> result = Optional.empty();
        try {
            final URL url = new URL(query);
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
     * @param query The query.
     * @return A {@code List<T>} instance, never {@code null}.
     */
    private <T> List<T> arrayWebQuery(final Class<T> targetClass, final String query) {
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "arrayWebQuery " + query); // NOI18N.
        List<T> result = Collections.EMPTY_LIST;
        try {
            final URL url = new URL(query);
            final Collection<T> value = JsonpContext.SAX.loadObjectArray(targetClass, url);
            result = new ArrayList<>(value);
            result = Collections.unmodifiableList(result);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    private <T> PageResult<T> pageWebQuery(final Class<T> targetClass, final String query) {
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "arrayWebQuery " + query); // NOI18N.
        PageResult<T> result = PageResult.EMPTY;
        try {
            final URL url = new URL(query);
            result = JsonpContext.SAX.loadPage(targetClass, url);;
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    public Optional<TokenInfo> queryTokenInfo(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<TokenInfo> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
            result = Optional.ofNullable(DemoSupport.INSTANCE.loadTokenInfo());
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/tokeninfo?access_token=%s", appKey); // NOI18N.
            result = objectWebQuery(TokenInfo.class, query);
        }
        return result;
    }

    public Optional<Account> queryAccount(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<Account> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
            result = Optional.ofNullable(DemoSupport.INSTANCE.loadAccount());
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/account?access_token=%s", appKey); // NOI18N.
            result = objectWebQuery(Account.class, query);
        }
        return result;
    }

    public List<World> queryWorlds(final int... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<World> result = Collections.EMPTY_LIST;
        if (isOffline) {
            result = DemoSupport.INSTANCE.loadWorlds(ids);
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/worlds?lang=%s&ids=%s", getLanguageCode(), idsToString(ids)); // NOI18N.
            result = arrayWebQuery(World.class, query);
        }
        return result;
    }

    public List<File> queryFiles() {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<File> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            final String query = "https://api.guildwars2.com/v2/files?ids=all"; // NOI18N.        
            result = arrayWebQuery(File.class, query);
        }
        return result;
    }

    public List<GuildDetails> queryGuildDetails(final String... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        // V1 endpoint: can only query one guild at a time.
        List<GuildDetails> result = Collections.EMPTY_LIST;
        if (isOffline) {
            result = DemoSupport.INSTANCE.loadGuilds(ids);
        } else {
            result = new ArrayList(ids.length);
            for (final String id : ids) {
                final String query = String.format("https://api.guildwars2.com/v1/guild_details.php?guild_id=%s", id); // NOI18N.
                final Optional<GuildDetails> value = objectWebQuery(GuildDetails.class, query);
                if (value.isPresent()) {
                    result.add(value.get());
                }
            }
            result = Collections.unmodifiableList(result);
        }
        return result;
    }

    public List<Member> queryGuildMembers(final String appKey, final String id) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Member> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
            result = DemoSupport.INSTANCE.loadGuildRoster(id);
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/guild/%s/members?access_token=%s", id, appKey); // NOI18N.
            result = arrayWebQuery(Member.class, query);
        }
        return result;
    }

    public List<LogEvent> queryGuildLogs(final String appKey, final String id) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<LogEvent> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
            result = DemoSupport.INSTANCE.loadGuildLogs(id);
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/guild/%s/log?access_token=%s", id, appKey); // NOI18N.
            result = arrayWebQuery(LogEvent.class, query);
        }
        return result;
    }

    public List<Treasury> queryGuildTreasury(final String appKey, final String id) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Treasury> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/guild/%s/treasury?access_token=%s", id, appKey); // NOI18N.
            result = arrayWebQuery(Treasury.class, query);
        }
        return result;
    }

    public List<Upgrade> queryGuildUpgrades(int... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Upgrade> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/guild/upgrades?lang=%s&ids=%s", getLanguageCode(), idsToString(ids)); // NOI18N.
            result = arrayWebQuery(Upgrade.class, query);
        }
        return result;
    }

    public List<Item> queryItems(int... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Item> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/items?lang=%s&ids=%s", getLanguageCode(), idsToString(ids)); // NOI18N.
            result = arrayWebQuery(Item.class, query);
        }
        return result;
    }

    /**
     * Query a WvW using world id.
     * @param id The id of a world participating in the match.
     * @return An {@code Optional<Match>} instance, never {@code null}.
     */
    public Optional<Match> queryWvwMatch(final int id) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<Match> result = Optional.empty();
        if (isOffline) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/wvw/matches?world=%d", id); // NOI18N.
            result = objectWebQuery(Match.class, query);
        }
        return result;
    }

    /**
     * Query a WvW using match ids.
     * @param ids The id(s) of WvW matches.
     * @return A {@code List<Match>} instance, never {@code null}.
     */
    public List<Match> queryWvwMatches(final String... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Match> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/wvw/matches?ids=%s", idsToString(ids)); // NOI18N.
            result = arrayWebQuery(Match.class, query);
        }
        return result;
    }

    public List<String> queryCharacterNames(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<String> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/characters?access_token=%s", appKey); // NOI18N.
            result = arrayWebQuery(String.class, query);
        }
        return result;
    }

    public Optional<Character> queryCharacter(final String appKey, final String characterName) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<Character> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            try {
                final String escapedCharacterName = encodeURLParameter(characterName);
                final String query = String.format("https://api.guildwars2.com/v2/characters/%s?access_token=%s", escapedCharacterName, appKey); // NOI18N.
                result = objectWebQuery(Character.class, query);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return result;
    }

    public List<Character> queryCharacters(final String appKey, final String... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Character> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            result = new ArrayList(ids.length);
            for (final String id : ids) {
                try {
                    final String escapedCharacterName = encodeURLParameter(id);
                    final String query = String.format("https://api.guildwars2.com/v2/characters/%s?access_token=%s", escapedCharacterName, appKey); // NOI18N.
                    final Optional<Character> value = objectWebQuery(Character.class, query);
                    if (value.isPresent()) {
                        result.add(value.get());
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            result = Collections.unmodifiableList(result);
        }
        return result;
    }

    public List<Currency> queryCurrencies(int... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Currency> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/currencies?lang=%s&ids=%s", getLanguageCode(), idsToString(ids)); // NOI18N.
            result = arrayWebQuery(Currency.class, query);
        }
        return result;
    }

    public List<CurrencyAmount> queryWallet(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<CurrencyAmount> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/account/wallet?access_token=%s", appKey); // NOI18N.
            result = arrayWebQuery(CurrencyAmount.class, query);
        }
        return result;
    }

    public List<Profession> queryProfessions(String... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Profession> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/professions?lang=%s&ids=%s", getLanguageCode(), idsToString(ids)); // NOI18N.
            result = arrayWebQuery(Profession.class, query);
        }
        return result;
    }

    public Optional<Stat> queryPvPStats(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<Stat> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            final String query = String.format("https://api.guildwars2.com/v2/pvp/stats/?access_token=%s", appKey); // NOI18N.
            result = objectWebQuery(Stat.class, query);
        }
        return result;
    }
}
