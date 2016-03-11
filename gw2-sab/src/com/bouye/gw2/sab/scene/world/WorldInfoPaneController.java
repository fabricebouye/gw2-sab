/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.world;

import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.account.AccountInfoPaneController;
import com.bouye.gw2.sab.tasks.world.WorldSolverTask;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        Bindings.select(nodeProperty(), "worldId").addListener((observable, oldValue, newValue) -> updateContent());
    }

    @Override
    protected void clearContent(final WorldInfoPane parent) {
        nameLabel.setText(null);
        regionLabel.setText(null);
        languageLabel.setText(null);
        populationLabel.setText(null);
    }

    @Override
    protected void installContent(final WorldInfoPane parent) {
        final int worldId = parent.getWorldId();
        if (worldId == -1) {
            return;
        }
        nameLabel.setText(String.valueOf(worldId));
        updateWorldValuesAsync(worldId);
    }

    private void updateWorldValuesAsync(final int worldId) {
        final Service<List<World>> service = new Service<List<World>>() {
            @Override
            protected Task<List<World>> createTask() {
                return new WorldSolverTask(worldId);
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final List<World> result = service.getValue();
            if (!result.isEmpty()) {
                final World world = result.get(0);
                nameLabel.setText(world.getName());
                regionLabel.setText(world.getRegion().name());
                languageLabel.setText(world.getLanguage().name());
                populationLabel.setText(world.getPopulation().name());
            }
        });
        addAndStartService(service, "updateWorldValuesAsync");
    }
}
