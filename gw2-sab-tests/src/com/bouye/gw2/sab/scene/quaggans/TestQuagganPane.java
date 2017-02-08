/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.quaggans;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.quaggans.Quaggan;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestQuagganPane extends Application {

    private ComboBox<String> quagganCombo;
    private boolean isEditing = false;

    @Override
    public void start(Stage primaryStage) {
        final QuagganPane quagganPane = new QuagganPane();
        VBox.setVgrow(quagganPane, Priority.ALWAYS);
        final Button previousButton = new Button("<");
        previousButton.setOnAction(actionEvent -> quagganPane.previous());
        final Button nextButton = new Button(">");
        nextButton.setOnAction(actionEvent -> quagganPane.next());
        final Button randomButton = new Button("¿?");
        randomButton.setOnAction(actionEvent -> quagganPane.random());
        quagganCombo = new ComboBox();
        quagganCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditing) {
                return;
            }
            try {
                isEditing = true;
                final int index = quagganCombo.getItems().indexOf(newValue);
                quagganPane.getSelectionModel().select(index);
            } finally {
                isEditing = false;
            }
        });
        quagganCombo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(quagganCombo, Priority.ALWAYS);
        quagganPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditing) {
                return;
            }
            try {
                isEditing = true;
                quagganCombo.getSelectionModel().select(newValue.intValue());
            } finally {
                isEditing = false;
            }
        });
        final HBox buttonHBox = new HBox();
        buttonHBox.setSpacing(6);
        buttonHBox.getChildren().setAll(previousButton, quagganCombo, nextButton, randomButton);
        final Label titleLabel = new Label();
        titleLabel.textProperty().bind(quagganPane.titleProperty());
        VBox.setVgrow(titleLabel, Priority.NEVER);
        final VBox root = new VBox();
        root.getChildren().setAll(buttonHBox, titleLabel, quagganPane);
        final Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("TestQuagganPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        loadTestAsync(quagganPane);
    }

    private void loadTestAsync(final QuagganPane quagganPane) {
        final Service<Collection<Quaggan>> service = new Service<Collection<Quaggan>>() {
            @Override
            protected Task<Collection<Quaggan>> createTask() {
                return new Task<Collection<Quaggan>>() {
                    @Override
                    protected Collection<Quaggan> call() throws Exception {
                        final Collection<Quaggan> result = SABConstants.INSTANCE.isOffline() ? loadLocalTest() : loadRemoteTest();
                        return result;
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final Collection<Quaggan> value = (Collection<Quaggan>) workerStateEvent.getSource().getValue();
            quagganCombo.getItems().setAll(value.stream()
                    .map(Quaggan::getId)
                    .collect(Collectors.toList()));
            quagganPane.getQuaggans().setAll(value);
            quagganCombo.getSelectionModel().select(value.isEmpty() ? -1 : 0);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.start();
    }

    private Collection<Quaggan> loadRemoteTest() {
        final Collection<Quaggan> result = WebQuery.INSTANCE.queryQuaggans();
        return result;
    }

    private Collection<Quaggan> loadLocalTest() throws IOException {
        final URL quagganURL = getClass().getResource("quaggans.json"); // NOI18N.
        final Collection<Quaggan> result = JsonpContext.SAX.loadObjectArray(Quaggan.class, quagganURL);
        return result;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
