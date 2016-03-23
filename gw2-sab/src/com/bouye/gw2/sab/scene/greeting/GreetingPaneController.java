/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.greeting;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class GreetingPaneController implements Initializable {

    @FXML
    private Button bltButton;
    @FXML
    private Button mfButton;
    @FXML
    private Button pvpButton;
    @FXML
    private Button wvwButton;
    @FXML
    private Button guildButton;
    @FXML
    private Button charactersButton;
    @FXML
    private Button accountButton;
    @FXML
    private Button addAccountButton;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        addAccountButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("account"), true);
    }
}
