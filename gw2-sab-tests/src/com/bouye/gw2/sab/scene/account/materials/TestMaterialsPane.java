/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.materials;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.account.materials.Material;
import api.web.gw2.mapping.v2.materials.MaterialStorage;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.characters.inventory.TestInventoryPane;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestMaterialsPane extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final MaterialsPane materialsPane = new MaterialsPane();
        final Optional<URL> materialCategories = Optional.ofNullable(getClass().getResource("material_categories.json")); // NOI18N.
        materialCategories.ifPresent(url -> {
            try {
                final Collection<MaterialStorage> values = JsonpContext.SAX.loadObjectArray(MaterialStorage.class, url);
                materialsPane.getMaterialStorage().setAll(values);
            } catch (IOException ex) {
                Logger.getLogger(TestInventoryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
        final Optional<URL> accountMaterials = Optional.ofNullable(getClass().getResource("account_materials.json")); // NOI18N.
        accountMaterials.ifPresent(url -> {
            try {
                final Collection<Material> values = JsonpContext.SAX.loadObjectArray(Material.class, url);
                materialsPane.getMaterials().setAll(values);
            } catch (IOException ex) {
                Logger.getLogger(TestInventoryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
        final BorderPane root = new BorderPane();
        root.setCenter(materialsPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestMaterialsPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
