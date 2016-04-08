/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.wallet;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public class TestWalletPane extends Application {
    @Override
    public void start(Stage primaryStage) throws NullPointerException, IOException {
        final List<CurrencyAmount> wallet = JsonpContext.SAX.loadObjectArray(CurrencyAmount.class, getClass().getResource("wallet.json"))
                .stream()
                .collect(Collectors.toList()); // NOI18N.
        final WalletPane walletPane = new WalletPane();
        walletPane.getCurrencies().setAll(wallet);
        final StackPane root = new StackPane();
        root.setBackground(Background.EMPTY);
        root.getChildren().add(walletPane);
        final Scene scene = new Scene(root);
        primaryStage.setTitle("TestWalletPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
//        ScenicView.show(root);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
