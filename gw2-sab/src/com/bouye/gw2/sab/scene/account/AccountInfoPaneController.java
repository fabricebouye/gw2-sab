/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import api.web.gw2.mapping.core.JsonpUtils;
import api.web.gw2.mapping.v1.guilddetails.GuildDetails;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.account.AccountAccessType;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.query.WebQuery;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class AccountInfoPaneController extends SABControllerBase<AccountInfoPane> {

    @FXML
    private Label accountNameLabel;
    @FXML
    private Label accessLabel;
    @FXML
    private Label worldLabel;
    @FXML
    private CheckBox commanderCheck;
    @FXML
    private TextFlow guildsTextFlow;
    @FXML
    private Label dailyApLabel;
    @FXML
    private Label monthlyApLabel;
    @FXML
    private FlowPane permissionsFlowPane;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
//        nodeProperty().addListener((observable, oldValue, newValue) -> {
//            final Optional<AccountInfoPane> oldParent = Optional.ofNullable(oldValue);
//            oldParent.ifPresent(p -> {
//                newValue.accountProperty().removeListener(accountInvalidationListener);
//                newValue.tokenInfoProperty().removeListener(tokenInfoInvalidationListener);
//            });
//            final Optional<AccountInfoPane> newParent = Optional.ofNullable(newValue);
//            newParent.ifPresent(p -> {
//                newValue.accountProperty().addListener(accountInvalidationListener);
//                newValue.tokenInfoProperty().addListener(tokenInfoInvalidationListener);
//            });
//            updateAccountContent();
//            updateTokenInfoContent();
//        });
        Bindings.select(nodeProperty(), "account").addListener(accountInvalidationListener);
        Bindings.select(nodeProperty(), "tokenInfo").addListener(tokenInfoInvalidationListener);
        Platform.runLater(() -> {
            updateAccountContent();
            updateTokenInfoContent();
        });
    }

    private final InvalidationListener accountInvalidationListener = observable -> updateAccountContent();
    private final InvalidationListener tokenInfoInvalidationListener = observable -> updateTokenInfoContent();

    private void updateAccountContent() {
        final Optional<AccountInfoPane> parent = Optional.ofNullable(getNode());
        parent.ifPresent(this::clearAccountContent);
        parent.ifPresent(this::installAccountContent);
    }

    private void clearAccountContent(final AccountInfoPane parent) {
        // Style.
        Arrays.stream(AccountAccessType.values()).forEach(accessType -> {
            final String pseudoClassName = JsonpUtils.INSTANCE.javaEnumToJavaClassName(accessType);
            final PseudoClass pseudoClass = PseudoClass.getPseudoClass(pseudoClassName);
            parent.pseudoClassStateChanged(pseudoClass, false);
        });
        // Name.
        accountNameLabel.setText(null);
        accessLabel.setText(null);
        commanderCheck.setSelected(false);
        dailyApLabel.setText(null);
        monthlyApLabel.setText(null);
        guildsTextFlow.getChildren().clear();
    }

    private void installAccountContent(final AccountInfoPane parent) {
        final Optional<Account> account = Optional.ofNullable(parent.getAccount());
        account.ifPresent(a -> {
            // Style.
            final AccountAccessType accessType = a.getAccess();
            final String pseudoClassName = JsonpUtils.INSTANCE.javaEnumToJavaClassName(accessType);
            final PseudoClass pseudoClass = PseudoClass.getPseudoClass(pseudoClassName);
            parent.pseudoClassStateChanged(pseudoClass, true);
            // Name.
            accountNameLabel.setText(a.getName());
            final String accessText = String.format("[ %s ]", JsonpUtils.INSTANCE.javaEnumToJavaClassName(a.getAccess()));
            accessLabel.setText(accessText);
            //
            final int worldId = a.getWorld();
            worldLabel.setUserData(worldId);
            worldLabel.setText(String.valueOf(worldId));
            //
            commanderCheck.setSelected(a.isCommander());
            dailyApLabel.setText(String.valueOf(a.getDailyAp()));
            monthlyApLabel.setText(String.valueOf(a.getMonthlyAp()));
            //
            final List<Hyperlink> guildLinks = a.getGuilds()
                    .stream()
                    .map(guildId -> {
                        final Hyperlink guildLink = new Hyperlink(String.valueOf(guildId));
                        guildLink.setUserData(guildId);
                        guildLink.setOnAction(actionEvent -> System.out.println("Give guild details " + guildId));
                        return guildLink;
                    })
                    .collect(Collectors.toList());
            guildsTextFlow.getChildren().setAll(guildLinks);
            //
            updateAccountValuesAsync(worldLabel, guildLinks);
        });
    }

    private void updateTokenInfoContent() {
        permissionsFlowPane.getChildren().clear();
        final Optional<AccountInfoPane> parent = Optional.ofNullable(getNode());
        parent.ifPresent(this::clearTokenInfoContent);
        parent.ifPresent(this::installTokenInfoContent);
    }

    private void clearTokenInfoContent(final AccountInfoPane parent) {
        // Permissions.
        permissionsFlowPane.getChildren().clear();
    }

    private void installTokenInfoContent(final AccountInfoPane parent) {
        final Optional<TokenInfo> tokenInfo = Optional.ofNullable(parent.getTokenInfo());
        // App-key permissions.
        tokenInfo.ifPresent(ti -> {
            // Permissions.
            final List<Label> permissionLabels = ti.getPermissions()
                    .stream()
                    .map(permission -> {
                        final Label icon = new Label(SABConstants.I18N.getString("icon.gear")); // NOI18N.
                        icon.getStyleClass().addAll("awesome-icon", "permission-icon"); // NOI18N.
                        final Label label = new Label();
                        label.getStyleClass().add("permission-label"); // NOI18N.
                        final String text = JsonpUtils.INSTANCE.javaEnumToJavaClassName(permission);
                        label.setText(text);
                        label.setGraphic(icon);
                        return label;
                    })
                    .collect(Collectors.toList());
            permissionsFlowPane.getChildren().setAll(permissionLabels);
        });
    }

    private void updateAccountValuesAsync(final Label worldLabel, final List<Hyperlink> guildLinks) {
        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new AccountInfoUpdateTaks(worldLabel, guildLinks);
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            System.out.println("World update ok");
            removeService(service);
        });
        service.setOnFailed(workerStateEvent -> {
            System.out.println("World update bad");
            removeService(service);
            final Throwable ex = service.getException();
            Logger.getLogger(AccountInfoPaneController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        });
        addService(service);
        service.start();
    }

    /**
     * Update info associated to an account (ie: server name, guilds' names, etc.).
     * @author Fabrice Bouyé
     */
    private class AccountInfoUpdateTaks extends Task<Void> {

        private final Label worldLabel;
        private final List<Hyperlink> guildLinks;

        public AccountInfoUpdateTaks(final Label worldLabel, final List<Hyperlink> guildLinks) {
            this.worldLabel = worldLabel;
            this.guildLinks = guildLinks;
        }

        @Override
        protected Void call() throws Exception {
            final boolean isDemo = SABConstants.INSTANCE.isDemo();
            final int worldId = (Integer) worldLabel.getUserData();
            final List<World> worlds = WebQuery.INSTANCE.queryWorlds(isDemo, worldId);
            final World world = worlds.get(0);
            final String[] guildIds = guildLinks.stream()
                    .map(hyperlink -> (String) hyperlink.getUserData())
                    .toArray(size -> new String[size]);
            final Map<String, GuildDetails> guilds = WebQuery.INSTANCE.queryGuildDetails(isDemo, guildIds)
                    .stream()
                    .collect(Collectors.toMap(guildDetails -> guildDetails.getGuildId(), Function.identity()));
            // Update everything on JavaFX application thread.
            Platform.runLater(() -> {
                worldLabel.setText(world.getName());
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
