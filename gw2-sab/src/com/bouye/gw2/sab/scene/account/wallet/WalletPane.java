/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import com.bouye.gw2.sab.SABControlBase;

/**
 * Displays the wallet.
 * @author Fabrice Bouyé
 */
public final class WalletPane extends SABControlBase<WalletPaneController> {

    /**
     * Creates a new instance.
     */
    public WalletPane() throws NullPointerException {
        super("fxml/scene/account/wallet/WalletPane.fxml"); // NOI18N.
        getStyleClass().add("wallet-pane"); // NOI18N.
    }
    
    public void dispose() {
        getController().ifPresent(c -> c.dispose());
    }
}
