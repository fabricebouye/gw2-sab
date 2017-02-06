/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import api.web.gw2.mapping.core.JsonpUtils;
import api.web.gw2.mapping.v2.characters.Character;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.text.LabelUtils;
import com.bouye.gw2.sab.wrappers.CharacterWrapper;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class CharacterListRendererController extends SABControllerBase<CharacterListRenderer> {

    @FXML
    private Label nameLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label professionLabel;
    @FXML
    private Label raceLabel;

    /**
     * Creates a new instance.
     */
    public CharacterListRendererController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        nameLabel.setText(null);
        levelLabel.setText(null);
        professionLabel.setText(null);
        raceLabel.setText(null);
    }

    /**
     * Called whenever a value is invalidated.
     */
    private final InvalidationListener valueInvalidationListener = observable -> updateUI();

    private ObjectBinding<Character> characterBindging;

    @Override
    protected void uninstallNode(final CharacterListRenderer node) {
        node.characterProperty().removeListener(valueInvalidationListener);
        characterBindging.removeListener(valueInvalidationListener);
        characterBindging.dispose();
        characterBindging = null;
    }

    @Override
    protected void installNode(final CharacterListRenderer node) {
        node.characterProperty().addListener(valueInvalidationListener);
        characterBindging = Bindings.select(node.characterProperty(), "character");
        characterBindging.addListener(valueInvalidationListener);
    }

    @Override
    protected void updateUI() {
        final Optional<CharacterListRenderer> parent = parentNode();
        parent.ifPresent(this::clearOldStyle);
        final CharacterWrapper wrapper = parent.isPresent() ? parent.get().getCharacter() : null;
        final Character character = (characterBindging == null) ? null : characterBindging.get();
        if (wrapper == null) {
            nameLabel.setText(null);
            levelLabel.setText(null);
            professionLabel.setText(null);
            raceLabel.setText(null);
        } else {
            final String name = wrapper.getName();
            nameLabel.setText(name);
            String level = null;
            String profession = null;
            String race = null;
            if (character != null) {
                level = String.valueOf(character.getLevel());
                profession = character.getProfession().name();
                race = character.getRace().name();
            }
            levelLabel.setText(level);
            professionLabel.setText(profession);
            raceLabel.setText(race);
            parent.ifPresent(this::installNewStyle);
        }
    }

    private void clearOldStyle(final CharacterListRenderer parent) {
        Arrays.stream(CharacterProfession.values())
                .filter(profession -> profession != CharacterProfession.UNKNOWN)
                .forEach(profession -> {
                    final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(profession);
                    parent.pseudoClassStateChanged(pseudoClass, false);
                });
    }

    private void installNewStyle(final CharacterListRenderer parent) {
        final Optional<Character> character = Optional.ofNullable((characterBindging == null) ? null : characterBindging.get());
        character.ifPresent(c -> {
            final CharacterProfession profession = c.getProfession();
            final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(profession);
            parent.pseudoClassStateChanged(pseudoClass, true);
        });
    }

    @FXML
    private void handleActionButton() {
        final Optional<CharacterListRenderer> parent = parentNode();
        parent.ifPresent(n -> {
            final Optional<CharacterWrapper> item = Optional.ofNullable(n.getCharacter());
            final Optional<Consumer<CharacterWrapper>> onSelect = Optional.ofNullable(n.getOnSelect());
            if (item.isPresent() && onSelect.isPresent()) {
                onSelect.get().accept(item.get());
            }
        });
    }
}
