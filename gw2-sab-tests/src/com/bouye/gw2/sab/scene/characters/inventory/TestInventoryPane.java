/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.inventory;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.account.inventory.SharedInventory;
import api.web.gw2.mapping.v2.characters.inventory.InventoryBag;
import com.bouye.gw2.sab.SAB;
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
public final class TestInventoryPane extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final InventoryPane inventoryPane = new InventoryPane();
        final Optional<URL> sharedInventoryURL = Optional.ofNullable(getClass().getResource("account_inventories.json")); // NOI18N.
        sharedInventoryURL.ifPresent(url -> {
            try {
                final Collection<SharedInventory> inventory = JsonpContext.SAX.loadObjectArray(SharedInventory.class, url);
                inventoryPane.getSharedInventory().setAll(inventory);
            } catch (IOException ex) {
                Logger.getLogger(TestInventoryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
        final Optional<URL> characterInventoryURL = Optional.ofNullable(getClass().getResource("character_inventories.json")); // NOI18N.
        characterInventoryURL.ifPresent(url -> {
            try {
                final Collection<InventoryBag> inventory = JsonpContext.SAX.loadObjectArray(InventoryBag.class, url);
                inventoryPane.getCharacterInventory().setAll(inventory);
            } catch (IOException ex) {
                Logger.getLogger(TestInventoryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
        final BorderPane root = new BorderPane();
        root.setCenter(inventoryPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestInventoryPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
