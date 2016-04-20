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
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
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
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(inventoryPane);
        final ToolBar toolBar = new ToolBar();
        final ToggleGroup displayToggleGroup = new ToggleGroup();
        Arrays.stream(InventoryDisplay.values())
                .forEach(display -> {
                    final RadioButton button = new RadioButton(display.name());
                    button.setSelected(display == inventoryPane.getDisplay());
                    button.setUserData(display);
                    button.setToggleGroup(displayToggleGroup);
                    toolBar.getItems().add(button);
                });
        displayToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            final InventoryDisplay display = (InventoryDisplay) newValue.getUserData();
            inventoryPane.setDisplay(display);
        });
        final BorderPane root = new BorderPane();
        root.setTop(toolBar);
        root.setCenter(scrollPane);
        final Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("TestInventoryPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
