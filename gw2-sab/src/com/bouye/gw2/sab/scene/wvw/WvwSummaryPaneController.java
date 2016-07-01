/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.wvw.matches.Match;
import api.web.gw2.mapping.v2.wvw.matches.MatchTeam;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.MatchWrapper;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;

/**
 * FXML Controller class.
 * <br>This controller might be used in standalone mode (when FXML is directly included into another control) or linked to a {@code WvwSummaryPane} control.
 * @author Fabrice Bouyé
 */
public final class WvwSummaryPaneController extends SABControllerBase<WvwSummaryPane> {

    @FXML
    private BarChart scoreBarChart;
    @FXML
    private CategoryAxis scoreCategoryAxis;
    @FXML
    private PieChart ebPieChart;
    @FXML
    private PieChart redPieChart;
    @FXML
    private PieChart bluePieChart;
    @FXML
    private PieChart greenPieChart;

    private final List<MatchTeam> teams = WvwUtils.INSTANCE.getTeams();
    private final List<MatchTeam> pieTeams = WvwUtils.INSTANCE.getPieTeams();

    /**
     * Creates a new instance.
     */
    public WvwSummaryPaneController() {
    }

    private PieChart[] pieCharts;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        // Prepare bar and pie charts.
        final List<String> teamNames = teams.stream()
                .map(team -> team.name())
                .collect(Collectors.toList());
        scoreCategoryAxis.getCategories().setAll(teamNames);
        teams.stream()
                .forEach(team -> {
                    final BarChart.Series series = new BarChart.Series();
                    // Initially the series' name is the team's name.
                    series.setName(team.name());
                    series.getData().add(new BarChart.Data(0, team.name()));
                    scoreBarChart.getData().add(series);
                });
        pieTeams.stream()
                .forEach(team -> {
                    ebPieChart.getData().add(new PieChart.Data(team.name(), 0));
                    redPieChart.getData().add(new PieChart.Data(team.name(), 0));
                    bluePieChart.getData().add(new PieChart.Data(team.name(), 0));
                    greenPieChart.getData().add(new PieChart.Data(team.name(), 0));
                });
        pieCharts = new PieChart[]{ebPieChart, bluePieChart, greenPieChart, redPieChart};
        //
        matchProperty().addListener(matchChangleListener);
    }

    // When in linked node.
    @Override
    protected void uninstallNode(final WvwSummaryPane node) {
        matchProperty().unbind();
    }

    @Override
    protected void installNode(final WvwSummaryPane node) {
        matchProperty().bind(node.matchProperty());
    }

    private final ChangeListener<MatchWrapper> matchChangleListener = (observable, oldValue, newValue) -> updateUI();

    @Override
    protected void updateUI() {
        final MatchWrapper wrapper = getMatch();
        final Match match = (wrapper == null) ? null : wrapper.getMatch();
        final List<World> worlds = (wrapper == null) ? null : wrapper.getWorlds();
        if (match == null) {
            // Clear bar chart.
            scoreBarChart.getData()
                    .stream()
                    .forEach(obj -> {
                        // FB - using map() to cast values gives inconstant error due to generic issues?
                        final BarChart.Series series = (BarChart.Series) obj;
                        final BarChart.Data data = (BarChart.Data) series.getData().get(0);
                        data.setXValue(0);
                    });
            // Clear pie charts.
            Arrays.stream(pieCharts)
                    .forEach(pieChart -> pieChart.getData()
                            .stream()
                            .map(obj -> (PieChart.Data) obj)
                            .forEach(data -> data.setPieValue(0)));
        } else {
            final Map<MatchTeam, String> worldNames = findWorldNamesForTeams(match, worlds);
            // Update score bar chart.
            IntStream.range(0, teams.size())
                    .forEach(teamIndex -> {
                        final MatchTeam team = teams.get(teamIndex);
                        final int teamScore = match.getScores().get(team);
                        final String worldName = worldNames.get(team);
                        final BarChart.Series series = (BarChart.Series) scoreBarChart.getData().get(teamIndex);
                        series.setName(worldName);
                        // Update data.
                        final BarChart.Data data = (BarChart.Data) series.getData().get(0);
                        data.setXValue(teamScore);
                    });
            // Update each map's pie chart.
            match.getMaps()
                    .stream()
                    .forEach(map -> {
                        // Find proper pie chart.
                        PieChart chart = ebPieChart;
                        switch (map.getType()) {
                            case BLUE_HOME:
                                chart = bluePieChart;
                                break;
                            case GREEN_HOME:
                                chart = greenPieChart;
                                break;
                            case RED_HOME:
                                chart = redPieChart;
                                break;
                            case CENTER:
                            default:
                        }
                        // Update the chart.
                        final PieChart pieChart = chart;
                        IntStream.range(0, pieTeams.size())
                                .forEach(teamIndex -> {
                                    final MatchTeam team = pieTeams.get(teamIndex);
                                    final int teamScore = map.getScores().get(team);
                                    final Optional<PieChart.Data> data = pieChart.getData()
                                            .stream()
                                            .map(obj -> (PieChart.Data) obj)
                                            .filter(d -> d.getName().equals(team.name()))
                                            .findFirst();
                                    data.ifPresent(d -> d.setPieValue(teamScore));
                                });
                    });
        }
    }

    /**
     * Return proper world name for given world id.
     * @param worldId The world id.
     * @param worldList The world list.
     * @return A {@code String} instance, never {@code null}.
     * <br>If the world's name cannot be resolved, the returned {@code String} contains the world's id instead.
     */
    private String findWorldNameForId(final int worldId, final List<World> worldList) {
        final Optional<World> world = worldList.stream()
                .filter(w -> w.getId() == worldId)
                .findFirst();
        return world.isPresent() ? world.get().getName() : String.valueOf(worldId);
    }

    /**
     * Find proper world names for each match team.
     * @param match The match descriptor.
     * @param worldList The world list.
     * @return A {@code Map<MatchTeam, String>}, never {@code null}.
     * <br> The map does not contain {@code null} values.
     */
    private Map<MatchTeam, String> findWorldNamesForTeams(final Match match, final List<World> worldList) {
        final Map<MatchTeam, Integer> worldIds = match.getWorlds();
        final Map<MatchTeam, Set<Integer>> allWorldIds = match.getAllWorlds();
        final boolean pairedMatch = !allWorldIds.isEmpty();
        final Map<MatchTeam, String> result = teams.stream()
                .collect(Collectors.toMap(Function.identity(), team -> {
                    String value = "";
                    // Support for non-paired matches.
                    if (!pairedMatch) {
                        final int id = worldIds.get(team);
                        value = findWorldNameForId(id, worldList);
                    } // Support for paired matches.
                    else {
                        final Set<Integer> ids = allWorldIds.get(team);
                        final String separator = (ids.size() == 2) ? " & " : ", "; // NOI18N.
                        value = ids.stream()
                                .map(id -> findWorldNameForId(id, worldList))
                                .collect(Collectors.joining(separator));
                    }
                    return value;
                }));
        return Collections.unmodifiableMap(result);
    }

    ////////////////////////////////////////////////////////////////////////////
    private final ObjectProperty<MatchWrapper> match = new SimpleObjectProperty<>(this, "match", null); // NOI18N.

    public final MatchWrapper getMatch() {
        return match.get();
    }

    public final void setMatch(final MatchWrapper value) {
        match.set(value);
    }

    public final ObjectProperty<MatchWrapper> matchProperty() {
        return match;
    }
}
