/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.characters.Character;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.CharacterWrapper;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * FXML Controller class.
 * @author Fabrice Bouyé
 */
public final class CharactersListPaneController extends SABControllerBase<CharactersListPane> {
    
    @FXML
    private ListView<CharacterWrapper> charactersListView;
    
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        charactersListView.setCellFactory(listView -> new CharacterListCell());
        charactersListView.setItems(filteredCharacters);
    }
    
    private final InvalidationListener valueInvalidationListener = observable -> updateUI();
    
    @Override
    protected void uninstallNode(final CharactersListPane node) {
        node.sessionProperty().removeListener(valueInvalidationListener);
    }
    
    @Override
    protected void installNode(final CharactersListPane node) {
        node.sessionProperty().addListener(valueInvalidationListener);
    }
    
    @Override
    protected void updateUI() {
        if (characterLoadingService != null) {
            characterLoadingService.cancel();
        }
        final Optional<CharactersListPane> parent = parentNode();
        final Session session = parent.isPresent() ? parent.get().getSession() : null;
        if (session == null) {
            characters.clear();
        } else {
            loadCharactersAsync();
        }
    }
    
    private final ObservableList<CharacterWrapper> characters = FXCollections.observableArrayList();
    private final FilteredList<CharacterWrapper> filteredCharacters = new FilteredList<>(characters);
    
    private Service<Void> characterLoadingService;
    
    private void loadCharactersAsync() {
        if (characterLoadingService != null) {
            characterLoadingService.cancel();
        }
        final Optional<CharactersListPane> parent = parentNode();
        final Session session = parent.isPresent() ? parent.get().getSession() : null;
        if (session == null) {
            return;
        }
        if (characterLoadingService == null) {
            characterLoadingService = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new CharacterLoadingTask(session);
                }
            };
        }
        addAndStartService(characterLoadingService, "CharactersListPaneController::loadCharactersAsync");
    }

    /**
     * Load character info async.
     * @author Fabrice Bouyé
     */
    private final class CharacterLoadingTask extends Task<Void> {
        
        private final Session session;
        
        public CharacterLoadingTask(final Session session) {
            this.session = session;
        }
        
        @Override
        protected Void call() throws Exception {
            final String namesQuery = String.format("https://api.guildwars2.com/v2/characters?access_token=%s", session.getAppKey());
            final List<String> characterNames = JsonpContext.SAX.loadObjectArray(String.class, new URL(namesQuery))
                    .stream()
                    .collect(Collectors.toList());
            Collections.sort(characterNames);
            final List<CharacterWrapper> wrappers = characterNames.stream()
                    .map(characterName -> new CharacterWrapper(characterName))
                    .collect(Collectors.toList());
            Platform.runLater(() -> characters.setAll(wrappers));
            for (final CharacterWrapper wrapper : wrappers) {
                final String escapedCharacterName = WebQuery.INSTANCE.encodeURLParameter(wrapper.getName());
                final String characterQuery = String.format("https://api.guildwars2.com/v2/characters/%s?access_token=%s", escapedCharacterName, session.getAppKey());
                final Character character = JsonpContext.SAX.loadObject(Character.class, new URL(characterQuery));
                Platform.runLater(() -> wrapper.setCharacter(character));
            }
            return null;
        }
    }
}
