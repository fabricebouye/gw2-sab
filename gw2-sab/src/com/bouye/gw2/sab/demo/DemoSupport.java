/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.demo;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v1.guilddetails.GuildDetails;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.guild.id.log.LogEvent;
import api.web.gw2.mapping.v2.guild.id.members.Member;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.worlds.World;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
     * @param id The id of the world to load.
     * @return An {@code World} instance, may be {@code null}.
     */
    public World loadWorld(final int id) {
        try {
            final URL url = getClass().getResource(String.format("v2/worlds/world_%d.json", id)); // NOI18N.
            return JsonpContext.SAX.loadObject(World.class, url);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(DemoSupport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Load demo world.
     * @param ids The ids of the worlds to load.
     * @return An {@code List<World>} instance, never {@code null}.
     */
    public List<World> loadWorlds(final int... ids) {
        final List<World> result = Arrays.stream(ids)
                .mapToObj(id -> loadWorld(id))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    /**
     * Load demo guild.
     * @param id The id of the guild to load.
     * @return An {@code GuildDetails} instance, may be {@code null}.
     */
    public GuildDetails loadGuild(final String id) {
        try {
            final URL url = getClass().getResource(String.format("v1/guilddetails/guilddetails_%s.json", id)); // NOI18N.
            return JsonpContext.SAX.loadObject(GuildDetails.class, url);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(DemoSupport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Load demo guild.
     * @param ids The ids of the guilds to load.
     * @return An {@code List<GuildDetails>} instance, never {@code null}.
     */
    public List<GuildDetails> loadGuilds(final String... ids) {
        final List<GuildDetails> result = Arrays.stream(ids)
                .map(id -> loadGuild(id))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    public List<Member> loadGuildRoster(final String id) {
        try {
            final URL url = getClass().getResource(String.format("v2/guild/id/members/guildroster_%s.json", id)); // NOI18N.
            final Collection<Member> result = JsonpContext.SAX.loadObjectArray(Member.class, url);
            return Collections.unmodifiableList(new ArrayList(result));
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(DemoSupport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public List<LogEvent> loadGuildLogs(final String id) {
        try {
            final URL url = getClass().getResource(String.format("v2/guild/id/log/guildlog_%s.json", id)); // NOI18N.
            final Collection<LogEvent> result = JsonpContext.SAX.loadObjectArray(LogEvent.class, url);
            return Collections.unmodifiableList(new ArrayList(result));
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(DemoSupport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
