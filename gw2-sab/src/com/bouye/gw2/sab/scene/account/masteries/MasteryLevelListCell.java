/*
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.masteries;

import api.web.gw2.mapping.v2.masteries.MasteryLevel;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.util.Pair;

/**
 * Mastery level list cell.
 * @author Fabrice Bouyé
 */
final class MasteryLevelListCell extends ListCell<MasteryLevel> {

    private Node renderer;
    private MasteryLevelRendererController controller;

    public MasteryLevelListCell() {
        final Optional<Pair<Node, MasteryLevelRendererController>> loadResult = SABFXMLUtils.INSTANCE.load("fxml/scene/account/masteries/MasteryLevelRenderer.fxml");
        loadResult.ifPresent(pair -> {
            renderer = pair.getKey();
            controller = pair.getValue();
        });
    }

    @Override
    protected void updateItem(final MasteryLevel item, final boolean empty) {
        super.updateItem(item, empty);
        final String text = null;
        Node node = null;
        if (!empty && item != null) {
            node = renderer;
            if (controller != null) {
                controller.setMasteryLevel(item);
            }
        }
        setText(text);
        setGraphic(node);
    }
}
