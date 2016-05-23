/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.achievements.Achievement;
import api.web.gw2.mapping.v2.achievements.categories.Category;
import java.util.List;
import java.util.Objects;

/**
 * Wraps the account's achievements.
 * @author Fabrice Bouyé
 */
public class AccountAchievementCategoryWrapper {

    private final Category category;
    private final List<Achievement> achievements;

    public AccountAchievementCategoryWrapper(final Category category, final List<Achievement> achievements) {
        Objects.requireNonNull(category);
        Objects.requireNonNull(achievements);
        this.category = category;
        this.achievements = achievements;
    }

    public Category getCategory() {
        return category;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }
}
