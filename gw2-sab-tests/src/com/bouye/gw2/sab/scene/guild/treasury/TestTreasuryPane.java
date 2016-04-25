/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild.treasury;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v1.guilddetails.GuildDetails;
import api.web.gw2.mapping.v2.guild.id.treasury.Treasury;
import api.web.gw2.mapping.v2.guild.upgrades.Upgrade;
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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestTreasuryPane extends Application {

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
        ScenicView.show(scene);
        loadTestAsync(treasuryPane);
    }

    /**
     * Load test in a background service.
     * @param treasuryPane The target pane.
     */
    private void loadTestAsync(final TreasuryPane treasuryPane) {
        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        List<TreasuryWrapper> wrappers = Collections.EMPTY_LIST;
                        if (SABConstants.INSTANCE.isOffline()) {
                            wrappers = loadLocalTest();
                        } else {
                            final Session session = SABTestUtils.INSTANCE.getTestSession();
                            if (session.getTokenInfo().getPermissions().contains(TokenInfoPermission.GUILDS)) {
                                final String[] guildIds = session.getAccount().getGuilds().stream().toArray(String[]::new);
                                final List<GuildDetails> guildDetails = WebQuery.INSTANCE.queryGuildDetails(guildIds);
                            }
                        }
                        final List<TreasuryWrapper> w = wrappers;
                        Platform.runLater(() -> treasuryPane.getTreasury().setAll(w));
                        return null;
                    }
                };
            }
        };
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getLogger(TestTreasuryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        });
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
            final Collection<Treasury> guildTreasury = JsonpContext.SAX.loadObjectArray(Treasury.class, guildTreasuryURL.get());
            final Map<Integer, Upgrade> guildUpgrades = JsonpContext.SAX.loadObjectArray(Upgrade.class, guildUpgradesURL.get())
                    .stream()
                    .collect(Collectors.toMap(Upgrade::getId, Function.identity()));
            final Map<Integer, Item> items = JsonpContext.SAX.loadObjectArray(Item.class, itemsURL.get())
                    .stream()
                    .collect(Collectors.toMap(Item::getId, Function.identity()));
            // Wrap everything (we still have upgrades & items missing currently so they'll end up being null).
            result = guildTreasury.stream()
                    .map(treasury -> {
                        final Item item = items.get(treasury.getId());
                        final Upgrade[] upgrades = treasury.getNeededBy()
                                .stream()
                                .map(treasuryUpgrade -> guildUpgrades.get(treasuryUpgrade.getUpgradeId()))
                                .toArray(Upgrade[]::new);
                        return new TreasuryWrapper(treasury, item, upgrades);
                    })
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
