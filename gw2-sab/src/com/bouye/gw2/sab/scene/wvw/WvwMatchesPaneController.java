/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.wvw.matches.WvwMatchTeam;
import api.web.gw2.mapping.v2.wvw.objectives.WvwObjectiveType;
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
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import api.web.gw2.mapping.v2.wvw.matches.WvwMatch;
import api.web.gw2.mapping.v2.wvw.matches.WvwMatchMap;

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

    private static final PseudoClass ODD_PSEUDO_CLASS = PseudoClass.getPseudoClass("odd"); // NOI18N.
    private static final PseudoClass EVEN_PSEUDO_CLASS = PseudoClass.getPseudoClass("even"); // NOI18N.
    private static final PseudoClass SMALL_PSEUDO_CLASS = PseudoClass.getPseudoClass("small"); // NOI18N.
    private final List<WvwMatchTeam> teams = WvwUtils.INSTANCE.getTeams();
    private final List<WvwMatchTeam> pieTeams = WvwUtils.INSTANCE.getPieTeams();
    private final List<WvwObjectiveType> objectiveTypes = WvwUtils.INSTANCE.getObjectiveTypes();
    private final Map<WvwObjectiveType, Integer> objectivePoints = WvwUtils.INSTANCE.getObjectivePoints();

    @Override
    protected void updateUI() {
        super.updateUI();
        rootPane.getChildren().clear();
        final Optional<WvwMatchesPane> node = parentNode();
        final MatchesWrapper wrapper = node.isPresent() ? node.get().getMatches() : null;
        final Map<String, WvwMatch> matches = (wrapper == null) ? Collections.EMPTY_MAP : wrapper.getMatches();
        final Map<Integer, World> worlds = (wrapper == null) ? Collections.EMPTY_MAP : wrapper.getWorlds();
        final int matchNumber = matches.size();
        final int teamNumber = teams.size();
        // Clear content.
        rootPane.getRowConstraints().clear();
        rootPane.getChildren().clear();
        // Create row constraints.
        rootPane.getRowConstraints().add(headerRowConstraints);
        final int totalRows = teamNumber * matchNumber;
        IntStream.range(0, totalRows)
                .mapToObj(this::createTeamRowConstraints)
                .forEach(rootPane.getRowConstraints()::add);
        // Restore header.
        rootPane.getChildren().addAll(headerNodes);
        if (matchNumber == 0) {
            return;
        }
        // Start creating match content.
        final Iterator<WvwMatch> matchIterator = matches.values()
                .stream()
                .sorted((m1, m2) -> m1.getId().compareTo(m2.getId()))
                .iterator();
        final List<Node> rowNodes = new ArrayList();
        IntStream.range(0, matchNumber)
                .forEach(matchIndex -> {
                    final int startRowIndex = teamNumber * matchIndex + 1;
                    final WvwMatch match = matchIterator.next();
                    final Map<WvwMatchTeam, String> worldNames = createWorldNames(match, worlds);
                    // Compute aggragated objectives and income.
                    final Map<WvwMatchTeam, int[]> aggregateObjectives = computeAggregateObjectives(match);
                    final Map<WvwMatchTeam, Integer> incomes = computeIncome(aggregateObjectives);
                    // Column#1 - Names.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final WvwMatchTeam team = teams.get(teamIndex);
                        //
                        final int mainWorldId = match.getWorlds().get(team);
                        final Set<Integer> auxWorldIds = match.getAllWorlds().get(team);
                        final TextFlow nameLabel = createWorldLabels(worlds, mainWorldId, auxWorldIds);
                        GridPane.setConstraints(nameLabel, 1, rowIndex);
                        final String baseName = worldNames.get(team);
                        final Tooltip nameTooltip = new Tooltip(baseName);
                        Tooltip.install(nameLabel, nameTooltip);
                        rowNodes.add(nameLabel);
                    }
                    // Column#2 - Scores.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final WvwMatchTeam team = teams.get(teamIndex);
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
                    final double maxScore = match.getScores()
                            .values()
                            .stream()
                            .mapToInt(value -> value)
                            .max()
                            .getAsInt();
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final WvwMatchTeam team = teams.get(teamIndex);
                        //
                        final int scoreValue = match.getScores().get(team);
                        final String score = String.valueOf(scoreValue);
                        final PseudoClass teamPseudoClass = PseudoClass.getPseudoClass(team.name());
                        final ProgressBar scoreProgressBar = new ProgressBar();
                        scoreProgressBar.setProgress(scoreValue / maxScore);
                        scoreProgressBar.pseudoClassStateChanged(teamPseudoClass, true);
                        scoreProgressBar.setMaxWidth(Double.MAX_VALUE);
                        GridPane.setConstraints(scoreProgressBar, 3, rowIndex, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
                        final Tooltip scoreTooltip = new Tooltip(score);
                        scoreProgressBar.setTooltip(scoreTooltip);
                        rowNodes.add(scoreProgressBar);
                    }
                    // Column#4 - Income pie chart.
                    //final PieChart summaryPieChart = createSummaryPieChart(incomes, worldNames);
                    final PieChart summaryPieChart = createSummaryPieChart(incomes, worldNames);
                    GridPane.setConstraints(summaryPieChart, 4, startRowIndex, 1, teamNumber, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
                    rowNodes.add(summaryPieChart);
                    // Column#5 - Incomes.
                    for (int teamIndex = 0; teamIndex < teamNumber; teamIndex++) {
                        final int rowIndex = startRowIndex + teamIndex;
                        final WvwMatchTeam team = teams.get(teamIndex);
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
                        final WvwMatchTeam team = teams.get(teamIndex);
                        //
                        final int[] teamObjectives = aggregateObjectives.get(team);
                        final int value = teamObjectives[objectiveTypes.indexOf(WvwObjectiveType.CAMP)];
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
                        final WvwMatchTeam team = teams.get(teamIndex);
                        //
                        final int[] teamObjectives = aggregateObjectives.get(team);
                        final int value = teamObjectives[objectiveTypes.indexOf(WvwObjectiveType.TOWER)];
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
                        final WvwMatchTeam team = teams.get(teamIndex);
                        //
                        final int[] teamObjectives = aggregateObjectives.get(team);
                        final int value = teamObjectives[objectiveTypes.indexOf(WvwObjectiveType.KEEP)];
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
                        final WvwMatchTeam team = teams.get(teamIndex);
                        //
                        final int[] teamObjectives = aggregateObjectives.get(team);
                        final int value = teamObjectives[objectiveTypes.indexOf(WvwObjectiveType.CASTLE)];
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

    /**
     * Create a row constraints for team.
     * @param rowIndex The row index.
     * @return A {@code RowConstraints} instance, never {@code null}.
     */
    private RowConstraints createTeamRowConstraints(final int rowIndex) {
        final RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(Region.USE_COMPUTED_SIZE);
        rowConstraints.setPrefHeight(Region.USE_COMPUTED_SIZE);
        rowConstraints.setMaxHeight(Region.USE_COMPUTED_SIZE);
        rowConstraints.setVgrow(Priority.NEVER);
        return rowConstraints;
    }

    /**
     * Compute aggregate objectives owned by each team on given match.
     * @param match The match.
     * @return A {@code Map<MatchTeam, int[]>}, never {@code null}.
     */
    private Map<WvwMatchTeam, int[]> computeAggregateObjectives(final WvwMatch match) {
        return IntStream.range(0, teams.size())
                .mapToObj(teams::get)
                .collect(Collectors.toMap(Function.identity(), team -> countAggregateObjectives(match, team)));
    }

    /**
     * Aggregate all objectives owned by given team for given match.
     * @param match The match.
     * @param team The team.
     * @return An {@code int[]} instance, never {@code null}.
     */
    int[] countAggregateObjectives(final WvwMatch match, final WvwMatchTeam team) {
        return objectiveTypes.stream()
                .mapToInt(objectiveType -> countObjective(match, team, objectiveType))
                .toArray();
    }

    /**
     * Count given objective type owned by given team for given match.
     * @param map The map.
     * @param team The team.
     * @param objectiveType The type of the objective.
     * @return An {@code int}.
     */
    final int countObjective(final WvwMatch match, final WvwMatchTeam team, final WvwObjectiveType objectiveType) {
        return match.getMaps()
                .stream()
                .mapToInt(map -> countObjectiveOnMap(map, team, objectiveType))
                .sum();
    }

    /**
     * Count given objective type owned by given team for given map.
     * @param map The map.
     * @param team The team.
     * @param objectiveType The type of the objective.
     * @return An {@code int}.
     */
    private int countObjectiveOnMap(final WvwMatchMap map, final WvwMatchTeam team, final WvwObjectiveType objectiveType) {
        return (int) map.getObjectives()
                .stream()
                .filter(objective -> (objective.getType() == objectiveType) && (objective.getOwner() == team))
                .count();
    }

    /**
     * Compute income based on aggregate objectives owned by teams.
     * @param aggregateObjectives Map of aggregate objectives.
     * @return A {@code Map<MatchTeam, Integer>} instance, never {@code null}.
     */
    private Map<WvwMatchTeam, Integer> computeIncome(final Map<WvwMatchTeam, int[]> aggregateObjectives) {
        return teams.stream()
                .collect(Collectors.toMap(Function.identity(), team -> computeAggregateIncome(aggregateObjectives, team)));
    }

    /**
     * Compute aggregated income for given team.
     * @param aggregateObjectives Map of aggregate objectives.
     * @param team The team.
     * @return An {@code int}.
     */
    private int computeAggregateIncome(final Map<WvwMatchTeam, int[]> aggregateObjectives, final WvwMatchTeam team) {
        final int[] teamObjectives = aggregateObjectives.get(team);
        return objectiveTypes.stream()
                .mapToInt(objectiveType -> computeObjectiveIncome(teamObjectives, objectiveType))
                .sum();
    }

    /**
     * Compute income for given objective type.
     * @param teamObjectives Arrays of objectives owned by team.
     * @param objectiveType The type of the objective.
     * @return An {@code int}.
     */
    private int computeObjectiveIncome(final int[] teamObjectives, final WvwObjectiveType objectiveType) {
        final int objectiveIndex = objectiveTypes.indexOf(objectiveType);
        final int point = objectivePoints.get(objectiveType);
        final int objectiveNumber = teamObjectives[objectiveIndex];
        return objectiveNumber * point;
    }

    private Map<WvwMatchTeam, String> createWorldNames(final WvwMatch match, final Map<Integer, World> worlds) {
        final Map<WvwMatchTeam, Integer> mainWorlds = match.getWorlds();
        final Map<WvwMatchTeam, Set<Integer>> allWorlds = match.getAllWorlds();
        final Map<WvwMatchTeam, String> result = new HashMap<>();
        teams.stream()
                .forEach(team -> {
                    final int mainWorldId = mainWorlds.get(team);
                    final Set<Integer> allWorldIds = allWorlds.get(team);
                    final int[] secondaryWorldIds = allWorldIds.stream()
                            .mapToInt(id -> id)
                            .toArray();
                    final String name = WvwUtils.INSTANCE.createTeamName(worlds, mainWorldId, secondaryWorldIds);
                    result.put(team, name);
                });
        return Collections.unmodifiableMap(result);
    }

    private TextFlow createWorldLabels(final Map<Integer, World> worlds, final int mainWorldId, final Set<Integer> auxWorldIds) {
        final TextFlow result = new TextFlow();
        final int[] secondaryWorldIds = auxWorldIds.stream()
                .mapToInt(id -> id)
                .toArray();
        result.getChildren().setAll(WvwUtils.INSTANCE.createTeamLabels(worlds, mainWorldId, secondaryWorldIds));
        return result;
    }

    /**
     * Create the income pie chart.
     * @param incomes The income map for each team.
     * @param worldNames World names.s
     * @return A {@code PieChart} instance, never {@code null}.
     */
    private PieChart createSummaryPieChart(final Map<WvwMatchTeam, Integer> incomes, final Map<WvwMatchTeam, String> worldNames) {
        final PieChart summaryPieChart = new PieChart();
        summaryPieChart.getStyleClass().add("wvw-pie-chart");
        summaryPieChart.setLegendVisible(false);
        // Prepare content.
        pieTeams.stream()
                .map(team -> new PieChart.Data(team.name(), 0))
                .forEach(summaryPieChart.getData()::add);
        // Update content.
        IntStream.range(0, pieTeams.size())
                .forEach(teamIndex -> {
                    final WvwMatchTeam team = pieTeams.get(teamIndex);
                    final int teamScore = incomes.get(team);
                    final String worldName = worldNames.get(team);
                    final PieChart.Data data = (PieChart.Data) summaryPieChart.getData().get(teamIndex);
                    data.setName(worldName);
                    data.setPieValue(teamScore);
                });
        return summaryPieChart;
    }

    /**
     * Called whenever the match wrapper changes.
     */
    private final ChangeListener<MatchesWrapper> matchesChangeListener = (observable, oldValue, newValue) -> updateUI();
}
