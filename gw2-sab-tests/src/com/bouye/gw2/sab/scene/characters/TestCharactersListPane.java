/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public class TestCharactersListPane extends Application {
    
    @Override
    public void start(Stage primaryStage) throws NullPointerException, IOException {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        final CharactersListPane charactersListPane = new CharactersListPane();
        charactersListPane.setSession(session);
        final StackPane root = new StackPane();
        root.setBackground(Background.EMPTY);
        root.getChildren().add(charactersListPane);
        final Scene scene = new Scene(root);
        final URL cssURL = SAB.class.getResource("styles/Styles.css"); // NOI18N.
        scene.getStylesheets().add(cssURL.toExternalForm());
        primaryStage.setTitle("TestCharacterListPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(root);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
