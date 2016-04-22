/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.bank;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.account.bank.BankSlot;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.characters.inventory.TestInventoryPane;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestBankPane extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final BankPane bankPane = new BankPane();
        final Optional<URL> bankURL = Optional.ofNullable(getClass().getResource("bank.json")); // NOI18N.
        bankURL.ifPresent(url -> {
            try {
                final Collection<BankSlot> inventory = JsonpContext.SAX.loadObjectArray(BankSlot.class, url);
                bankPane.getSlots().setAll(inventory);
            } catch (IOException ex) {
                Logger.getLogger(TestInventoryPane.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
        final BorderPane root = new BorderPane();
        root.setCenter(bankPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestBankPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
