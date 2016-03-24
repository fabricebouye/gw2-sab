/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
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
     * @todo support ScheduledService instances.
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
                    removeService(service);
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

    protected final void updateContent() {
        // @todo check if we REALLY nned a source node for that in most cases.
        final Optional<N> parent = Optional.ofNullable(getNode());
        parent.ifPresent(this::clearContent);
        parent.ifPresent(this::installContent);
    }

    protected void clearContent(final N parent) {
    }

    protected void installContent(final N parent) {
    }
}
