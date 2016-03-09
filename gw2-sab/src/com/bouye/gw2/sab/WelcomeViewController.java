/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab;

import com.bouye.gw2.sab.scene.account.AccountListPaneController;
import com.bouye.gw2.sab.scene.account.NewAccountPaneController;
import com.bouye.gw2.sab.data.account.AccessToken;
import com.bouye.gw2.sab.db.DBStorage;
import com.bouye.gw2.sab.scene.account.AccountInfoPane;
import com.bouye.gw2.sab.tasks.account.AccessTokenUpdaterTask;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class WelcomeViewController extends SABControllerBase {
    
    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuButton accountMenuButton;
    @FXML
    private AccountListPaneController accountListPaneController;
    
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        accountListPaneController.setAccounts(accessTokens);
        accountListPaneController.setOnNewAccount(this::addNewAccount);
        accountListPaneController.setOnDeleteAccount(this::deleteAccount);
        currentAccessToken.bind(accountListPaneController.currentAccesTokenProperty());
        currentAccessToken.addListener(currentAccessTokenChangeListener);
        //
        final List<AccessToken> accessTokens = DBStorage.INSTANCE.getApplicationKeys();
        accessTokens.stream().forEach(System.out::println);
        this.accessTokens.addListener(accessTokensListChangeListener);
        this.accessTokens.addAll(accessTokens);
    }

    /**
     * Add a new account.
     */
    private void addNewAccount() {
        try {
            final URL fxmlURL = getClass().getResource("fxml/scene/account/NewAccountPane.fxml"); // NOI18N.
            final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, SABConstants.I18N);
            final Node content = fxmlLoader.load();
            final NewAccountPaneController controller = fxmlLoader.getController();
            final Dialog<AccessToken> dialog = new Dialog();
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(accountMenuButton.getScene().getWindow());
            dialog.setTitle(SABConstants.I18N.getString("action.add.account.title")); // NOI18N.
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.disableProperty().bind(controller.accessTokenProperty().isNull());
            dialog.setResultConverter(buttonType -> {
                AccessToken result = null;
                if (buttonType == ButtonType.OK) {
                    result = controller.getAccessToken();
                }
                return result;
            });
//        ScenicView.show(dialog.getDialogPane());
            final Optional<AccessToken> result = dialog.showAndWait();
            result.ifPresent(accessToken -> {
                doAddAccount(accessToken);
            });
            okButton.disableProperty().unbind();
        } catch (IOException ex) {
            Logger.getLogger(WelcomeViewController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Called whenever the selected access token changes.
     */
    private final ChangeListener<AccessToken> currentAccessTokenChangeListener = (observable, oldValue, newValue) -> {
        accountMenuButton.hide();
        if (newValue != null) {
            if (newValue.accountProperty() != null && newValue.tokenInfoProperty() != null) {
                final AccountInfoPane accountInfoPane = new AccountInfoPane();
                accountInfoPane.setAccount(newValue.getAccount());
                accountInfoPane.setTokenInfo(newValue.getTokenInfo());
                rootPane.setCenter(accountInfoPane);
            } else {
            }
        }
    };

    /**
     * Called whenever the content the list of access tokens changes.
     */
    private final ListChangeListener<AccessToken> accessTokensListChangeListener = change -> {
        while (change.next()) {
            final List<? extends AccessToken> added = change.getAddedSubList();
            if (!added.isEmpty()) {
                updateNewAccessTokensAsync(added);
            }
            final List<? extends AccessToken> removed = change.getRemoved();
        }
    };

    /**
     * Collect account and tokeninfo information of newly added access tokens in a background service.
     * @param accessTokens The new tokens.
     */
    private void updateNewAccessTokensAsync(final List<? extends AccessToken> accessTokens) {
        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                final AccessToken[] tokens = accessTokens.toArray(new AccessToken[accessTokens.size()]);
                return new AccessTokenUpdaterTask(tokens);
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            System.out.println("updateNewAccessTokensAsync ok");
            removeService(service);
        });
        service.setOnFailed(workerStateEvent -> {
            System.out.println("updateNewAccessTokensAsync bad");
            removeService(service);
        });
        addService(service);
        service.start();
    }
    
    private void doAddAccount(final AccessToken value) {
        DBStorage.INSTANCE.addApplicationKey(value.getAppKey());
        accessTokens.add(value);
        accountListPaneController.setCurrentAccessToken(value);
    }

    /**
     * Delete an account.
     * @param value The account to be deleted, never {@code null}.
     */
    private void deleteAccount(final AccessToken value) {
        System.out.println("Deleting " + value);
        // @todo Ask for confirmation from user.
        final Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Application key");
        alert.setHeaderText("You will not be able to access this Guild Wars 2 account any more once the application key has been deleted.");
        alert.setContentText("Proceeed?");
        final Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(type -> {
            if (type == ButtonType.OK) {
                doDeleteAccount(value);
            }
        });
    }
    
    private void doDeleteAccount(final AccessToken value) {
        // @todo Close current display if active.
        if (value.equals(getCurrentAccessToken())) {
            
        }
        // Remove from tokens list.
        accessTokens.remove(value);
        // Remove from long term storage.
        DBStorage.INSTANCE.deleteApplicationKey(value.getAppKey());
    }
    
    @FXML
    private void handleHelpButton() {
        System.out.println(SABConstants.INSTANCE.getVersion());
    }

    /**
     * The access token that was selected from the account management menu.
     */
    private final ReadOnlyObjectWrapper<AccessToken> currentAccessToken = new ReadOnlyObjectWrapper(this, "currentAccessToken"); // NOI18N.

    public final AccessToken getCurrentAccessToken() {
        return currentAccessToken.get();
    }
    
    public final ReadOnlyObjectProperty<AccessToken> currentAccesTokenProperty() {
        return currentAccessToken.getReadOnlyProperty();
    }

    /**
     * Access tokens to display in the account menu.
     */
    private final ListProperty<AccessToken> accessTokens = new SimpleListProperty(this, "accessTokens", FXCollections.observableList(new LinkedList())); // NOI18N.

    public final ObservableList<AccessToken> getAccessTokens() {
        return accessTokens.get();
    }
    
    public void setAccessTokens(final ObservableList<AccessToken> value) {
        accessTokens.set(value);
    }
    
    public final ListProperty<AccessToken> accessTokensProperty() {
        return accessTokens;
    }
    
}
