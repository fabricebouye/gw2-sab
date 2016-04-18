/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.specializations;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.characters.CharacterSpecialization;
import api.web.gw2.mapping.v2.specializations.Specialization;
import com.bouye.gw2.sab.SAB;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestSpecializationPane extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final SpecializationsPane specializationsPane = new SpecializationsPane();
        final StackPane root = new StackPane();
        root.getChildren().add(specializationsPane);
        final Scene scene = new Scene(root, 650, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestSpecializationPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
//        ScenicView.show(root);
        final Optional<URL> specializationURL = Optional.ofNullable(getClass().getResource("specializations.json")); // NOI18N.
        final Optional<URL> charSpecsURL = Optional.ofNullable(getClass().getResource("character_specializations.json")); // NOI18N.
        if (specializationURL.isPresent() && charSpecsURL.isPresent()) {
            try {
                final Collection<Specialization> specializations = JsonpContext.SAX.loadObjectArray(Specialization.class, specializationURL.get());
                specializationsPane.getSpecializationPool().setAll(specializations);
                final Collection<CharacterSpecialization> charSpecs = JsonpContext.SAX.loadObjectArray(CharacterSpecialization.class, charSpecsURL.get());
                final Iterator<CharacterSpecialization> it = charSpecs.iterator();
                specializationsPane.setBuild1(it.next());
                specializationsPane.setBuild2(it.next());
                specializationsPane.setBuild3(it.next());
            } catch (NullPointerException | IOException ex) {
                Logger.getLogger(TestSpecializationEditor.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}
