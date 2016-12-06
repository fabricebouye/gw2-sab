/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.core.URLReference;
import api.web.gw2.mapping.v2.items.Item;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.GW2APIClient;
import com.bouye.gw2.sab.query.ImageCache;
import com.bouye.gw2.sab.wrappers.ItemWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestItemTooltipRenderer extends Application {

    private GridPane gridPane;

    @Override
    public void start(final Stage primaryStage) {
        // Left pane: all renderers.
        gridPane = new GridPane();
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.getColumnConstraints().setAll(IntStream.rangeClosed(0, 1)
                .mapToObj(index -> {
                    final ColumnConstraints columnConstraints = new ColumnConstraints();
                    columnConstraints.setHgrow(Priority.ALWAYS);
                    return columnConstraints;
                })
                .toArray(ColumnConstraints[]::new));
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(gridPane);
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
        ScenicView.show(root);
        loadTestAsync();
    }

    private Service loadService;

    private void loadTestAsync() {
        if (loadService == null) {
            final Service<List<ItemWrapper>> service = new Service<List<ItemWrapper>>() {
                @Override
                protected Task<List<ItemWrapper>> createTask() {
                    return new Task<List<ItemWrapper>>() {
                        @Override
                        protected List<ItemWrapper> call() throws Exception {
                            final List<ItemWrapper> result = SABConstants.INSTANCE.isOffline() ? loadLocalTest() : loadRemoteTest();
                            return result;
                        }
                    };
                }
            };
            service.setOnSucceeded(workerStateEvent -> {
                final List<ItemWrapper> items = (List<ItemWrapper>) workerStateEvent.getSource().getValue();
                populateUI(items);
            });
            service.setOnFailed(workerStateEvent -> {
                final Throwable ex = workerStateEvent.getSource().getException();
                Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
            });
            loadService = service;
        }
        loadService.restart();
    }

    /**
     * Populate UI with provided item wrappers.
     * @param wrapper Item wrappers.
     */
    private void populateUI(final List<ItemWrapper> wrappers) {
        final int itemNumber = wrappers.size();
        gridPane.getRowConstraints().setAll(IntStream.range(0, itemNumber)
                .mapToObj(index -> {
                    final RowConstraints rowConstraints = new RowConstraints();
                    return rowConstraints;
                })
                .toArray(RowConstraints[]::new));
        for (int index = 0; index < itemNumber; index++) {
            final ItemWrapper wrapper = wrappers.get(index);
            final Item item = wrapper.getItem();
            if (item != null) {
                final URLReference url = item.getIcon();
                final Image image = url.isPresent() ? ImageCache.INSTANCE.getImage(url.get().toExternalForm()) : null;
                final ImageView imageView = new ImageView();
                imageView.setImage(image);
                GridPane.setConstraints(imageView, 0, index);
                gridPane.getChildren().add(imageView);
            }
            final ItemTooltipRenderer renderer1 = new ItemTooltipRenderer();
            renderer1.setItem(wrapper);
            final Tooltip tooltip = ItemTooltipRenderer.asTooltip(wrapper);
            Tooltip.install(renderer1, tooltip);
            GridPane.setConstraints(renderer1, 1, index);
            gridPane.getChildren().add(renderer1);
        }
    }

    /**
     * Load remote test.
     * @return A {@code List<ItemWrapper>} instance, never {@code null}.
     * @throws IOException In case of IO error.
     */
    private List<ItemWrapper> loadRemoteTest() throws IOException {
        List<ItemWrapper> result = Collections.EMPTY_LIST;
        URL url = getClass().getResource("items.json"); // NOI18N.
        if (url != null) {
            // Get ids from local objects.
            final int[] ids = JsonpContext.SAX.loadObjectArray(Item.class, url)
                    .stream()
                    .mapToInt(Item::getId)
                    .toArray();
            result = GW2APIClient.create()
                    .ids(ids)
                    .endPoint("items") // NOI18N.
                    .queryArray(Item.class)
                    .stream()
                    .map(item -> new ItemWrapper(item, null))
                    .collect(Collectors.toList());
        }
        return Collections.unmodifiableList(result);

    }

    /**
     * Load local test.
     * @return A {@code List<ItemWrapper>} instance, never {@code null}.
     * @throws IOException In case of IO error.
     */
    private List<ItemWrapper> loadLocalTest() throws IOException {
        List<ItemWrapper> result = Collections.EMPTY_LIST;
        URL url = getClass().getResource("items.json");
        if (url != null) {
            result = JsonpContext.SAX.loadObjectArray(Item.class, url)
                    .stream()
                    .map(item -> new ItemWrapper(item, null))
                    .collect(Collectors.toList());
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Program entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
