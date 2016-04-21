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
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author fabriceb
 */
public final class TestWvWSummaryPane extends Application {

    @Override
    public void start(Stage primaryStage) {
        final WvwSummaryPane wvwSummaryPane = new WvwSummaryPane();
        final Optional<URL> matchURL = Optional.ofNullable(getClass().getResource("matches/match01.json")); // NOI18N.
        matchURL.ifPresent(url -> {
            try {
                final Match match = JsonpContext.SAX.loadObject(Match.class, url);
                wvwSummaryPane.setMatch(match);
            } catch (NullPointerException | IOException ex) {
                Logger.getLogger(TestWvWSummaryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
        final Optional<URL> worldsURL = Optional.ofNullable(getClass().getResource("matches/worlds01.json")); // NOI18N.
        worldsURL.ifPresent(url -> {
            try {
                final Collection<World> worlds = JsonpContext.SAX.loadObjectArray(World.class, url);
                wvwSummaryPane.getWorlds().setAll(worlds);
            } catch (NullPointerException | IOException ex) {
                Logger.getLogger(TestWvWSummaryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
//        final ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setFitToWidth(true);
//        scrollPane.setContent(wvwSummaryPane);
//        final StackPane root = new StackPane();
//        root.getChildren().add(scrollPane);
        final StackPane root = new StackPane();
        root.getChildren().add(wvwSummaryPane);
        final Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("TestWvWSummaryPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
    }

    public static void main(String... args) {
        launch(args);
    }
}
