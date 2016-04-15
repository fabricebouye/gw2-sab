/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.scene.characters.CharacterListCell;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * Base class for all FXML-based list cells.
 * @author Fabrice Bouyé
 * @param <V> The type of the value.
 * @param <C> The type of the controller.
 */
public abstract class SABListCellBase<V, C extends SABControllerBase> extends ListCell<V> {

    private Optional<Node> node = Optional.empty();
    private Optional<C> controller = Optional.empty();

    /**
     * Creates a new instance.
     * @param fxml The location of the FXML file.
     * @throws NullPointerException If {@code fxml} is null.
     */
    public SABListCellBase(final String fxml) throws NullPointerException {
        super();
        Objects.requireNonNull(fxml);
        getStyleClass().add("list-cell"); // NOI18N.
        try {
            final URL fxmlURL = SAB.class.getResource(fxml);
            final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, SABConstants.I18N);
            node = Optional.of(fxmlLoader.load());
            controller = Optional.of(fxmlLoader.getController());
            Platform.runLater(this::postInit);
        } catch (IOException ex) {
            Logger.getLogger(CharacterListCell.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Actions to execute after this list cell has been initialized.
     */
    private void postInit() {
        controller.ifPresent(c -> c.setNode(SABListCellBase.this));
    }

    /**
     * Gets the controller of this list cell (if any).
     * @return An {@code Optional<C>} instance, never {@code null}.
     */
    protected final Optional<C> getController() {
        return controller;
    }

    @Override
    protected final void updateItem(final V item, final boolean empty) {
        super.updateItem(item, empty);
        final String text = null;
        Node graphic = null;
        if (!empty) {
            graphic = (item == null) ? null : node.orElse(null);
        }
        setText(text);
        setGraphic(graphic);
    }
}
