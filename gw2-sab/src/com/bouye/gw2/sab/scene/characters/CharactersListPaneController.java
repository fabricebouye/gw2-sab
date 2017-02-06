/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import api.web.gw2.mapping.v2.characters.Character;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.CharacterWrapper;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class.
 * @author Fabrice Bouyé
 */
public final class CharactersListPaneController extends SABControllerBase<CharactersListPane> {

    @FXML
    private TextField searchField;
    @FXML
    private ListView<CharacterWrapper> charactersListView;

    /**
     * Creates a new instance.
     */
    public CharactersListPaneController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        charactersListView.setCellFactory(listView -> {
            final CharacterListCell result = new CharacterListCell();
            result.onSelectProperty().bind(onSelect);
            return result;
        });
        charactersListView.setItems(filteredCharacters);
        searchField.textProperty().addListener(searchTextInvalidationListener);
    }

    @Override
    protected void uninstallNode(final CharactersListPane node) {
        onSelect.unbind();
        characters.set(null);
    }

    @Override
    protected void installNode(final CharactersListPane node) {
        onSelect.bind(node.onSelectProperty());
        characters.set(node.getCharacters());
    }

    private final ObjectProperty<Consumer<CharacterWrapper>> onSelect = new SimpleObjectProperty<>(this, "onSelect", null); // NOI18N.

    /**
     * Called whenever the search text is invalidated.
     */
    private final InvalidationListener searchTextInvalidationListener = observable -> applySearchFilter();

    private final ListProperty<CharacterWrapper> characters = new SimpleListProperty<>(this, "characters"); // NOI18N.
    private final FilteredList<CharacterWrapper> filteredCharacters = new FilteredList<>(characters);

    /**
     * Apply filter from the search box.
     */
    private void applySearchFilter() {
        final String searchValue = searchField.getText();
        Predicate<CharacterWrapper> predicate = null;
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            final String criteria = searchValue.trim().toLowerCase();
            predicate = characterWrapper -> {
                boolean result = false;
                result |= characterWrapper.getName().toLowerCase().contains(criteria);
                if (characterWrapper.getCharacter() != null) {
                    final Character character = characterWrapper.getCharacter();
                    result |= character.getProfession().name().toLowerCase().contains(criteria);
                    // @todo Search on localized profession name.
                    result |= String.valueOf(character.getLevel()).contains(criteria);
                }
                return result;
            };
        }
        filteredCharacters.setPredicate(predicate);
    }
}
