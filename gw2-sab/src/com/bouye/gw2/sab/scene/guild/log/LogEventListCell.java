/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild.log;

import api.web.gw2.mapping.v2.guild.id.log.LogEvent;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.SABConstants;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * List cell for guild log events.
 * @author Fabrice Bouyé
 */
public final class LogEventListCell extends ListCell<LogEvent> {
    
    private Optional<Node> node = Optional.empty();
    private Optional<LogEventListCellController> controller = Optional.empty();

    /**
     * Creates a new instance.
     */
    public LogEventListCell() {
        try {
            final String fxml = "fxml/scene/guild/log/LogEventListCell.fxml";
            final URL fxmlURL = SAB.class.getResource(fxml);
            final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, SABConstants.I18N);
            node = Optional.of(fxmlLoader.load());
            controller = Optional.of(fxmlLoader.getController());
        } catch (IOException ex) {
            Logger.getLogger(LogEventListCell.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    @Override
    protected void updateItem(final LogEvent logEvent, final boolean empty) {
        super.updateItem(logEvent, empty);
        setText(null);
        controller.ifPresent(c -> c.setLogEvent(logEvent));
        final Node graphic = (empty || logEvent == null || !node.isPresent() ? null : node.get());
        setGraphic(graphic);
    }
}
