/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.worlds;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.worlds.World;
import api.web.gw2.mapping.v2.wvw.matches.Match;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.scene.wvw.TestWvWSummaryPane;
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
public final class TestWorldInfoPane extends Application {

    @Override
    public void start(Stage primaryStage) {
        final WorldInfoPane worldInfoPane = new WorldInfoPane();
        final StackPane root = new StackPane();
        root.getChildren().add(worldInfoPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestWorldInfoPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
        loadTestAsync(worldInfoPane);
    }

    private void loadTestAsync(final WorldInfoPane worldInfoPane) {
        final ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (SABConstants.INSTANCE.isOffline()) {
                            doLocalTest(worldInfoPane);
                        } else {
                            doRemoteTest(worldInfoPane);
                        }
                        return null;
                    }
                };
            }
        };
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getLogger(TestWorldInfoPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.setPeriod(Duration.minutes(5));
        service.setRestartOnFailure(true);
        service.start();
    }

    /**
     * Do a remote test.
     * @return A {@code MatchWrapper}, may be {@code null}.
     */
    private void doRemoteTest(final WorldInfoPane worldInfoPane) {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        final int worldId = session.getAccount().getWorld();
        final World world = WebQuery.INSTANCE.queryWorlds(worldId).get(0);
        Platform.runLater(() -> worldInfoPane.setWorld(world));
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
        final MatchWrapper matchWrapper = (!match.isPresent()) ? null : new MatchWrapper(match.get(), worlds);
        if (matchWrapper != null) {
            Platform.runLater(() -> worldInfoPane.setMatch(matchWrapper));
        }
    }

    /**
     * Do a local test.
     * @return A {@code MatchWrapper}, may be {@code null}.
     */
    private void doLocalTest(final WorldInfoPane worldInfoPane) throws IOException {
        final Optional<URL> worldURL = Optional.ofNullable(getClass().getResource("world.json")); // NOI18N.
        final World world = (!worldURL.isPresent()) ? null : JsonpContext.SAX.loadObject(World.class, worldURL.get());
        if (world != null) {
            Platform.runLater(() -> worldInfoPane.setWorld(world));
        }
        final Optional<URL> matchURL = Optional.ofNullable(TestWvWSummaryPane.class.getResource("matches/match01.json")); // NOI18N.
        final Match match = (!matchURL.isPresent()) ? null : JsonpContext.SAX.loadObject(Match.class, matchURL.get());
        final Optional<URL> worldsURL = Optional.ofNullable(TestWvWSummaryPane.class.getResource("matches/worlds01.json")); // NOI18N.
        final List<World> worlds = (!matchURL.isPresent()) ? Collections.EMPTY_LIST : JsonpContext.SAX.loadObjectArray(World.class, worldsURL.get())
                .stream()
                .collect(Collectors.toList());
        final MatchWrapper matchWrapper = (match == null) ? null : new MatchWrapper(match, worlds);
        if (matchWrapper != null) {
            Platform.runLater(() -> worldInfoPane.setMatch(matchWrapper));
        }
    }

    public static void main(String... args) {
        launch(args);
    }
}
