/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.account.AccountAccessType;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfoPermission;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.session.Session;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

/**
 * Test.
 * @author Fabrice Bouyé
 */
public final class TestAccountListRenderer extends Application {

    @Override
    public void start(final Stage primaryStage) {
        final CheckBox deletableCheckBox = new CheckBox("Deletable?"); // NOI18N.
        final ToolBar toolBar = new ToolBar();
        toolBar.getItems().setAll(deletableCheckBox);
        // Initialize all the sessions.
        final List<Session> sessions = Arrays.stream(AccountAccessType.values())
                .filter(accessType -> accessType != AccountAccessType.UNKNOWN)
                .map(this::createFakeSession)
                .collect(Collectors.toList());
        // Left pane: all renderers.
        final VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.getChildren().setAll(sessions
                .stream()
                .map(session -> {
                    final AccountListRenderer renderer = new AccountListRenderer();
                    renderer.setSession(session);
                    renderer.deletableProperty().bind(deletableCheckBox.selectedProperty());
                    return renderer;
                })
                .collect(Collectors.toList()));
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(vbox);
        // Middle pane: bare list.
        final ListView<Session> listView1 = new ListView();
        listView1.getItems().setAll(sessions);
        // Right pane: use account list cell which are using renderers.
        final ListView<Session> listView2 = new ListView();
        listView2.setCellFactory(listView -> {
            final AccountListCell listCell = new AccountListCell();
            listCell.deletableProperty().bind(deletableCheckBox.selectedProperty());
            return listCell;
        });
        listView2.getItems().setAll(sessions);
        //
        final SplitPane splitPane = new SplitPane();
        splitPane.getItems().setAll(scrollPane, listView1, listView2);
        final BorderPane root = new BorderPane();
        root.setTop(toolBar);
        root.setCenter(splitPane);
        final Scene scene = new Scene(root, 800, 600);
        final Optional<URL> cssURL = Optional.ofNullable(SAB.class.getResource("styles/Styles.css")); // NOI18N.
        cssURL.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        primaryStage.setTitle("TestAccountListRenderer"); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.runLater(() -> splitPane.setDividerPositions(0.33, 0.66));
//        ScenicView.show(root);
    }

    /**
     * Creates a fake session.
     * @param accessType The type of game access for this session.
     * @return A {@code Session} instance, never {@code null}.
     */
    private Session createFakeSession(final AccountAccessType accessType) {
        final String appKey = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXXXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"; // NOI18N.
        final String accountName = String.format("Test.%s", accessType.name().toLowerCase()); // NOI18N.
        final Session session = new Session(appKey, accountName);
        final Account account = createFakeAccount(accessType, accountName);
        session.setAccount(account);
        final TokenInfo tokenInfo = createFakeTokenInfo(accessType, appKey);
        session.setTokenInfo(tokenInfo);
        return session;
    }

    /**
     * Creates a fake account.
     * @param accessType The type of game access for this session.
     * @param accountName The name of the account.
     * @return An {@code Account} instance, never {@code null}.
     */
    private Account createFakeAccount(final AccountAccessType accessType, final String accountName) {
        return new Account() {
            @Override
            public String getId() {
                return accountName;
            }

            @Override
            public String getName() {
                return accountName;
            }

            @Override
            public int getWorld() {
                return 1;
            }

            @Override
            public Set<String> getGuilds() {
                return Collections.EMPTY_SET;
            }

            @Override
            public ZonedDateTime getCreated() {
                return ZonedDateTime.now();
            }

            @Override
            public AccountAccessType getAccess() {
                return accessType;
            }

            @Override
            public int getFractalLevel() {
                return 1;
            }

            @Override
            public boolean isCommander() {
                return false;
            }

            @Override
            public int getDailyAp() {
                return 0;
            }

            @Override
            public int getMonthlyAp() {
                return 0;
            }

            @Override
            public int getWvwRank() {
                return 1;
            }

            @Override
            public Set<String> getGuildLeader() {
                return Collections.EMPTY_SET;
            }
        };
    }

    /**
     * Creates a fake token info.
     * @param accessType The type of game access for this session.
     * @param appKey The app key.
     * @return A {@code TokenInfo} instance, never {@code null}.
     */
    private TokenInfo createFakeTokenInfo(final AccountAccessType accessType, final String appKey) {
        return new TokenInfo() {
            @Override
            public String getId() {
                return appKey;
            }

            @Override
            public String getName() {
                return String.format("Test key for %s", accessType.name().toLowerCase()); // NOI18N.
            }

            @Override
            public Set<TokenInfoPermission> getPermissions() {
                return Arrays.stream(TokenInfoPermission.values())
                        .filter(permission -> permission != TokenInfoPermission.UNKNOWN)
                        .collect(Collectors.toSet());
            }
        };
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
