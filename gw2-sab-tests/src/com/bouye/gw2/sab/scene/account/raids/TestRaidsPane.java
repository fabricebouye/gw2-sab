/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.raids;

import api.web.gw2.mapping.core.APILevel;
import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.raids.Raid;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfoPermission;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.GW2APIClient;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.RaidsWrapper;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestRaidsPane extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final RaidsPane raidsPane = new RaidsPane();
        final BorderPane root = new BorderPane();
        root.setCenter(raidsPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestRaidsPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            if (loadService != null) {
                loadService.cancel();
                loadService = null;
            }
        });
        loadTestAsync(raidsPane);
//        ScenicView.show(scene);
    }

    private ScheduledService<RaidsWrapper> loadService;

    /**
    * Loads the test in a background service.
    * @param raidsPane The target pane.
    */
    private void loadTestAsync(final RaidsPane raidsPane) {
        if (loadService != null) {
            loadService.cancel();
        } else {
            final ScheduledService<RaidsWrapper> service = new ScheduledService<RaidsWrapper>() {
                @Override
                protected Task<RaidsWrapper> createTask() {
                    return new Task<RaidsWrapper>() {
                        @Override
                        protected RaidsWrapper call() throws Exception {
                            System.out.println("Querying...");
                            final RaidsWrapper raids = (SABConstants.INSTANCE.isOffline()) ? doLocalTest(this) : doRemoteTest(this);
                            return raids;
                        }
                    };
                }
            };
            service.setPeriod(Duration.minutes(5));
            service.setRestartOnFailure(true);
            service.setOnSucceeded(workerStateEvent -> {
                final RaidsWrapper wrapper = (RaidsWrapper) workerStateEvent.getSource().getValue();
                raidsPane.setRaids(wrapper);
            });
            service.setOnFailed(workerStateEvent -> {
                final Throwable ex = workerStateEvent.getSource().getException();
                Logger.getLogger(TestRaidsPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            });
            loadService = service;
        }
        loadService.restart();
    }

    /**
     * Do a remote test.
     * @param task The task.
     * @return A {@code RaidsWrapper}, maybe {@code null}.
     */
    private RaidsWrapper doRemoteTest(final Task<RaidsWrapper> task) throws Exception {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        RaidsWrapper result = null;
        if (session.getTokenInfo().getPermissions().contains(TokenInfoPermission.PROGRESSION)) {
            // Load raid defintions.
            final Set<Raid> raids = new LinkedHashSet<>(GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("raids") // NOI18N.
                    .ids("all")
                    .queryArray(Raid.class));
            if (task.isCancelled()) {
                return null;
            }
            // Load encounters.
            final Set<String> encounterIds = new LinkedHashSet<>(GW2APIClient.create()
                    .apiLevel(APILevel.V2)
                    .endPoint("account/raids") // NOI18N.
                    .applicationKey(session.getAppKey())
                    .queryArray(String.class));
            if (task.isCancelled()) {
                return null;
            }
            //
            result = new RaidsWrapper(raids, encounterIds);
        }
        return result;
    }

    /**
     * Do a local test.
     * @param task The task.
     * @return A {@code RaidsWrapper}, maybe {@code null}.
     */
    private RaidsWrapper doLocalTest(final Task<RaidsWrapper> task) throws Exception {
        // Load raid defintions.
        final URL raidsURL = getClass().getResource("raids.json"); // NOI18N.
        final Set<Raid> raids = new LinkedHashSet<>(JsonpContext.SAX.loadObjectArray(Raid.class, raidsURL));
        if (task.isCancelled()) {
            return null;
        }
        // Load encounters.
        final URL encounterIdsURL = getClass().getResource("encounters.json"); // NOI18N.
        final Set<String> encounterIds = new LinkedHashSet<>(JsonpContext.SAX.loadObjectArray(String.class, encounterIdsURL));
        if (task.isCancelled()) {
            return null;
        }
        if (task.isCancelled()) {
            return null;
        }
        //
        final RaidsWrapper result = new RaidsWrapper(raids, encounterIds);
        return result;
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
