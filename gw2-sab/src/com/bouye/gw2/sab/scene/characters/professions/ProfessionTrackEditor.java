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
import api.web.gw2.mapping.v2.professions.ProfessionTrackCost;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.text.LabelUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * Allows to train a profession.
 * @author Fabrice Bouyé
 */
public final class ProfessionTrackEditor extends Region {

    private final ImageView background = new ImageView();
    private final Arc arc = new Arc();

    /**
     * Creates a new instance.
     */
    public ProfessionTrackEditor() {
        super();
        setId("ProfessionTrackEditor"); // NOI18N.
        getStyleClass().add("profession-track-editor"); // NOI18N.
        //
        background.setId("background");
        background.setPreserveRatio(false);
        //
        arc.setId("arc"); // NOI18N.
        arc.setStartAngle(80);
        arc.setLength(-340);
        arc.setFill(null);
        arc.setStroke(Color.BLACK);
        arc.setVisible(false);
        //
        getChildren().setAll(background,
                arc);
        //
        trackProperty().addListener(trackChangeListener);
        profession.addListener(professionChangeListener);
        tickLengthProperty().addListener(observable -> requestLayout());
        tickLabelGapProperty().addListener(observable -> requestLayout());
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
        //
        background.setLayoutX(areaX);
        background.setLayoutY(areaY);
        background.setFitWidth(areaW - 1);
        background.setFitHeight(areaH - 1);
        //
        double centerX = areaX + areaW / 2d;
        double centerY = areaY + areaH / 2d;
        double radius = Math.min(areaW, areaH) / 2d / 2d;
        arc.setCenterX(centerX);
        arc.setCenterY(centerY);
        arc.setRadiusX(radius);
        arc.setRadiusY(radius);
        //
        if (!ticks.isEmpty()) {
            final int costsNumber = ticks.size();
            final double startAngle = arc.getStartAngle();
            final double length = arc.getLength();
            final double tickLength = getTickLength();
            final double tickLabelGap = getTickLabelGap();
            final double delta = length / Math.max(1, costsNumber - 1);
            IntStream.range(0, costsNumber)
                    .forEach(index -> {
                        final double angle = 2 * Math.PI * ((startAngle + index * delta) / 360d);
                        final double startX = centerX + radius * Math.cos(angle);
                        final double startY = centerY - radius * Math.sin(angle);
                        final double endX = centerX + (radius + tickLength) * Math.cos(angle);
                        final double endY = centerY - (radius + tickLength) * Math.sin(angle);
                        final Line tick = ticks.get(index);
                        tick.setStartX(startX);
                        tick.setStartY(startY);
                        tick.setEndX(endX);
                        tick.setEndY(endY);
                        final double unlockX = centerX + (radius + tickLength + tickLabelGap) * Math.cos(angle);
                        final double unlockY = centerY - (radius + tickLength + tickLabelGap) * Math.sin(angle);
                        final StackPane unlockStack = unlocks.get(index);
                        unlockStack.relocate(unlockX - unlockStack.getPrefWidth() / 2d, unlockY - unlockStack.getPrefHeight() / 2d);
                    });
        }
    }

    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/professions/ProfessionTrackEditor.css"); // NOI18N.
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

    /**
     * Called whenever the track changes.
     */
    private final ChangeListener<ProfessionTrack> trackChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallProfessionTrack);
        Optional.ofNullable(newValue)
                .ifPresent(this::installProfessionTrack);
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
    }

    /**
     * Install a profession.
     * @param profession The profession, never {@code null}.
     */
    private void installProfession(final Profession profession) {
        final CharacterProfession cProfession = EnumValueFactory.INSTANCE.mapEnumValue(CharacterProfession.class, profession.getId());
        final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(cProfession);
        pseudoClassStateChanged(pseudoClass, true);
    }

    private final List<Line> ticks = new LinkedList();
    private final List<StackPane> unlocks = new LinkedList();

    /**
     * Uninstall a profession track.
     * @param track The profession track, never {@code null}.
     */
    private void uninstallProfessionTrack(final ProfessionTrack track) {
        arc.setVisible(false);
        getChildren().removeAll(ticks);
        ticks.clear();
        getChildren().removeAll(unlocks);
        unlocks.clear();
    }

    /**
     * Uninstall a profession track.
     * @param track The profession track, never {@code null}.
     */
    private void installProfessionTrack(final ProfessionTrack track) {
        arc.setVisible(true);
        final Set<ProfessionTrackCost> costs = track.getTrack();
        final Iterator<ProfessionTrackCost> costIterator = costs.iterator();
        final int costsNumber = costs.size();
        IntStream.range(0, costsNumber)
                .forEach(index -> {
                    final ProfessionTrackCost cost = costIterator.next();
                    final Line tick = new Line();
                    ticks.add(tick);
                    final StackPane unlockStack = new StackPane();
                    unlockStack.setId("unlockStack"); // NOI18N.
                    unlockStack.getStyleClass().add("unlock-stack"); // NOI18N.
                    unlocks.add(unlockStack);
                    final Circle circle = new Circle();
                    circle.setRadius(5);
                    circle.setFill(Color.RED);
                });
        getChildren().addAll(ticks);
        getChildren().addAll(unlocks);
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

    private final ObjectProperty<ProfessionTrack> track = new SimpleObjectProperty<>(this, "track", null);

    public final ProfessionTrack getTrack() {
        return track.get();
    }

    public final void setTrack(final ProfessionTrack value) {
        track.set(value);
    }

    public final ObjectProperty<ProfessionTrack> trackProperty() {
        return track;
    }

    ////////////////////////////////////////////////////////////////////////////
    /** 
     * Default tick length (in pixels).
     */
    private static final double DEFAULT_TICK_LENGTH = 30;

    private StyleableDoubleProperty tickLength;

    public final double getTickLength() {
        return (tickLength == null) ? DEFAULT_TICK_LENGTH : tickLength.get();
    }

    public final void setTickLength(final double value) {
        tickLengthProperty().set(value);
    }

    public final StyleableDoubleProperty tickLengthProperty() {
        if (tickLength == null) {
            tickLength = new SimpleStyleableDoubleProperty(StyleableProperties.TICK_LENGTH_CSS_META_DATA, this, "tickLength", DEFAULT_TICK_LENGTH); // NOI18N.
        }
        return tickLength;
    }

    /** 
     * Default tick label gap (in pixels).
     */
    private static final double DEFAULT_TICK_LABEL_GAP = 45;

    private StyleableDoubleProperty tickLabelGap;

    public final double getTickLabelGap() {
        return (tickLabelGap == null) ? DEFAULT_TICK_LABEL_GAP : tickLabelGap.get();
    }

    public final void setTickLabelGap(final double value) {
        tickLabelGapProperty().set(value);
    }

    public final StyleableDoubleProperty tickLabelGapProperty() {
        if (tickLabelGap == null) {
            tickLabelGap = new SimpleStyleableDoubleProperty(StyleableProperties.TICK_LABEL_GAP_CSS_META_DATA, this, "tickLabelGap", DEFAULT_TICK_LABEL_GAP); // NOI18N.
        }
        return tickLabelGap;
    }

    ////////////////////////////////////////////////////////////////////////////
    /** 
     * Styleable properties support.
     * @author Fabrice Bouyé
     */
    private static final class StyleableProperties {

        /**
         * Tick length CSS meta data.
         */
        private static final CssMetaData<ProfessionTrackEditor, Number> TICK_LENGTH_CSS_META_DATA = new CssMetaData<ProfessionTrackEditor, Number>("-fx-tick-length", StyleConverter.getSizeConverter(), DEFAULT_TICK_LENGTH) { // NOI18N.
            @Override
            public boolean isSettable(final ProfessionTrackEditor styleable) {
                return (styleable.tickLength == null) || !styleable.tickLength.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(final ProfessionTrackEditor styleable) {
                return styleable.tickLengthProperty();
            }
        };

        /**
         * Tick label gap CSS meta data.
         */
        private static final CssMetaData<ProfessionTrackEditor, Number> TICK_LABEL_GAP_CSS_META_DATA = new CssMetaData<ProfessionTrackEditor, Number>("-fx-tick-label-gap", StyleConverter.getSizeConverter(), DEFAULT_TICK_LABEL_GAP) { // NOI18N.
            @Override
            public boolean isSettable(final ProfessionTrackEditor styleable) {
                return (styleable.tickLabelGap == null) || !styleable.tickLabelGap.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(final ProfessionTrackEditor styleable) {
                return styleable.tickLabelGapProperty();
            }
        };
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Region.getClassCssMetaData());
            styleables.add(TICK_LENGTH_CSS_META_DATA);
            styleables.add(TICK_LABEL_GAP_CSS_META_DATA);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }

    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}
