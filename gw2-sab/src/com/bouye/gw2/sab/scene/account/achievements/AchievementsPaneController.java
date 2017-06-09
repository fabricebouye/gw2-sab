/*
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.achievements;

import api.web.gw2.mapping.v2.achievements.Achievement;
import api.web.gw2.mapping.v2.achievements.categories.Category;
import api.web.gw2.mapping.v2.achievements.groups.Group;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.wrappers.AccountAchievementCategoryWrapper;
import com.bouye.gw2.sab.wrappers.AccountAchievementsWrapper;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;

/**
 * FXML controller.
 * @author Fabrice Bouyé
 */
public final class AchievementsPaneController extends SABControllerBase<AchievementsPane> {

    private enum Option {
        SUMMARY, WATCH_LIST;
    }

    @FXML
    private TreeView<Object> categoryTreeView;
    @FXML
    private StackPane content;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        categoryTreeView.setCellFactory(o -> new TreeCell<Object>() {
            @Override
            protected void updateItem(final Object item, final boolean empty) {
                super.updateItem(item, empty);
                String text = null;
                if (!empty && item != null) {
                    if (item instanceof Option) {
                        final Option option = (Option) item;
                        text = option.name();
                    } else if (item instanceof Group) {
                        final Group group = (Group) item;
                        text = group.getName();
                        text += String.format(" (%d)", group.getOrder());
                    } else if (item instanceof Category) {
                        final Category category = (Category) item;
                        text = category.getName();
                        text += String.format(" (%d)", category.getOrder());
                    }
                }
                setWrapText(true);
                setText(text);
            }
        });
        categoryTreeView.getSelectionModel().selectedItemProperty().addListener(treeSelectionChangeListener);
    }

    @Override
    protected void uninstallNode(final AchievementsPane node) {
        node.achievementsProperty().removeListener(achievementsChangeListener);
    }

    @Override
    protected void installNode(final AchievementsPane node) {
        node.achievementsProperty().addListener(achievementsChangeListener);
    }

    @Override
    protected void updateUI() {
        updateCategoryTree();
    }

    protected void updateCategoryTree() {
        final Optional<AchievementsPane> node = parentNode();
        final AccountAchievementsWrapper wrapper = (node.isPresent()) ? node.get().getAchievements() : null;
        if (wrapper == null) {
            categoryTreeView.setRoot(null);
        } else {
            final TreeItem root = createTree(wrapper);
            categoryTreeView.setRoot(root);
        }
    }

    private TreeItem createTree(final AccountAchievementsWrapper wrapper) {
        final Map<Integer, Category> categories = wrapper.getAchievementCategories()
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
        final TreeItem result = new TreeItem();
        result.getChildren().setAll(Arrays.stream(Option.values())
                .map(this::treeItemForOption)
                .collect(Collectors.toList()));
        result.getChildren().addAll(
                wrapper.getAchievementGroups()
                        .stream()
                        .sorted((g1, g2) -> g1.getOrder() - g2.getOrder())
                        .map(group -> {
                            final TreeItem groupItem = treeItemForGroup(group);
                            groupItem.getChildren().addAll(
                                    group.getCategories()
                                            .stream()
                                            .map(categories::get)
                                            .sorted((c1, c2) -> c1.getOrder() - c2.getOrder())
                                            .map(this::treeItemForCategory)
                                            .collect(Collectors.toList()));
                            return groupItem;
                        })
                        .collect(Collectors.toList()));
        return result;
    }

    private TreeItem treeItemForOption(final Option option) {
        final TreeItem result = new TreeItem();
        result.setValue(option);
        return result;
    }

    private TreeItem treeItemForGroup(final Group group) {
        final TreeItem result = new TreeItem();
        result.setValue(group);
        return result;
    }

    private TreeItem treeItemForCategory(final Category category) {
        final TreeItem result = new TreeItem();
        result.setValue(category);
        return result;
    }

    /**
     * Called whenever the achievements wrapper changes in the parent node.
     */
    private final ChangeListener<AccountAchievementsWrapper> achievementsChangeListener = (observable, oldValue, newValue) -> updateCategoryTree();

    /**
     * Called whenever the selection changes in the tree.
     */
    private final ChangeListener<TreeItem> treeSelectionChangeListener = (observable, oldValue, newValue) -> {
        content.getChildren().clear();
        final Optional<AchievementsPane> node = parentNode();
        final AccountAchievementsWrapper wrapper = (node.isPresent()) ? node.get().getAchievements() : null;
        if (wrapper == null) {
            return;
        }
        final Object item = newValue.getValue();
        if (item instanceof Option) {

        } else if (item instanceof Group) {
            final Group group = (Group) item;
        } else if (item instanceof Category) {
            final Category category = (Category) item;
            final Function<Category, List<Achievement>> producer = wrapper.getAchievementProducer();
            final List<Achievement> achievements = producer.apply(category);
            final AccountAchievementCategoryWrapper categoryWrapper = new AccountAchievementCategoryWrapper(category, achievements);
            final CategoryPane categoryPane = new CategoryPane();
            categoryPane.setCategory(categoryWrapper);
            content.getChildren().setAll(categoryPane);
        }
    };
}
