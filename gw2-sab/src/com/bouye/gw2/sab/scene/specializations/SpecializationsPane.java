/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.specializations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
        IntStream.rangeClosed(0, 3)
                .forEach(index -> {
                    final SpecializationEditor editor = new SpecializationEditor();
                    editor.editableProperty().bind(editableProperty());
                    editors.add(editor);
                    content.getChildren().add(editor);
                });
        getChildren().setAll(content);
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

    private final List<SpecializationEditor> editors = new ArrayList<>();
    private final List<SpecializationEditor> editorsReadOnly = Collections.unmodifiableList(editors);

    public List<SpecializationEditor> getEditors() {
        return editorsReadOnly;
    }

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

}
