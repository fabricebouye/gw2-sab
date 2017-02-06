/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.pvp.seasons;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.pvp.seasons.SeasonDivision;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestDivisionPane extends Application {

    @Override
    public void start(Stage primaryStage) throws NullPointerException, IOException {
        final SeasonDivision division = JsonpContext.SAX.loadObject(SeasonDivision.class, getClass().getResource("division.json")); // NOI18N.
        final DivisionPane divisionPane = new DivisionPane();
        divisionPane.setDivision(division);
        final StackPane root = new StackPane();
        root.setBackground(Background.EMPTY);
        root.getChildren().add(divisionPane);
        final Scene scene = new Scene(root);
        scene.setFill(Color.GRAY);
        primaryStage.setTitle("TestDivisionPane"); // NOI18N.
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
