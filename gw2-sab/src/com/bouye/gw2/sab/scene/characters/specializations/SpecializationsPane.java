/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.specializations;

import api.web.gw2.mapping.v2.characters.CharacterSpecialization;
import api.web.gw2.mapping.v2.specializations.Specialization;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Displays the specializations and traits of a character.
 * @author Fabrice Bouyé
 */
public final class SpecializationsPane extends Region {

    private final VBox content = new VBox();

    /**
     * Creates a new instance.
     */
    public SpecializationsPane() {
        super();
        setId("SpecializationsPane"); // NOI18N.
        getStyleClass().add("specializations-pane"); // NOI18N.
        setMaxWidth(VBox.USE_PREF_SIZE);
        setMaxHeight(VBox.USE_PREF_SIZE);
        //
        content.setMaxWidth(VBox.USE_PREF_SIZE);
        content.setMaxHeight(VBox.USE_PREF_SIZE);
        content.getChildren().setAll(
                IntStream.range(0, 3)
                .mapToObj(index -> {
                    final SpecializationEditor editor = new SpecializationEditor();
                    editor.editableProperty().bind(editableProperty());
                    editors.add(editor);
                    return editor;
                })
                .collect(Collectors.toList()));
        getChildren().setAll(content);
//        specializationPool.addListener(listener);
        build.addListener(buildChangeListener);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double width = getWidth();
        final double height = getHeight();
        final Insets insets = getInsets();
        final double areaX = insets.getLeft();
        final double areaY = insets.getTop();
        final double areaW = Math.max(0, width - (insets.getLeft() + insets.getRight()));
        final double areaH = Math.max(0, height - (insets.getTop() + insets.getBottom()));
        content.resizeRelocate(areaX, areaY, areaW, areaH);
    }
    ////////////////////////////////////////////////////////////////////////////

    private final ListChangeListener<CharacterSpecialization> buildChangeListener = change -> forwardBuildChange();

    ////////////////////////////////////////////////////////////////////////////
    private final List<SpecializationEditor> editors = new ArrayList<>();

    /**
     * Sets wether this control is editable.
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

    private ObservableList<Specialization> specializationPool = FXCollections.observableArrayList();

    public ObservableList<Specialization> getSpecializationPool() {
        return specializationPool;
    }

    private ObservableList<CharacterSpecialization> build = FXCollections.observableArrayList(null, null, null);

    public void setBuild1(final CharacterSpecialization specialization) {
        build.set(0, specialization);
    }

    public void setBuild2(final CharacterSpecialization specialization) {
        build.set(1, specialization);
    }

    public void setBuild3(final CharacterSpecialization specialization) {
        build.set(2, specialization);
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Called whenever the character build changes.
     */
    private void forwardBuildChange() {
        IntStream.range(0, build.size())
                .forEach(index -> {
                    final CharacterSpecialization charSpec = build.get(index);
                    final SpecializationEditor editor = editors.get(index);
                    if (charSpec == null) {
                        editor.setSpecialization(null);
                    } else {
                        final Optional<Specialization> specFound = getSpecializationPool()
                                .stream()
                                .filter(s -> s.getId() == charSpec.getId())
                                .findFirst();
                        final Specialization specialization = specFound.orElse(null);
                        editor.setSpecialization(specialization);
                        if (specialization != null) {
                            final Iterator<Integer> traitsIterator = charSpec.getTraits().iterator();
                            editor.setMajorTrait1(traitsIterator.next());
                            editor.setMajorTrait2(traitsIterator.next());
                            editor.setMajorTrait3(traitsIterator.next());
                        }
                    }
                });
    }

}
