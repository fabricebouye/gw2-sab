/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.inventory;

import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.skins.Skin;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.CharacterBagWrapper;
import com.bouye.gw2.sab.wrappers.CharacterInventoryWrapper;
import com.bouye.gw2.sab.wrappers.SharedInventoryWrapper;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import api.web.gw2.mapping.v2.characters.id.inventory.CharacterInventory;
import api.web.gw2.mapping.v2.account.inventory.AccountInventory;

/**
 * FXML controller.
 * @author Fabrice Bouyé
 */
public final class InventoryPaneController extends SABControllerBase<InventoryPane> {

    @FXML
    private Label inventoryLabel;
    @FXML
    private MenuButton optionsButton;
    @FXML
    private TextField searchField;
    @FXML
    private VBox inventoryVBox;

    final ToggleGroup displayToggleGroup = new ToggleGroup();

    /**
     * Creates a new instance.
     */
    public InventoryPaneController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        inventoryLabel.setText(null);
        //
        Arrays.stream(InventoryDisplay.values())
                .forEach(display -> {
                    final String displayKey = String.format("inventory.display.%s.label", display.name().toLowerCase().replaceAll("_", "-")); // NOI18N.
                    final String displayText = SABConstants.I18N.getString(displayKey);
                    final RadioMenuItem displayItem = new RadioMenuItem(displayText);
                    displayItem.setUserData(display);
                    displayItem.setToggleGroup(displayToggleGroup);
                    optionsButton.getItems().add(displayItem);
                });
        displayToggleGroup.selectedToggleProperty().addListener(selectedDisplayChangeListener);
        searchField.textProperty().addListener(searchTextInvalidationListener);
    }

    @Override
    protected void uninstallNode(final InventoryPane node) {
        node.displayProperty().removeListener(displayChangeListener);
        node.getSharedInventory().removeListener(sharedInventoryListChangeListener);
        node.getCharacterInventory().removeListener(characterInventoryListChangeListener);
    }

    @Override
    protected void installNode(final InventoryPane node) {
        final InventoryDisplay display = node.getDisplay();
        displayToggleGroup.getToggles().stream()
                .forEach(toggle -> toggle.setSelected(toggle.getUserData() == display));
        node.displayProperty().addListener(displayChangeListener);
        node.getSharedInventory().addListener(sharedInventoryListChangeListener);
        node.getCharacterInventory().addListener(characterInventoryListChangeListener);
    }

    private final Map<String, Stream<Node>> inventoryContent = new LinkedHashMap<>();

    @Override
    protected void updateUI() {
        final Optional<InventoryPane> parent = parentNode();
        inventoryContent.clear();
        final InventoryDisplay display = parent.isPresent() ? parent.get().getDisplay() : InventoryDisplay.BAGS_SHOWN;
        final List<SharedInventoryWrapper> sharedInventory = parent.isPresent() ? parent.get().getSharedInventory() : Collections.EMPTY_LIST;
        final List<CharacterBagWrapper> characterInventory = parent.isPresent() ? parent.get().getCharacterInventory() : Collections.EMPTY_LIST;
        //
        final int totalSharedSlots = sharedInventory.size();
        final int totalCharacterSlots = characterInventory.stream()
                .mapToInt(bag -> (bag == null) ? 0 : bag.getBag().getSize())
                .sum();
        final int totalSlots = totalSharedSlots + totalCharacterSlots;
        final int usedSharedSlots = sharedInventory.stream()
                .mapToInt(inventory -> (inventory == null) ? 0 : 1)
                .sum();
        final int usedCharacterSlots = characterInventory.stream()
                .mapToInt(bag -> (bag == null) ? 0 : bag.getBag().getInventory()
                        .stream()
                        .mapToInt(inventory -> (inventory == null) ? 0 : 1)
                        .sum())
                .sum();
        final int usedSlots = usedSharedSlots + usedCharacterSlots;
        inventoryLabel.setText(String.format("%d / %d", usedSlots, totalSlots)); // NOI18N.
        //
        switch (display) {
            case BAGS_SHOWN: {
                final Stream<Node> allSharedInventory = createSharedInventorySlots(sharedInventory);
                inventoryContent.put("shared", allSharedInventory); // NOI18N.
                IntStream.range(0, characterInventory.size())
                        .forEach(index -> {
                            final CharacterBagWrapper bag = characterInventory.get(index);
                            final Stream<Node> bagInventory = createCharacterInventorySlots(bag);
                            final String key = (bag == null) ? String.valueOf(index) : String.valueOf(bag.getBag().getId());
                            inventoryContent.put(key, bagInventory);
                        });
            }
            break;
            case BAGS_HIDDEN: {
                final Stream<Node> allInventory = Stream.concat(createSharedInventorySlots(sharedInventory), createCompactCharacterInventorySlots(characterInventory));
                inventoryContent.put("all", allInventory); // NOI18N.
            }
            break;
            case BAGS_HIDDEN_SEPARATED: {
                final Stream<Node> allSharedInventory = createSharedInventorySlots(sharedInventory);
                inventoryContent.put("shared", allSharedInventory); // NOI18N.
                final Stream<Node> allCharacterInventory = createCompactCharacterInventorySlots(characterInventory);
                inventoryContent.put("character", allCharacterInventory); // NOI18N.
            }
            break;
        }
        if (inventoryContent.containsKey("all")) {
            inventoryVBox.getChildren().setAll(createBagContent(inventoryContent.get("all"))); // NOI18N.
        } else {
            inventoryVBox.getChildren().setAll(
                    inventoryContent.entrySet()
                    .stream()
                    .map(entry -> createBagView(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList())
            );
        }
    }

    // Some of the inventory layout management code will be re-used for bank, collection and wardrobe.
    /**
     * Creates a view of shared inventories.
     * @return A {@code Stream<Node>}, never {@code null}.
     */
    private Stream<Node> createSharedInventorySlots(final List<SharedInventoryWrapper> sharedInventory) {
        return sharedInventory
                .stream()
                .map(this::createSharedInventorySlot);
    }

    /**
     * Creates a view of a character bag.
     * @return A {@code Stream<Node>}, never {@code null}.
     */
    private Stream<Node> createCharacterInventorySlots(final CharacterBagWrapper bag) {
        return (bag == null) ? Stream.empty() : bag.getContent()
                .stream()
                .map(this::createCharacterInventorySlot);
    }

    /**
     * Creates a compact view of all character bags.
     * @return A {@code Stream<Node>}, never {@code null}.
     */
    private Stream<Node> createCompactCharacterInventorySlots(final List<CharacterBagWrapper> characterInventory) {
        Stream<Node> result = Stream.empty();
        for (final CharacterBagWrapper bag : characterInventory) {
            result = Stream.concat(result, createCharacterInventorySlots(bag));
        }
        return result;
    }

    private Node createBagContent(final Stream<Node> nodes) {
        final TilePane container = new TilePane();
        container.getStyleClass().add("bag-content"); // NOI18N.
        container.getChildren().setAll(nodes.collect(Collectors.toList()));
        return container;
    }

    private Node createBagView(final String bagName, final Stream<Node> nodes) {
        final Node content = createBagContent(nodes);
        final TitledPane titledPane = new TitledPane();
        titledPane.getStyleClass().add("bag"); // NOI18N.
        titledPane.setText(bagName);
        titledPane.setContent(content);
        return titledPane;
    }

    private static final PseudoClass ACCOUNT_PSEUDO_CLASS = PseudoClass.getPseudoClass("account"); // NOI18N.
    private static final PseudoClass CHARACTER_PSEUDO_CLASS = PseudoClass.getPseudoClass("character"); // NOI18N.

    private Node createSharedInventorySlot(final SharedInventoryWrapper value) {
        final StackPane result = new StackPane();
        result.getStyleClass().add("slot"); // NOI18N.
        result.pseudoClassStateChanged(ACCOUNT_PSEUDO_CLASS, true);
        if (value != null) {
            final AccountInventory inventory = value.getInventory();
            final Item item = value.getItem();
            final Skin skin = value.getSkin();
            final int id = inventory.getId();
            result.setUserData(id);
            final int count = inventory.getCount();
            final Text countText = new Text();
            countText.setText(String.valueOf(count));
            result.getChildren().add(countText);
        };
        return result;
    }

    private Node createCharacterInventorySlot(final CharacterInventoryWrapper value) {
        final StackPane result = new StackPane();
        result.getStyleClass().add("slot"); // NOI18N.
        result.pseudoClassStateChanged(CHARACTER_PSEUDO_CLASS, true);
        if (value != null) {
            final CharacterInventory inventory = value.getInventory();
            final Item item = value.getItem();
            final Skin skin = value.getSkin();
            final int id = inventory.getId();
            result.setUserData(id);
            final int count = inventory.getCount();
            final Text countText = new Text();
            countText.setText(String.valueOf(count));
            result.getChildren().add(countText);
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Called whenever the selected display menu item changes in the options menu.
     */
    private final ChangeListener<Toggle> selectedDisplayChangeListener = (observable, oldValue, newValue) -> {
        if (newValue instanceof RadioMenuItem) {
            final InventoryDisplay display = (InventoryDisplay) newValue.getUserData();
            parentNode().ifPresent(n -> n.setDisplay(display));
        }
    };

    /**
     * Called whenever the display value changes in the parent node.
     */
    private final ChangeListener<InventoryDisplay> displayChangeListener = (observable, oldValue, newValue) -> updateUI();

    /**
     * Called whenever the shared inventory changes in the parent node.
     */
    private final ListChangeListener<SharedInventoryWrapper> sharedInventoryListChangeListener = change -> updateUI();

    /**
     * Called whenever the character inventory changes in the parent node.
     */
    private final ListChangeListener<CharacterBagWrapper> characterInventoryListChangeListener = change -> updateUI();

    /**
     * Called whenever the search text is invalidated.
     */
    private final InvalidationListener searchTextInvalidationListener = observable -> applySearchFilter();

    /**
     * Apply filter from the search box.
     */
    private void applySearchFilter() {
        final String searchValue = searchField.getText();
        // @todo Filter content.
    }
}
