/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild;

import com.bouye.gw2.sab.SABControllerBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public class GuildInfoPaneController extends SABControllerBase<GuildInfoPane> {

    @FXML
    private Label guildNameLabel;
    @FXML
    private TextArea motdArea = null;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }
}