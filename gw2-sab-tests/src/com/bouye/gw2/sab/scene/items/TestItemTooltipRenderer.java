/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.items.Item;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.wrappers.ItemWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestItemTooltipRenderer extends Application {

    private VBox vbox;

    @Override
    public void start(final Stage primaryStage) {
        // Left pane: all renderers.
        vbox = new VBox();
        vbox.setFillWidth(true);
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(vbox);
        //
        final SplitPane splitPane = new SplitPane();
        splitPane.getItems().setAll(scrollPane);
        final BorderPane root = new BorderPane();
        root.setCenter(splitPane);
        final Scene scene = new Scene(root, 800, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestItemTooltipRenderer"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
//        Platform.runLater(() -> splitPane.setDividerPositions(0.33, 0.66));
//        ScenicView.show(root);
        loadTestAsync();
    }

    private void loadTestAsync() {
        Service<List<ItemWrapper>> service = new Service<List<ItemWrapper>>() {
            @Override
            protected Task<List<ItemWrapper>> createTask() {
                return new Task<List<ItemWrapper>>() {
                    @Override
                    protected List<ItemWrapper> call() throws Exception {
                        List<ItemWrapper> result = SABConstants.INSTANCE.isOffline() ? loadLocalTest() : loadRemoteTest();
                        return result;
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final List<ItemWrapper> items = (List<ItemWrapper>) workerStateEvent.getSource().getValue();
            vbox.getChildren().setAll(items
                    .stream()
                    .map(item -> {
                        final ItemTooltipRenderer renderer1 = new ItemTooltipRenderer();
                        renderer1.setItem(item);
                        final Tooltip tooltip = ItemTooltipRenderer.asTooltip(item);
                        Tooltip.install(renderer1, tooltip);
                        return renderer1;
                    })
                    .collect(Collectors.toList()));

        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.start();
    }

    private List<ItemWrapper> loadRemoteTest() throws IOException {
        List<ItemWrapper> result = Collections.EMPTY_LIST;
        URL url = getClass().getResource("items.json");
        if (url != null) {
            // Get ids from local objects.
            int[] ids = JsonpContext.SAX.loadObjectArray(Item.class, url)
                    .stream()
                    .mapToInt(Item::getId)
                    .toArray();
            result = WebQuery.INSTANCE.queryItems(ids)
                    .stream()
                    .map(item -> new ItemWrapper(item, null))
                    .collect(Collectors.toList());
        }
        return result;

    }

    private List<ItemWrapper> loadLocalTest() throws IOException {
        List<ItemWrapper> result = Collections.EMPTY_LIST;
        URL url = getClass().getResource("items.json");
        if (url != null) {
            result = JsonpContext.SAX.loadObjectArray(Item.class, url)
                    .stream()
                    .map(item -> new ItemWrapper(item, null))
                    .collect(Collectors.toList());
        }
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
