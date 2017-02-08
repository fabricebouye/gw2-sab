/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.quaggans;

import api.web.gw2.mapping.core.URLReference;
import api.web.gw2.mapping.v2.quaggans.Quaggan;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.query.ImageCache;
import java.net.URL;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 * Displays a quaggan.
 * @author Fabrice Bouyé
 */
public final class QuagganPane extends Region {

    private final ImageView imageView;

    /**
     * Creates a new instance.
     */
    public QuagganPane() {
        super();
        setId("quagganPane"); // NOI18N.
        getStyleClass().add("quaggan-pane"); // NOI18N.
        //
        imageView = new ImageView();
        imageView.setId("imageView"); // NOI18N.
        // @todo switch to CSS whenever supported.
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        getChildren().setAll(imageView);
        //
        getSelectionModel().selectedIndexProperty().addListener(indexChangeListener);
        getQuaggans().addListener(quaggansListChangeListener);
    }

    @Override
    public String getUserAgentStylesheet() {
        final URL cssURL = SAB.class.getResource("styles/scene/quaggans/QuagganPane.css"); // NOI18N.
        return (cssURL == null) ? null : cssURL.toExternalForm();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double width = getWidth();
        final double height = getHeight();
        final Insets insets = getInsets();
        final double areaX = insets.getLeft();
        final double areaY = insets.getTop();
        final double areaW = Math.max(0, width - (insets.getLeft() + insets.getRight()));
        final double areaH = Math.max(0, height - (insets.getTop() + insets.getBottom()));
        imageView.relocate(areaX, areaY);
        imageView.setFitWidth(areaW);
    }

    private ObservableList<Quaggan> quaggans = FXCollections.observableArrayList();

    public final ObservableList<Quaggan> getQuaggans() {
        return quaggans;
    }

    /**
     * Called whenever the selected index changes in the selection model,
     */
    private final ChangeListener<Number> indexChangeListener = (observable, oldValue, newValue) -> {
        switchToQuaggan(newValue.intValue());
    };

    /**
     * Called whenever the content of the quaggan list changes.
     */
    private final ListChangeListener<Quaggan> quaggansListChangeListener = change -> {
        final int quagganNumber = quaggans.size();
        final int currentIndex = getSelectionModel().getSelectedIndex();
        int newIndex = Math.min(currentIndex, quagganNumber - 1);
        switchToQuaggan(newIndex);
    };

    /**
     * Switch to quaggan at given index.
     * @param index The new current index.
     */
    private void switchToQuaggan(final int index) {
        final Quaggan quaggan = (index == -1) ? null : quaggans.get(index);
        final String titleValue = (quaggan == null) ? null : quaggan.getId();
        title.set(titleValue);
        final URLReference imageURL = (quaggan == null) ? null : quaggan.getUrl();
        final Image image = (imageURL == null || !imageURL.isPresent()) ? null : ImageCache.INSTANCE.getImage(imageURL.get().toExternalForm());
        imageView.setImage(image);
    }

    private final SelectionModel<Quaggan> selectionModel = new SingleSelectionModel<Quaggan>() {
        @Override
        protected Quaggan getModelItem(final int index) {
            return (index < 0) ? null : quaggans.get(index);
        }

        @Override
        protected int getItemCount() {
            return quaggans.size();
        }
    };

    public final SelectionModel<Quaggan> getSelectionModel() {
        return selectionModel;
    }

    /**
     * Select next quaggan.
     */
    public void next() {
        final int quagganNumber = quaggans.size();
        if (quagganNumber > 0) {
            final int currentIndex = getSelectionModel().getSelectedIndex();
            final int newIndex = (currentIndex + 1) % quagganNumber;
            getSelectionModel().select(newIndex);
        }
    }

    /**
     * Select previous quaggan.
     */
    public void previous() {
        final int quagganNumber = quaggans.size();
        if (quagganNumber > 0) {
            final int currentIndex = getSelectionModel().getSelectedIndex();
            final int newIndex = (currentIndex == 0) ? quagganNumber - 1 : currentIndex - 1;
            getSelectionModel().select(newIndex);
        }
    }

    /**
     * Select a random quaggan.
     */
    public void random() {
        final int quagganNumber = quaggans.size();
        if (quagganNumber > 0) {
            final int currentIndex = getSelectionModel().getSelectedIndex();
            final int newIndex = (int) (quagganNumber * Math.random());
            getSelectionModel().select(newIndex);
        }
    }

    /**
     * The title of this pane.
     */
    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper(this, "title"); // NOI18N.

    public final String getTitle() {
        return title.get();
    }

    public final ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }
}
