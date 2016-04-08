/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.Node;

/**
 * Base class for all FXML controllers.
 * @author Fabrice Bouyé
 * @param <N> The type of the parent node or control.
 */
public abstract class SABControllerBase<N extends Node> implements Initializable {

    /**
     * Creates a new instance.
     */
    public SABControllerBase() {
        nodeProperty().addListener(nodeChangeListener);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }

    /**
     * Dispose this controller.
     * <br>Once this method has been called, the controller is not in a usable state anymore.
     * <br>Default implementation stops all pending services.
     */
    public void dispose() {
        stopServices();
    }

    /**
     * Called whenever the parent node changes.
     */
    private final ChangeListener<N> nodeChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallNode);
        Optional.ofNullable(newValue)
                .ifPresent(this::installNode);
        updateUI();
    };

    /**
     * Uninstall parent node from this controller.
     * <br>Default implementation does nothing.
     * @param node The parent node to uninstall, never {@code null}.
     */
    protected void uninstallNode(final N node) {
    }

    /**
     * Install parent node in this controller.
     * <br>Default implementation does nothing.
     * @param node The parent node to install, never {@code null}.
     */
    protected void installNode(final N node) {
    }

    /**
     * Update this control's content.
     * <br>Default implementation does nothing.
     */
    protected void updateUI() {
    }

    /**
     * Gets an optional reference to the parent node.
     * @return An {@code Optional<N>} instance, never {@code null}.
     */
    protected final Optional<N> parentNode() {
        return Optional.ofNullable(getNode());
    }

    /**
     * Holds a reference to the parent node.
     */
    private final ObjectProperty<N> node = new SimpleObjectProperty<>(this, "node", null); // NOI18N.

    public final N getNode() {
        return node.get();
    }

    public final void setNode(final N value) {
        node.set(value);
    }

    public final ObjectProperty<N> nodeProperty() {
        return node;
    }

    /**
     * Will store services attached to the controller.
     */
    private final Map<Service, String> services = Collections.synchronizedMap(new HashMap());

    /**
     * Stop all pending services.
     * <br>Calling this method will call {@code cancel()} on each pending service and will clear the pending services list.
     */
    protected void stopServices() {
        synchronized (services) {
            services.entrySet()
                    .stream()
                    .forEach(entry -> {
                        final Service service = entry.getKey();
                        service.stateProperty().removeListener(serviceStateChangeListener);
                        service.cancel();
                    });
            services.clear();
        }
    }

    /**
     * Add a service to the list of pending services.
     * @param service The service to add, may be {@code null}.
     * @param description A description for the service, may be {@code null}.
     */
    protected void addAndStartService(final Service service, final String description) {
        if (service == null) {
            return;
        }
        service.stateProperty().addListener(serviceStateChangeListener);
        synchronized (services) {
            services.put(service, description);
        }
        service.start();
    }

    /**
     * Removes a service from the list of pending services.
     * @param service The service to remove, may be {@code null}.
     */
    private void removeService(final Service service) {
        if (service == null) {
            return;
        }
        service.stateProperty().removeListener(serviceStateChangeListener);
        synchronized (services) {
            services.remove(service);
        }
    }

    /**
     * Called whenever the state of of the service changes.
     */
    private final ChangeListener<Service.State> serviceStateChangeListener = (observable, oldValue, newValue) -> {
        switch (newValue) {
            case SUCCEEDED:
            case CANCELLED:
            case FAILED:
                final Optional<Map.Entry<Service, String>> entry = services.entrySet()
                        .stream()
                        .filter(e -> observable == e.getKey().stateProperty())
                        .findFirst();
                entry.ifPresent(e -> {
                    final Service service = e.getKey();
                    String description = e.getValue();
                    if (description == null || description.trim().isEmpty()) {
                        description = service.toString();
                    }
                    // Remove from service list.
                    if (service instanceof ScheduledService) {
                        final ScheduledService scheduledService = (ScheduledService) service;
                        if (newValue == Worker.State.CANCELLED) {
                            removeService(service);
                        }
                    } else {
                        removeService(service);
                    }
                    // Log about this event.
                    switch (newValue) {
                        case SUCCEEDED:
                            Logger.getLogger(SABControlBase.class.getName()).log(Level.INFO, String.format("%s succeeded.", description));
                            break;
                        case CANCELLED:
                            Logger.getLogger(SABControlBase.class.getName()).log(Level.INFO, String.format("%s cancelled.", description));
                            break;
                        case FAILED:
                            Logger.getLogger(SABControlBase.class.getName()).log(Level.INFO, String.format("%s failed.", description));
                            final Throwable ex = service.getException();
                            Logger.getLogger(SABControlBase.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            break;
                        default:
                    }
                });
                break;
            default:
        }
    };
}
