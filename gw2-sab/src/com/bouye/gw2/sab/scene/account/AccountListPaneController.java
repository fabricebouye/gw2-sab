/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.session.Session;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class AccountListPaneController extends SABControllerBase {

    @FXML
    private ListView<Session> accountsListView;
    @FXML
    private Button addAccountButton;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        accountsListView.setCellFactory(listView -> {
            final AccountListCell result = new AccountListCell();
            result.onDeleteAccountProperty().bind(onDeleteAccountProperty());
            return result;
        });
        selectedSession.bind(accountsListView.getSelectionModel().selectedItemProperty());
    }

    @FXML
    private void handleAddAccountButton() {
        final Optional<Runnable> onNewAccount = Optional.ofNullable(getOnNewAccount());
        onNewAccount.ifPresent(runnable -> runnable.run());
    }

    public void setAccounts(final ObservableList<Session> value) {
        accountsListView.setItems(value);
    }

    /**
     * The session that was selected from the account management menu.
     */
    private final ReadOnlyObjectWrapper<Session> selectedSession = new ReadOnlyObjectWrapper(this, "selectedSession"); // NOI18N.

    public final Session getSelectedSession() {
        return selectedSession.get();
    }

    public final void setSelectedSession(final Session value) {
        accountsListView.getSelectionModel().select(value);
    }

    public final ReadOnlyObjectProperty<Session> selectedSessionProperty() {
        return selectedSession.getReadOnlyProperty();
    }

    /**
     * Called when the user creates a new account.
     */
    private final ObjectProperty<Runnable> onNewAccount = new SimpleObjectProperty(this, "onNewAccount"); // NOI18N.

    public final Runnable getOnNewAccount() {
        return onNewAccount.get();
    }

    public final void setOnNewAccount(final Runnable value) {
        onNewAccount.set(value);
    }

    public final ObjectProperty<Runnable> onNewAccountProperty() {
        return onNewAccount;
    }

    /**
     * Called when the user deletes an account.
     */
    private final ObjectProperty<Consumer<Session>> onDeleteAccount = new SimpleObjectProperty(this, "onDeleteAccount"); // NOI18N.

    public final Consumer<Session> getOnDeleteAccount() {
        return onDeleteAccount.get();
    }

    public final void setOnDeleteAccount(final Consumer<Session> value) {
        onDeleteAccount.set(value);
    }

    public final ObjectProperty<Consumer<Session>> onDeleteAccountProperty() {
        return onDeleteAccount;
    }
}
