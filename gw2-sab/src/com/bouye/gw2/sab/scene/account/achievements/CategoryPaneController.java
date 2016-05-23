/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.achievements;

import api.web.gw2.mapping.v2.achievements.Achievement;
import api.web.gw2.mapping.v2.achievements.categories.Category;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.AccountAchievementCategoryWrapper;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

/**
 * FXML controller.
 * @author Fabrice Bouyé
 */
public final class CategoryPaneController extends SABControllerBase<CategoryPane> {

    @FXML
    private Label descriptionLabel;
    @FXML
    private FlowPane achievementsFlow;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }

    @Override
    protected void uninstallNode(CategoryPane node) {
        node.categoryProperty().removeListener(categoryChangeListener);
    }

    @Override
    protected void installNode(CategoryPane node) {
        node.categoryProperty().addListener(categoryChangeListener);
    }

    @Override
    protected void updateUI() {
        final Optional<CategoryPane> node = parentNode();
        final AccountAchievementCategoryWrapper wrapper = (node.isPresent()) ? node.get().getCategory() : null;
        if (wrapper == null) {
            descriptionLabel.setText(null);
            achievementsFlow.getChildren().clear();
        } else {
            final Category category = wrapper.getCategory();
            final Map<Integer, Achievement> achievements = wrapper.getAchievements()
                    .stream()
                    .collect(Collectors.toMap(Achievement::getId, Function.identity()));
            final String categoryName = category.getName();
            descriptionLabel.setText(categoryName);
            achievementsFlow.getChildren().setAll(category.getAchievements()
                    .stream()
                    .map(achievements::get)
                    .map(this::nodeForAchievement)
                    .collect(Collectors.toList()));

        }
    }

    private Node nodeForAchievement(final Achievement achievement) {
        final StackPane result = new StackPane();
        result.setMinWidth(200);
        result.setPrefHeight(40);
        final Label label = new Label(achievement.getName());
        label.setWrapText(true);
        result.getChildren().add(label);
        return result;
    }

    private final ChangeListener<AccountAchievementCategoryWrapper> categoryChangeListener = (observable, oldValue, newValue) -> updateUI();
}
