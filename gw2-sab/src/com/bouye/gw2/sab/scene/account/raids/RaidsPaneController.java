/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.raids;

import api.web.gw2.mapping.v2.raids.Raid;
import api.web.gw2.mapping.v2.raids.RaidWing;
import api.web.gw2.mapping.v2.raids.RaidWingEvent;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.RaidsWrapper;
import java.util.Optional;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * FXML controller.
 * @author Fabrice Bouyé
 */
public final class RaidsPaneController extends SABControllerBase<RaidsPane> {

    @FXML
    private VBox rootPane;

    @Override
    protected void uninstallNode(final RaidsPane node) {
        node.raidsProperty().removeListener(raidsChangeListener);
    }

    @Override
    protected void installNode(final RaidsPane node) {
        node.raidsProperty().addListener(raidsChangeListener);
    }

    /**
     * Called whenever the raid values changes.
     */
    private final ChangeListener<RaidsWrapper> raidsChangeListener = (observable, oldValue, newValue) -> updateUI();

    @Override
    protected void updateUI() {
        rootPane.getChildren().clear();
        //
        final Optional<RaidsPane> parent = parentNode();
        final RaidsWrapper wrapper = (parent.isPresent()) ? parent.get().getRaids() : null;
        if (wrapper == null) {
            return;
        }
        final Set<String> encounterIds = wrapper.getEncounterIds();
        wrapper.getRaids()
                .stream()
                .map(raid -> createNodeForRaid(raid, encounterIds))
                .forEach(rootPane.getChildren()::add);
    }

    private Node createNodeForRaid(final Raid raid, final Set<String> encounterIds) {
        final VBox content = new VBox();
        final TitledPane result = new TitledPane();
        result.getStyleClass().add("raid");
        result.setText(raid.getId());
        result.setContent(content);
        result.setCollapsible(false);
        result.setExpanded(true);
        final Set<RaidWing> wings = raid.getWings();
        // So far only raid #1 has multiple wings.
        if (wings.size() > 1) {
            wings.stream()
                    .map(wing -> createNodeForWing(wing, encounterIds))
                    .forEach(content.getChildren()::add);
        } else {
            wings.iterator()
                    .next()
                    .getEvents()
                    .stream()
                    .map(event -> createNodeForEvent(event, encounterIds))
                    .forEach(content.getChildren()::add);
        }
        return result;
    }

    private Node createNodeForWing(final RaidWing wing, final Set<String> encounterIds) {
        final VBox content = new VBox();
        final TitledPane result = new TitledPane();
        result.getStyleClass().add("wing");
        result.setText(wing.getId());
        result.setContent(content);
        result.setCollapsible(false);
        result.setExpanded(true);
        wing.getEvents()
                .stream()
                .map(event -> createNodeForEvent(event, encounterIds))
                .forEach(content.getChildren()::add);
        return result;
    }

    final PseudoClass DONE_PSEUDO_CLASS = PseudoClass.getPseudoClass("done");

    private Node createNodeForEvent(final RaidWingEvent event, final Set<String> encounterIds) {
        final String id = event.getId();
        final Label result = new Label(id);
        result.getStyleClass().add("event");
        result.pseudoClassStateChanged(DONE_PSEUDO_CLASS, encounterIds.contains(id));
        return result;
    }
}
