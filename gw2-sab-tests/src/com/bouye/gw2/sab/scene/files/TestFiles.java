/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.files;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.files.File;
import com.bouye.gw2.sab.query.ImageCache;
import java.net.URL;
import java.util.Collection;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public class TestFiles extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        final String query = "https://api.guildwars2.com/v2/files?ids=all";
        final Collection<File> files = JsonpContext.SAX.loadObjectArray(File.class, new URL(query));
        final FlowPane flowPane = new FlowPane();
        flowPane.getChildren()
                .setAll(files.stream().
                        map(file -> {
                            final Label label = new Label(file.getId());
                            final VBox result = new VBox();
                            result.setPadding(new Insets(10));
                            result.setSpacing(6);
                            final Image image = ImageCache.INSTANCE.getImage(file.getIcon().get().toExternalForm());
                            final ImageView imageView = new ImageView(image);
                            result.getChildren().setAll(label, imageView);
                            return result;
                        })
                        .collect(Collectors.toList()));
        final ScrollPane root = new ScrollPane();
        root.setFitToWidth(true);
        root.setContent(flowPane);
        Scene scene = new Scene(root, 600, 600);

        primaryStage.setTitle("TestFiles");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
