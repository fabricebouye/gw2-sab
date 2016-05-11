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
import api.web.gw2.mapping.v2.professions.ProfessionTrack;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

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
        final ComboBox<ProfessionTrack> trackCombo = new ComboBox<>();
        trackCombo.setButtonCell(new SimpleProfessionTackListCell());
        trackCombo.setCellFactory(listView -> new SimpleProfessionTackListCell());
        professionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            final Set<ProfessionTrack> tracks = (newValue == null) ? Collections.EMPTY_SET : newValue.getTraining();
            trackCombo.getItems().setAll(tracks);
            if (!tracks.isEmpty()) {
                trackCombo.getSelectionModel().select(0);
            }
            professionEditor.setProfession(newValue);
        });
        trackCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            professionEditor.setTrack(newValue);
        });
        final ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(professionCombo, trackCombo);
        final BorderPane root = new BorderPane();
        root.setTop(toolBar);
        root.setCenter(professionEditor);
        final Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("TestProfessionTrackEditor"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
//        ScenicView.show(root);
        loadTestAsync(professionCombo);
    }

    private void loadTestAsync(final ComboBox<Profession> professionCombo) {
        Service<Collection<Profession>> service = new Service<Collection<Profession>>() {
            @Override
            protected Task<Collection<Profession>> createTask() {
                return new Task<Collection<Profession>>() {
                    @Override
                    protected Collection<Profession> call() throws Exception {
                        final Collection<Profession> professions = SABConstants.INSTANCE.isOffline() ? loadLocalTest() : loadRemoteTest();
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

    private Collection<Profession> loadRemoteTest() {
        return WebQuery.INSTANCE.queryProfessions();
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
