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
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.query.WebQuery;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;

/**
 * FXML Controller class
 * @author fabriceb
 */
public final class WvwSummaryPaneController extends SABControllerBase {

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

    // Valid teams for match results.
    private final List<MatchTeam> teams = Arrays.stream(MatchTeam.values())
            .filter(team -> (team != MatchTeam.NEUTRAL) && (team != MatchTeam.UNKNOWN))
            .collect(Collectors.toList());

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
                    ebPieChart.getData().add(new PieChart.Data(team.name(), 0));
                    redPieChart.getData().add(new PieChart.Data(team.name(), 0));
                    bluePieChart.getData().add(new PieChart.Data(team.name(), 0));
                    greenPieChart.getData().add(new PieChart.Data(team.name(), 0));
                });
        //
        matchIdProperty().addListener(observable -> updateFromMatchId(getMatchId()));
        worldIdProperty().addListener(observable -> updateFromWorldId(getWorldId()));
    }

    private void updateFromMatchId(final int matchId) {
    }

    private void updateFromWorldId(final int worldId) {
        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        final boolean isDemo = SABConstants.INSTANCE.isDemo();
                        // Get match result.
                        final Optional<Match> matchResult = WebQuery.INSTANCE.queryWvwMatch(isDemo, worldId);
                        if (matchResult.isPresent()) {
                            final Match match = matchResult.get();
                            final int[] worldIds = match.getWorlds()
                                    .entrySet()
                                    .stream()
                                    .mapToInt(entry -> entry.getValue())
                                    .toArray();
                            WvwSummaryPaneController.this.match = Optional.of(match);
                            // Get world names.
                            final List<World> worlds = WebQuery.INSTANCE.queryWorlds(isDemo, worldIds);
                            WvwSummaryPaneController.this.worlds = Optional.of(worlds);
                        }
                        return null;
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            updateMatchContent();
        });
        addAndStartService(service, "updateMatchFromWorld");
    }

    private Optional<Match> match = Optional.empty();
    private Optional<List<World>> worlds = Optional.empty();

    private void updateMatchContent() {
        if (match.isPresent() && worlds.isPresent()) {
            installMatchContent(match.get(), worlds.get());
        }
    }

    private void installMatchContent(final Match match, final List<World> worldList) {
        final Map<MatchTeam, Integer> worldIds = match.getWorlds();
        final Map<MatchTeam, String> worldNames = teams.stream()
                .collect(Collectors.toMap(Function.identity(), team -> {
                    final int worldId = worldIds.get(team);
                    final Optional<World> world = worldList.stream()
                            .filter(w -> w.getId() == worldId)
                            .findFirst();
                    return world.isPresent() ? world.get().getName() : null;
                }));
        // Update score bar chart.
        IntStream.range(0, teams.size())
                .forEach(teamIndex -> {
                    final MatchTeam team = teams.get(teamIndex);
                    final int teamScore = match.getScores().get(team);
                    final Optional<String> worldName = Optional.ofNullable(worldNames.get(team));
                    final BarChart.Series series = (BarChart.Series) scoreBarChart.getData().get(teamIndex);
                    // Replace series name with world name if available.
                    worldName.ifPresent(wn -> series.setName(wn));
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
                    IntStream.range(0, teams.size())
                            .forEach(teamIndex -> {
                                final MatchTeam team = teams.get(teamIndex);
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

    /**
     * Update match display using a match Id.
     */
    private final ReadOnlyIntegerWrapper matchId = new ReadOnlyIntegerWrapper(this, "matchId", -1); // NOI18N.

    public final int getMatchId() {
        return matchId.get();
    }

    public final void setMatchId(final int value) {
        final int v = (value < -1) ? -1 : value;
        matchId.set(v);
    }

    public final ReadOnlyIntegerProperty matchIdProperty() {
        return matchId.getReadOnlyProperty();
    }

    /**
     * Update match display using a world Id.
     */
    private final ReadOnlyIntegerWrapper worldId = new ReadOnlyIntegerWrapper(this, "worldId", -1); // NOI18N.

    public final int getWorldId() {
        return worldId.get();
    }

    public final void setWorldId(final int value) {
        final int v = (value < -1) ? -1 : value;
        worldId.set(v);
    }

    public final ReadOnlyIntegerProperty worldIdProperty() {
        return worldId.getReadOnlyProperty();
    }
}
