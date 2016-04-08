/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import com.bouye.gw2.sab.SABListCellBase;
import com.bouye.gw2.sab.session.Session;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * List cell for the session management list.
 * @author Fabrice Bouyé
 */
public final class AccountListCell extends SABListCellBase<Session, AccountListCellController> {

    /**
     * Creates a new empty instance.
     */
    public AccountListCell() {
        super("fxml/scene/account/AccountListCell.fxml"); // NOI18N.
        getStyleClass().add("account-list-cell"); // NOI18N.
    }

    /**
     * Item deletion has been activated.
     */
    private final BooleanProperty deletable = new SimpleBooleanProperty(this, "deletable", false); // NOI18N.

    public final boolean isDeletable() {
        return deletable.get();
    }

    public void setDeletable(final boolean value) {
        deletable.set(value);
    }

    public final BooleanProperty deletableProperty() {
        return deletable;
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
