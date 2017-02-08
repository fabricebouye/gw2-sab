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
import com.bouye.gw2.sab.wrappers.AccountAchievementCategoryWrapper;
import java.net.URL;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.VBox;

/**
 * Display the category from the account.
 * @author Fabrice Bouyé
 */
public final class CategoryPane extends VBox {

    /**
     * This node's controller.
     */
    private final Optional<CategoryPaneController> controller;

    /**
     * Creates a new instance.
     */
    public CategoryPane() {
        super();
        setId("categoryPane"); // NOI18N.
        // An issue here is that we do not dispose of the controller.
        controller = SABFXMLUtils.INSTANCE.loadAndInject("fxml/scene/account/achievements/CategoryPane.fxml", this); // NOI18N.
    }

//    @Override 
//    public void dispose() {
//        SABFXMLUtils.INSTANCE.disposeController(controller);
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/account/achievements/CategoryPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
    }

    private final ObjectProperty<AccountAchievementCategoryWrapper> category = new SimpleObjectProperty<>(this, "category", null); // NOI18N.

    public final AccountAchievementCategoryWrapper getCategory() {
        return category.get();
    }

    public final void setCategory(final AccountAchievementCategoryWrapper value) {
        category.set(value);
    }

    public final ObjectProperty<AccountAchievementCategoryWrapper> categoryProperty() {
        return category;
    }
}
