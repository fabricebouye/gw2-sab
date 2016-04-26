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
import api.web.gw2.mapping.v2.wvw.matches.Match;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.MatchWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author fabriceb
 */
public final class TestWvWSummaryPane extends Application {

    @Override
    public void start(Stage primaryStage) {
        final WvwSummaryPane wvwSummaryPane = new WvwSummaryPane();
        final StackPane root = new StackPane();
        root.getChildren().add(wvwSummaryPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestWvWSummaryPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
        loadTestAsync(wvwSummaryPane);
    }

    private void loadTestAsync(final WvwSummaryPane wvwSummaryPane) {
        ScheduledService<MatchWrapper> service = new ScheduledService<MatchWrapper>() {
            @Override
            protected Task<MatchWrapper> createTask() {
                return new Task<MatchWrapper>() {
                    @Override
                    protected MatchWrapper call() throws Exception {
                        return (SABConstants.INSTANCE.isOffline()) ? doLocalTest() : doRemoteTest();
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final MatchWrapper wrapper = (MatchWrapper) workerStateEvent.getSource().getValue();
            wvwSummaryPane.setMatch(wrapper);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getLogger(TestWvWSummaryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.setPeriod(Duration.minutes(5));
        service.setRestartOnFailure(true);
        service.start();
    }

    /**
     * Do a remote test.
     * @return A {@code MatchWrapper}, may be {@code null}.
     */
    private MatchWrapper doRemoteTest() {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        final int worldId = session.getAccount().getWorld();
        final Optional<Match> match = WebQuery.INSTANCE.queryWvwMatch(worldId);
        int[] worldIds = new int[0];
        if (match.isPresent()) {
            // @todo To test.
            final Stream<Integer> allIds = match.get()
                    .getAllWorlds()
                    .values()
                    .stream()
                    .map(set -> set.stream())
                    .reduce(Stream.empty(), Stream::concat);
//            Stream<Integer> allIds = Stream.empty();
//            for (Set<Integer> worlds : match.get().getAllWorlds().values()) {
//                allIds = Stream.concat(allIds, worlds.stream());
//            }
            worldIds = allIds.distinct()
                    .mapToInt(value -> value)
                    .toArray();
        }
        final List<World> worlds = (worldIds.length == 0) ? Collections.EMPTY_LIST : WebQuery.INSTANCE.queryWorlds(worldIds);
        return (!match.isPresent()) ? null : new MatchWrapper(match.get(), worlds);
    }

    /**
     * Do a local test.
     * @return A {@code MatchWrapper}, may be {@code null}.
     */
    private MatchWrapper doLocalTest() throws IOException {
        final Optional<URL> matchURL = Optional.ofNullable(getClass().getResource("matches/match01.json")); // NOI18N.
        final Match match = (!matchURL.isPresent()) ? null : JsonpContext.SAX.loadObject(Match.class, matchURL.get());
        final Optional<URL> worldsURL = Optional.ofNullable(getClass().getResource("matches/worlds01.json")); // NOI18N.
        final List<World> worlds = (!matchURL.isPresent()) ? Collections.EMPTY_LIST : JsonpContext.SAX.loadObjectArray(World.class, worldsURL.get())
                .stream()
                .collect(Collectors.toList());
        return (match == null) ? null : new MatchWrapper(match, worlds);
    }

    public static void main(String... args) {
        launch(args);
    }
}
