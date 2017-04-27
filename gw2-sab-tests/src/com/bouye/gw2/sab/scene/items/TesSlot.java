/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.items.Item;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TesSlot extends Application {

    private List values;

    private Object loadValueFromFile(final Class zeClass, final String path) throws NullPointerException, IOException {
        Object result = null;
        final URL url = getClass().getResource(path);
        if (url != null) {
            result = JsonpContext.SAX.loadObject(zeClass, url);
        }
        return result;
    }

    private List loadValuesFromFile(List result, final Class zeClass, final String path) {
        result = (result == null) ? new LinkedList() : result;
        try {
            final Object value = loadValueFromFile(zeClass, path);
            result.add(value);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(TesSlot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public void init() throws Exception {
        // Preload items.
        final String[] files = {
            "item01.json",
            "item02.json",};
        final Class[] classes = {
            Item.class,
            Item.class,};
        for (int index = 0; index < files.length; index++) {
            values = loadValuesFromFile(values, classes[index], files[index]);
        }
        this.values = Collections.unmodifiableList(values);
    }

    @Override
    public void start(Stage primaryStage) {
        final List<Slot> slots = IntStream.range(0, values.size())
                .mapToObj(index -> {
                    final Object value = values.get(index);
                    final Slot slot = new Slot();
                    slot.setShowRarity(true);
                    slot.setValue(value);
                    return slot;
                })
                .collect(Collectors.toList());
        final TilePane tilePane = new TilePane();
        tilePane.getChildren().setAll(slots);
        tilePane.setAlignment(Pos.CENTER);
        final StackPane stackPane = new StackPane();
        stackPane.getChildren().add(tilePane);
        final Consumer<ToggleButton> toggleButtonAction = toggleButton -> {
            final String userData = (String) toggleButton.getUserData();
            changeSlotSize(userData, slots);
        };
        final ToolBar toolBar = new ToolBar();
        final ToggleGroup toggleGroup = new ToggleGroup();
        toolBar.getItems().setAll(Arrays.asList("small", "medium", "large", "extra-large", "extra-extra-large")
                .stream()
                .map(value -> {
                    final ToggleButton button = new ToggleButton(value);
                    button.setSelected("medium".equals(value));
                    button.setToggleGroup(toggleGroup);
                    button.setUserData(value);
                    button.setOnAction(actionEvent -> toggleButtonAction.accept(button));
                    return button;
                })
                .toArray(ToggleButton[]::new));
        final BorderPane root = new BorderPane();
        root.setCenter(stackPane);
        root.setTop(toolBar);
        final Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("TestSlot");
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
//        loadTestAsync(slot);
    }

    private void changeSlotSize(final String size, final List<Slot> slots) {
        slots.stream()
                .parallel()
                .forEach(slot -> changeSlotSize(size, slot));
    }

    private void changeSlotSize(final String size, final Slot slot) {
        switch (size) {
            case "small": {
                slot.pseudoClassStateChanged(Slot.SMALL_PSEUDO_CLASS, true);
                slot.pseudoClassStateChanged(Slot.LARGE_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.EXTRA_LARGE_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.EXTRA_EXTRA_LARGE_PSEUDO_CLASS, false);
            }
            break;
            case "medium": {
                slot.pseudoClassStateChanged(Slot.SMALL_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.LARGE_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.EXTRA_LARGE_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.EXTRA_EXTRA_LARGE_PSEUDO_CLASS, false);
            }
            break;
            case "large": {
                slot.pseudoClassStateChanged(Slot.SMALL_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.LARGE_PSEUDO_CLASS, true);
                slot.pseudoClassStateChanged(Slot.EXTRA_LARGE_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.EXTRA_EXTRA_LARGE_PSEUDO_CLASS, false);
            }
            break;
            case "extra-large": {
                slot.pseudoClassStateChanged(Slot.SMALL_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.LARGE_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.EXTRA_LARGE_PSEUDO_CLASS, true);
                slot.pseudoClassStateChanged(Slot.EXTRA_EXTRA_LARGE_PSEUDO_CLASS, false);
            }
            break;
            case "extra-extra-large": {
                slot.pseudoClassStateChanged(Slot.SMALL_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.LARGE_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.EXTRA_LARGE_PSEUDO_CLASS, false);
                slot.pseudoClassStateChanged(Slot.EXTRA_EXTRA_LARGE_PSEUDO_CLASS, true);
            }
            break;
        }
    }

    private Service<Item> loadService;

    private void loadTestAsync(final Slot slot) {
        if (loadService != null) {
            loadService.cancel();
            loadService = null;
        }
        final Service<Item> service = new Service<Item>() {
            @Override
            protected Task<Item> createTask() {
                return new Task<Item>() {
                    @Override
                    protected Item call() throws Exception {
                        return (Item) loadValueFromFile(Item.class, "item01.json");
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            Logger.getGlobal().log(Level.INFO, "Load succeeded");
            final Item value = (Item) workerStateEvent.getSource().getValue();
            slot.setValue(value);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        });
        loadService = service;
        loadService.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
