/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.raids;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.raids.Raid;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.wrappers.RaidsWrapper;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

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
        doLoadTest(raidsPane);
        ScenicView.show(scene);
    }

    private Service<RaidsWrapper> loadService;

    private void doLoadTest(final RaidsPane raidsPane) {
        if (loadService != null) {
            loadService.cancel();
        } else {
            Service<RaidsWrapper> service = new Service<RaidsWrapper>() {
                @Override
                protected Task<RaidsWrapper> createTask() {
                    return new Task<RaidsWrapper>() {
                        @Override
                        protected RaidsWrapper call() throws Exception {
                            return doLocalTest();
                        }
                    };
                }
            };
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

    private RaidsWrapper doLocalTest() throws Exception {
        // Load raid defintions.
        final URL raidsURL = getClass().getResource("raids.json"); // NOI18N.
        final Set<Raid> raids = new LinkedHashSet<>(JsonpContext.SAX.loadObjectArray(Raid.class, raidsURL));
        final URL encounterIdsURL = getClass().getResource("encounters.json"); // NOI18N.
        final Set<String> encounterIds = new LinkedHashSet<>(JsonpContext.SAX.loadObjectArray(String.class, encounterIdsURL));
        return new RaidsWrapper(raids, encounterIds);
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
