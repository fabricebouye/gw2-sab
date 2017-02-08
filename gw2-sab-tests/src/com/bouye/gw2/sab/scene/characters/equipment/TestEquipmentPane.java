/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.equipment;

import api.web.gw2.mapping.v2.characters.equipment.EquipmentResponse;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestEquipmentPane extends Application {

    final ComboBox<String> characterCombo = new ComboBox<>();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        characterCombo.valueProperty().addListener((observable, oldValue, newValue) -> loadEquipmentAsync(session, newValue));
        final EquipmentPane equipmentPane = new EquipmentPane();
        final BorderPane root = new BorderPane();
        root.setCenter(equipmentPane);
        root.setTop(characterCombo);
        final Scene scene = new Scene(root, 800, 700);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestEquipmentPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
        loadCharacterNamesAsync(session, characterCombo);
    }

    private Service<Void> characterNamesLoadingService;

    private void loadCharacterNamesAsync(final Session session, final ComboBox<String> characterCombo) {
        if (characterNamesLoadingService != null) {
            characterNamesLoadingService.cancel();
        }
        if (characterNamesLoadingService == null) {
            final Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new CharacterNamesLoadingTask(session, characterCombo);
                }
            };
            service.setOnFailed(workerStateEvent -> {
                final Throwable ex = workerStateEvent.getSource().getException();
                Logger.getLogger(TestEquipmentPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            });
            characterNamesLoadingService = service;
        }
        characterNamesLoadingService.restart();
    }

    private Service<Void> equipmentLoadingService;

    private void loadEquipmentAsync(final Session session, final String characterName) {
        if (equipmentLoadingService != null) {
            equipmentLoadingService.cancel();
        }
        if (equipmentLoadingService == null) {
            final Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new EquipmentLoadingTask(session, characterName);
                }
            };
            service.setOnFailed(workerStateEvent -> {
                final Throwable ex = workerStateEvent.getSource().getException();
                Logger.getLogger(TestEquipmentPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            });
            equipmentLoadingService = service;
        }
        equipmentLoadingService.restart();
    }

    /**
     * Load character info async.
     * @author Fabrice Bouyé
     */
    private final class CharacterNamesLoadingTask extends Task<Void> {

        private final Session session;
        private final ComboBox<String> characterCombo;

        public CharacterNamesLoadingTask(final Session session, final ComboBox<String> characterCombo) {
            this.session = session;
            this.characterCombo = characterCombo;
        }

        @Override
        protected Void call() throws Exception {
            final List<String> characterNames = new ArrayList(WebQuery.INSTANCE.queryCharacterNames(session.getAppKey()));
            Collections.sort(characterNames);
            Platform.runLater(() -> characterCombo.getItems().setAll(characterNames));
            return null;
        }
    }

    /**
     * Load character info async.
     * @author Fabrice Bouyé
     */
    private final class EquipmentLoadingTask extends Task<Void> {

        private final Session session;
        private final String characterName;

        public EquipmentLoadingTask(final Session session, final String characterName) {
            this.session = session;
            this.characterName = characterName;
        }

        @Override
        protected Void call() throws Exception {
            final Optional<EquipmentResponse> response = WebQuery.INSTANCE.queryCharacterEquipment(session.getAppKey(), characterName);
            final Optional<api.web.gw2.mapping.v2.characters.Character> character = WebQuery.INSTANCE.queryCharacter(session.getAppKey(), characterName);
            final List<String> characterNames =  WebQuery.INSTANCE.queryCharacterNames(session.getAppKey());
            final List<api.web.gw2.mapping.v2.characters.Character> characters = WebQuery.INSTANCE.queryCharacters(session.getAppKey(), characterNames.toArray(new String[0]));
            return null;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
