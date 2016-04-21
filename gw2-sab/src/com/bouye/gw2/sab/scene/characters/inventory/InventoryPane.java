/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.inventory;

import api.web.gw2.mapping.v2.account.inventory.SharedInventory;
import api.web.gw2.mapping.v2.characters.inventory.Inventory;
import api.web.gw2.mapping.v2.characters.inventory.InventoryBag;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Display the account and character inventory.
 * @author Fabrice Bouyé
 */
public final class InventoryPane extends BorderPane {

    private final TextField searchField = new TextField();
    private final MenuButton optionsButton = new MenuButton();
    private final HBox topBar = new HBox();
    private final VBox container = new VBox();

    /**
     * Creates a new instance.
     */
    public InventoryPane() {
        super();
        setId("inventoryPane"); // NOI18N.
        getStyleClass().add("inventory-pane"); // NOI18N.
//        setPrefHeight(USE_COMPUTED_SIZE);
        //
        final Text optionsGraphic = new Text(SABConstants.I18N.getString("icon.fa.gear")); // NOI18N.
        optionsGraphic.getStyleClass().add("awesome-icon"); // NOI18N.
        optionsButton.setId("optionsButton"); // NOI18N.
        optionsButton.getStyleClass().add("options-button"); // NOI18N.
        optionsButton.setText(SABConstants.I18N.getString("inventory.options.label")); // NOI18N.)
        optionsButton.setGraphic(optionsGraphic);
        final ToggleGroup displayToggleGroup = new ToggleGroup();
        Arrays.stream(InventoryDisplay.values())
                .forEach(display -> {
                    final String displayKey = String.format("inventory.display.%s.label", display.name().toLowerCase().replaceAll("_", "-")); // NOI18N.
                    final String displayText = SABConstants.I18N.getString(displayKey);
                    final RadioMenuItem displayItem = new RadioMenuItem(displayText);
                    displayItem.setSelected(display == getDisplay());
                    displayItem.setUserData(display);
                    displayItem.setToggleGroup(displayToggleGroup);
                    optionsButton.getItems().add(displayItem);
                });
        displayToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof RadioMenuItem) {
                final InventoryDisplay display = (InventoryDisplay) newValue.getUserData();
                setDisplay(display);
            }
        });
        HBox.setHgrow(searchField, Priority.ALWAYS);
        topBar.setId("topBar"); // NOI18N.
        topBar.getStyleClass().add("top-bar"); // NOI18N.
        topBar.getChildren().setAll(searchField, optionsButton);
        setTop(topBar);
        setCenter(container);
        //
        displayProperty().addListener(displayChangeListener);
        getSharedInventory().addListener(sharedInventoryListChangeListener);
        getCharacterInventory().addListener(characterInventoryListChangeListener);
    }

    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/characters/inventory/InventoryPane.css"); // NOI18N.
        return url.toExternalForm();
    }

    private final Map<String, Stream<Node>> inventoryContent = new LinkedHashMap<>();

    private void generateContent() {
        inventoryContent.clear();
        final InventoryDisplay display = getDisplay();
        switch (display) {
            case BAGS_SHOWN: {
                final Stream<Node> allSharedInventory = createSharedInventorySlot();
                inventoryContent.put("shared", allSharedInventory); // NOI18N.
                final List<InventoryBag> bags = getCharacterInventory();
                IntStream.range(0, bags.size())
                        .forEach(index -> {
                            InventoryBag bag = bags.get(index);
                            final Stream<Node> bagInventory = createCharacterInventorySlots(bag);
                            final String key = (bag == null) ? String.valueOf(index) : String.valueOf(bag.getId());
                            inventoryContent.put(key, bagInventory);
                        });
            }
            break;
            case BAGS_HIDDEN: {
                final Stream<Node> allInventory = Stream.concat(createSharedInventorySlot(), createCompactCharacterInventorySlots());
                inventoryContent.put("all", allInventory); // NOI18N.
            }
            break;
            case BAGS_HIDDEN_SEPARATED: {
                final Stream<Node> allSharedInventory = createSharedInventorySlot();
                inventoryContent.put("shared", allSharedInventory); // NOI18N.
                final Stream<Node> allCharacterInventory = createCompactCharacterInventorySlots();
                inventoryContent.put("character", allCharacterInventory); // NOI18N.
            }
            break;
        }
        if (inventoryContent.containsKey("all")) {
            container.getChildren().setAll(createBagContent(inventoryContent.get("all"))); // NOI18N.
        } else {
            container.getChildren().setAll(
                    inventoryContent.entrySet()
                    .stream()
                    .map(entry -> createBagView(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList())
            );
        }
    }

    /**
     * Creates a view of shared inventories.
     * @return A {@code Stream<Node>}, never {@code null}.
     */
    private Stream<Node> createSharedInventorySlot() {
        return getSharedInventory()
                .stream()
                .map(si -> createInventorySlot(Optional.ofNullable(si), true));
    }

    /**
     * Creates a view of a character bag.
     * @return A {@code Stream<Node>}, never {@code null}.
     */
    private Stream<Node> createCharacterInventorySlots(final InventoryBag bag) {
        return (bag == null) ? Stream.empty() : bag.getInventory()
                .stream()
                .map(i -> createInventorySlot(Optional.ofNullable(i), false));
    }

    /**
     * Creates a compact view of all character bags.
     * @return A {@code Stream<Node>}, never {@code null}.
     */
    private Stream<Node> createCompactCharacterInventorySlots() {
        Stream<Node> result = Stream.empty();
        for (final InventoryBag bag : getCharacterInventory()) {
            result = Stream.concat(result, createCharacterInventorySlots(bag));
        }
        return result;
    }

    private Node createBagContent(final Stream<Node> nodes) {
        final FlowPane flowPane = new FlowPane();
        flowPane.getStyleClass().add("bag-content"); // NOI18N.
        flowPane.getChildren().setAll(nodes.collect(Collectors.toList()));
        return flowPane;
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

    private Node createInventorySlot(final Optional<?> value, final boolean isShared) {
        final StackPane result = new StackPane();
        result.getStyleClass().add("slot"); // NOI18N.
        result.pseudoClassStateChanged(ACCOUNT_PSEUDO_CLASS, isShared);
        result.pseudoClassStateChanged(CHARACTER_PSEUDO_CLASS, !isShared);
        value.ifPresent(val -> {
            final int id = (val instanceof SharedInventory) ? ((SharedInventory) val).getId() : ((Inventory) val).getId();
            result.setUserData(id);
            final int count = (val instanceof SharedInventory) ? ((SharedInventory) val).getCount() : ((Inventory) val).getCount();
            final Text counText = new Text();
            counText.setText(String.valueOf(count));
            result.getChildren().add(counText);
        });
        return result;
    }

    private final ObservableList<SharedInventory> sharedInventory = FXCollections.observableArrayList();

    public final ObservableList<SharedInventory> getSharedInventory() {
        return sharedInventory;
    }

    private final ObservableList<InventoryBag> characterInventory = FXCollections.observableArrayList();

    public ObservableList<InventoryBag> getCharacterInventory() {
        return characterInventory;
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Called whenever the display value changes.
     */
    private final ChangeListener<InventoryDisplay> displayChangeListener = (observable, oldValue, newValue) -> {
        generateContent();
        requestLayout();
    };

    /**
     * Called whenever the shared inventory changes.
     */
    private final ListChangeListener<SharedInventory> sharedInventoryListChangeListener = change -> {
        generateContent();
        requestLayout();
    };

    /**
     * Called whenever the character inventory changes.
     */
    private final ListChangeListener<InventoryBag> characterInventoryListChangeListener = change -> {
        generateContent();
        requestLayout();
    };
    ////////////////////////////////////////////////////////////////////////////
    private final BooleanProperty showRarity = new SimpleBooleanProperty(this, "showRarity", false); // NOI18N.

    public final void setShowRarity(final boolean value) {
        showRarity.set(value);
    }

    public final boolean isShowRarity() {
        return showRarity.get();
    }

    public final BooleanProperty showRarityProperty() {
        return showRarity;
    }

    private final ReadOnlyObjectWrapper<InventoryDisplay> display = new ReadOnlyObjectWrapper<>(this, "display", InventoryDisplay.BAGS_SHOWN);

    public final void setDisplay(final InventoryDisplay value) {
        display.set(value == null ? InventoryDisplay.BAGS_SHOWN : value);
    }

    public final InventoryDisplay getDisplay() {
        return display.get();
    }

    public final ReadOnlyObjectProperty<InventoryDisplay> displayProperty() {
        return display.getReadOnlyProperty();
    }
}
