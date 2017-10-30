/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import api.web.gw2.mapping.v1.guilddetails.GuildDetails;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.account.AccountAccessType;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.text.LabelUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class AccountInfoPaneController extends SABControllerBase<AccountInfoPane> {

    @FXML
    private Label accountIconLabel;
    @FXML
    private Label accountNameLabel;
    @FXML
    private Label accessLabel;
    @FXML
    private Hyperlink worldLink;
    @FXML
    private CheckBox commanderCheck;
    @FXML
    private TextFlow guildsTextFlow;
    @FXML
    private Label dailyApLabel;
    @FXML
    private Label monthlyApLabel;
    @FXML
    private Label wvwRankLabel;
    @FXML
    private FlowPane permissionsFlowPane;

    /**
     * Creates a new instance.
     */
    public AccountInfoPaneController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }

    @Override
    public void dispose() {
        try {
            if (infoService != null) {
                infoService.cancel();
            }
        } finally {
            super.dispose();
        }
    }

    /**
     * Called whenever observed values are invalidated.
     */
    private final InvalidationListener valueInvalidationListener = observable -> updateUI();

    /**
     * Monitors the session validity.
     */
    private BooleanBinding validBinding;

    @Override
    protected void uninstallNode(final AccountInfoPane parent) {
        parent.sessionProperty().removeListener(valueInvalidationListener);
        validBinding.removeListener(valueInvalidationListener);
        validBinding.dispose();
        validBinding = null;
        clearOldStyle(parent);
    }

    @Override
    protected void installNode(final AccountInfoPane parent) {
        parent.sessionProperty().addListener(valueInvalidationListener);
        validBinding = Bindings.selectBoolean(parent.sessionProperty(), "valid"); // NOI18N.
        validBinding.addListener(valueInvalidationListener);
    }

    @Override
    protected void updateUI() {
        if (infoService != null) {
            infoService.cancel();
        }
        final Optional<AccountInfoPane> parent = parentNode();
        final Session session = parent.isPresent() ? parent.get().getSession() : null;
        parent.ifPresent(this::clearOldStyle);
        if (session == null || !session.isValid()) {
            // Other.
            accountNameLabel.setText(null);
            accessLabel.setText(null);
            commanderCheck.setSelected(false);
            dailyApLabel.setText(null);
            monthlyApLabel.setText(null);
            wvwRankLabel.setText(null);
            guildsTextFlow.getChildren().clear();
            // Permissions.
            permissionsFlowPane.getChildren().clear();
        } else {
            final Account account = session.getAccount();
            final TokenInfo tokenInfo = session.getTokenInfo();
            // Name.
            accountNameLabel.setText(account.getName());
            final String accessText = account.getAccess()
                    .stream()
                    .map(LabelUtils.INSTANCE::toLabel)
                    .collect(Collectors.joining(", ", "[", "]")); // NOI18N.
            accessLabel.setText(accessText);
            //
            final int worldId = account.getWorld();
            worldLink.setUserData(worldId);
            worldLink.setOnAction(actionEvent -> displayWorldDetails(worldId));
            worldLink.setText(String.valueOf(worldId));
            //
            commanderCheck.setSelected(account.isCommander());
            dailyApLabel.setText(String.valueOf(account.getDailyAp()));
            monthlyApLabel.setText(String.valueOf(account.getMonthlyAp()));
            wvwRankLabel.setText(String.valueOf(account.getWvwRank()));
            //
            final List<Labeled> guildLinks = account.getGuilds()
                    .stream()
                    .map(guildId -> {
                        final Hyperlink guildLink = new Hyperlink(String.valueOf(guildId));
                        guildLink.setUserData(guildId);
                        guildLink.setOnAction(actionEvent -> displayGuildDetails(guildId));
                        return guildLink;
                    })
                    .collect(Collectors.toList());
            guildsTextFlow.getChildren().setAll(guildLinks);
            // Permissions.
            final List<Label> permissionLabels = tokenInfo.getPermissions()
                    .stream()
                    .map(permission -> {
                        final Label icon = new Label(SABConstants.I18N.getString("icon.fa.gear")); // NOI18N.
                        icon.getStyleClass().addAll("awesome-icon", "permission-icon"); // NOI18N.
                        final Label label = new Label();
                        label.getStyleClass().add("permission-label"); // NOI18N.
                        final String text = LabelUtils.INSTANCE.toLabel(permission);
                        label.setText(text);
                        label.setGraphic(icon);
                        return label;
                    })
                    .collect(Collectors.toList());
            permissionsFlowPane.getChildren().setAll(permissionLabels);
            updateOtherValuesAsync(session, worldLink, guildLinks);
            parent.ifPresent(this::installNewStyle);
        }
    }

    /**
     * Clear old style from given parent.
     * @param parent The parent, never {@code null}.
     */
    private void clearOldStyle(final AccountInfoPane parent) {
        Arrays.stream(AccountAccessType.values()).forEach(accessType -> {
            final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(accessType);
            parent.pseudoClassStateChanged(pseudoClass, false);
        });
    }

    /**
     * Apply new style to given parent.
     * @param parent The parent, never {@code null}.
     */
    private void installNewStyle(final AccountInfoPane parent) {
        final Session session = parent.getSession();
        if (session != null && session.isValid()) {
            final Account account = session.getAccount();
            final Set<AccountAccessType> access = account.getAccess();
            final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(access.stream()
                    .findFirst()
                    .orElse(AccountAccessType.NONE));
            parent.pseudoClassStateChanged(pseudoClass, true);
        }
    }

    /**
     * Invoked when the user clicks on the world hyperlink.
     * @param worldId The id of the world to inspect.
     */
    private void displayWorldDetails(final int worldId) {
        final Optional<AccountInfoPane> parent = Optional.ofNullable(getNode());
        parent.ifPresent(p -> {
            final Optional<BiConsumer<Session, Integer>> onWorldDetails = Optional.ofNullable(p.getOnWorldDetails());
            onWorldDetails.ifPresent(c -> c.accept(p.getSession(), worldId));
        });
    }

    /**
     * Invoked when the user clicks on one of the guild hyperlinks.
     * @param guildId The id of the guild to inspect.
     */
    private void displayGuildDetails(final String guildId) {
        final Optional<AccountInfoPane> parent = Optional.ofNullable(getNode());
        parent.ifPresent(p -> {
            final Optional<BiConsumer<Session, String>> onGuildDetails = Optional.ofNullable(p.getOnGuildDetails());
            onGuildDetails.ifPresent(c -> c.accept(p.getSession(), guildId));
        });
    }

    private Service<Void> infoService;

    private void updateOtherValuesAsync(final Session session, final Labeled worldLabel, final List<Labeled> guildLinks) {
        if (infoService == null) {
            infoService = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new AccountInfoUpdateTaks(session, worldLabel, guildLinks);
                }
            };
        }
        addAndStartService(infoService, "AccountInfoPaneController::updateOtherValuesAsync");
    }

    /**
     * Update info associated to an account (ie: server name, guilds' names, etc.).
     * @author Fabrice Bouyé
     */
    private class AccountInfoUpdateTaks extends Task<Void> {

        private final Session session;
        private final Labeled worldLabel;
        private final List<Labeled> guildLinks;

        public AccountInfoUpdateTaks(final Session session, final Labeled worldLabel, final List<Labeled> guildLinks) {
            this.session = session;
            this.worldLabel = worldLabel;
            this.guildLinks = guildLinks;
        }

        @Override
        protected Void call() throws Exception {
            // World.
            final int worldId = (Integer) worldLabel.getUserData();
            final List<World> worlds = WebQuery.INSTANCE.queryWorlds(worldId);
            if (!worlds.isEmpty()) {
                final World world = worlds.get(0);
                // Update on JavaFX application thread.
                Platform.runLater(() -> worldLabel.setText(world.getName()));
            }
            // Guild.
            final String[] guildIds = guildLinks.stream()
                    .map(hyperlink -> (String) hyperlink.getUserData())
                    .toArray(size -> new String[size]);
            final Map<String, GuildDetails> guilds = WebQuery.INSTANCE.queryGuildDetails(guildIds)
                    .stream()
                    .collect(Collectors.toMap(guildDetails -> guildDetails.getGuildId(), Function.identity()));
            // Update on JavaFX application thread.
            Platform.runLater(() -> {
                guildLinks.stream().forEach(guildLink -> {
                    final String guildId = (String) guildLink.getUserData();
                    final GuildDetails guildDetails = guilds.get(guildId);
                    final String label = String.format("%s [%s]", guildDetails.getGuildName(), guildDetails.getTag());
                    guildLink.setText(label);
                });
            });
            return null;
        }
    }
}
