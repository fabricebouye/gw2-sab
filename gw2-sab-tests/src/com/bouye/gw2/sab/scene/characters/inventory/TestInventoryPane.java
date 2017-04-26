/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.inventory;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.skins.Skin;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfoPermission;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.GW2APIClient;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.CharacterBagWrapper;
import com.bouye.gw2.sab.wrappers.CharacterInventoryWrapper;
import com.bouye.gw2.sab.wrappers.SharedInventoryWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import api.web.gw2.mapping.v2.characters.id.inventory.CharacterInventory;
import api.web.gw2.mapping.v2.characters.id.inventory.CharacterInventoryBag;
import api.web.gw2.mapping.v2.account.inventory.AccountInventory;

/**
 * Test.
 *
 * @author Fabrice Bouyé
 */
public final class TestInventoryPane extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final InventoryPane inventoryPane = new InventoryPane();
        final BorderPane root = new BorderPane();
        root.setCenter(inventoryPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestInventoryPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
        loadTestAsync(inventoryPane);
    }

    /**
     * Load test in a background service.
     *
     * @param inventoryPane The target pane.
     */
    private void loadTestAsync(final InventoryPane inventoryPane) {
        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (SABConstants.INSTANCE.isOffline()) {
                            loadLocalTest(inventoryPane);
                        } else {
                            loadRemoteTest(inventoryPane);
                        }
                        return null;
                    }
                };
            }
        };
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getLogger(TestInventoryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.start();
    }

    /**
     * Run the local test.
     *
     * @param inventoryPane The target pane.
     */
    private void loadLocalTest(final InventoryPane inventoryPane) throws IOException {
        // Load items (if present).
        final Optional<URL> itemsURL = Optional.ofNullable(getClass().getResource("items.json")); // NOI18N.
        final Map<Integer, Item> items = (!itemsURL.isPresent()) ? Collections.EMPTY_MAP : JsonpContext.SAX.loadObjectArray(Item.class, itemsURL.get())
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        // Load skins (if present).
        final Optional<URL> skinsURL = Optional.ofNullable(getClass().getResource("skins.json")); // NOI18N.
        final Map<Integer, Skin> skins = (!skinsURL.isPresent()) ? Collections.EMPTY_MAP : JsonpContext.SAX.loadObjectArray(Skin.class, skinsURL.get())
                .stream()
                .collect(Collectors.toMap(Skin::getId, Function.identity()));
        // Load shared inventory.
        final Optional<URL> sharedInventoryURL = Optional.ofNullable(getClass().getResource("account_inventories.json")); // NOI18N.
        if (sharedInventoryURL.isPresent()) {
            final Collection<AccountInventory> sharedInventories = JsonpContext.SAX.loadObjectArray(AccountInventory.class, sharedInventoryURL.get());
            final List<SharedInventoryWrapper> wrappers = sharedInventories.stream()
                    .map(inventory -> {
                        SharedInventoryWrapper wrapper = null;
                        if (inventory != null) {
                            final Item inventoryItem = items.get(inventory.getId());
                            final Skin inventorySkin = (inventoryItem == null || !inventoryItem.getDefaultSkin().isPresent()) ? null : skins.get(inventoryItem.getDefaultSkin().getAsInt());
                            wrapper = new SharedInventoryWrapper(inventory, inventoryItem, inventorySkin);
                        }
                        return wrapper;
                    })
                    .collect(Collectors.toList());
            Platform.runLater(() -> inventoryPane.getSharedInventory().setAll(wrappers));
        }
        // Load character inventory.
        final Optional<URL> characterInventoryURL = Optional.ofNullable(getClass().getResource("character_inventories.json")); // NOI18N.
        if (characterInventoryURL.isPresent()) {
            final Collection<CharacterInventoryBag> inventoryBags = JsonpContext.SAX.loadObjectArray(CharacterInventoryBag.class, characterInventoryURL.get());
            final List<CharacterBagWrapper> wrappers = inventoryBags.stream()
                    .map(bag -> {
                        CharacterBagWrapper result = null;
                        if (bag != null) {
                            final Item bagItem = items.get(bag.getId());
                            final CharacterInventoryWrapper[] bagContent = bag.getInventory()
                                    .stream()
                                    .map(inventory -> {
                                        CharacterInventoryWrapper wrapper = null;
                                        if (inventory != null) {
                                            final Item inventoryItem = items.get(inventory.getId());
                                            final Skin inventorySkin = (inventoryItem == null || !inventoryItem.getDefaultSkin().isPresent()) ? null : skins.get(inventoryItem.getDefaultSkin().getAsInt());
                                            wrapper = new CharacterInventoryWrapper(inventory, inventoryItem, inventorySkin);
                                        }
                                        return wrapper;
                                    })
                                    .toArray(CharacterInventoryWrapper[]::new);
                            result = new CharacterBagWrapper(bag, bagItem, bagContent);
                        }
                        return result;
                    })
                    .collect(Collectors.toList());
            Platform.runLater(() -> inventoryPane.getCharacterInventory().setAll(wrappers));
        }
    }

    /**
     * Run the remote test.
     *
     * @param inventoryPane The target pane.
     */
    private void loadRemoteTest(final InventoryPane inventoryPane) {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        if (session.getTokenInfo().getPermissions().contains(TokenInfoPermission.INVENTORIES)) {
            // Shared inventory.
            final List<AccountInventory> sharedInventories = GW2APIClient.create()
                    .applicationKey(session.getAppKey())
                    .endPoint("account/inventory")
                    .queryArray(AccountInventory.class);
            if (!sharedInventories.isEmpty()) {
                // Extract items.
                final List<Integer> objectIds = sharedInventories.stream()
                        .filter(sharedInventory -> sharedInventory != null)
                        .map(AccountInventory::getId)
                        .collect(Collectors.toList());
                final int[] itemIdsToResolve = objectIds.stream()
                        .mapToInt(id -> id)
                        .distinct()
                        .sorted()
                        .toArray();
                final Map<Integer, Item> items = (itemIdsToResolve.length == 0) ? Collections.emptyMap() : GW2APIClient.create()
                        .endPoint("items")
                        .ids(itemIdsToResolve)
                        .queryArray(Item.class)
                        .stream()
                        .collect(Collectors.toMap(Item::getId, Function.identity()));
                // Extract skins.
                final int[] skinIdsToResolve = items.values()
                        .stream()
                        .map(Item::getDefaultSkin)
                        .filter(OptionalInt::isPresent)
                        .mapToInt(OptionalInt::getAsInt)
                        .distinct()
                        .sorted()
                        .toArray();
                final Map<Integer, Skin> skins = (skinIdsToResolve.length == 0) ? Collections.emptyMap() : GW2APIClient.create()
                        .endPoint("skins")
                        .ids(skinIdsToResolve)
                        .queryArray(Skin.class)
                        .stream()
                        .collect(Collectors.toMap(Skin::getId, Function.identity()));
                // Wrap.
                final List<SharedInventoryWrapper> wrappers = sharedInventories.stream()
                        .map(inventory -> {
                            SharedInventoryWrapper wrapper = null;
                            if (inventory != null) {
                                final Item inventoryItem = items.get(inventory.getId());
                                final Skin inventorySkin = (inventoryItem == null || !inventoryItem.getDefaultSkin().isPresent()) ? null : skins.get(inventoryItem.getDefaultSkin().getAsInt());
                                wrapper = new SharedInventoryWrapper(inventory, inventoryItem, inventorySkin);
                            }
                            return wrapper;
                        })
                        .collect(Collectors.toList());
                Platform.runLater(() -> inventoryPane.getSharedInventory().setAll(wrappers));
            }
            // Character inventory.
            final Optional<api.web.gw2.mapping.v2.characters.Character> character = GW2APIClient.create()
                    .applicationKey(session.getAppKey())
                    .endPoint("characters")
                    .id(SABTestUtils.INSTANCE.getTestCharacter())
                    .queryObject(api.web.gw2.mapping.v2.characters.Character.class);
            if (character.isPresent()) {
                final List<CharacterInventoryBag> inventoryBags = character.get().getBags();
                final List<Integer> objectIds = new ArrayList<>();
                inventoryBags.stream()
                        .forEach(inventoryBag -> {
                            final List<Integer> bagItemIds = inventoryBag.getInventory()
                                    .stream()
                                    .filter(inventory -> inventory != null)
                                    .map(CharacterInventory::getId)
                                    .collect(Collectors.toList());
                            objectIds.addAll(bagItemIds);
                        });
                final int[] itemIdsToResolve = objectIds.stream()
                        .mapToInt(id -> id)
                        .distinct()
                        .sorted()
                        .toArray();
                final Map<Integer, Item> items = (itemIdsToResolve.length == 0) ? Collections.emptyMap() : GW2APIClient.create()
                        .endPoint("items")
                        .ids(itemIdsToResolve)
                        .queryArray(Item.class)
                        .stream()
                        .collect(Collectors.toMap(Item::getId, Function.identity()));
                // Extract skins.
                final int[] skinIdsToResolve = items.values()
                        .stream()
                        .map(Item::getDefaultSkin)
                        .filter(OptionalInt::isPresent)
                        .mapToInt(OptionalInt::getAsInt)
                        .distinct()
                        .sorted()
                        .toArray();
                final Map<Integer, Skin> skins = (skinIdsToResolve.length == 0) ? Collections.emptyMap() : GW2APIClient.create()
                        .endPoint("skins")
                        .ids(skinIdsToResolve)
                        .queryArray(Skin.class)
                        .stream()
                        .collect(Collectors.toMap(Skin::getId, Function.identity()));
                // Wrap.
                final List<CharacterBagWrapper> wrappers = inventoryBags.stream()
                        .map(bag -> {
                            CharacterBagWrapper result = null;
                            if (bag != null) {
                                final Item bagItem = items.get(bag.getId());
                                final CharacterInventoryWrapper[] bagContent = bag.getInventory()
                                        .stream()
                                        .map(inventory -> {
                                            CharacterInventoryWrapper wrapper = null;
                                            if (inventory != null) {
                                                final Item inventoryItem = items.get(inventory.getId());
                                                final Skin inventorySkin = (inventoryItem == null || !inventoryItem.getDefaultSkin().isPresent()) ? null : skins.get(inventoryItem.getDefaultSkin().getAsInt());
                                                wrapper = new CharacterInventoryWrapper(inventory, inventoryItem, inventorySkin);
                                            }
                                            return wrapper;
                                        })
                                        .toArray(CharacterInventoryWrapper[]::new);
                                result = new CharacterBagWrapper(bag, bagItem, bagContent);
                            }
                            return result;
                        })
                        .collect(Collectors.toList());
                Platform.runLater(() -> inventoryPane.getCharacterInventory().setAll(wrappers));
            }
        }
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
