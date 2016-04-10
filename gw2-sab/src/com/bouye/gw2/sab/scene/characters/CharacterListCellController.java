/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import api.web.gw2.mapping.core.JsonpUtils;
import api.web.gw2.mapping.v2.characters.Character;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.wrappers.CharacterWrapper;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
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
public final class CharacterListCellController extends SABControllerBase<CharacterListCell> {

    @FXML
    private Label nameLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label professionLabel;

    /**
     * Creates a new instance.
     */
    public CharacterListCellController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        nameLabel.setText(null);
        professionLabel.setText(null);
    }

    /**
     * Called whenever a value is invalidated.
     */
    private final InvalidationListener valueInvalidationListener = observable -> updateUI();

    private ObjectBinding<Character> characterBindging;

    @Override
    protected void uninstallNode(final CharacterListCell node) {
        node.itemProperty().removeListener(valueInvalidationListener);
        characterBindging.removeListener(valueInvalidationListener);
        characterBindging.dispose();
        characterBindging = null;
    }

    @Override
    protected void installNode(final CharacterListCell node) {
        node.itemProperty().addListener(valueInvalidationListener);
        characterBindging = Bindings.select(node.itemProperty(), "character");
        characterBindging.addListener(valueInvalidationListener);
    }

    @Override
    protected void updateUI() {
        final Optional<CharacterListCell> parent = parentNode();
        parent.ifPresent(this::clearOldStyle);
        final CharacterWrapper wrapper = parent.isPresent() ? parent.get().getItem() : null;
        final Character character = (characterBindging == null) ? null : characterBindging.get();
        if (wrapper == null) {
            nameLabel.setText(null);
            professionLabel.setText(null);
            levelLabel.setText(null);
        } else {
            final String name = wrapper.getName();
            nameLabel.setText(name);
            String level = null;
            String profession = null;
            if (character != null) {
                level = String.valueOf(character.getLevel());
                profession = character.getProfession().name();
            }
            levelLabel.setText(level);
            professionLabel.setText(profession);
            parent.ifPresent(this::installNewStyle);
        }
    }

    private void clearOldStyle(final CharacterListCell parent) {
        Arrays.stream(CharacterProfession.values())
                .filter(profession -> profession != CharacterProfession.UNKNOWN)
                .forEach(profession -> {
                    final PseudoClass professionPseudoClass = PseudoClass.getPseudoClass(JsonpUtils.INSTANCE.javaEnumToJavaClassName(profession));
                    parent.pseudoClassStateChanged(professionPseudoClass, false);
                });
    }

    private void installNewStyle(final CharacterListCell parent) {
        final Character character = (characterBindging == null) ? null : characterBindging.get();
        if (character != null) {
            final CharacterProfession profession = character.getProfession();
            final PseudoClass professionPseudoClass = PseudoClass.getPseudoClass(JsonpUtils.INSTANCE.javaEnumToJavaClassName(profession));
            parent.pseudoClassStateChanged(professionPseudoClass, true);
        }
    }

    @FXML
    private void handleActionButton() {
    }
}
