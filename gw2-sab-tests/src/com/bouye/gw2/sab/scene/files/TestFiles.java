/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.files;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.files.File;
import com.bouye.gw2.sab.db.DBStorage;
import com.bouye.gw2.sab.query.ImageCache;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public class TestFiles extends Application {

    @Override
    public void init() throws Exception {
        DBStorage.INSTANCE.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final String query = "https://api.guildwars2.com/v2/files?ids=all";
        final Collection<File> files = JsonpContext.SAX.loadObjectArray(File.class, new URL(query));
        final HBox hbox = new HBox();
        // Gets files directly from the web api.
        {
            final FlowPane flowPane = new FlowPane();
            flowPane.getChildren()
                    .setAll(files.stream()
                            .map(file -> {
                                final Image image = ImageCache.INSTANCE.getImage(file.getIcon().get().toExternalForm());
                                return nodeForFile(file.getId(), image);
                            })
                            .collect(Collectors.toList()));
            final VBox vbox = new VBox();
            vbox.getChildren().setAll(new Label("Remote"), flowPane);
            HBox.setHgrow(vbox, Priority.ALWAYS);
            hbox.getChildren().add(vbox);
        }
        // Gets files stored in the local DB.
        {
            final FlowPane flowPane = new FlowPane();
            flowPane.getChildren()
                    .setAll(files.stream()
                            .map(file -> {
                                final Image image = ImageCache.INSTANCE.getImage(file.getId());
                                return nodeForFile(file.getId(), image);
                            })
                            .collect(Collectors.toList()));
            final VBox vbox = new VBox();
            vbox.getChildren().setAll(new Label("Local"), flowPane);
            HBox.setHgrow(vbox, Priority.ALWAYS);
            hbox.getChildren().add(vbox);
        }
        final ScrollPane root = new ScrollPane();
        root.setFitToWidth(true);
        root.setContent(hbox);
        final Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("TestFiles");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node nodeForFile(final String id, final Image image) {
        final Label label = new Label(id);
        final ImageView imageView = new ImageView(image);
        final VBox result = new VBox();
        result.setPadding(new Insets(10));
        result.setSpacing(6);
        result.getChildren().setAll(label, imageView);
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
