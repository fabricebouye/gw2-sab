/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.pvp;

import api.web.gw2.mapping.core.JsonpContext;
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
import api.web.gw2.mapping.v2.pvp.stats.PvpStat;

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
//        ScenicView.show(root);
        loadTestAsync(statsPane);
    }

    /**
     * Load the test asynchronously.
     * @param statsPane The PvP stats pane.
     */
    private void loadTestAsync(final PvPStatsPane statsPane) {
        final Service<PvpStat> service = new Service<PvpStat>() {
            @Override
            protected Task<PvpStat> createTask() {
                return new Task<PvpStat>() {
                    @Override
                    protected PvpStat call() throws Exception {
                        return (SABConstants.INSTANCE.isOffline()) ? loadLocalTest() : loadRemoteTest();
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final PvpStat stat = (PvpStat) workerStateEvent.getSource().getValue();
            statsPane.setStat(stat);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.start();
    }
    
    /**
     * Load remote test.
     * @return A {@code PvpStat} object may be null.
     */
    private PvpStat loadRemoteTest() {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        final Optional<PvpStat> result = WebQuery.INSTANCE.queryPvpStats(session.getAppKey());
        return result.orElse(null);
    }

    /**
     * Load local test.
     * @return A {@code PvpStat} object may be null.
     */
    private PvpStat loadLocalTest() throws NullPointerException, IOException {
        final URL url = getClass().getResource("stats.json");
        final PvpStat result = (url == null) ? null : JsonpContext.SAX.loadObject(PvpStat.class, url);
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
