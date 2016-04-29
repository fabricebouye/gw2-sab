/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import api.web.gw2.mapping.v2.items.ItemRarity;
import com.bouye.gw2.sab.SAB;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestItemTooltipRenderer extends Application {

    @Override
    public void start(final Stage primaryStage) {
        // Initialize all the sessions.
        final List<?> items = Arrays.stream(ItemRarity.values())
                .filter(rarity -> rarity != ItemRarity.UNKNOWN)
                .collect(Collectors.toList());
        // Left pane: all renderers.
        final VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.getChildren().setAll(items
                .stream()
                .map(item -> {
                    final ItemTooltipRenderer renderer = new ItemTooltipRenderer();
                    return renderer;
                })
                .collect(Collectors.toList()));
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(vbox);
        //
        final SplitPane splitPane = new SplitPane();
        splitPane.getItems().setAll(scrollPane);
        final BorderPane root = new BorderPane();
        root.setCenter(splitPane);
        final Scene scene = new Scene(root, 800, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestItemTooltipRenderer"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
//        Platform.runLater(() -> splitPane.setDividerPositions(0.33, 0.66));
//        ScenicView.show(root);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
