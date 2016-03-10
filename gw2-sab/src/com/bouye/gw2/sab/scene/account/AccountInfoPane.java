/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import com.bouye.gw2.sab.SabControlBase;
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
}
