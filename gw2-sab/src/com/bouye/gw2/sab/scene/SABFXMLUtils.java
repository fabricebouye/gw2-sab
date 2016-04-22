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
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * Utility class for handling FXML content in SAB.
 * @author Fabrice Bouyé
 */
public enum SABFXMLUtils {
    INSTANCE;

    /**
     * Loads an FXML that uses the {@code fx:root} construct and inject it in given {@code parent} node.
     * <br>This method will intercept any exception raised during the FXML loading and will issue logs when this happens.
     * <br>Thus in case of loading failure, the parent control may be blank and voided of any content.
     * @param <T> The type of the returned controller (if any).
     * @param fxml The location of the source FXML (from the SAB root folder).
     * @param parent The parent node.
     * @return An {@code Optional<T>} instance, never {@code null}, may be empty.
     * @throws NullPointerException If either {@code fxml} or {@code parent} is {@code null}.
     */
    public <T extends SABControllerBase> Optional<T> loadAndInject(final String fxml, final Node parent) throws NullPointerException {
        Objects.requireNonNull(fxml);
        Objects.requireNonNull(parent);
        Optional<T> result = Optional.empty();
        final Optional<URL> url = Optional.ofNullable(SAB.class.getResource(fxml));
        if (url.isPresent()) {
            try {
                final FXMLLoader fxmlLoader = new FXMLLoader(url.get(), SABConstants.I18N);
                fxmlLoader.setRoot(parent);
                fxmlLoader.load();
                result = Optional.ofNullable(fxmlLoader.getController());
                if (result.isPresent()) {
                    result.get().setNode(parent);
                }
            } catch (Exception ex) {
                Logger.getLogger(SABFXMLUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return result;
    }

    public <T extends SABControllerBase> void disposeController(final Optional<T> controller) throws NullPointerException {
        Objects.requireNonNull(controller);
        controller.ifPresent(c -> disposeController(c));
    }

    public <T extends SABControllerBase> void disposeController(final T controller) throws NullPointerException {
        Objects.requireNonNull(controller);
        try {
            controller.setNode(null);
            controller.dispose();
        } catch (Exception ex) {
            Logger.getLogger(SABFXMLUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
