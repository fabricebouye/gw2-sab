/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.wvw.matches.MatchTeam;
import api.web.gw2.mapping.v2.wvw.objectives.ObjectiveType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * WvW utility class.
 * @author Fabrice Bouyé
 */
public enum WvwUtils {
    /**
     * The unique instance of this class.
     */
    INSTANCE;

    /**
     * Team order in most reports.
     */
    private final List<MatchTeam> teams = Collections.unmodifiableList(Arrays.asList(MatchTeam.GREEN, MatchTeam.BLUE, MatchTeam.RED));
    /**
     * Order of teams in pie chart is different.
     */
    private final List<MatchTeam> pieTeams = Collections.unmodifiableList(Arrays.asList(MatchTeam.BLUE, MatchTeam.GREEN, MatchTeam.RED));

    /**
     * Gets the teams to be used in most reports.
     * @return A non-modifiable {@code List<MatchTeam>}, never {@code null}.
     * <br>The list is sorted in decreasing rank order.
     */
    public List<MatchTeam> getTeams() {
        return teams;
    }

    /**
     * Gets the teams to be used in pie chart reports.
     * @return A non-modifiable {@code List<MatchTeam>}, never {@code null}.
     * <br>The list is sorted in the order used by ArenaNet for pie charts in game..
     */
    public List<MatchTeam> getPieTeams() {
        return pieTeams;
    }

    /**
     * Objectives that give points.
     * <br>Not currently accessible by API?
     */
    private final List<ObjectiveType> objectiveTypes = Collections.unmodifiableList(Arrays.asList(
            ObjectiveType.CAMP,
            ObjectiveType.TOWER,
            ObjectiveType.KEEP,
            ObjectiveType.CASTLE
    ));
    /**
     * Points per objectives.
     * <br>Not currently accessible by API?
     */
    private final List<Integer> pointsPerObjective = Collections.unmodifiableList(Arrays.asList(
            5,
            10,
            25,
            35
    ));
    /**
     * Map: objectives that give points -> points.
     */
    private final Map<ObjectiveType, Integer> objectivePoints = Collections.unmodifiableMap(IntStream.range(0, objectiveTypes.size())
            .mapToObj(index -> index)
            .collect(Collectors.toMap(objectiveTypes::get, pointsPerObjective::get)));

    /**
     * Gets the list of objective types that give points to a server in WvW.
     * @return A non-modifiable {@code List<ObjectiveType>} instance, never {@code null}.
     * <br>The list is sorted from the objective type that gives the least point to the objective type that gives the maximum points.
     */
    public List<ObjectiveType> getObjectiveTypes() {
        return objectiveTypes;
    }

    /**
     * Gets the amount of points given by objective type in WvW.
     * @return A non-modifiable {@code Map<ObjectiveType, Integer>} instance, never {@code null}.
     */
    public Map<ObjectiveType, Integer> getObjectivePoints() {
        return objectivePoints;
    }

    private static final PseudoClass MAIN_SERVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("main"); // NOI18N.
    private static final PseudoClass SECONDARY_SERVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("secondary"); // NOI18N.
    private static final String TEAM_NAME_STYLE_CLASS = "team-name"; // NOI18N.
    private static final String WORLD_NAME_STYLE_CLASS = "world-name"; // NOI18N.

    /**
     * Get rich display nodes for given WvW team.
     * @param worlds Map of world ids -> world.
     * @param mainWorldId The current world id.
     * @param secondaryWorldIds The secondary world ids.
     * @return A non-modifiable {@code List<Node>}, never {@code null}.
     * @throws NullPointerException If {@code worlds} is {@code null}.
     */
    public List<Node> createTeamLabels(final Map<Integer, World> worlds, int mainWorldId, final int... secondaryWorldIds) throws NullPointerException {
        Objects.requireNonNull(worlds);
        final List<Node> result = new ArrayList<>();
        final Text mainWorldLabel = new Text(WvwUtils.this.createServerName(worlds, mainWorldId));
        mainWorldLabel.getStyleClass().addAll(TEAM_NAME_STYLE_CLASS, WORLD_NAME_STYLE_CLASS);
        mainWorldLabel.pseudoClassStateChanged(MAIN_SERVER_PSEUDO_CLASS, true);
        result.add(mainWorldLabel);
        if (secondaryWorldIds.length > 1) {
            final Text plusLabel = new Text(" + "); // NOI18N.
            plusLabel.getStyleClass().add(TEAM_NAME_STYLE_CLASS);
            result.add(plusLabel);
            Arrays.stream(secondaryWorldIds)
                    .filter(secondaryWorldId -> secondaryWorldId != mainWorldId)
                    .mapToObj(secondaryWorldId -> WvwUtils.this.createServerName(worlds, secondaryWorldId))
                    .forEach(secondaryWorldName -> {
                        final Text secondaryLabel = new Text(secondaryWorldName);
                        secondaryLabel.getStyleClass().addAll(TEAM_NAME_STYLE_CLASS, WORLD_NAME_STYLE_CLASS);
                        secondaryLabel.pseudoClassStateChanged(SECONDARY_SERVER_PSEUDO_CLASS, true);
                        result.add(secondaryLabel);
                        final Text commaLabel = new Text(", "); // NOI18N.
                        commaLabel.getStyleClass().add(TEAM_NAME_STYLE_CLASS);
                        result.add(commaLabel);
                    });
            result.remove(result.size() - 1);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Get display name for given WvW team.
     * @param worlds Map of world ids -> world.
     * @param mainWorldId The current world id.
     * @param secondaryWorldIds The secondary world ids.
     * @return A {@code String}, never {@code null}.
     * @throws NullPointerException If {@code worlds} is {@code null}.
     */
    public String createTeamName(final Map<Integer, World> worlds, int mainWorldId, final int... secondaryWorldIds) throws NullPointerException {
        Objects.requireNonNull(worlds);
        String result = WvwUtils.this.createServerName(worlds, mainWorldId);
        if (secondaryWorldIds.length > 1) {
            result += Arrays.stream(secondaryWorldIds)
                    .filter(secondaryWorldId -> secondaryWorldId != mainWorldId)
                    .mapToObj(worldId -> WvwUtils.this.createServerName(worlds, worldId))
                    .collect(Collectors.joining(", ", " + ", "")); // NOI18N.
        }
        return result;
    }

    /**
     * Get display name for given world id.
     * @param worlds Map of world ids -> world.
     * @param worldId The current world id.
     * @return A {@code String}, never {@code null}.
     * @throws NullPointerException If {@code worlds} is {@code null}.
     */
    public String createServerName(final Map<Integer, World> worlds, final int worldId) throws NullPointerException {
        Objects.requireNonNull(worlds);
        final World world = worlds.get(worldId);
        return createServerName(world, worldId);
    }

    /**
     * Get display name for given world.
     * @param world The world, may be {@code null}.
     * @param worldId The current world id.
     * @return A {@code String}, never {@code null}.
     * <br>If {@code world} is not {@code null}, the result contains the localized world name.
     * <br>If {@code world} is {@code null}, the result contains {@code String.valueOf(worldId)} instead.
     */
    public String createServerName(final World world, final int worldId) {
        return (world == null) ? String.valueOf(worldId) : world.getName();
    }

}
