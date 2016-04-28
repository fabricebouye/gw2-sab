/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.bank;

import api.web.gw2.mapping.v2.account.bank.BankSlot;
import com.bouye.gw2.sab.scene.SABControllerBase;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * FXML controller.
 * @author Fabrice Bouyé
 */
public final class BankPaneController extends SABControllerBase<BankPane> {

    public static final int TAB_SIZE = 30;

    @FXML
    private TextField searchField;
    @FXML
    private VBox bankVBox;

    /**
     * Create a new instance.
     */
    public BankPaneController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }

    @Override
    protected void uninstallNode(final BankPane node) {
        node.getSlots().removeListener(slotListChangeListener);
    }

    @Override
    protected void installNode(final BankPane node) {
        node.getSlots().addListener(slotListChangeListener);
    }

    private final Map<String, Stream<Node>> bankContent = new LinkedHashMap<>();

    @Override
    protected void updateUI() {
        final Optional<BankPane> parent = parentNode();
        bankContent.clear();
        final List<BankSlot> slots = (parent.isPresent()) ? parent.get().getSlots() : Collections.EMPTY_LIST;
        final int tabNumber = slots.size() / TAB_SIZE;
        IntStream.range(0, tabNumber)
                .forEach(tabIndex -> {
                    final int startIndex = tabIndex * TAB_SIZE;
                    final List<BankSlot> tabSlots = slots.subList(startIndex, startIndex + TAB_SIZE);
                    final Stream<Node> bankInventory = createTabSlots(tabSlots);
                    final String key = String.format("tab %d", tabIndex + 1);
                    bankContent.put(key, bankInventory);
                });
        bankVBox.getChildren().setAll(
                bankContent.entrySet()
                .stream()
                .map(entry -> createTabView(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList())
        );
    }

    private Stream<Node> createTabSlots(final List<BankSlot> slots) {
        return slots.stream()
                .map(Optional::ofNullable)
                .map(this::createInventorySlot);
    }

    private Node createInventorySlot(final Optional<BankSlot> value) {
        final StackPane result = new StackPane();
        result.getStyleClass().add("slot"); // NOI18N.
        value.ifPresent(val -> {
            final int id = val.getId();
            result.setUserData(id);
            final int count = val.getCount();
            final Text countText = new Text();
            countText.setText(String.valueOf(count));
            result.getChildren().add(countText);
        });
        return result;
    }

    private Node createTabContent(final Stream<Node> nodes) {
        final TilePane container = new TilePane();
        container.getStyleClass().add("bank-tab-content"); // NOI18N.
        container.getChildren().setAll(nodes.collect(Collectors.toList()));
        return container;
    }

    private Node createTabView(final String bagName, final Stream<Node> nodes) {
        final Node content = createTabContent(nodes);
        final TitledPane titledPane = new TitledPane();
        titledPane.getStyleClass().add("bank-tab"); // NOI18N.
        titledPane.setText(bagName);
        titledPane.setContent(content);
        return titledPane;
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Called whenever the slot content changes in the parent node.
     */
    private ListChangeListener<BankSlot> slotListChangeListener = change -> updateUI();
}
