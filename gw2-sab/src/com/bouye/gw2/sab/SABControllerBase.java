/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.fxml.Initializable;
import javafx.scene.Node;

/**
 * Base class for all FXML controllers.
 * @author Fabrice Bouyé
 * @param <T> The type of the parent node to use.
 */
public abstract class SABControllerBase<T extends Node> implements Initializable {

    /**
     * Creates a new instance.
     */
    public SABControllerBase() {
    }

    private final ObjectProperty<T> node = new SimpleObjectProperty<>(this, "node", null); // NOI18N.

    public final T getNode() {
        return node.get();
    }

    public final void setNode(final T value) {
        node.set(value);
    }

    public final ObjectProperty<T> nodeProperty() {
        return node;
    }

    /**
     * Will store services attached to the controller.
     */
    private final List<Service> services = Collections.synchronizedList(new LinkedList());

    /**
     * Stop all pending services.
     * <br>Calling this method will call {@code cancel()} on each pending service and will clear the pending services list.
     */
    protected void stopServices() {
        synchronized (services) {
            services.stream()
                    .forEach(service -> service.cancel());
            services.clear();
        }
    }

    /**
     * Add a service to the list of pending services.
     * @param service The service to add, may be {@code null}.
     */
    protected void addService(final Service service) {
        if (service == null) {
            return;
        }
        synchronized (services) {
            services.add(service);
        }
    }

    /**
     * Removes a service from the list of pending services.
     * @param service The service to remove, may be {@code null}.
     */
    protected void removeService(final Service service) {
        if (service == null) {
            return;
        }
        synchronized (services) {
            services.remove(service);
        }
    }
}
