/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.db.DBStorage;
import com.bouye.gw2.sab.text.LabelUtils;
import java.net.URL;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public class TestCoinRendering extends Application {

    @Override
    public void init() throws Exception {
        DBStorage.INSTANCE.init();
    }

    @Override
    public void start(final Stage primaryStage) {
        final int[] values = {0, 1, 100, 10000, 101, 10100, 10101, 10100};
        final GridPane root = new GridPane();
        IntStream.range(0, 3)
                .forEach(column -> {
                    final ColumnConstraints columnContraints = new ColumnConstraints();
                    columnContraints.setHgrow(Priority.ALWAYS);
                    root.getColumnConstraints().add(columnContraints);
                });
        IntStream.range(0, values.length)
                .forEach(row -> {
                    final RowConstraints rowContraints = new RowConstraints();
                    rowContraints.setVgrow(Priority.NEVER);
                    root.getRowConstraints().add(rowContraints);
                });
        IntStream.range(0, values.length)
                .forEach(row -> {
                    final int amout = values[row];
                    final Label label = new Label(String.valueOf(amout));
                    GridPane.setConstraints(label, 0, row);
                    final TextFlow textFlow1 = new TextFlow();
                    textFlow1.getChildren().setAll(LabelUtils.INSTANCE.labelsForCoins(amout, true));
                    GridPane.setConstraints(textFlow1, 1, row);
                    final TextFlow textFlow2 = new TextFlow();
                    textFlow2.getChildren().setAll(LabelUtils.INSTANCE.labelsForCoins(amout, false));
                    GridPane.setConstraints(textFlow2, 2, row);
                    root.getChildren().addAll(label, textFlow1, textFlow2);
                });
        {
            // Gems.
            final int row = values.length;
            final int amout = 1000;
            final Label label = new Label(String.valueOf(amout));
            GridPane.setConstraints(label, 0, row);
            final TextFlow textFlow1 = new TextFlow();
            textFlow1.getChildren().setAll(LabelUtils.INSTANCE.labelsForGems(amout));
            GridPane.setConstraints(textFlow1, 1, row);
            final TextFlow textFlow2 = new TextFlow();
            textFlow2.getChildren().setAll(LabelUtils.INSTANCE.labelsForGems(amout));
            GridPane.setConstraints(textFlow2, 2, row);
            root.getChildren().addAll(label, textFlow1, textFlow2);
        }
        final Scene scene = new Scene(root, 600, 600);
        final URL cssURL = SAB.class.getResource("styles/Styles.css"); // NOI18N.
        scene.getStylesheets().add(cssURL.toExternalForm());
        primaryStage.setTitle("TestCoinRendering"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
//        ScenicView.show(root);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
