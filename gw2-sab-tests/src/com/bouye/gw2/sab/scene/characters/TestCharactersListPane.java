/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import api.web.gw2.mapping.v2.characters.Character;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.CharacterWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestCharactersListPane extends Application {

    @Override
    public void start(Stage primaryStage) throws NullPointerException, IOException {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        final CharactersListPane charactersListPane = new CharactersListPane();
        charactersListPane.setOnSelect(character -> System.out.println(character.getName()));
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
        loadCharactersAsync(session, charactersListPane);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private Service<Void> characterLoadingService;

    private void loadCharactersAsync(final Session session, final CharactersListPane charactersListPane) {
        if (characterLoadingService != null) {
            characterLoadingService.cancel();
        }
        if (characterLoadingService == null) {
            characterLoadingService = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new CharacterLoadingTask(session, charactersListPane);
                }
            };
        }
        characterLoadingService.restart();
    }

    /**
     * Load character info async.
     * @author Fabrice Bouyé
     */
    private final class CharacterLoadingTask extends Task<Void> {

        private final Session session;
        private final CharactersListPane charactersListPane;

        public CharacterLoadingTask(final Session session, final CharactersListPane charactersListPane) {
            this.session = session;
            this.charactersListPane = charactersListPane;
        }

        @Override
        protected Void call() throws Exception {
            final List<String> characterNames = new ArrayList(WebQuery.INSTANCE.queryCharacterNames(session.getAppKey()));
            Collections.sort(characterNames);
            final List<CharacterWrapper> wrappers = characterNames.stream()
                    .map(CharacterWrapper::new)
                    .collect(Collectors.toList());
            // Load wrappers straigth into list.
            Platform.runLater(() -> charactersListPane.getCharacters().setAll(wrappers));
            // Nothing to do.
            if (wrappers.isEmpty()) {
                return null;
            }
            final String[] characterIds = characterNames.stream()
                    .toArray(String[]::new);
            // Upload all characters at once (previous test did individual requests which was slow.
            // @todo We may want to do page queries for account with huge numbers of characters.
            final Map<String, Character> characterMap = WebQuery.INSTANCE.queryCharacters(session.getAppKey(), characterIds)
                    .stream()
                    .collect(Collectors.toMap(Character::getName, Function.identity()));
            if (characterMap.isEmpty()) {
                return null;
            }
            // Now update wrappers.
            wrappers.stream()
                    .forEach(wrapper -> {
                        final String characterName = wrapper.getName();
                        final Character character = characterMap.get(characterName);
                        Platform.runLater(() -> wrapper.setCharacter(character));
                    });
            return null;
        }
    }
}
