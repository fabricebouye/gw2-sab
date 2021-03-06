/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild.treasury;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v1.guilddetails.GuildDetails;
import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfoPermission;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.SABTestUtils;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.wrappers.TreasuryWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import api.web.gw2.mapping.v2.guild.id.treasury.GuildTreasury;
import api.web.gw2.mapping.v2.guild.id.treasury.GuildTreasuryUpgrade;
import api.web.gw2.mapping.v2.guild.upgrades.GuildUpgrade;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestTreasuryPane extends Application {

    private Stage stage;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final TreasuryPane treasuryPane = new TreasuryPane();
        final BorderPane root = new BorderPane();
        root.setCenter(treasuryPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestTreasuryPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
//        ScenicView.show(scene);
        loadTestAsync(treasuryPane);
    }

    /**
     * Load test in a background service.
     * @param treasuryPane The target pane.
     */
    private void loadTestAsync(final TreasuryPane treasuryPane) {
        final ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        final List<TreasuryWrapper> wrappers = (SABConstants.INSTANCE.isOffline()) ? loadLocalTest() : loadRemoteTest();
                        Platform.runLater(() -> treasuryPane.getTreasury().setAll(wrappers));
                        return null;
                    }
                };
            }
        };
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getLogger(TestTreasuryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.setPeriod(Duration.minutes(5));
        service.setRestartOnFailure(true);
        service.start();
    }

    /**
     * Run the local test.
     * @return A {@code List<TreasuryWrapper>}, never {@code null}.
     */
    private List<TreasuryWrapper> loadLocalTest() throws IOException {
        List<TreasuryWrapper> result = Collections.EMPTY_LIST;
        final Optional<URL> guildTreasuryURL = Optional.ofNullable(getClass().getResource("guild_treasury.json")); // NOI18N.
        final Optional<URL> guildUpgradesURL = Optional.ofNullable(getClass().getResource("guild_upgrades.json")); // NOI18N.
        final Optional<URL> itemsURL = Optional.ofNullable(getClass().getResource("items.json")); // NOI18N.
        if (guildTreasuryURL.isPresent() && guildUpgradesURL.isPresent() && itemsURL.isPresent()) {
            final Collection<GuildTreasury> guildTreasury = JsonpContext.SAX.loadObjectArray(GuildTreasury.class, guildTreasuryURL.get());
            final Map<Integer, GuildUpgrade> guildUpgrades = JsonpContext.SAX.loadObjectArray(GuildUpgrade.class, guildUpgradesURL.get())
                    .stream()
                    .collect(Collectors.toMap(GuildUpgrade::getId, Function.identity()));
            final Map<Integer, Item> items = JsonpContext.SAX.loadObjectArray(Item.class, itemsURL.get())
                    .stream()
                    .collect(Collectors.toMap(Item::getId, Function.identity()));
            // Wrap everything (we still have upgrades & items missing currently so they'll end up being null).
            result = guildTreasury.stream()
                    .map(treasury -> {
                        final Item item = items.get(treasury.getItemId());
                        final GuildUpgrade[] upgrades = treasury.getNeededBy()
                                .stream()
                                .map(treasuryUpgrade -> guildUpgrades.get(treasuryUpgrade.getUpgradeId()))
                                .toArray(GuildUpgrade[]::new);
                        return new TreasuryWrapper(treasury, item, upgrades);
                    })
                    .collect(Collectors.toList());
        }
        return result;
    }

    private synchronized List<TreasuryWrapper> loadRemoteTest() throws InterruptedException {
        List<TreasuryWrapper> result = Collections.EMPTY_LIST;
        final Session session = SABTestUtils.INSTANCE.getTestSession();
        if (session.getTokenInfo().getPermissions().contains(TokenInfoPermission.GUILDS)) {
            final String[] guildIds = session.getAccount().getGuilds().stream().toArray(String[]::new);
            final Map<String, GuildDetails> guildDetails = WebQuery.INSTANCE.queryGuildDetails(guildIds)
                    .stream()
                    .collect(Collectors.toMap(GuildDetails::getGuildId, Function.identity()));
            Platform.runLater(() -> requestGuild(guildIds, guildDetails));
            wait();
            final List<GuildTreasury> guildTreasury = WebQuery.INSTANCE.queryGuildTreasury(session.getAppKey(), selectedGuild.get());
            final int[] upgradeIds = guildTreasury.stream()
                    .map(GuildTreasury::getNeededBy)
                    .map(Set::stream)
                    .reduce(Stream.empty(), Stream::concat)
                    .mapToInt(GuildTreasuryUpgrade::getUpgradeId)
                    .distinct()
                    .toArray();
            final Map<Integer, GuildUpgrade> guildUpgrades = WebQuery.INSTANCE.queryGuildUpgrades(upgradeIds)
                    .stream()
                    .collect(Collectors.toMap(GuildUpgrade::getId, Function.identity()));
            final int[] itemIds = guildTreasury.stream()
                    .mapToInt(GuildTreasury::getItemId)
                    .toArray();
            final Map<Integer, Item> items = WebQuery.INSTANCE.queryItems(itemIds)
                    .stream()
                    .collect(Collectors.toMap(Item::getId, Function.identity()));
            // Wrap everything (we still have upgrades & items missing currently so they'll end up being null).
            result = guildTreasury.stream()
                    .map(treasury -> {
                        final Item item = items.get(treasury.getItemId());
                        final GuildUpgrade[] upgrades = treasury.getNeededBy()
                                .stream()
                                .map(treasuryUpgrade -> guildUpgrades.get(treasuryUpgrade.getUpgradeId()))
                                .toArray(GuildUpgrade[]::new);
                        return new TreasuryWrapper(treasury, item, upgrades);
                    })
                    .collect(Collectors.toList());
        }
        return result;
    }

    private synchronized void requestGuild(final String[] guildIds, final Map<String, GuildDetails> guildDetails) {
        final ListView<String> guildList = new ListView();
        guildList.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                final String text = (empty || item == null) ? null : guildDetails.get(item).getGuildName();
                setText(text);
            }
        });
        guildList.getItems().setAll(guildIds);
        final Dialog dialog = new Dialog();
        dialog.initOwner(stage);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(guildList);
        Optional result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selectedGuild = Optional.ofNullable(guildList.getSelectionModel().getSelectedItem());
        }
        notify();
    }

    private volatile Optional<String> selectedGuild = Optional.empty();

    public static void main(String... args) {
        Application.launch(args);
    }
}
