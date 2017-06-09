/*
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.masteries;

import api.web.gw2.mapping.v2.masteries.Mastery;
import api.web.gw2.mapping.v2.masteries.MasteryLevel;
import api.web.gw2.mapping.v2.masteries.MasteryRegion;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.AccountMasteriesWrapper;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * FXML controller.
 * @author Fabrice Bouyé
 */
public final class MasteriesPaneController extends SABControllerBase<MasteriesPane> {

    @FXML
    private TreeView<Object> categoryTreeView;
    @FXML
    private Label regionLabel;
    @FXML
    private Label masteryLabel;
    @FXML
    private ListView<MasteryLevel> levelsListView;

    /**
     * Map: region %rarr; pseudo-class.
     */
    private final Map<MasteryRegion, PseudoClass> regionPseudoClasses;

    /**
     * Creates a new instance.
     */
    public MasteriesPaneController() {
        regionPseudoClasses = Arrays.stream(MasteryRegion.values())
                .collect(Collectors.toMap(region -> region, region -> PseudoClass.getPseudoClass(region.name())));
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        categoryTreeView.setCellFactory(treeView -> new MasteryTreeCell());
        categoryTreeView.getSelectionModel().selectedItemProperty().addListener(treeSelectionChangeListener);
        //
        levelsListView.setCellFactory(listView -> new MasteryLevelListCell());
    }

    @Override
    protected void uninstallNode(final MasteriesPane node) {
        node.masteriesProperty().removeListener(masteriesChangeListener);
        final AccountMasteriesWrapper wrapper = node.getMasteries();
        Optional.ofNullable(wrapper)
                .ifPresent(this::uninstallMastery);
    }

    @Override
    protected void installNode(final MasteriesPane node) {
        node.masteriesProperty().addListener(masteriesChangeListener);
        final AccountMasteriesWrapper wrapper = node.getMasteries();
        Optional.ofNullable(wrapper)
                .ifPresent(this::installMastery);
    }

    /*
     * Uninstall previous mastery.
     * @param wrappers The wrapper.
     */
    protected void uninstallMastery(final AccountMasteriesWrapper wrapper) {
        categoryTreeView.setRoot(null);
    }

    /*
     * Install new mastery.
     * @param wrappers The wrapper.
     */
    protected void installMastery(final AccountMasteriesWrapper wrapper) {
        final TreeItem root = createTree(wrapper);
        categoryTreeView.setRoot(root);
    }

    private TreeItem createTree(final AccountMasteriesWrapper wrapper) {
        final MasteryRegion[] regions = Arrays.stream(MasteryRegion.values())
                .filter(region -> region != MasteryRegion.UNKNOWN)
                .toArray(MasteryRegion[]::new);
        final TreeItem result = new TreeItem();
        result.getChildren().setAll(Arrays.stream(regions)
                .map(region -> treeItemForMasteryRegion(region, wrapper))
                .collect(Collectors.toList()));
        return result;
    }

    private TreeItem treeItemForMasteryRegion(final MasteryRegion region, final AccountMasteriesWrapper wrapper) {
        final TreeItem result = new TreeItem();
        result.setValue(region);
        final Set<Mastery> masteries = wrapper.getMasteries();
        result.getChildren().setAll(masteries.stream()
                .filter(mastery -> region == mastery.getRegion())
                .sorted((m1, m2) -> m1.getOrder() - m2.getOrder())
                .map(this::treeItemForMastery)
                .collect(Collectors.toList()));
        return result;
    }

    private TreeItem treeItemForMastery(final Mastery mastery) {
        final TreeItem result = new TreeItem();
        result.setValue(mastery);
        return result;
    }

    /**
     * Uninstall new content from the tree selection.
     * @param item The tree item to uninstall.
     */
    private void uninstallContentFromTree(final TreeItem item) {
        masteryLabel.setText(null);
        regionLabel.setText(null);
        levelsListView.getItems().clear();
        regionPseudoClasses.values()
                .stream()
                .forEach(pseudoClass -> levelsListView.pseudoClassStateChanged(pseudoClass, false));
    }

    /**
     * Install new content from the tree selection.
     * @param item The tree item to install.
     */
    private void installContentFromTree(final TreeItem item) {
        final Optional<MasteriesPane> node = parentNode();
        final AccountMasteriesWrapper wrapper = (node.isPresent()) ? node.get().getMasteries() : null;
        if (wrapper == null) {
            return;
        }
        final Object value = item.getValue();
        if (value instanceof MasteryRegion) {
            final MasteryRegion region = (MasteryRegion) value;
            masteryLabel.setText(null);
            regionLabel.setText(region.name());
        } else if (value instanceof Mastery) {
            final Mastery mastery = (Mastery) value;
            masteryLabel.setText(mastery.getName());
            final MasteryRegion masteryRegion = mastery.getRegion();
            regionLabel.setText(masteryRegion.name());
            levelsListView.getItems().setAll(mastery.getLevels());
            // Set proper pseudo-class for current region.
            final PseudoClass masteryRegionPseudoClass = regionPseudoClasses.get(masteryRegion);
            levelsListView.pseudoClassStateChanged(masteryRegionPseudoClass, true);
        }
    }

    /**
     * Called whenever the masteries wrapper changes in the parent node.
     */
    private final ChangeListener<AccountMasteriesWrapper> masteriesChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallMastery);
        Optional.ofNullable(newValue)
                .ifPresent(this::installMastery);
    };

    /**
     * Called whenever the selection changes in the tree.
     */
    private final ChangeListener<TreeItem> treeSelectionChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallContentFromTree);
        Optional.ofNullable(newValue)
                .ifPresent(this::installContentFromTree);
    };
}
