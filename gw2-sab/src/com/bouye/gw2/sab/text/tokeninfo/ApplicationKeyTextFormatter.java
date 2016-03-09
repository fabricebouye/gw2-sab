/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.text.tokeninfo;

import javafx.scene.control.TextFormatter;

/**
 * App-key formatter.
 * @author Fabrice Bouyé
 */
public final class ApplicationKeyTextFormatter extends TextFormatter<String> {

    /**
     * Creates a new instance.
     */
    public ApplicationKeyTextFormatter() {
        super(change -> {
            final String newText = change.getText();
            // X letter is needed for demo mode support.
            if (newText != null && !change.getText().matches("[a-fxA-FX0-9\\-]+")) { // NOI18N.
                change.setText("");
            }
            return change;
        });
    }
}
