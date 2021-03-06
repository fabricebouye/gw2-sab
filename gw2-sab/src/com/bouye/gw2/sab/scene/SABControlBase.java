/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.session.Session;
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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Base class for all FXML-based controls.
 * @author Fabrice Bouyé
 * @param <C> The type of the controller.
 */
public abstract class SABControlBase<C extends SABControllerBase> extends Region {

    private Optional<Node> node = Optional.empty();
    private Optional<C> controller = Optional.empty();

    /**
     * Creates a new instance.
     * @param fxml The location of the FXML file.
     * @throws NullPointerException If {@code fxml} is null.
     */
    public SABControlBase(final String fxml) throws NullPointerException {
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
            Logger.getLogger(SABControlBase.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Dispose this control.
     * <br>Once this method has been called, the control is not in a usable state anymore.
     */
    public void dispose() {
        getController().ifPresent(c -> {
            c.setNode(null);
            c.dispose();
        });
    }

    /**
     * Actions to execute after this node has been initialized.
     */
    private void postInit() {
        controller.ifPresent(c -> c.setNode(SABControlBase.this));
    }

    /**
     * Gets the controller of this control (if any).
     * @return An {@code Optional<C>} instance, never {@code null}.
     */
    protected final Optional<C> getController() {
        return controller;
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

    /**
     * The session token that this control will use when accessing endpoints of the Web API which require authentication.
     */
    private final ObjectProperty<Session> session = new SimpleObjectProperty(this, "session"); // NOI18N.

    public final Session getSession() {
        return session.get();
    }

    public final void setSession(final Session value) {
        session.set(value);
    }

    public final ObjectProperty<Session> sessionProperty() {
        return session;
    }
}
