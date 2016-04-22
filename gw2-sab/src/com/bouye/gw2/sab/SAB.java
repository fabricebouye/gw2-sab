/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab;

import com.bouye.gw2.sab.db.DBStorage;
import com.bouye.gw2.sab.views.welcome.WelcomeView;
import java.net.URL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * App class.
 * @author Fabrice Bouyé
 */
public final class SAB extends Application {

    @Override
    public void init() throws Exception {
        DBStorage.INSTANCE.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        final WelcomeView welcomeView = new WelcomeView();
        final StackPane root = new StackPane();
        root.getChildren().setAll(welcomeView);
        final Scene scene = new Scene(root);
        final URL cssURL = getClass().getResource("styles/Styles.css"); // NOI18N.
        scene.getStylesheets().add(cssURL.toExternalForm());
        stage.setTitle(SABConstants.I18N.getString("app.title")); // NOI18N.
        stage.setScene(scene);
        stage.show();
//        ScenicView.show(root);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
