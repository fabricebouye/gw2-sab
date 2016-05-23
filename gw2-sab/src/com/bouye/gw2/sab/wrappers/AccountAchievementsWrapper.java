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
import api.web.gw2.mapping.v2.achievements.groups.Group;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Wraps the account's achievements.
 * @author Fabrice Bouyé
 */
public final class AccountAchievementsWrapper {

    private final List<Group> achievementGroups;
    private final List<Category> achievementCategories;
    private final Function<Category, List<Achievement>> achievementProducer;

    public AccountAchievementsWrapper(final List<Group> achievementGroups, final List<Category> achievementCategories, final Function<Category, List<Achievement>> achievementProducer) {
        Objects.requireNonNull(achievementGroups);
        Objects.requireNonNull(achievementCategories);
        this.achievementGroups = achievementGroups;
        this.achievementCategories = achievementCategories;
        this.achievementProducer = achievementProducer;
    }

    public List<Group> getAchievementGroups() {
        return achievementGroups;
    }

    public List<Category> getAchievementCategories() {
        return achievementCategories;
    }

    public Function<Category, List<Achievement>> getAchievementProducer() {
        return achievementProducer;
    }
}
