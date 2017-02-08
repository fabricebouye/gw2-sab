/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild;

import api.web.gw2.mapping.core.APILevel;
import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.guild.id.Guild;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.GW2APIClient;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.scene.guild.treasury.TestTreasuryPane;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.GuildInfoWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestGuildInfoPane extends Application {

    private Stage stage;
    private ComboBox<String> guildIdCombo;
    private GuildInfoPane guildInfoPane;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        guildInfoPane = new GuildInfoPane();
        guildIdCombo = new ComboBox<>();
        guildIdCombo.getSelectionModel().selectedItemProperty().addListener(guildIdChangeListener);
        guildIdCombo.getItems().setAll(session.getAccount().getGuilds());
        final BorderPane root = new BorderPane();
        root.setTop(guildIdCombo);
        root.setCenter(guildInfoPane);
        final Scene scene = new Scene(root);
        primaryStage.setTitle("TestGuildInfoPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        this.stage = primaryStage;
    }

    private final ChangeListener<String> guildIdChangeListener = (observable, oldValue, newValue) -> {
        loadGuildIndoAsync(guildInfoPane, guildIdCombo);
    };

    private ScheduledService<GuildInfoWrapper> guildUpdateService;
    private GuildInfoWrapper guildWrapper;

    private void loadGuildIndoAsync(final GuildInfoPane guildInfoPane, final ComboBox<String> guildIdCombo) {
        if (guildUpdateService == null) {
            final ScheduledService<GuildInfoWrapper> service = new ScheduledService<GuildInfoWrapper>() {
                @Override
                protected Task<GuildInfoWrapper> createTask() {
                    return new Task<GuildInfoWrapper>() {
                        @Override
                        protected GuildInfoWrapper call() throws Exception {
                            final String guildId = guildIdCombo.getValue();
                            GuildInfoWrapper result = guildWrapper;
                            if (guildId != null) {
                                if (result == null) {
                                    result = new GuildInfoWrapper(true);
                                }
                                result = (SABConstants.INSTANCE.isOffline()) ? loadLocalGuildInfo(result, guildId) : loadRemoteGuildInfo(result, guildId);
                            }
                            return result;
                        }
                    };
                }
            };
            service.setOnSucceeded(workerStateEvent -> {
                guildWrapper = (GuildInfoWrapper) workerStateEvent.getSource().getValue();
                guildInfoPane.setGuild(guildWrapper);
            });
            service.setOnFailed(workerStateEvent -> {
                final Throwable ex = workerStateEvent.getSource().getException();
                Logger.getLogger(TestTreasuryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            });
            service.setPeriod(Duration.minutes(5));
            service.setRestartOnFailure(true);
            guildUpdateService = service;
        }
        guildUpdateService.restart();
    }

    private GuildInfoWrapper loadLocalGuildInfo(final GuildInfoWrapper wrapper, final String guildId) throws NullPointerException, IOException {
        final String filename = String.format("guild%s.json", guildId);
        final URL url = getClass().getResource(filename);
        if (url != null) {
            final Guild guild = JsonpContext.SAX.loadObject(Guild.class, url);
            Platform.runLater(() -> wrapper.setGuild(guild));
        }
        return wrapper;
    }

    private GuildInfoWrapper loadRemoteGuildInfo(final GuildInfoWrapper wrapper, final String guildId) {
        final Optional<Guild> guild = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .endPoint(String.format("guild/%s", guildId))
                .queryObject(Guild.class);
        guild.ifPresent(g -> Platform.runLater(() -> wrapper.setGuild(g)));
        return wrapper;
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
