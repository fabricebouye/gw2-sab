/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Base class for all FXML-based controls.
 * @author Fabrice Bouyé
 * @param <T> The type of the controller class to use.
 */
public abstract class SabControlBase<T extends SABControllerBase> extends Region {

    private Optional<Node> node = Optional.empty();
    private Optional<T> controller = Optional.empty();

    /**
     * Creates a new empty instance.
     * @param fxml The location of the FXML file.
     * @throws NullPointerException If {@code fxml} is null.
     */
    public SabControlBase(final String fxml) throws NullPointerException {
        super();
        Objects.requireNonNull(fxml);
        try {
            final URL fxmlURL = SAB.class.getResource(fxml);
            final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, SABConstants.I18N);
            node = Optional.of(fxmlLoader.load());
            node.ifPresent(n -> getChildren().setAll(n));
            controller = Optional.of(fxmlLoader.getController());
            Platform.runLater(this::postInit);
        } catch (IOException ex) {
            Logger.getLogger(SabControlBase.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void postInit() {
        controller.ifPresent(c -> c.setNode(SabControlBase.this));
    }

    @Override
    protected final void layoutChildren() {
        super.layoutChildren();
        node.ifPresent(n -> {
            final double width = getWidth();
            final double height = getHeight();
            final Insets insets = getInsets();
            final double areaX = insets.getLeft();
            final double areaY = insets.getTop();
            final double areaW = Math.max(0, width - (insets.getLeft() + insets.getRight()));
            final double areaH = Math.max(0, height - (insets.getTop() + insets.getBottom()));
            n.resizeRelocate(areaX, areaY, areaW, areaH);
        });
    }
}
