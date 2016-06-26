/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.worlds.WorldRegion;
import api.web.gw2.mapping.v2.wvw.matches.Match;
import api.web.gw2.mapping.v2.wvw.matches.MatchTeam;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.wrappers.MatchesWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.scenicview.ScenicView;

/**
 * Test.
 *
 * @author fabriceb
 */
public final class TestWvwMatchesPane extends Application {

    @Override
    public void start(Stage primaryStage) {
        final WvwMatchesPane wvwMatchesPane = new WvwMatchesPane();
        final WorldRegion[] allRegions = Arrays.stream(WorldRegion.values())
                .filter(region -> region != WorldRegion.UNKNOWN)
                .toArray(WorldRegion[]::new);
        final ComboBox<WorldRegion> regionComboBox = new ComboBox<>();
        regionComboBox.getItems().setAll(allRegions);
        final ToolBar toolBar = new ToolBar();
        toolBar.getItems().setAll(regionComboBox);
        final BorderPane root = new BorderPane();
        root.setTop(toolBar);
        root.setCenter(wvwMatchesPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestWvwMatchesPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
        regionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadTestAsync(wvwMatchesPane, newValue);
            }
        });
    }

    private ScheduledService<MatchesWrapper> service;

    private void loadTestAsync(final WvwMatchesPane wvwMatchesPane, final WorldRegion region) {
        if (service != null) {
            service.cancel();
            service = null;
        }
        service = new ScheduledService<MatchesWrapper>() {
            @Override
            protected Task<MatchesWrapper> createTask() {
                return new Task<MatchesWrapper>() {
                    @Override
                    protected MatchesWrapper call() throws Exception {
                        return (SABConstants.INSTANCE.isOffline()) ? doLocalTest(region) : doRemoteTest(region);
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final MatchesWrapper wrapper = (MatchesWrapper) workerStateEvent.getSource().getValue();
            wvwMatchesPane.setMatches(wrapper);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getLogger(TestWvwMatchesPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.setPeriod(Duration.minutes(5));
        service.setRestartOnFailure(true);
        service.start();
    }

    /**
     * Do a remote test.
     * @return A {@code MatchesWrapper}, may be {@code null}.
     */
    private MatchesWrapper doRemoteTest(final WorldRegion region) {
        final Map<Integer, World> worldIds = WebQuery.INSTANCE.queryWorlds()
                .stream()
                .filter(world -> world.getRegion() == region)
                .collect(Collectors.toMap(World::getId, Function.identity()));
        final Map<String, Match> matchIds = WebQuery.INSTANCE.queryWvwMatches()
                .stream()
                .filter(match -> worldIds.containsKey(match.getWorlds().get(MatchTeam.GREEN)))
                .collect(Collectors.toMap(Match::getId, Function.identity()));
        return new MatchesWrapper(matchIds, worldIds);
    }

    /**
     * Do a local test.
     * @return A {@code MatchesWrapper}, may be {@code null}.
     */
    private MatchesWrapper doLocalTest(final WorldRegion region) throws IOException {
        final Optional<URL> worldsURL = Optional.ofNullable(getClass().getResource("matches/worlds01.json")); // NOI18N.
        final Map<Integer, World> worldIds = (!worldsURL.isPresent()) ? Collections.EMPTY_MAP : JsonpContext.SAX.loadObjectArray(World.class, worldsURL.get())
                .stream()
                .collect(Collectors.toMap(World::getId, Function.identity()));
        final Optional<URL> matchURL = Optional.ofNullable(getClass().getResource("matches/match01.json")); // NOI18N.
        final Map<String, Match> matchsId = (!matchURL.isPresent()) ? Collections.EMPTY_MAP : Arrays.asList(JsonpContext.SAX.loadObject(Match.class, matchURL.get()))
                .stream()
                .collect(Collectors.toMap(Match::getId, Function.identity()));
        return new MatchesWrapper(matchsId, worldIds);
    }

    public static void main(String... args) {
        launch(args);
    }
}
