/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import api.web.gw2.mapping.v2.characters.Character;
import api.web.gw2.mapping.v2.characters.id.wvw_abilities.CharacterAbility;
import api.web.gw2.mapping.v2.characters.id.crafting.CharacterCrafting;
import api.web.gw2.mapping.v2.characters.id.equipment_pvp.CharacterEquipmentPvP;
import api.web.gw2.mapping.v2.characters.id.specializations.CharacterSpecializationGameType;
import api.web.gw2.mapping.v2.characters.CharacterGender;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import api.web.gw2.mapping.v2.characters.CharacterRace;
import api.web.gw2.mapping.v2.characters.id.skills.CharacterSkillSet;
import api.web.gw2.mapping.v2.characters.id.specializations.CharacterSpecialization;
import api.web.gw2.mapping.v2.characters.id.training.CharacterTraining;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.wrappers.CharacterWrapper;
import java.net.URL;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import api.web.gw2.mapping.v2.characters.id.equipment.CharacterEquipment;
import api.web.gw2.mapping.v2.characters.id.inventory.CharacterInventoryBag;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestCharacterListRenderer extends Application {

    @Override
    public void start(final Stage primaryStage) {
        // Initialize all the sessions.
        final List<CharacterWrapper> characters = Arrays.stream(CharacterProfession.values())
                .filter(profession -> profession != CharacterProfession.UNKNOWN)
                .map(this::createFakeCharacterWrapper)
                .collect(Collectors.toList());
        // Left pane: all renderers.
        final VBox renderersVBox = new VBox();
        renderersVBox.setFillWidth(true);
        renderersVBox.getChildren().setAll(characters
                .stream()
                .map(character -> {
                    final CharacterListRenderer renderer = new CharacterListRenderer();
                    renderer.setCharacter(character);
                    return renderer;
                })
                .collect(Collectors.toList()));
        final ScrollPane renderersScrollPane = new ScrollPane();
        renderersScrollPane.setFitToWidth(true);
        renderersScrollPane.setFitToHeight(true);
        renderersScrollPane.setContent(renderersVBox);
        // Middle pane: bare list.
        final ListView<CharacterWrapper> listView1 = new ListView();
        listView1.getItems().setAll(characters);
        // Right pane: use account list cell which are using renderers.
        final ListView<CharacterWrapper> listView2 = new ListView();
        listView2.setCellFactory(character -> {
            final CharacterListCell listCell = new CharacterListCell();
            return listCell;
        });
        listView2.getItems().setAll(characters);
        //
        final ComboBox<CharacterWrapper> comboBox = new ComboBox<>();
        comboBox.setButtonCell(new ListCell<CharacterWrapper>() {
            @Override
            protected void updateItem(final CharacterWrapper item, final boolean empty) {
                super.updateItem(item, empty);
                final Character character = (empty || item == null) ? null : item.getCharacter();
                final String text = (empty || character == null) ? null : String.format("%s - %s - %s (%d)", character.getName(), character.getRace(), character.getProfession(), character.getLevel());
                setText(text);
            }
        });
        comboBox.setCellFactory(listView -> {
            final CharacterListCell listCell = new CharacterListCell();
            return listCell;
        });
        comboBox.getItems().setAll(characters);
        final VBox comboVBox = new VBox(comboBox);
        //
        final SplitPane splitPane = new SplitPane();
        splitPane.getItems().setAll(renderersScrollPane, listView1, listView2, comboVBox);
        final BorderPane root = new BorderPane();
        root.setCenter(splitPane);
        final Scene scene = new Scene(root, 800, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestCharacterListRenderer"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.runLater(() -> splitPane.setDividerPositions(0.25, 0.5, 0.75));
//        ScenicView.show(root);
    }

    /**
     * Creates a fake character.
     * @param profession The profession of this character.
     * @return A {@code CharacterWrapper} instance, never {@code null}.
     */
    private CharacterWrapper createFakeCharacterWrapper(final CharacterProfession profession) {
        final String characterName = String.format("Test %s", profession.name().toLowerCase());
        final CharacterRace[] allRaces = Arrays.stream(CharacterRace.values())
                .filter(race -> race != CharacterRace.UNKNOWN)
                .toArray(CharacterRace[]::new);
        final CharacterRace characterRace = allRaces[(int) Math.round((allRaces.length - 1) * Math.random())];
        final CharacterGender[] allGenders = Arrays.stream(CharacterGender.values())
                .filter(gender -> gender != CharacterGender.UNKNOWN)
                .toArray(CharacterGender[]::new);
        final CharacterGender characterGender = allGenders[(int) Math.round((allGenders.length - 1) * Math.random())];
        final int characterLevel = (int) Math.round(80 * Math.random());
        //
        final CharacterWrapper wrapper = new CharacterWrapper(characterName);
        final Character character = new Character() {
            @Override
            public String getName() {
                return characterName;
            }

            @Override
            public CharacterRace getRace() {
                return characterRace;
            }

            @Override
            public CharacterProfession getProfession() {
                return profession;
            }

            @Override
            public CharacterGender getGender() {
                return characterGender;
            }

            @Override
            public int getLevel() {
                return characterLevel;
            }

            @Override
            public Optional<String> getGuild() {
                return Optional.empty();
            }

            @Override
            public ZonedDateTime getCreated() {
                return ZonedDateTime.now();
            }

            @Override
            public Duration getAge() {
                return Duration.ZERO;
            }

            @Override
            public int getDeaths() {
                return 0;
            }

            @Override
            public List<CharacterEquipment> getEquipment() {
                return Collections.emptyList();
            }

            @Override
            public List<CharacterInventoryBag> getBags() {
                return Collections.emptyList();
            }

            @Override
            public Set<CharacterCrafting> getCrafting() {
                return Collections.emptySet();
            }

            @Override
            public Map<CharacterSpecializationGameType, Set<CharacterSpecialization>> getSpecializations() {
                return Collections.emptyMap();
            }

            @Override
            public Set<Integer> getRecipes() {
                return Collections.emptySet();
            }

            @Override
            public Map<CharacterSpecializationGameType, CharacterSkillSet> getSkills() {
                return Collections.emptyMap();
            }

            @Override
            public Set<CharacterTraining> getTraining() {
                return Collections.emptySet();
            }

            @Override
            public Set<Integer> getBackstory() {
                return Collections.emptySet();
            }

            @Override
            public OptionalInt getTitle() {
                return OptionalInt.empty();
            }

            @Override
            public Optional<CharacterEquipmentPvP> getEquipmentPvp() {
                return Optional.empty();
            }

            @Override
            public Set<CharacterAbility> getWvwAbilities() {
                return Collections.emptySet();
            }
        };
        wrapper.setCharacter(character);
        return wrapper;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
