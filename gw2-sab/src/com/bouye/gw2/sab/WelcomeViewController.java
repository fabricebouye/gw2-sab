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
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.db.DBStorage;
import com.bouye.gw2.sab.scene.account.AccountInfoPane;
import com.bouye.gw2.sab.scene.guild.GuildInfoPane;
import com.bouye.gw2.sab.scene.world.WorldInfoPane;
import com.bouye.gw2.sab.tasks.account.SessionUpdaterTask;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
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
    private Button backButton;
    @FXML
    private MenuButton accountMenuButton;
    @FXML
    private AccountListPaneController accountListPaneController;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        backButton.visibleProperty().bind(Bindings.size(history).greaterThan(1));
        backButton.managedProperty().bind(backButton.visibleProperty());
        accountListPaneController.setAccounts(sessions);
        accountListPaneController.setOnNewAccount(this::addNewAccount);
        accountListPaneController.setOnDeleteAccount(this::deleteAccount);
        session.bind(accountListPaneController.selectedSessionProperty());
        session.addListener(sessionChangeListener);
        //
        final List<Session> sessions = DBStorage.INSTANCE.getApplicationKeys();
        sessions.stream().forEach(System.out::println);
        this.sessions.addListener(sessionsListChangeListener);
        this.sessions.addAll(sessions);
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
            final Dialog<Session> dialog = new Dialog();
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(accountMenuButton.getScene().getWindow());
            dialog.setTitle(SABConstants.I18N.getString("action.add.account.title")); // NOI18N.
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.disableProperty().bind(controller.sessionProperty().isNull());
            dialog.setResultConverter(buttonType -> {
                Session result = null;
                if (buttonType == ButtonType.OK) {
                    result = controller.getSession();
                }
                return result;
            });
//        ScenicView.show(dialog.getDialogPane());
            final Optional<Session> result = dialog.showAndWait();
            result.ifPresent(session -> {
                doAddAccount(session);
            });
            okButton.disableProperty().unbind();
        } catch (IOException ex) {
            Logger.getLogger(WelcomeViewController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Called whenever the session changes.
     */
    private final ChangeListener<Session> sessionChangeListener = (observable, oldValue, newValue) -> {
        accountMenuButton.hide();
        displayAccountDetails(newValue);
    };

    /**
     * Called whenever the content the list of sessions changes.
     */
    private final ListChangeListener<Session> sessionsListChangeListener = change -> {
        while (change.next()) {
            final List<? extends Session> added = change.getAddedSubList();
            if (!added.isEmpty()) {
                updateNewSessionAsync(added);
            }
            final List<? extends Session> removed = change.getRemoved();
        }
    };

    /**
     * Collect account and tokeninfo information of newly added sessions in a background service.
     * @param sessions The new sessions.
     */
    private void updateNewSessionAsync(final List<? extends Session> sessions) {
        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                final Session[] tokens = sessions.toArray(new Session[sessions.size()]);
                return new SessionUpdaterTask(tokens);
            }
        };
        addAndStartService(service, "WelcomeViewController::updateNewSessionAsync");
    }

    private void doAddAccount(final Session value) {
        DBStorage.INSTANCE.addApplicationKey(value.getAppKey());
        sessions.add(value);
        accountListPaneController.setSelectedSession(value);
    }

    /**
     * Delete an account.
     * @param value The account to be deleted, never {@code null}.
     */
    private void deleteAccount(final Session value) {
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

    private void doDeleteAccount(final Session value) {
        // @todo Close current display if active.
        if (value.equals(getSession())) {

        }
        // Remove from tokens list.
        sessions.remove(value);
        // Remove from long term storage.
        DBStorage.INSTANCE.deleteApplicationKey(value.getAppKey());
    }

    @FXML
    private void handleHelpButton() {
        System.out.println(SABConstants.INSTANCE.getVersion());
    }

    @FXML
    private void handleBackButton() {
        popFromDisplay();
    }

    private ObservableList<Node> history = FXCollections.observableList(new LinkedList<>());

    private void clearDisplay() {
        history.clear();
        rootPane.setCenter(null);
    }
    
    private void pushToDisplay(final Node content) {
        if (content != null) {
            history.add(0, content);
            rootPane.setCenter(content);
        }
    }

    private void popFromDisplay() {
        // Remove from history.
        if (!history.isEmpty()) {
            final Node previous = history.get(0);
            history.remove(0);
            if (previous instanceof SABControlBase) {
                ((SABControlBase)previous).dispose();
            }
        }
        // Restore previous.
        final Node newContent = history.isEmpty() ? null : history.get(0);
        rootPane.setCenter(newContent);
    }

    /**
     * Display some account info.
     * @param session The session.
     */
    private void displayAccountDetails(final Session session) {
        if (session == null) {
            return;
        }
        final AccountInfoPane content = new AccountInfoPane();
        content.setSession(session);
        content.setOnWorldDetails(this::displayWorldDetails);
        content.setOnGuildDetails(this::displayGuildDetails);
        clearDisplay();
        pushToDisplay(content);
    }

    /**
     * Display some world info.
     * @param session The session.
     * @param worldId The id of the world.
     */
    private void displayWorldDetails(final Session session, final int worldId) {
        final WorldInfoPane content = new WorldInfoPane();
        content.setSession(session);
        content.setWorldId(worldId);
        pushToDisplay(content);
    }

    /**
     * Display some guild info.
     * @param session The session.
     * @param guildId The id of the guild.
     */
    private void displayGuildDetails(final Session session, final String guildId) {
        if (guildId == null) {
            return;
        }
        final GuildInfoPane content = new GuildInfoPane();
        content.setSession(session);
        content.setGuildId(guildId);
        pushToDisplay(content);
    }

    /**
     * The session token.
     */
    private final ReadOnlyObjectWrapper<Session> session = new ReadOnlyObjectWrapper(this, "session"); // NOI18N.

    public final Session getSession() {
        return session.get();
    }

    public final ReadOnlyObjectProperty<Session> sessionProperty() {
        return session.getReadOnlyProperty();
    }

    /**
     * Sessions to display in the account menu.
     */
    private final ListProperty<Session> sessions = new SimpleListProperty(this, "sessions", FXCollections.observableList(new LinkedList())); // NOI18N.

    public final ObservableList<Session> getSessions() {
        return sessions.get();
    }

    public void setSessions(final ObservableList<Session> value) {
        sessions.set(value);
    }

    public final ListProperty<Session> sessionsProperty() {
        return sessions;
    }

}
