/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.session.Session;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * List cell for the session management list.
 * @author Fabrice Bouyé
 */
public final class AccountListCell extends ListCell<Session> {

    private Optional<Node> node = Optional.empty();
    private Optional<AccountListCellController> controller = Optional.empty();

    /**
     * Creates a new empty instance.
     */
    public AccountListCell() {
        super();
        getStyleClass().add("account-list-cell"); // NOI18N.
        try {
            final URL fxmlURL = SAB.class.getResource("fxml/scene/account/AccountListCell.fxml"); // NOI18N.
            final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, SABConstants.I18N);
            node = Optional.of(fxmlLoader.load());
            controller = Optional.of(fxmlLoader.getController());
            controller.ifPresent(c -> c.setNode(AccountListCell.this));
        } catch (IOException ex) {
            Logger.getLogger(AccountListCell.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    protected void updateItem(final Session item, final boolean empty) {
        super.updateItem(item, empty);
        Node graphic = null;
        if (!empty && item != null && node.isPresent()) {
            graphic = node.isPresent() ? node.get() : null;
            controller.ifPresent(c -> c.setSession(item));
        }
        setGraphic(graphic);
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
