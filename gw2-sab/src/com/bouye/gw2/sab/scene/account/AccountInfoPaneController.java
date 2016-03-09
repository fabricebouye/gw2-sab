/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import api.web.gw2.mapping.core.JsonpUtils;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.account.AccountAccessType;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.tasks.world.WorldSolverTask;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

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
    private Label dailyApLabel;
    @FXML
    private Label monthlyApLabel;
    @FXML
    private FlowPane permissionsFlowPane;

    /**
     * Initializes the controller class.
     */
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
            final String worldText = String.valueOf(a.getWorld());
            worldLabel.setText(worldText);
            if (world == null) {
                queryWorldAsync(a.getWorld());
            }
            //
            commanderCheck.setSelected(a.isCommander());
            dailyApLabel.setText(String.valueOf(a.getDailyAp()));
            monthlyApLabel.setText(String.valueOf(a.getMonthlyAp()));
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
    
    private World world;
    
    private void queryWorldAsync(final int id) {
        final Service<List<World>> service = new Service<List<World>>() {
            @Override
            protected Task<List<World>> createTask() {
                return new WorldSolverTask(id);
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            System.out.println("World update ok");
            removeService(service);
            final World world = service.getValue().get(0);
            worldLabel.setText(world.getName());
        });
        service.setOnFailed(workerStateEvent -> {
            System.out.println("World update bad");
            removeService(service);
        });
        addService(service);
        service.start();
    }
}
