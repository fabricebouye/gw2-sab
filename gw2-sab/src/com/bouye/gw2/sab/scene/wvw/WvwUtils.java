/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import api.web.gw2.mapping.v2.wvw.matches.MatchTeam;
import api.web.gw2.mapping.v2.wvw.objectives.ObjectiveType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
}
