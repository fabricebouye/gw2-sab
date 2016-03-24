/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab;

import com.bouye.gw2.sab.scene.characters.CharacterListCell;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            controller.ifPresent(c -> c.setNode(SABListCellBase.this));
        } catch (IOException ex) {
            Logger.getLogger(CharacterListCell.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    protected final void updateItem(final V item, final boolean empty) {
        super.updateItem(item, empty);
        String text = null;
        Node graphic = null;
        if (!empty) {
            // We clear the graphic also when the item is null.
            graphic = (item == null) ? null : node.orElse(null);
            // Update controller, help cleaning previous ref.
            controller.ifPresent(c -> updateController(c, item));
        }
        setText(text);
        setGraphic(graphic);
    }

    /**
     * Update the controller with the item to display.
     * <br>This method may be called :
     * <ul>
     * <li>When {@code item} is non-{@code null} and is going to be displayed in the list cell with the appropriate graphic.</li>
     * <li>When {@code item} is {@code null} and the controller needs to be cleaned up from its previous reference.</li>
     * </ul>
     * <br>Default implementation does nothing.
     * @param controller The controller, never {@code null}.
     * @param item The item, may be {@code null}.
     */
    protected void updateController(final C controller, final V item) {
    }
}
