/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.specializations;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import api.web.gw2.mapping.v2.specializations.Specialization;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.db.DBStorage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestSpecializationEditor extends Application {

    @Override
    public void init() throws Exception {
        DBStorage.INSTANCE.init();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Tab styleTab = new Tab("Style");
        styleTab.setContent(createStyleTest());
        styleTab.setClosable(false);
        final Tab editionTab = new Tab("Edition");
        editionTab.setContent(createEditionTab());
        editionTab.setClosable(false);
        final TabPane tabPane = new TabPane();
        tabPane.getTabs().setAll(styleTab, editionTab);
        final Scene scene = new Scene(tabPane, 700, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestSpecializationEditor"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
    }

    private Node createStyleTest() {
        final List<SpecializationEditor> editors = new ArrayList<>(10);
        // Base non-styled editor.
        final SpecializationEditor editor1 = new SpecializationEditor();
        editors.add(editor1);
        // Editors styled per profession.
        editors.addAll(
                Arrays.stream(CharacterProfession.values())
                .filter(profession -> profession != CharacterProfession.UNKNOWN)
                .map(profession -> {
                    final SpecializationEditor editor = new SpecializationEditor();
                    editor.setProfession(profession);
                    return editor;
                })
                .collect(Collectors.toList()));
        final VBox vbox = new VBox();
        vbox.getChildren().setAll(editors);
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vbox);
        return scrollPane;
    }

    private Node createEditionTab() {
        final SpecializationEditor editor1 = new SpecializationEditor();
        editor1.getMajorTraitsReadOnly().addListener((ListChangeListener) change -> System.out.println(editor1.getMajorTraitsReadOnly()));
        final SpecializationEditor editor2 = new SpecializationEditor();
        editor2.setEditable(false);
        final VBox vbox = new VBox();
        vbox.setMaxWidth(VBox.USE_PREF_SIZE);
        vbox.setMaxHeight(VBox.USE_PREF_SIZE);
        vbox.getChildren().setAll(editor1, editor2);
        final StackPane stackPane = new StackPane();
        stackPane.getChildren().add(vbox);
        final Optional<URL> jsonURL = Optional.ofNullable(getClass().getResource("specialization01.json")); // NOI18N.
        jsonURL.ifPresent(url -> {
            try {
                final Specialization specialization = JsonpContext.SAX.loadObject(Specialization.class, url);
                editor1.setSpecialization(specialization);
                editor1.setMajorTrait1(701);
                editor1.setMajorTrait2(1960);
                editor1.setMajorTrait3(704);
                editor2.setSpecialization(specialization);
                editor2.setMajorTrait1(701);
                editor2.setMajorTrait2(1960);
                editor2.setMajorTrait3(704);
            } catch (NullPointerException | IOException ex) {
                Logger.getLogger(TestSpecializationEditor.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
        return stackPane;
    }
}
