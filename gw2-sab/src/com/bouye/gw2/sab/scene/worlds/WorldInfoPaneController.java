/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.worlds;

import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.scene.wvw.WvwSummaryPane;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class WorldInfoPaneController extends SABControllerBase<WorldInfoPane> {

    @FXML
    private Label nameLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label populationLabel;
    @FXML
    private StackPane wvwSummaryContainer;

    private WvwSummaryPane wvwSummaryPane;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        wvwSummaryPane = new WvwSummaryPane();
        wvwSummaryContainer.getChildren().add(wvwSummaryPane);
        updateUI();
    }

    @Override
    public void dispose() {
        try {
        } finally {
            super.dispose();
        }
    }

    /**
     * Called whenever world is invalidated.
     */
    private final InvalidationListener worldInvalidationListener = observable -> updateUI();

    @Override
    protected void uninstallNode(final WorldInfoPane parent) {
        parent.worldProperty().removeListener(worldInvalidationListener);
        wvwSummaryPane.matchProperty().unbind();
    }

    @Override
    protected void installNode(final WorldInfoPane parent) {
        parent.worldProperty().addListener(worldInvalidationListener);
        wvwSummaryPane.matchProperty().bind(parent.matchProperty());
    }

    @Override
    protected void updateUI() {
        final Optional<WorldInfoPane> parent = parentNode();
        final World world = parent.isPresent() ? parent.get().getWorld() : null;
        if (world == null) {
            nameLabel.setText(null);
            regionLabel.setText(null);
            languageLabel.setText(null);
            populationLabel.setText(null);
        } else {
            nameLabel.setText(world.getName());
            regionLabel.setText(world.getRegion().name());
            languageLabel.setText(world.getLanguage().name());
            populationLabel.setText(world.getPopulation().name());
        }
    }
}
