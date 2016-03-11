/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import com.bouye.gw2.sab.SabControlBase;
import com.bouye.gw2.sab.session.Session;
import java.util.function.BiConsumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Displays account info.
 * @author Fabrice Bouyé
 */
public final class AccountInfoPane extends SabControlBase<AccountInfoPaneController> {

    /**
     * Creates a new empty instance.
     */
    public AccountInfoPane() {
        super("fxml/scene/account/AccountInfoPane.fxml"); // NOI18N.
        getStyleClass().add("account-info-pane"); // NOI18N.
    }

    private final ObjectProperty<BiConsumer<Session, Integer>> onWorldDetails = new SimpleObjectProperty(this, "onWorldDetails", null);

    public final BiConsumer<Session, Integer> getOnWorldDetails() {
        return onWorldDetails.get();
    }

    public final void setOnWorldDetails(final BiConsumer<Session, Integer> value) {
        onWorldDetails.set(value);
    }

    public final ObjectProperty<BiConsumer<Session, Integer>> onWorldDetailsProperty() {
        return onWorldDetails;
    }

    private final ObjectProperty<BiConsumer<Session, String>> onGuildDetails = new SimpleObjectProperty(this, "onGuildDetails", null);

    public final BiConsumer<Session, String> getOnGuildDetails() {
        return onGuildDetails.get();
    }

    public final void setOnGuildDetails(final BiConsumer<Session, String> value) {
        onGuildDetails.set(value);
    }

    public final ObjectProperty<BiConsumer<Session, String>> onGuildDetailsProperty() {
        return onGuildDetails;
    }
}
