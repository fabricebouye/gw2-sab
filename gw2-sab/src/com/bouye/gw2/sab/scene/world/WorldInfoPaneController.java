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
import com.bouye.gw2.sab.scene.wvw.WvwSummaryPaneController;
import com.bouye.gw2.sab.tasks.world.WorldSolverTask;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

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
    private WvwSummaryPaneController wvwSummaryController;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        worlId = Bindings.selectInteger(nodeProperty(), "worldId");
        worlId.addListener(worldIdInvalidationListener);
    }

    @Override
    public void dispose() {
        try {
            worlId.removeListener(worldIdInvalidationListener);
            worlId.dispose();
            if (worldQueryService != null) {
                worldQueryService.cancel();
            }
        } finally {
            super.dispose();
        }
    }

    private final InvalidationListener worldIdInvalidationListener = observable -> updateContent();

    private IntegerBinding worlId;

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
        wvwSummaryController.setWorldId(worldId);
    }

    private ScheduledService<List<World>> worldQueryService;

    private void updateWorldValuesAsync(final int worldId) {
        // Service lazy initialization.
        if (worldQueryService == null) {
            final ScheduledService<List<World>> service = new ScheduledService<List<World>>() {
                @Override
                protected Task<List<World>> createTask() {
                    return new WorldSolverTask(worldId);
                }
            };
            service.setPeriod(Duration.minutes(5));
            service.setRestartOnFailure(true);
            service.setOnSucceeded(workerStateEvent -> {
                final List<World> result = (List<World>) workerStateEvent.getSource().getValue();
                if (!result.isEmpty()) {
                    final World world = result.get(0);
                    nameLabel.setText(world.getName());
                    regionLabel.setText(world.getRegion().name());
                    languageLabel.setText(world.getLanguage().name());
                    populationLabel.setText(world.getPopulation().name());
                }
            });
            worldQueryService = service;
        }
        addAndStartService(worldQueryService, "WorldInfoPaneController::updateWorldValuesAsync");
    }
}
