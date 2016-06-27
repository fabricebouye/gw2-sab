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
import api.web.gw2.mapping.v2.wvw.matches.MatchMap;
import api.web.gw2.mapping.v2.wvw.matches.MatchMapObjective;
import api.web.gw2.mapping.v2.wvw.matches.MatchTeam;
import api.web.gw2.mapping.v2.wvw.objectives.ObjectiveType;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.MatchesWrapper;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class WvwMatchesPaneController extends SABControllerBase<WvwMatchesPane> {

    @FXML
    private GridPane rootPane;

    private List<Node> headerNodes;
    private RowConstraints headerRowConstraints;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        headerNodes = Collections.unmodifiableList(new ArrayList<>(rootPane.getChildren()));
        headerRowConstraints = rootPane.getRowConstraints().get(0);
    }

    @Override
    protected void uninstallNode(final WvwMatchesPane node) {
        node.matchesProperty().removeListener(matchesChangeListener);
    }

    @Override
    protected void installNode(final WvwMatchesPane node) {
        node.matchesProperty().addListener(matchesChangeListener);
    }

    private final List<MatchTeam> teams = Arrays.asList(MatchTeam.GREEN, MatchTeam.BLUE, MatchTeam.RED);

    private static final PseudoClass ODD_PSEUDO_CLASS = PseudoClass.getPseudoClass("odd"); // NOI18N.
    private static final PseudoClass EVEN_PSEUDO_CLASS = PseudoClass.getPseudoClass("even"); // NOI18N.

    @Override
    protected void updateUI() {
        super.updateUI();
        rootPane.getChildren().clear();
        final Optional<WvwMatchesPane> node = parentNode();
        final MatchesWrapper wrapper = node.isPresent() ? node.get().getMatches() : null;
        final Map<String, Match> matches = (wrapper == null) ? Collections.EMPTY_MAP : wrapper.getMatches();
        final Map<Integer, World> worlds = (wrapper == null) ? Collections.EMPTY_MAP : wrapper.getWorlds();
        final int matchNumber = matches.size();
        final int teamNumber = teams.size();
        rootPane.getRowConstraints().clear();
        rootPane.getRowConstraints().add(headerRowConstraints);
        IntStream.range(0, teamNumber * matchNumber)
                .mapToObj(rowIndex -> {
                    final RowConstraints rowConstraints = new RowConstraints();
                    rowConstraints.setMinHeight(Region.USE_PREF_SIZE);
                    rowConstraints.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    rowConstraints.setVgrow(Priority.NEVER);
                    return rowConstraints;
                })
                .forEach(rootPane.getRowConstraints()::add);
        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(headerNodes);
        final Iterator<Match> matchIterator = matches.values().iterator();
        final List<Node> rowNodes = new ArrayList();
        IntStream.range(0, matchNumber)
                .forEach(matchIndex -> {
                    final int startRowIndex = teamNumber * matchIndex + 1;
                    final Match match = matchIterator.next();
                    final Map<MatchTeam, String> worldNames = createWorldNames(match, worlds);
                    final Map<MatchTeam, Integer> incomes = teams.stream()
                            .collect(Collectors.toConcurrentMap(Function.identity(), team -> match.getMaps()
                                    .stream()
                                    .mapToInt(map -> map.getScores().get(team))
                                    .sum()));
                    // Column#1 - Names.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final MatchTeam team = teams.get(teamIndex);
                        //
                        final String name = worldNames.get(team);
                        final Label nameLabel = new Label(name);
                        nameLabel.setWrapText(true);
                        GridPane.setConstraints(nameLabel, 1, rowIndex);
                        final Tooltip nameTooltip = new Tooltip(name);
                        nameLabel.setTooltip(nameTooltip);
                        rowNodes.add(nameLabel);
                    }
                    // Column#2 - Scores.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final MatchTeam team = teams.get(teamIndex);
                        //
                        final int scoreValue = match.getScores().get(team);
                        final String score = String.valueOf(scoreValue);
                        final Label scoreLabel = new Label(score);
                        GridPane.setConstraints(scoreLabel, 2, rowIndex);
                        final Tooltip scoreTooltip = new Tooltip(score);
                        scoreLabel.setTooltip(scoreTooltip);
                        rowNodes.add(scoreLabel);
                    }
                    // Column#3 - Score bar chart.
                    final BarChart scoreBarChart = createScoreBarChart(match, worldNames);
                    GridPane.setConstraints(scoreBarChart, 3, startRowIndex, 1, teamNumber, HPos.LEFT, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);
                    rowNodes.add(scoreBarChart);
                    // Column#4 - Income pie chart.
                    final PieChart summaryPieChart = createSummaryPieChart(incomes, worldNames);
                    GridPane.setConstraints(summaryPieChart, 4, startRowIndex, 1, teamNumber, HPos.LEFT, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);
                    rowNodes.add(summaryPieChart);
                    // Column#5 - Incomes.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final MatchTeam team = teams.get(teamIndex);
                        //
                        final int incomeValue = incomes.get(team);
                        final String income = String.format("+ %d", incomeValue); // NOI18N.
                        final Label incomeLabel = new Label(income);
                        GridPane.setConstraints(incomeLabel, 5, rowIndex);
                        final Tooltip incomeTooltip = new Tooltip(income);
                        incomeLabel.setTooltip(incomeTooltip);
                        rowNodes.add(incomeLabel);
                    }
                    // Column#6 - Camps.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final MatchTeam team = teams.get(teamIndex);
                        //
                        final int value = match.getMaps()
                                .stream()
                                .mapToInt(map -> (int) map.getObjectives()
                                        .stream()
                                        .filter(objective -> (objective.getType() == ObjectiveType.CAMP) && (objective.getOwner() == team))
                                        .count())
                                .sum();
                        final String owned = String.valueOf(value);
                        final Label ownedLabel = new Label(owned);
                        GridPane.setConstraints(ownedLabel, 6, rowIndex);
                        final Tooltip ownedTooltip = new Tooltip(owned);
                        ownedLabel.setTooltip(ownedTooltip);
                        rowNodes.add(ownedLabel);
                    }
                    // Column#7 - Towers.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final MatchTeam team = teams.get(teamIndex);
                        //
                        final int value = match.getMaps()
                                .stream()
                                .mapToInt(map -> (int) map.getObjectives()
                                        .stream()
                                        .filter(objective -> (objective.getType() == ObjectiveType.TOWER) && (objective.getOwner() == team))
                                        .count())
                                .sum();
                        final String owned = String.valueOf(value);
                        final Label ownedLabel = new Label(owned);
                        GridPane.setConstraints(ownedLabel, 7, rowIndex);
                        final Tooltip ownedTooltip = new Tooltip(owned);
                        ownedLabel.setTooltip(ownedTooltip);
                        rowNodes.add(ownedLabel);
                    }
                    // Column#8 - Keeps.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final MatchTeam team = teams.get(teamIndex);
                        //
                        final int value = match.getMaps()
                                .stream()
                                .mapToInt(map -> (int) map.getObjectives()
                                        .stream()
                                        .filter(objective -> (objective.getType() == ObjectiveType.KEEP) && (objective.getOwner() == team))
                                        .count())
                                .sum();
                        final String owned = String.valueOf(value);
                        final Label ownedLabel = new Label(owned);
                        GridPane.setConstraints(ownedLabel, 8, rowIndex);
                        final Tooltip ownedTooltip = new Tooltip(owned);
                        ownedLabel.setTooltip(ownedTooltip);
                        rowNodes.add(ownedLabel);
                    }
                    // Column#9 - Castle.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final MatchTeam team = teams.get(teamIndex);
                        //
                        final int value = match.getMaps()
                                .stream()
                                .mapToInt(map -> (int) map.getObjectives()
                                        .stream()
                                        .filter(objective -> (objective.getType() == ObjectiveType.CASTLE) && (objective.getOwner() == team))
                                        .count())
                                .sum();
                        final String owned = String.valueOf(value);
                        final Label ownedLabel = new Label(owned);
                        GridPane.setConstraints(ownedLabel, 9, rowIndex);
                        final Tooltip ownedTooltip = new Tooltip(owned);
                        ownedLabel.setTooltip(ownedTooltip);
                        rowNodes.add(ownedLabel);
                    }
                    //
                    final PseudoClass rowPseudoClass = (matchIndex % 2 == 0) ? EVEN_PSEUDO_CLASS : ODD_PSEUDO_CLASS;
                    rowNodes.stream()
                            .forEach(n -> {
                                n.pseudoClassStateChanged(EVEN_PSEUDO_CLASS, false);
                                n.pseudoClassStateChanged(ODD_PSEUDO_CLASS, false);
                                n.pseudoClassStateChanged(rowPseudoClass, true);
                            });
                    rootPane.getChildren().addAll(rowNodes);
                    rowNodes.clear();
                });
    }

    private Map<MatchTeam, String> createWorldNames(final Match match, final Map<Integer, World> worlds) {
        final Map<MatchTeam, Set<Integer>> allWorlds = match.getAllWorlds();
        final Map<MatchTeam, String> result = new HashMap<>();
        teams.stream()
                .forEach(team -> {
                    final Set<Integer> worldIds = allWorlds.get(team);
                    final String name = worldIds.stream()
                            .map(worlds::get)
                            .map(World::getName)
                            .collect(Collectors.joining(" + ", "", ""));
                    result.put(team, name);
                });
        return Collections.unmodifiableMap(result);
    }

    private Node createServersHeader(final Match match, final Map<MatchTeam, String> worldNames) {
        final Map<MatchTeam, Set<Integer>> allWorlds = match.getAllWorlds();
        final String serverLabel = teams.stream()
                .map(worldNames::get)
                .collect(Collectors.joining("\n", "", ""));
        final Text serverText = new Text(serverLabel);
        return new TextFlow(serverText);
    }

    private BarChart createScoreBarChart(final Match match, final Map<MatchTeam, String> worldNames) {
        final CategoryAxis scoreCategoryAxis = new CategoryAxis();
        scoreCategoryAxis.setTickMarkVisible(false);
        scoreCategoryAxis.setTickLabelsVisible(false);
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickLabelsVisible(false);
        final BarChart scoreBarChart = new BarChart(xAxis, scoreCategoryAxis);
        scoreBarChart.getStyleClass().add("wvw-bar-chart"); // NOI18N.
        scoreBarChart.setMinWidth(0);
        scoreBarChart.setMinHeight(0);
        scoreBarChart.setPrefWidth(0);
        scoreBarChart.setPrefHeight(0);
        scoreBarChart.setLegendVisible(false);
        scoreBarChart.setCategoryGap(0);
        scoreBarChart.setBarGap(0);
        scoreBarChart.setHorizontalGridLinesVisible(false);
        scoreBarChart.setVerticalGridLinesVisible(false);
        final List<String> teamNames = teams.stream()
                .map(team -> team.name())
                .collect(Collectors.toList());
        scoreCategoryAxis.getCategories().setAll(teamNames);
        // Prepare content.
        teams.stream()
                .forEach(team -> {
                    final BarChart.Series series = new BarChart.Series();
                    // Initially the series' name is the team's name.
                    series.setName(team.name());
                    series.getData().add(new BarChart.Data(0, team.name()));
                    scoreBarChart.getData().add(series);
                });
        // Update content.
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
        return scoreBarChart;
    }

    private PieChart createSummaryPieChart(final Map<MatchTeam, Integer> incomes, final Map<MatchTeam, String> worldNames) {
        final PieChart summaryPieChart = new PieChart();
        summaryPieChart.getStyleClass().add("wvw-pie-chart");
        summaryPieChart.setMinWidth(0);
        summaryPieChart.setMinHeight(0);
        summaryPieChart.setPrefWidth(0);
        summaryPieChart.setPrefHeight(0);
        summaryPieChart.setLegendVisible(false);
        summaryPieChart.setLabelsVisible(false);
        // Prepare content.
        teams.stream()
                .forEach(team -> summaryPieChart.getData().add(new PieChart.Data(team.name(), 0)));
        // Update content.
        IntStream.range(0, teams.size())
                .forEach(teamIndex -> {
                    final MatchTeam team = teams.get(teamIndex);
                    final int teamScore = incomes.get(team);
                    final String worldName = worldNames.get(team);
                    final PieChart.Data data = (PieChart.Data) summaryPieChart.getData().get(teamIndex);
                    data.setName(worldName);
                    data.setPieValue(teamScore);
                });
        return summaryPieChart;
    }

    private final ChangeListener<MatchesWrapper> matchesChangeListener = (observable, oldValue, newValue) -> updateUI();
}
