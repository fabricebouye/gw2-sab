/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account;

import api.web.gw2.mapping.core.JsonpUtils;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.account.AccountAccessType;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.session.Session;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class AccountListCellController extends SABControllerBase<AccountListCell> {

    @FXML
    private GridPane rootPane;
    @FXML
    private Button deleteButton;
    @FXML
    private Label accountNameLabel;
    @FXML
    private Label appKeyLabel;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        session.addListener(sessionChangeListener);
        final ColumnConstraints columnConstraits = rootPane.getColumnConstraints().get(1);
        columnConstraits.setMinWidth(0);
        columnConstraits.setPrefWidth(0);
        columnConstraits.setMaxWidth(0);
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
        modifyProperty().addListener((observable, oldValue, newValue) -> {
            columnConstraits.setMinWidth(newValue ? -1 : 0);
            columnConstraits.setPrefWidth(newValue ? -1 : 0);
            columnConstraits.setMaxWidth(newValue ? -1 : 0);
            deleteButton.setVisible(newValue);
            deleteButton.setManaged(newValue);
        });
    }

    private final BooleanProperty modify = new SimpleBooleanProperty(this, "modify", false); // NOI18N.

    public final boolean isModify() {
        return modify.get();
    }

    public void setModify(final boolean value) {
        modify.set(value);
    }

    public final BooleanProperty modifyProperty() {
        return modify;
    }

    /**
     * The session that was selected from the account management menu.
     */
    private final ObjectProperty<Session> session = new SimpleObjectProperty(this, "session"); // NOI18N.

    public final Session getSession() {
        return session.get();
    }

    public final void setSession(final Session value) {
        session.set(value);
    }

    public final ObjectProperty<Session> sessionProperty() {
        return session;
    }

    /**
     * Called whenever the account of the session changes.
     */
    private final InvalidationListener accountInvalidationListener = observable -> updateAccountMayBe();

    /**
     * Called whenever the session on display changes.
     */
    private final ChangeListener<Session> sessionChangeListener = (observable, oldValue, newValue) -> {
        accountNameLabel.textProperty().unbind();
        appKeyLabel.setText(null);
        // Configure old.
        final Optional<Session> oldSession = Optional.ofNullable(oldValue);
        oldSession.ifPresent(s -> {
            s.accountProperty().removeListener(accountInvalidationListener);
        });
        // Configure new.
        final Optional<Session> newSession = Optional.ofNullable(newValue);
        newSession.ifPresent(s -> {
            accountNameLabel.textProperty().bind(s.accountNameProperty());
            appKeyLabel.setText(s.getAppKey());
            s.accountProperty().addListener(accountInvalidationListener);
        });
        updateAccountMayBe();
    };

    /**
     * Update some more the list cell after the account has loaded.
     */
    private void updateAccountMayBe() {
        final Optional<AccountListCell> parent = Optional.ofNullable(getNode());
        parent.ifPresent(this::clearOldStyle);
        parent.ifPresent(this::installNewStyle);
    }

    /**
     * Clear old style from given parent.
     * @param parent The parent, never {@code null}.
     */
    private void clearOldStyle(final AccountListCell parent) {
        Arrays.stream(AccountAccessType.values()).forEach(accessType -> {
            final String pseudoClassName = JsonpUtils.INSTANCE.javaEnumToJavaClassName(accessType);
            final PseudoClass pseudoClass = PseudoClass.getPseudoClass(pseudoClassName);
            parent.pseudoClassStateChanged(pseudoClass, false);
        });
    }

    /**
     * Apply new style to given parent.
     * @param parent The parent, never {@code null}.
     */
    private void installNewStyle(final AccountListCell parent) {
        final Optional<Session> session = Optional.ofNullable(getSession());
        session.ifPresent(s -> {
            final Optional<Account> account = Optional.ofNullable(s.getAccount());
            account.ifPresent(a -> {
                final AccountAccessType accessType = a.getAccess();
                final String pseudoClassName = JsonpUtils.INSTANCE.javaEnumToJavaClassName(accessType);
                final PseudoClass pseudoClass = PseudoClass.getPseudoClass(pseudoClassName);
                parent.pseudoClassStateChanged(pseudoClass, true);
            });
        });
    }

    @FXML
    private void handleDeleteButton() {
        final Optional<Session> session = Optional.ofNullable(getSession());
        session.ifPresent(s -> {
            final Optional<AccountListCell> parent = Optional.ofNullable(getNode());
            parent.ifPresent(p -> {
                final Optional<Consumer<Session>> onDeleteAccount = Optional.of(p.getOnDeleteAccount());
                onDeleteAccount.ifPresent(consumer -> consumer.accept(s));
            });
        });
    }
}
