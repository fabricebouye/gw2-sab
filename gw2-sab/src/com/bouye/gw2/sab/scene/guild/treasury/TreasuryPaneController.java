/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild.treasury;

import api.web.gw2.mapping.v2.guild.id.treasury.Treasury;
import api.web.gw2.mapping.v2.guild.id.treasury.TreasuryUpgrade;
import api.web.gw2.mapping.v2.guild.upgrades.Upgrade;
import api.web.gw2.mapping.v2.items.Item;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.TreasuryWrapper;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * FXML Controller class.
 * @author Fabrice Bouyé
 */
public final class TreasuryPaneController extends SABControllerBase<TreasuryPane> {

    @FXML
    private TextField searchField;
    @FXML
    private FlowPane treasuryContentFlow;

    /**
     * Creates a new instance.
     */
    public TreasuryPaneController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        treasury.addListener(treasuryListChangeListener);
        searchField.textProperty().addListener(searchTextInvalidationListener);
    }

    @Override
    protected void uninstallNode(final TreasuryPane node) {
        treasury.set(null);
    }

    @Override
    protected void installNode(final TreasuryPane node) {
        treasury.set(node.getTreasury());
    }

    @Override
    protected void updateUI() {
        treasuryContentFlow.getChildren().clear();
        nodes.clear();
        final List<Node> slots = treasury.stream()
                .map(this::createTreasurySlot)
                .collect(Collectors.toList());
        nodes.setAll(slots);
        treasuryContentFlow.getChildren().setAll(filteredNodes);
    }

    private Node createTreasurySlot(final TreasuryWrapper value) {
        final VBox result = new VBox();
        result.getStyleClass().add("treasury"); // NOI18N.
        result.setUserData(value);
        result.getChildren().add(createInventorySlot(value));
        final int count = value.getTreasury().getCount();
        final int totalCount = value.getTreasury().getNeededBy()
                .stream()
                .mapToInt(TreasuryUpgrade::getCount)
                .sum();
        final Text quantityText = new Text();
        quantityText.setId("quantityText"); // NOI18N.
        quantityText.getStyleClass().add("quantity-label"); // NOI18N.
        quantityText.setText(String.format("%d / %d", count, totalCount));
        result.getChildren().add(quantityText);
        return result;
    }

    private Node createInventorySlot(final TreasuryWrapper value) {
        final StackPane result = new StackPane();
        result.getStyleClass().add("slot"); // NOI18N.
        final int id = value.getTreasury().getId();
        result.setUserData(id);
        final int count = value.getTreasury().getCount();
        final Text countText = new Text();
        countText.setText(String.valueOf(count));
        result.getChildren().add(countText);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    private final ListProperty<TreasuryWrapper> treasury = new SimpleListProperty<>(this, "treasury"); // NOI18N.

    private final ObservableList<Node> nodes = FXCollections.observableArrayList();
    private final FilteredList<Node> filteredNodes = new FilteredList<>(nodes);
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Called whenever the content of the treasury changes.
     */
    private final ListChangeListener<TreasuryWrapper> treasuryListChangeListener = change -> updateUI();

    /**
     * Called whenever the search text is invalidated.
     */
    private final InvalidationListener searchTextInvalidationListener = observable -> applySearchFilter();

    /**
     * Apply filter from the search box.
     */
    private void applySearchFilter() {
        final String searchValue = searchField.getText();
        Predicate<Node> predicate = null;
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            final String criteria = searchValue.trim().toLowerCase();
            predicate = node -> {
                final TreasuryWrapper treasuryWrapper = (TreasuryWrapper) node.getUserData();
                final Treasury treasury = treasuryWrapper.getTreasury();
                final Item item = treasuryWrapper.getItem();
                final List<Upgrade> upgrades = treasuryWrapper.getUpgrades();
                boolean result = false;
                result |= (item == null) ? false : item.getName().toLowerCase().contains(criteria);
                result |= upgrades.stream()
                        .map(upgrade -> (upgrade == null) ? false : upgrade.getName().toLowerCase().contains(criteria))
                        .reduce(false, (accumulator, value) -> accumulator | value);
                result |= String.valueOf(treasury.getCount()).contains(criteria);
                return result;
            };
        }
        filteredNodes.setPredicate(predicate);
        //
        treasuryContentFlow.getChildren().clear();
        treasuryContentFlow.getChildren().setAll(filteredNodes);
    }
}