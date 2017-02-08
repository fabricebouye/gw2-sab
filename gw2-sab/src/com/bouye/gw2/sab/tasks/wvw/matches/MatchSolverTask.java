/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.tasks.wvw.matches;

import com.bouye.gw2.sab.wrappers.MatchWrapper;
import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.wvw.matches.Match;
import api.web.gw2.mapping.v2.wvw.matches.MatchTeam;
import com.bouye.gw2.sab.query.WebQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.concurrent.Task;

/**
 * This task queries the v2/wvw/matches and v2/worlds endpoints to resolve WvW matches.
 * @author Fabrice Bouyé
 */
public final class MatchSolverTask extends Task<List<MatchWrapper>> {

    private final boolean queryWorldId;
    /**
     * The world id.
     */
    private final int worldId;
    /**
     * The match ids.
     */
    private final String[] matchIds;

    /**
     * Create a new instance that will resolve a single WvW match based on given world id.
     * @param worldId The world id.
     */
    public MatchSolverTask(final int worldId) {
        queryWorldId = true;
        this.worldId = worldId;
        matchIds = new String[0];
    }

    /**
     * Create a new instance that will resolve multiple WvW matches based on given match ids.
     * @param matchIds The match ids.
     */
    public MatchSolverTask(final String... matchIds) {
        queryWorldId = false;
        worldId = -1;
        this.matchIds = matchIds;
    }

    @Override
    protected List<MatchWrapper> call() throws Exception {
        // Nothing to do.
        if (queryWorldId && worldId == -1) {
            return Collections.EMPTY_LIST;
        }
        // Get requested match(es).
        List<Match> matches = Collections.EMPTY_LIST;
        if (queryWorldId) {
            final Optional<Match> match = WebQuery.INSTANCE.queryWvwMatch(worldId);
            if (match.isPresent()) {
                matches = Arrays.asList(match.get());
            }
        } else {
            // Note : if matchesIds is empty we query all matches.
            matches = WebQuery.INSTANCE.queryWvwMatches(matchIds);
        }
        // Nothing to return.
        if (matches.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        // Find all unique world ids.
        IntStream allWorldIdsStream = IntStream.empty();
        for (final Match match : matches) {
            final IntStream matchWorlIds = worldIdsForMatch(match);
            allWorldIdsStream = IntStream.concat(allWorldIdsStream, matchWorlIds);
        }
        final int[] allWorldIds = allWorldIdsStream.distinct()
                .toArray();
        // Get world names for each id.
        final Map<Integer, World> worldIdsMap = WebQuery.INSTANCE.queryWorlds(allWorldIds)
                .stream()
                .collect(Collectors.toMap(world -> world.getId(), Function.identity()));
        // Assemble results.
        final List<MatchWrapper> result = matches.stream()
                .map(match -> {
                    final List<World> worldList = worldIdsForMatch(match)
                            .mapToObj(worldID -> worldIdsMap.get(worldID))
                            .collect(Collectors.toList());
                    return new MatchWrapper(match, worldList);
                })
                .collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    /**
     * Gets a list of all world ids in given match.
     * <br>Since WvW may support world pairing at least during the 2016 WvW beta test period, the method used to gather all world id for a given match may vary.
     * @param match The source match.
     * @return An {@code InStream}, never {@code null}.
     */
    private IntStream worldIdsForMatch(final Match match) {
        final Map<MatchTeam, Integer> worldIds = match.getWorlds();
        final Map<MatchTeam, Set<Integer>> allWorldIds = match.getAllWorlds();
        final boolean pairedMatch = !allWorldIds.isEmpty();
        IntStream result = IntStream.empty();
        // Support for non-paired match.
        if (!pairedMatch) {
            result = worldIds.entrySet()
                    .stream()
                    .mapToInt(entry -> entry.getValue());
        } // Support for paired match.
        else {
            for (final Set<Integer> teamWorlds : allWorldIds.values()) {
                result = IntStream.concat(result, teamWorlds.stream()
                        .mapToInt(value -> value));
            }
        }
        return result;

    }
}
