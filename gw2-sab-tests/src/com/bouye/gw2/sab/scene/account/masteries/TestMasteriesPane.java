/*
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.masteries;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.account.masteries.AccountMastery;
import api.web.gw2.mapping.v2.masteries.Mastery;
import com.bouye.gw2.sab.wrappers.AccountMasteriesWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestMasteriesPane extends Application {
    
    @Override
    public void start(final Stage primaryStage) {
        final MasteriesPane masteriesPane = new MasteriesPane();
        final StackPane root = new StackPane();
        root.getChildren().add(masteriesPane);
        final Scene scene = new Scene(root, 700, 600);
        primaryStage.setTitle("TestMasteriesPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        loadTestAsync(masteriesPane);
//        ScenicView.show(scene);
    }
    
    private Service<AccountMasteriesWrapper> loadService;
    
    private void loadTestAsync(final MasteriesPane masteriesPane) {
        if (loadService == null) {
            final Service<AccountMasteriesWrapper> service = new Service<AccountMasteriesWrapper>() {
                @Override
                protected Task<AccountMasteriesWrapper> createTask() {
                    return new Task<AccountMasteriesWrapper>() {
                        @Override
                        protected AccountMasteriesWrapper call() throws Exception {
                            final AccountMasteriesWrapper result = loadLocalTest();
                            return result;
                        }
                    };
                }
            };
            service.setOnSucceeded(workerStateEvent -> {
                final AccountMasteriesWrapper value = (AccountMasteriesWrapper) workerStateEvent.getSource().getValue();
                masteriesPane.setMasteries(value);
            });
            service.setOnFailed(workerStateEvent -> {
                final Throwable ex = workerStateEvent.getSource().getException();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            });
            loadService = service;
        }
        loadService.restart();
    }

    /**
     * Load local test.
     * @return An {@code AccountMasteriesWrapper} instance, never {@code null}.
     * @throws IOException In case of IO error.
     */
    private AccountMasteriesWrapper loadLocalTest() throws IOException {
        // Load mastery definitions.
        final URL masteriesURL = getClass().getResource("masteries.json"); // NOI18N.
        final Set<Mastery> masteries = new LinkedHashSet<>(JsonpContext.SAX.loadObjectArray(Mastery.class, masteriesURL));
        // Load account mastery unlocks.
        final URL accountMasteriesURL = getClass().getResource("account-masteries.json"); // NOI18N.
        final Set<AccountMastery> accountMasteries = new LinkedHashSet<>(JsonpContext.SAX.loadObjectArray(AccountMastery.class, accountMasteriesURL));
        //
        final AccountMasteriesWrapper result = new AccountMasteriesWrapper(masteries, accountMasteries);
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
