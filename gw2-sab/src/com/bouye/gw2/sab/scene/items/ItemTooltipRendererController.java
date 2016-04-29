/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import com.bouye.gw2.sab.scene.SABControllerBase;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class.
 * @author Fabrice Bouyé
 */
public final class ItemTooltipRendererController extends SABControllerBase<ItemTooltipRenderer> {

    @FXML
    private StackPane iconContainer;
    @FXML
    private Label nameLabel;
    @FXML
    private TextFlow descriptionFlow;

    /**
     * Creates a new instance.
     */
    public ItemTooltipRendererController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        nameLabel.setText(null);
        descriptionFlow.getChildren().clear();
    }

    @Override
    protected void uninstallNode(final ItemTooltipRenderer node) {
    }

    @Override
    protected void installNode(final ItemTooltipRenderer node) {
    }

    @Override
    protected void updateUI() {
        final Optional<ItemTooltipRenderer> node = parentNode();
//        final ItemWrapper item = (!node.isPresent()) ?  null : node.get().getItem();
        Object item = null;
        if (item == null) {
            nameLabel.setText(null);
            descriptionFlow.getChildren().clear();
        } else {
        }
    }
}
