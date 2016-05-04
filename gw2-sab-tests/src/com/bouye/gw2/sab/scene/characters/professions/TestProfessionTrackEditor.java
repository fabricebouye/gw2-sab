/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.professions;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.professions.Profession;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestProfessionTrackEditor extends Application {

    @Override
    public void start(Stage primaryStage) {
        final ProfessionTrackEditor professionEditor = new ProfessionTrackEditor();
        final ComboBox<Profession> professionCombo = new ComboBox<>();
        professionCombo.setButtonCell(new SimpleProfessionListCell());
        professionCombo.setCellFactory(listView -> new SimpleProfessionListCell());
        professionCombo.valueProperty().addListener((observable, oldValue, newValue) -> professionEditor.setProfession(newValue));
        final StackPane root = new StackPane();
        root.getChildren().add(professionEditor);
        final Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("TestProfessionTrackEditor"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        loadTestAsync(professionCombo);
    }

    private void loadTestAsync(final ComboBox<Profession> professionCombo) {
        Service<Collection<Profession>> service = new Service<Collection<Profession>>() {
            @Override
            protected Task<Collection<Profession>> createTask() {
                return new Task<Collection<Profession>>() {
                    @Override
                    protected Collection<Profession> call() throws Exception {
                        final Collection<Profession> professions = loadLocalTest();
                        return professions;
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final Collection<Profession> profession = (Collection<Profession>) workerStateEvent.getSource().getValue();
            professionCombo.getItems().setAll(profession);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.start();
    }

    private Collection<Profession> loadLocalTest() throws IOException {
        final URL url = getClass().getResource("professions.json");
        Collection<Profession> result = Collections.EMPTY_LIST;
        if (url != null) {
            result = JsonpContext.SAX.loadObjectArray(Profession.class, url);
        }
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
