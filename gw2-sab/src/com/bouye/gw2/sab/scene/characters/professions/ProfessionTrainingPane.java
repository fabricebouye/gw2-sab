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
import api.web.gw2.mapping.v2.professions.ProfessionElementalistAttunement;
import api.web.gw2.mapping.v2.professions.ProfessionTrack;
import api.web.gw2.mapping.v2.professions.ProfessionTrackCategory;
import api.web.gw2.mapping.v2.professions.ProfessionWeaponSkill;
import api.web.gw2.mapping.v2.professions.ProfessionWeaponSkillSet;
import api.web.gw2.mapping.v2.professions.ProfessionWeaponSlot;
import api.web.gw2.mapping.v2.professions.ProfessionWeaponType;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.text.LabelUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Profession details.
 * @author Fabrice Bouyé
 */
public final class ProfessionTrainingPane extends HBox {

    private final Tab skillsTab = new Tab("Skils");
    private final VBox weaponsVBox = new VBox();
    private final ScrollPane weaponsScroll = new ScrollPane(weaponsVBox);
    private final Tab weaponsTab = new Tab("Weapons", weaponsScroll);
    private final VBox tracksVBox = new VBox();
    private final ScrollPane tracksScroll = new ScrollPane(tracksVBox);
    private final Tab tracksTab = new Tab("Traits", tracksScroll);
    private final TabPane sideTabPane = new TabPane(skillsTab, weaponsTab, tracksTab);
    private final Label editorLabel = new Label();
    private final ProfessionTrackEditor trackEditor = new ProfessionTrackEditor();
    private final VBox editorVBox = new VBox(editorLabel, trackEditor);
    private final ToggleGroup trackToggleGroup = new ToggleGroup();

    /**
     * Creates a new instance.
     */
    public ProfessionTrainingPane() {
        super();
        setId("professionTrainingPane"); // NOI18N.
        getStyleClass().add("profession-training-pane"); // NOI18N.
        //
        weaponsVBox.setId("weaponsVBox"); // NOI18N.
        //
        weaponsScroll.setId("weaponsScroll"); // NOI18N.
        weaponsScroll.setFitToWidth(true);
        weaponsScroll.setFitToHeight(true);
        //
        tracksVBox.setId("tracksVBox"); // NOI18N.
        //
        tracksScroll.setId("traitsScroll"); // NOI18N.
        tracksScroll.setFitToWidth(true);
        tracksScroll.setFitToHeight(true);
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
        skillsTab.setClosable(false);
        weaponsTab.setClosable(false);
        tracksTab.setClosable(false);
        HBox.setHgrow(sideTabPane, Priority.ALWAYS);
        //
        getChildren().addAll(sideTabPane, editorVBox);
        //
        trackToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
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
        // Clear skills.
        // Clear weapons.
        weaponsVBox.getChildren().clear();
        // Clear tracks.
        tracksVBox.getChildren().clear();
        trackToggleGroup.getToggles().clear();
        //
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
        trackEditor.setProfession(profession);
        // Populate skills.
        // Populate weapons.
        weaponsVBox.getChildren().setAll(createWeaponsContent(profession));
        // Populate tracks.
        tracksVBox.getChildren().setAll(createTracksContent(profession));
        if (!trackToggleGroup.getToggles().isEmpty()) {
            trackToggleGroup.selectToggle(trackToggleGroup.getToggles().get(0));
        }
    }

    private List<Node> createWeaponsContent(final Profession profession) {
        final List<Node> result = profession.getWeapons()
                .entrySet()
                .stream()
                .map(entry -> createWeaponPane(profession, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return result;
    }

    private Node createWeaponPane(final Profession profession, final ProfessionWeaponType weaponType, final ProfessionWeaponSkillSet skillSet) {
        final VBox contentVBox = new VBox();
        final CharacterProfession cProfession = EnumValueFactory.INSTANCE.mapEnumValue(CharacterProfession.class, profession.getId());
        System.out.println(profession + "\t" + weaponType);
        final Map<ProfessionWeaponSlot, List<ProfessionWeaponSkill>> map = Arrays.stream(ProfessionWeaponSlot.values())
                .filter(slot -> slot != ProfessionWeaponSlot.UNKNOWN)
                .collect(Collectors.toMap(Function.identity(), slot -> skillSet.getSkills()
                        .stream()
                        .filter(skill -> slot == skill.getSlot())
                        .collect(Collectors.toList())));
        contentVBox.getChildren().add(createWeaponSkillsPane(weaponType, map));
        final TitledPane result = new TitledPane();
        final String titleKey = String.format("weapon.%s.label", LabelUtils.INSTANCE.toLabel(weaponType).toLowerCase()); // NOI18N.
        result.setText(SABConstants.I18N.getString(titleKey));
        result.setContent(contentVBox);
        return result;
    }

    private Node createWeaponSkillsPane(final ProfessionWeaponType weaponType, final Map<ProfessionWeaponSlot, List<ProfessionWeaponSkill>> map) {
        final int maxRows = map.values()
                .stream()
                .mapToInt(List::size)
                .max()
                .getAsInt();
        GridPane gridPane = new GridPane();
        IntStream.range(0, maxRows)
                .forEach(row -> {
                    RowConstraints rowContraints = new RowConstraints();
                    gridPane.getRowConstraints().add(rowContraints);
                });
        Arrays.stream(ProfessionWeaponSlot.values())
                .filter(slot -> slot != ProfessionWeaponSlot.UNKNOWN)
                .forEach(slot -> {
                    final int column = slot.ordinal();
                    final List<ProfessionWeaponSkill> skillList = map.get(slot);
                    final int skillNumbersForSlot = skillList.size();
                    IntStream.range(0, maxRows)
                            .forEach(row -> {
                                ProfessionWeaponSkill skill = row < skillNumbersForSlot ? skillList.get(row) : null;
                                final Node node = createWeaponSkillNode(weaponType, skill);
                                GridPane.setConstraints(node, column, row);
                                gridPane.getChildren().add(node);
                            });
                });
        return gridPane;
    }

    /**
     * Creates the visual representation for a given weapon skill.
     * @param skill The skill, may be {@code null}.
     * @return A {@code Node}, never {@code null}.
     */
    private Node createWeaponSkillNode(final ProfessionWeaponType weaponType, final ProfessionWeaponSkill skill) {
        final Rectangle rectangle = new Rectangle(32, 32);
        rectangle.setFill(Color.LIGHTGRAY);
        rectangle.setStroke(Color.BLACK);
        StackPane result = new StackPane();
        result.getChildren().add(rectangle);
        if (skill != null) {
            final Text text = new Text(String.valueOf(skill.getId()));
            result.getChildren().add(text);
            final Tooltip tooltip = new Tooltip();
            String tip = String.valueOf(skill.getId());
            tip += "\n" + weaponType;
            if (skill.getOffhand().isPresent()) {
                tip += "\n" + skill.getOffhand().get();
            }
            if (skill.getAttunement().isPresent()) {
                tip += "\n" + skill.getAttunement().get();
            }
            tip += "\n" + skill.getSlot();
            tooltip.setText(tip);
            Tooltip.install(result, tooltip);
        }
        return result;
    }

    private List<Node> createTracksContent(final Profession profession) {
        final List<Node> result = Arrays.stream(ProfessionTrackCategory.values())
                .filter(category -> category != ProfessionTrackCategory.UNKNOWN)
                .map(category -> createTrackPane(profession, category))
                .collect(Collectors.toList());
        return result;
    }

    private Node createTrackPane(final Profession profession, final ProfessionTrackCategory category) {
        final VBox contentVBox = new VBox();
        contentVBox.setId(String.format("%sVBox", category.name())); // NOI18N.
        contentVBox.getStyleClass().add("track-vbox"); // NOI18N.
        contentVBox.getChildren().setAll(profession.getTraining()
                .stream()
                .filter(track -> track.getCategory() == category)
                .map(this::createTraitNode)
                .collect(Collectors.toList()));
        final TitledPane result = new TitledPane();
        final String titleKey = String.format("profession-track.%s.label", LabelUtils.INSTANCE.toLabel(category).toLowerCase()); // NOI18N.
        result.setText(SABConstants.I18N.getString(titleKey));
        result.setContent(contentVBox);
        return result;
    }

    private Node createTraitNode(final ProfessionTrack track) {
        final ProfessionTrackLabel result = new ProfessionTrackLabel(track);
        result.setToggleGroup(trackToggleGroup);
        return result;
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

    private final ReadOnlyObjectWrapper<ProfessionTrack> track = new ReadOnlyObjectWrapper<>(this, "track", null); // NOI18N.

    public ProfessionTrack getTrack() {
        return track.get();
    }

    void setTrack(final ProfessionTrack value) {
        track.set(value);
    }

    public ReadOnlyObjectProperty<ProfessionTrack> trackProperty() {
        return track.getReadOnlyProperty();
    }

    /**
     * The profession track label.
     * <br>A {@code Toggle} that allows to select a profession track.
     * @author Fabrice Bouyé
     */
    private static final class ProfessionTrackLabel extends Label implements Toggle {

        /**
         * Creates a new instance.
         * @param index The index of the trait.
         */
        public ProfessionTrackLabel(final ProfessionTrack track) {
            super();
            setId(String.format("%sLabel", track.getName())); // NOI18N.
            getStyleClass().add("track-label"); // NOI18N.
            setUserData(track);
            setText(track.getName());
            setMaxWidth(Double.MAX_VALUE);
            // Listeners.
            toggleGroupProperty().addListener(toggleGroupChangeListener);
            selectedProperty().addListener(selectedChangeListener);
            setOnMouseClicked(mouseEventHandler);
        }

        ////////////////////////////////////////////////////////////////////////////
        /**
         * Called whenever the toggle group changes.
         */
        private final ChangeListener<ToggleGroup> toggleGroupChangeListener = (observable, oldValue, newValue) -> {
            final Optional<ToggleGroup> oldToggleGroup = Optional.ofNullable(oldValue);
            oldToggleGroup.ifPresent(tg -> tg.getToggles().remove(ProfessionTrackLabel.this));
            final Optional<ToggleGroup> newToggleGroup = Optional.ofNullable(newValue);
            newToggleGroup.ifPresent(tg -> tg.getToggles().add(ProfessionTrackLabel.this));
        };

        /**
         * Called whenever the selected state of this trait changes.
         */
        private final ChangeListener<Boolean> selectedChangeListener = (observable, oldValue, newValue) -> {
            final Optional<ToggleGroup> toggleGroup = Optional.ofNullable(getToggleGroup());
            // Selection.
            toggleGroup.ifPresent(tg -> tg.selectToggle(newValue ? ProfessionTrackLabel.this : null));
            // Style.
            ProfessionTrackLabel.this.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), newValue); // NOI18N.
        };

        /**
         * Called whenever the user clicks on this trait.
         */
        private final EventHandler<MouseEvent> mouseEventHandler = mouseEvent -> {
            setSelected(true);
        };

        ////////////////////////////////////////////////////////////////////////////        
        /**
         * Toggle group as defined in {@code Toggle}.
         */
        private final ObjectProperty<ToggleGroup> toggleGroup = new SimpleObjectProperty<>(this, "toggleGroup", null); // NOI18N.

        @Override
        public ToggleGroup getToggleGroup() {
            return toggleGroup.get();
        }

        @Override
        public void setToggleGroup(final ToggleGroup value) {
            toggleGroup.set(value);
        }

        @Override
        public ObjectProperty<ToggleGroup> toggleGroupProperty() {
            return toggleGroup;
        }

        /**
         * Selected state as defined in {@code Toggle}.
         */
        private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false); // NOI18N.

        @Override
        public boolean isSelected() {
            return selected.get();
        }

        @Override
        public void setSelected(final boolean value) {
            selected.set(value);
        }

        @Override
        public BooleanProperty selectedProperty() {
            return selected;
        }
    }
}
