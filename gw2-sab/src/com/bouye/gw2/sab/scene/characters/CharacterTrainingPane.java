/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import api.web.gw2.mapping.core.EnumValueFactory;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import api.web.gw2.mapping.v2.professions.Profession;
import api.web.gw2.mapping.v2.professions.ProfessionTrack;
import api.web.gw2.mapping.v2.professions.ProfessionTrackCategory;
import api.web.gw2.mapping.v2.professions.ProfessionTrackCost;
import api.web.gw2.mapping.v2.professions.ProfessionTrackCostType;
import api.web.gw2.mapping.v2.professions.ProfessionWeaponSkill;
import api.web.gw2.mapping.v2.professions.ProfessionWeaponSkillSet;
import api.web.gw2.mapping.v2.professions.ProfessionWeaponSlot;
import api.web.gw2.mapping.v2.professions.ProfessionWeaponType;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.scene.characters.specializations.SpecializationsPane;
import com.bouye.gw2.sab.text.LabelUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
public final class CharacterTrainingPane extends HBox {

    // Skills tab.
    private final VBox utilitySkillsVBox = new VBox();
    private final ScrollPane utilitySkillsScroll = new ScrollPane(utilitySkillsVBox);
    // @todo Localize!
    private final Tab utilitySkillsTab = new Tab("Skills", utilitySkillsScroll);
    // Weapons tab.
    private final VBox weaponSkillsVBox = new VBox();
    private final ScrollPane weaponSkillsScroll = new ScrollPane(weaponSkillsVBox);
    // @todo Localize!
    private final Tab weaponSkillsTab = new Tab("Weapons", weaponSkillsScroll);
    // Traits tabs.
    private final VBox traitsVBox = new VBox();
    private final ScrollPane traitsScroll = new ScrollPane(traitsVBox);
    // @todo Localize!
    private final Tab traitsTab = new Tab("Traits", traitsScroll);
    //
    private final TabPane sideTabPane = new TabPane(utilitySkillsTab, weaponSkillsTab, traitsTab);
    //
    private final SpecializationsPane specializationPane = new SpecializationsPane();
    private final VBox specializationEditorVBox = new VBox(specializationPane);

    /**
     * Creates a new instance.
     */
    public CharacterTrainingPane() {
        super();
        setId("characterTrainingPane"); // NOI18N.
        getStyleClass().add("character-training-pane"); // NOI18N.
        //
        utilitySkillsVBox.setId("utilitySkillsVBox"); // NOI18N.
        //
        utilitySkillsScroll.setId("utilitySkillsScroll"); // NOI18N.
        utilitySkillsScroll.setFitToWidth(true);
        utilitySkillsScroll.setFitToHeight(true);
        //
        weaponSkillsVBox.setId("weaponSkillsVBox"); // NOI18N.
        //
        weaponSkillsScroll.setId("weaponSkillsScroll"); // NOI18N.
        weaponSkillsScroll.setFitToWidth(true);
        weaponSkillsScroll.setFitToHeight(true);
        //
        traitsVBox.setId("traitsVBox"); // NOI18N.
        //
        traitsScroll.setId("traitsScroll"); // NOI18N.
        traitsScroll.setFitToWidth(true);
        traitsScroll.setFitToHeight(true);
        //
        utilitySkillsTab.setClosable(false);
        weaponSkillsTab.setClosable(false);
        traitsTab.setClosable(false);
        HBox.setHgrow(sideTabPane, Priority.ALWAYS);
        //
        HBox.setHgrow(specializationEditorVBox, Priority.NEVER);
        //        
        getChildren().addAll(sideTabPane, specializationEditorVBox);
        //
        professionProperty().addListener(professionChangeListener);
    }

    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/CharacterTrainingPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
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
        // Clear skills.
        utilitySkillsVBox.getChildren().clear();
        // Clear weapons.
        weaponSkillsVBox.getChildren().clear();
        // Clear traits.
        traitsVBox.getChildren().clear();
    }

    /**
     * Install a profession.
     * @param profession The profession, never {@code null}.
     */
    private void installProfession(final Profession profession) {
        final CharacterProfession cProfession = EnumValueFactory.INSTANCE.mapEnumValue(CharacterProfession.class, profession.getId());
        final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(cProfession);
        pseudoClassStateChanged(pseudoClass, true);
        // Populate skills.
        utilitySkillsVBox.getChildren().setAll(createUtilitySkillsContent(profession));
        // Populate weapons.
        weaponSkillsVBox.getChildren().setAll(createWeaponSkillsContent(profession));
        // Populate traits.
        traitsVBox.getChildren().setAll(createTraitsContent(profession));
    }

    /**
     * List all weapons skills for that profession.
     * @param profession The profession.
     * @return A {@code List<Node>}, never {@code null}.
     */
    private List<Node> createWeaponSkillsContent(final Profession profession) {
        final List<Node> result = profession.getWeapons()
                .entrySet()
                .stream()
                .map(entry -> createWeaponSkillsPane(profession, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return result;
    }

    private Node createWeaponSkillsPane(final Profession profession, final ProfessionWeaponType weaponType, final ProfessionWeaponSkillSet skillSet) {
        final VBox contentVBox = new VBox();
        final CharacterProfession cProfession = EnumValueFactory.INSTANCE.mapEnumValue(CharacterProfession.class, profession.getId());
        // Gets a list of skills for each slot.
        // This allows to get thief offhand and elementalist attuned skills.
        // Also alternate skills for regular weapons (some are missing in the API currently).
        final Map<ProfessionWeaponSlot, List<ProfessionWeaponSkill>> skillMap = Arrays.stream(ProfessionWeaponSlot.values())
                .filter(slot -> slot != ProfessionWeaponSlot.UNKNOWN)
                .collect(Collectors.toMap(Function.identity(), slot -> skillSet.getSkills()
                        .stream()
                        .filter(skill -> slot == skill.getSlot())
                        .collect(Collectors.toList())));
        final int maxRows = skillMap.values()
                .stream()
                .mapToInt(List::size)
                .max()
                .getAsInt();
        final GridPane gridPane = new GridPane();
        IntStream.range(0, maxRows)
                .forEach(row -> {
                    RowConstraints rowContraints = new RowConstraints();
                    gridPane.getRowConstraints().add(rowContraints);
                });
        Arrays.stream(ProfessionWeaponSlot.values())
                .filter(slot -> slot != ProfessionWeaponSlot.UNKNOWN)
                .forEach(slot -> {
                    final int column = slot.ordinal();
                    final List<ProfessionWeaponSkill> skillList = skillMap.get(slot);
                    final int skillNumbersForSlot = skillList.size();
                    IntStream.range(0, maxRows)
                            .forEach(row -> {
                                ProfessionWeaponSkill skill = row < skillNumbersForSlot ? skillList.get(row) : null;
                                final Node node = createWeaponSkillNode(weaponType, skill);
                                GridPane.setConstraints(node, column, row);
                                gridPane.getChildren().add(node);
                            });
                });
        contentVBox.getChildren().add(gridPane);
        final TitledPane result = new TitledPane();
        final String titleKey = String.format("weapon.%s.label", LabelUtils.INSTANCE.toLabel(weaponType).toLowerCase()); // NOI18N.
        result.setText(SABConstants.I18N.getString(titleKey));
        result.setContent(contentVBox);
        return result;
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

    /**
     * List all profession (utility) skills.
     * @param profession The profession.
     * @return A {@code List<Node>}, never {@code null}.
     */
    private List<Node> createUtilitySkillsContent(final Profession profession) {
        final Label professionLabel = new Label(profession.getName() + " Skills");
        final FlowPane skillsFlowPane = new FlowPane();
        skillsFlowPane.getChildren().setAll(profession.getTraining().stream()
                .filter(track -> track.getCategory() == ProfessionTrackCategory.SKILLS)
                .map(ProfessionTrack::getTrack)
                .map(Set::stream)
                .reduce(Stream.empty(), Stream::concat)
                .filter(trackCost -> trackCost.getType() == ProfessionTrackCostType.SKILL)
                .map(this::createTrackCostNode)
                .collect(Collectors.toList()));
        return Arrays.asList(professionLabel, skillsFlowPane);
    }

    /**
     * Create a node for given track cost.
     * @param trackCost The track cost, may be {@code null}.
     * @return A {@code Node} instance, never {@code null}.
     */
    private Node createTrackCostNode(final ProfessionTrackCost trackCost) {
        final Rectangle rectangle = new Rectangle(32, 32);
        rectangle.setFill(Color.LIGHTGRAY);
        rectangle.setStroke(Color.BLACK);
        StackPane result = new StackPane();
        result.getChildren().add(rectangle);
        if (trackCost != null) {
            if (trackCost.getSkillId().isPresent()) {
                final Text text = new Text(String.valueOf(trackCost.getSkillId().getAsInt()));
                result.getChildren().add(text);
            }
            final Tooltip tooltip = new Tooltip();
            String tip = "";
            if (trackCost.getSkillId().isPresent()) {
                tip += String.valueOf(trackCost.getSkillId().getAsInt());
            }
            if (trackCost.getTraitId().isPresent()) {
                tip += "\n" + trackCost.getTraitId().getAsInt();
            }
            tip += "\n" + trackCost.getType();
            tip += "\n" + trackCost.getCost();
            tooltip.setText(tip);
            Tooltip.install(result, tooltip);
        }
        return result;
    }

    /**
     * List all traits (from specialization and elite specialization).
     * @param profession The profession.
     * @return A {@code List<Node>}, never {@code null}.
     */
    private List<Node> createTraitsContent(final Profession profession) {
        final List<Node> result = Arrays.stream(ProfessionTrackCategory.values())
                .filter(category -> category == ProfessionTrackCategory.SPECIALIZATIONS || category == ProfessionTrackCategory.ELITE_SPECIALIZATIONS)
                .map(category -> profession.getTraining()
                        .stream()
                        .filter(track -> track.getCategory() == category))
                .reduce(Stream.empty(), Stream::concat)
                .map(this::createTrackNode)
                .collect(Collectors.toList());
        return result;
    }

    private Node createTrackNode(final ProfessionTrack track) {
        final GridPane gridPane = new GridPane();
        // @todo need to check the returned order of minor, adept, master, grand master traits.
        final List<ProfessionTrackCost> traits = track.getTrack()
                .stream()
                .filter(trackCost -> trackCost.getType() == ProfessionTrackCostType.TRAIT)
                .collect(Collectors.toList());
        // @todo layout content here.
        // @todo Minor go in 1st column.
        // @todo Adept go in 1st line.
        // @todo Master go in 2nd column.
        // @todo Grand master go in 3rd column.
        final TitledPane result = new TitledPane();
        result.setText(track.getName());
        result.setContent(gridPane);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    private final ObjectProperty<Profession> profession = new SimpleObjectProperty<>(this, "profession", null); // NOI18N.

    public final Profession getProfession() {
        return profession.get();
    }

    public void setProfession(final Profession value) {
        profession.set(value);
    }

    public final ObjectProperty<Profession> professionProperty() {
        return profession;
    }
}
