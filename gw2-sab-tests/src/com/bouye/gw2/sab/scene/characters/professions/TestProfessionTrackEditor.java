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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
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
        final StackPane root = new StackPane();
        root.getChildren().add(professionEditor);
        final Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("TestProfessionTrackEditor"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        loadTestAsync(professionEditor);
    }

    private void loadTestAsync(final ProfessionTrackEditor editor) {
        Service<Profession> service = new Service<Profession>() {
            @Override
            protected Task<Profession> createTask() {
                return new Task<Profession>() {
                    @Override
                    protected Profession call() throws Exception {
                        final Profession profession = loadLocalTest();
                        return profession;
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final Profession profession = (Profession) workerStateEvent.getSource().getValue();
            editor.setProfession(profession);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.start();
    }

    private Profession loadLocalTest() throws IOException {
        final URL url = getClass().getResource("professions.json");
        Profession result = null;
        if (url != null) {
            final Collection<Profession> professions = JsonpContext.SAX.loadObjectArray(Profession.class, url);
            result = professions.isEmpty() ? null : professions.iterator().next();
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
