/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters;

import com.bouye.gw2.sab.SABControllerBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class CharactersListPaneController extends SABControllerBase {

    @FXML
    private ListView charactersListView;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }
}
