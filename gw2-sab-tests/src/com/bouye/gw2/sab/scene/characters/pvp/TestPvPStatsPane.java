/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.pvp;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.pvp.stats.Stat;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public class TestPvPStatsPane extends Application {

    @Override
    public void start(Stage primaryStage) {
        final PvPStatsPane statsPane = new PvPStatsPane();
        final StackPane root = new StackPane();
        root.getChildren().add(statsPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestPvPStatsPane");
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(root);
        loadTest(statsPane);
    }

    private void loadTest(final PvPStatsPane statsPane) {
        final Service<Stat> service = new Service<Stat>() {
            @Override
            protected Task<Stat> createTask() {
                return new Task<Stat>() {
                    @Override
                    protected Stat call() throws Exception {
                        return (SABConstants.INSTANCE.isOffline()) ? loadLocalTest() : loadRemoteTest();
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final Stat stat = (Stat) workerStateEvent.getSource().getValue();
            statsPane.setStat(stat);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.start();
    }

    private Stat loadRemoteTest() {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        final Optional<Stat> result = WebQuery.INSTANCE.queryPvPStats(session.getAppKey());
        return result.orElse(null);
    }

    private Stat loadLocalTest() throws NullPointerException, IOException {
        final URL url = getClass().getResource("stats.json");
        final Stat result = (url == null) ? null : JsonpContext.SAX.loadObject(Stat.class, url);
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
