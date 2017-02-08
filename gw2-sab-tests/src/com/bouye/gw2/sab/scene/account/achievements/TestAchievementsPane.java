/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.achievements;

import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.v2.achievements.Achievement;
import api.web.gw2.mapping.v2.achievements.categories.Category;
import api.web.gw2.mapping.v2.achievements.groups.Group;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.wrappers.AccountAchievementsWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestAchievementsPane extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final AchievementsPane achievementsPane = new AchievementsPane();
        final StackPane root = new StackPane();
        root.getChildren().add(achievementsPane);
        final Scene scene = new Scene(root, 600, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestAchievementsPane"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        ScenicView.show(scene);
        loadTestAsync(achievementsPane);
    }

    private void loadTestAsync(final AchievementsPane achievementsPane) {
        final Service<AccountAchievementsWrapper> service = new Service<AccountAchievementsWrapper>() {
            @Override
            protected Task<AccountAchievementsWrapper> createTask() {
                return new Task<AccountAchievementsWrapper>() {
                    @Override
                    protected AccountAchievementsWrapper call() throws Exception {
                        return loadLocalTest();
                    }
                };
            }
        };
        service.setOnSucceeded(workerStateEvent -> {
            final AccountAchievementsWrapper result = (AccountAchievementsWrapper) workerStateEvent.getSource().getValue();
            achievementsPane.setAchievements(result);
        });
        service.setOnFailed(workerStateEvent -> {
            final Throwable ex = workerStateEvent.getSource().getException();
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        });
        service.start();
    }

    private AccountAchievementsWrapper loadLocalTest() throws IOException {
        final URL groupsURL = getClass().getResource("groups.json"); // NOI18N.
        final List<Group> groups = JsonpContext.SAX.loadObjectArray(Group.class, groupsURL)
                .stream()
                .collect(Collectors.toList());
        final URL categoriesURL = getClass().getResource("categories.json"); // NOI18N.
        final List<Category> categories = JsonpContext.SAX.loadObjectArray(Category.class, categoriesURL)
                .stream()
                .collect(Collectors.toList());
        final URL achievementsURL = getClass().getResource("achievements.json"); // NOI18N.
        final Map<Integer, Achievement> achievements = JsonpContext.SAX.loadObjectArray(Achievement.class, achievementsURL)
                .stream()
                .collect(Collectors.toMap(Achievement::getId, Function.identity()));
        final AccountAchievementsWrapper result = new AccountAchievementsWrapper(groups, categories,
                category -> category.getAchievements()
                .stream()
                .map(achievements::get)
                .collect(Collectors.toList()));
        return result;
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
