/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.demo.DemoSupport;
import com.bouye.gw2.sab.text.LabelUtils;
import com.bouye.gw2.sab.text.tokeninfo.ApplicationKeyTextFormatter;
import com.bouye.gw2.sab.text.tokeninfo.ApplicationKeyUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class NewAccountPaneController extends SABControllerBase {

    @FXML
    private TextFlow messageTextFlow;

    @FXML
    private TextField appKeyField;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        final String message = rb.getString("dialog.app-key.message"); // NOI18N.
        messageTextFlow.getChildren().setAll(LabelUtils.INSTANCE.split(message));
        appKeyField.setTextFormatter(new ApplicationKeyTextFormatter());
        appKeyField.textProperty().addListener(appKeyInvalidationListener);
    }

    @FXML
    private void onHandlePasteButton() {
        appKeyField.paste();
    }

    @FXML
    private void onHandleQRCodeButton() {
        // @todo handle camera and some QR scanning API.
    }

    private final ReadOnlyObjectWrapper<Session> session = new ReadOnlyObjectWrapper(this, "session"); // NOI18N.

    public final Session getSession() {
        return session.get();
    }

    public final ReadOnlyObjectProperty<Session> sessionProperty() {
        return session.getReadOnlyProperty();
    }

    /**
     * Called whenever the app key value changes in the text field.
     */
    private final InvalidationListener appKeyInvalidationListener = observable -> {
        String appKey = appKeyField.getText();
        if (appKey != null) {
            appKey = appKey.trim();
            appKey = appKey.toUpperCase();
        }
        if (!DemoSupport.INSTANCE.isDemoApplicationKey(appKey) && !ApplicationKeyUtils.INSTANCE.validateApplicationKey(appKey)) {
            session.set(null);
        } else {
            final Session newSession = new Session(appKey);
            session.set(newSession);
        }
    };
}
