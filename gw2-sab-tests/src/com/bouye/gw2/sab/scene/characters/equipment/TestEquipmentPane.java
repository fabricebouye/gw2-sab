/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.equipment;

import com.bouye.gw2.sab.SAB;
import java.net.URL;
import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestEquipmentPane extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final EquipmentPane equipmentPane = new EquipmentPane();
        final BorderPane root = new BorderPane();
        root.setCenter(equipmentPane);
        final Scene scene = new Scene(root, 800, 700);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestEquipmentPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
    }
}
