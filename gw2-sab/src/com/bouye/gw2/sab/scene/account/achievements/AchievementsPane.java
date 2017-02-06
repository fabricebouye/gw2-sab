/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.achievements;

import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.scene.SABFXMLUtils;
import com.bouye.gw2.sab.wrappers.AccountAchievementsWrapper;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;

/**
 * Display the achievements from the account.
 * @author Fabrice Bouyé
 */
public final class AchievementsPane extends BorderPane {

    /**
     * This node's controller.
     */
    private final Optional<AchievementsPaneController> controller;

    /**
     * Creates a new instance.
     */
    public AchievementsPane() {
        super();
        setId("achievementsPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/achievements/AchievementsPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/achievements/AchievementsPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<AccountAchievementsWrapper> achievements = new SimpleObjectProperty<>(this, "achievements", null); // NOI18N.

    public final AccountAchievementsWrapper getAchievements() {
        return achievements.get();
    }

    public final void setAchievements(final AccountAchievementsWrapper value) {
        achievements.set(value);
    }

    public final ObjectProperty<AccountAchievementsWrapper> achievementsProperty() {
        return achievements;
    }
}
