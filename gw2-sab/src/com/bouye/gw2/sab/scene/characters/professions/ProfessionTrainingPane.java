/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.professions;

import api.web.gw2.mapping.core.EnumValueFactory;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import api.web.gw2.mapping.v2.professions.Profession;
import api.web.gw2.mapping.v2.professions.ProfessionTrack;
import api.web.gw2.mapping.v2.professions.ProfessionTrackCategory;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.text.LabelUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Profession details.
 * @author Fabrice Bouyé
 */
public final class ProfessionTrainingPane extends HBox {

    private final VBox categoryVBox = new VBox();
    private final ScrollPane categoryScroll = new ScrollPane(categoryVBox);
    private final Label editorLabel = new Label();
    private final ProfessionTrackEditor trackEditor = new ProfessionTrackEditor();
    private final VBox editorVBox = new VBox(editorLabel, trackEditor);
    private final ToggleGroup categoryToggleGroup = new ToggleGroup();

    /**
     * Creates a new instance.
     */
    public ProfessionTrainingPane() {
        super();
        setId("professionTrainingPane"); // NOI18N.
        getStyleClass().add("profession-training-pane"); // NOI18N.
        //
        categoryVBox.setId("categoryVBox"); // NOI18N.
        //
        categoryScroll.setId("sideScroll"); // NOI18N.
        categoryScroll.setFitToWidth(true);
        categoryScroll.setFitToHeight(true);
        HBox.setHgrow(categoryScroll, Priority.ALWAYS);
        //
        trackEditor.setId("trackEditor"); // NOI18N.
        trackEditor.trackProperty().bind(trackProperty());
        //
        editorLabel.setId("editorLabel");
        editorLabel.setMaxWidth(Double.MAX_VALUE);
        //
        editorVBox.setId("editorVBox");
        HBox.setHgrow(editorVBox, Priority.NEVER);
        //
        getChildren().addAll(categoryScroll, editorVBox);
        //
        categoryToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            final ProfessionTrack track = (newValue == null) ? null : (ProfessionTrack) newValue.getUserData();
            editorLabel.setText(track == null ? null : track.getName() + " - " + track.getTrack().size());
            setTrack(track);
        });
        //
        profession.addListener(professionChangeListener);
    }
    
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/professions/ProfessionTrainingPane.css"); // NOI18N.
        return url.toExternalForm();
    }    

    ////////////////////////////////////////////////////////////////////////////    
    /**
     * Called whenever the profession changes.
     */
    private final ChangeListener<Profession> professionChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallProfession);
        Optional.ofNullable(newValue)
                .ifPresent(this::installProfession);
    };

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Uninstall a profession.
     * @param profession The profession, never {@code null}.
     */
    private void uninstallProfession(final Profession profession) {
        final CharacterProfession cProfession = EnumValueFactory.INSTANCE.mapEnumValue(CharacterProfession.class, profession.getId());
        final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(cProfession);
        pseudoClassStateChanged(pseudoClass, false);
        trackEditor.setProfession(null);
        categoryVBox.getChildren().clear();
        categoryToggleGroup.getToggles().clear();
        setTrack(null);
    }

    /**
     * Install a profession.
     * @param profession The profession, never {@code null}.
     */
    private void installProfession(final Profession profession) {
        final CharacterProfession cProfession = EnumValueFactory.INSTANCE.mapEnumValue(CharacterProfession.class, profession.getId());
        final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(cProfession);
        pseudoClassStateChanged(pseudoClass, true);
        trackEditor.setProfession(null);
        categoryVBox.getChildren().setAll(
                Arrays.stream(ProfessionTrackCategory.values())
                .filter(category -> category != ProfessionTrackCategory.UNKNOWN)
                .map(category -> {
                    final VBox contentBox = new VBox();
                    contentBox.getChildren().setAll(
                            profession.getTraining()
                            .stream()
                            .filter(track -> track.getCategory() == category)
                            .map(track -> {
                                final RadioButton button = new RadioButton(track.getName());
                                button.setUserData(track);
                                button.setToggleGroup(categoryToggleGroup);
                                return button;
                            })
                            .collect(Collectors.toList()));
                    final TitledPane categoryPane = new TitledPane();
                    // @todo Localize.
                    categoryPane.setText(category.name());
                    categoryPane.setContent(contentBox);
                    return categoryPane;
                })
                .collect(Collectors.toList()));
        if (!categoryToggleGroup.getToggles().isEmpty()) {
            categoryToggleGroup.selectToggle(categoryToggleGroup.getToggles().get(0));
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Sets whether this control is editable.
     */
    private final BooleanProperty editable = new SimpleBooleanProperty(this, "editable", true); // NOI18N.

    public final boolean isEditable() {
        return editable.get();
    }

    public final void setEditable(final boolean value) {
        editable.set(value);
    }

    public final BooleanProperty editableProperty() {
        return editable;
    }

    private final ReadOnlyObjectWrapper<Profession> profession = new ReadOnlyObjectWrapper<>(this, "profession", null); // NOI18N.

    public final Profession getProfession() {
        return profession.get();
    }

    public void setProfession(final Profession value) {
        profession.set(value);
    }

    public final ReadOnlyObjectProperty<Profession> professionProperty() {
        return profession.getReadOnlyProperty();
    }

    private final ReadOnlyObjectWrapper<ProfessionTrack> track = new ReadOnlyObjectWrapper<>(this, "track", null);

    public ProfessionTrack getTrack() {
        return track.get();
    }

    void setTrack(final ProfessionTrack value) {
        track.set(value);
    }

    public ReadOnlyObjectProperty<ProfessionTrack> trackProperty() {
        return track.getReadOnlyProperty();
    }
}
