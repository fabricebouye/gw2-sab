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
import com.bouye.gw2.sab.wrappers.MatchesWrapper;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class WvwMatchesPaneController extends SABControllerBase<WvwMatchesPane> {

    @FXML
    private VBox rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    protected void uninstallNode(final WvwMatchesPane node) {
        node.matchesProperty().removeListener(matchesChangeListener);
    }

    @Override
    protected void installNode(final WvwMatchesPane node) {
        node.matchesProperty().addListener(matchesChangeListener);
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        rootPane.getChildren().clear();
        final Optional<WvwMatchesPane> node = parentNode();
        final MatchesWrapper wrapper = node.isPresent() ? node.get().getMatches() : null;
        final Map<String, Match> matches = (wrapper == null) ? Collections.EMPTY_MAP : wrapper.getMatches();
        final Map<Integer, World> worlds = (wrapper == null) ? Collections.EMPTY_MAP : wrapper.getWorlds();
        matches.entrySet().stream()
                .forEach(entry -> {
                    final Match match = entry.getValue();
                    final Map<MatchTeam, Set<Integer>> allWorlds = match.getAllWorlds();
                    final String label = Arrays.asList(MatchTeam.GREEN, MatchTeam.BLUE, MatchTeam.RED)
                            .stream()
                            .map(team -> {
                                final Set<Integer> worldIds = allWorlds.get(team);
                                return worldIds.stream()
                                        .map(worlds::get)
                                        .map(World::getName)
                                        .collect(Collectors.joining(" + ", "", ""));
                            })
                            .collect(Collectors.joining(",\n", "", ""));
                    final Text text = new Text(label);
                    final TextFlow textFlow = new TextFlow(text);
                    rootPane.getChildren().add(textFlow);
                });
    }

    private final ChangeListener<MatchesWrapper> matchesChangeListener = (observable, oldValue, newValue) -> updateUI();
}
