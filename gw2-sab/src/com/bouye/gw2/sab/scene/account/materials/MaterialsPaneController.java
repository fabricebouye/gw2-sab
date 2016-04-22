/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.materials;

import api.web.gw2.mapping.v2.account.materials.Material;
import api.web.gw2.mapping.v2.materials.MaterialStorage;
import com.bouye.gw2.sab.scene.SABControllerBase;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * FXML controller.
 * @author Fabrice Bouyé
 */
public final class MaterialsPaneController extends SABControllerBase<MaterialsPane> {
    
    @FXML
    private TextField searchField;
    @FXML
    private VBox materialsVBox;

    /**
     * Create a new instance.
     */
    public MaterialsPaneController() {
    }
    
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }
    
    @Override
    protected void uninstallNode(final MaterialsPane node) {
        node.getMaterialStorage().removeListener(materialStorageListChangeListener);
        node.getMaterials().removeListener(materialsListChangeListener);
    }
    
    @Override
    protected void installNode(final MaterialsPane node) {
        node.getMaterialStorage().addListener(materialStorageListChangeListener);
        node.getMaterials().addListener(materialsListChangeListener);
    }
    
    private final Map<String, Stream<Node>> materialsContent = new LinkedHashMap<>();
    
    @Override
    protected void updateUI() {
        final Optional<MaterialsPane> parent = parentNode();
        materialsContent.clear();
        final List<MaterialStorage> materialCategories = (parent.isPresent()) ? parent.get().getMaterialStorage() : Collections.EMPTY_LIST;
        final List<Material> accountMaterials = (parent.isPresent()) ? parent.get().getMaterials() : Collections.EMPTY_LIST;
        // Nothing to do.
        if (materialCategories.isEmpty()) {
            return;
        }
        materialCategories.stream()
                .forEach(materialCategory -> {
                    final String key = materialCategory.getName();
                    final int categoryId = materialCategory.getId();
                    final List<Material> categoryMaterials = accountMaterials.stream()
                            .filter(material -> material.getCategory() == categoryId)
                            .collect(Collectors.toList());
                    final Stream<Node> materialsInventory = createTabSlots(materialCategory.getId(), materialCategory.getItems(), categoryMaterials);
                    materialsContent.put(key, materialsInventory);
                });
        materialsVBox.getChildren().setAll(
                materialsContent.entrySet()
                .stream()
                .map(entry -> createTabView(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList())
        );
    }
    
    private Stream<Node> createTabSlots(final int categoryId, final Set<Integer> materialIds, final List<Material> accountMaterials) {
        return materialIds.stream()
                .map(materialId -> {
                    final Optional<Material> material = accountMaterials.stream()
                            .filter(m -> m.getId() == materialId)
                            .findFirst();
                    return createInventorySlot(categoryId, material);
                });
    }
    
    private Node createInventorySlot(final int materialId, final Optional<Material> material) {
        final StackPane result = new StackPane();
        result.getStyleClass().add("slot"); // NOI18N.
        result.setUserData(materialId);
        material.ifPresent(m -> {
            final int count = m.getCount();
            final Text counText = new Text();
            counText.setText(String.valueOf(count));
            result.getChildren().add(counText);
        });
        return result;
    }
    
    private Node createTabContent(final Stream<Node> nodes) {
        final FlowPane flowPane = new FlowPane();
        flowPane.getStyleClass().add("materials-tab-content"); // NOI18N.
        flowPane.getChildren().setAll(nodes.collect(Collectors.toList()));
        return flowPane;
    }
    
    private Node createTabView(final String bagName, final Stream<Node> nodes) {
        final Node content = createTabContent(nodes);
        final TitledPane titledPane = new TitledPane();
        titledPane.getStyleClass().add("materials-tab"); // NOI18N.
        titledPane.setText(bagName);
        titledPane.setContent(content);
        return titledPane;
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Called whenever the material storage categories changes in the parent node.
     */
    private ListChangeListener<MaterialStorage> materialStorageListChangeListener = change -> updateUI();

    /**
     * Called whenever the slot content changes in the parent node.
     */
    private ListChangeListener<Material> materialsListChangeListener = change -> updateUI();
}
