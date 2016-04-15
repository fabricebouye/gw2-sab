/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.commerce.exchange;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestGemExchangePane extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        final GemExchangePane gemExchangePane = new GemExchangePane();
        final StackPane root = new StackPane();
        root.getChildren().add(gemExchangePane);
        final Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("TestGemExchangePane");
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
