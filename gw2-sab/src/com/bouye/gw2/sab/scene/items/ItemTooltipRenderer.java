/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.ItemWrapper;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

/**
 * Renderer used to display items in tooltips.
 * @author Fabrice Bouyé
 */
public final class ItemTooltipRenderer extends GridPane {

    private final Optional<ItemTooltipRendererController> controller;

    /**
     * Creates a new instance.
     */
    public ItemTooltipRenderer() {
        super();
        setId("itemRenderer");
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/items/ItemTooltipRenderer.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }    
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/items/ItemTooltipRenderer.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<ItemWrapper> item = new SimpleObjectProperty<>(this, "item", null); // NOI18N.

    public final ItemWrapper getItem() {
        return item.get();
    }

    public final void setItem(final ItemWrapper value) {
        item.set(value);
    }

    public final ObjectProperty<ItemWrapper> itemProperty() {
        return item;
    }

    public static Tooltip asTooltip(final ItemWrapper item) {
        final ItemTooltipRenderer renderer = new ItemTooltipRenderer();
        renderer.setItem(item);
        final Tooltip result = new Tooltip();
        result.setGraphic(new Group(renderer));
        return result;
    }
}
