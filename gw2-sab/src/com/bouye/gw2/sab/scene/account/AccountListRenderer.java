/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.session.Session;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.GridPane;

/**
 * Renders accounts (sessions) in list views.
 * @author Fabrice Bouyé
 */
public final class AccountListRenderer extends GridPane {

    private final Optional<AccountListRendererController> controller;

    /**
     * Creates a new instance.
     */
    public AccountListRenderer() {
        super();
        setId("accountListRenderer"); // NOI18N.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/AccountListRenderer.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }    
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/AccountListRenderer.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<Session> session = new SimpleObjectProperty<>(this, "session", null);

    public final void setSession(final Session value) {
        session.set(value);
    }

    public final Session getSession() {
        return session.get();
    }

    public final ObjectProperty<Session> sessionProperty() {
        return session;
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
