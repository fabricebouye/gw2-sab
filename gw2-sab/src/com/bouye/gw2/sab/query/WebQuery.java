/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

import api.web.gw2.mapping.core.APILevel;
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
import api.web.gw2.mapping.v2.quaggans.Quaggan;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.demo.DemoSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import static com.bouye.gw2.sab.query.GW2APIClient.*;
import api.web.gw2.mapping.v2.characters.id.equipment.CharacterEquipment;
import api.web.gw2.mapping.v2.characters.id.equipment.CharacterEquipmentResponse;
import api.web.gw2.mapping.v2.wvw.matches.WvwMatch;

/**
 * Centralized class for web-queries.
 * @author Fabrice Bouyé
 */
public enum WebQuery {
    INSTANCE;

    /**
     * Return the language code to be used when doing queries that return
     * localized values.
     * <br>If the user's current language is not supported, the default locale
     * will be used instead.
     * @return A {@code String} instance, never {@code null}.
     */
    private String getLanguageCode() {
        final Set<String> supportedLanguages = SABConstants.INSTANCE.getSupportedWebApiLanguages();
        final String currentLanguage = Locale.getDefault().getLanguage();
        return supportedLanguages.contains(currentLanguage) ? currentLanguage : SABConstants.DEFAULT_WEBAPI_LANGUAGE;
    }

    public Optional<TokenInfo> queryTokenInfo(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<TokenInfo> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
            result = Optional.ofNullable(DemoSupport.INSTANCE.loadTokenInfo());
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("tokeninfo") // NOI18N.
                    .applicationKey(appKey)
                    .queryObject(TokenInfo.class);
        }
        return result;
    }

    public Optional<Account> queryAccount(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<Account> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
            result = Optional.ofNullable(DemoSupport.INSTANCE.loadAccount());
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("account") // NOI18N.
                    .applicationKey(appKey)
                    .queryObject(Account.class);
        }
        return result;
    }

    public List<World> queryWorlds(final int... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<World> result = Collections.EMPTY_LIST;
        if (isOffline) {
            result = DemoSupport.INSTANCE.loadWorlds(ids);
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("worlds") // NOI18N.
                    .language(getLanguageCode())
                    .ids(ids)
                    .queryArray(World.class);
        }
        return result;
    }

    public List<File> queryFiles() {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<File> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("files") // NOI18N.
                    .ids(new int[0])
                    .queryArray(File.class);
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
            final GW2APIClient client = GW2APIClient.create()
                    .apiLevel(APILevel.V1)
                    .endPoint("guild_details.php"); // NOI18N.
            result = new ArrayList(ids.length);
            for (final String id : ids) {
                final Optional<GuildDetails> value = client
                        .putParameter("guild_id", id) // NOI18N.
                        .queryObject(GuildDetails.class);
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
            final String endPoint = String.format("guild/%s/members", id); // NOI18N.
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint(endPoint)
                    .applicationKey(appKey)
                    .queryArray(Member.class);
        }
        return result;
    }

    public List<LogEvent> queryGuildLogs(final String appKey, final String id) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<LogEvent> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
            result = DemoSupport.INSTANCE.loadGuildLogs(id);
        } else {
            final String endPoint = String.format("guild/%s/log", id); // NOI18N.
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint(endPoint)
                    .applicationKey(appKey)
                    .queryArray(LogEvent.class);
        }
        return result;
    }

    public List<Treasury> queryGuildTreasury(final String appKey, final String id) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Treasury> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            final String endPoint = String.format("guild/%s/treasury", id); // NOI18N.
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint(endPoint)
                    .applicationKey(appKey)
                    .queryArray(Treasury.class);
        }
        return result;
    }

    public List<Upgrade> queryGuildUpgrades(int... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Upgrade> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("guild/upgrades") // NOI18N.
                    .language(getLanguageCode())
                    .ids(ids)
                    .queryArray(Upgrade.class);
        }
        return result;
    }

    public List<Item> queryItems(int... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Item> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("guild/items") // NOI18N.
                    .language(getLanguageCode())
                    .ids(ids)
                    .queryArray(Item.class);
        }
        return result;
    }

    /**
     * Query a WvW using world id.
     * @param id The id of a world participating in the match.
     * @return An {@code Optional<Match>} instance, never {@code null}.
     */
    public Optional<WvwMatch> queryWvwMatch(final int id) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<WvwMatch> result = Optional.empty();
        if (isOffline) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("wvw/matches") // NOI18N.
                    .putParameter("world", id) // NOI18N.
                    .queryObject(WvwMatch.class);
        }
        return result;
    }

    /**
     * Query a WvW using match ids.
     * @param ids The id(s) of WvW matches.
     * @return A {@code List<Match>} instance, never {@code null}.
     */
    public List<WvwMatch> queryWvwMatches(final String... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<WvwMatch> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("wvw/matches") // NOI18N.
                    .ids(ids)
                    .queryArray(WvwMatch.class);
        }
        return result;
    }

    public List<String> queryCharacterNames(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<String> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("characters") // NOI18N.
                    .applicationKey(appKey)
                    .queryArray(String.class);
        }
        return result;
    }

    public Optional<Character> queryCharacter(final String appKey, final String characterName) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<Character> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            final String escapedCharacterName = encodeURLParameter(characterName);
            final String endPoint = String.format("characters/%s", escapedCharacterName); // NOI18N.
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint(endPoint)
                    .applicationKey(appKey)
                    .queryObject(Character.class);
        }
        return result;
    }

    public Optional<CharacterEquipmentResponse> queryCharacterEquipment(final String appKey, final String characterName) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<CharacterEquipmentResponse> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            final String escapedCharacterName = encodeURLParameter(characterName);
            final String endPoint = String.format("characters/%s/equipment", escapedCharacterName); // NOI18N.
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint(endPoint)
                    .applicationKey(appKey)
                    .queryObject(CharacterEquipmentResponse.class);
        }
        return result;
    }

    public List<Character> queryCharacters(final String appKey, final String... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Character> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            result = new ArrayList(ids.length);
            final GW2APIClient client = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .applicationKey(appKey);
            for (final String id : ids) {
                final String escapedCharacterName = encodeURLParameter(id);
                final String endPoint = String.format("characters/%s", escapedCharacterName); // NOI18N.
                final Optional<Character> value = client
                        .endPoint(endPoint)
                        .queryObject(Character.class);
                if (value.isPresent()) {
                    result.add(value.get());
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
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("currencies") // NOI18N.
                    .language(getLanguageCode())
                    .ids(ids)
                    .queryArray(Currency.class);
        }
        return result;
    }

    public List<CurrencyAmount> queryWallet(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<CurrencyAmount> result = Collections.EMPTY_LIST;
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("account/wallet") // NOI18N.
                    .applicationKey(appKey)
                    .queryArray(CurrencyAmount.class);
        }
        return result;
    }

    public List<Profession> queryProfessions(final String... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Profession> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("professions") // NOI18N.
                    .language(getLanguageCode())
                    .ids(ids)
                    .queryArray(Profession.class);
        }
        return result;
    }

    public Optional<Stat> queryPvPStats(final String appKey) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        Optional<Stat> result = Optional.empty();
        if (isOffline || DemoSupport.INSTANCE.isDemoApplicationKey(appKey)) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("pvp/stats") // NOI18N.
                    .applicationKey(appKey)
                    .queryObject(Stat.class);
        }
        return result;
    }

    public List<Quaggan> queryQuaggans(final String... ids) {
        final boolean isOffline = SABConstants.INSTANCE.isOffline();
        List<Quaggan> result = Collections.EMPTY_LIST;
        if (isOffline) {
        } else {
            result = GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("quaggans") // NOI18N.
                    .ids(ids)
                    .queryArray(Quaggan.class);
        }
        return result;
    }
}
