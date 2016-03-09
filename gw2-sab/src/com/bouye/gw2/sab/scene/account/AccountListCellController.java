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
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.data.account.AccessToken;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class AccountListCellController extends SABControllerBase<AccountListCell> {

    @FXML
    private Label accountNameLabel;
    @FXML
    private Label appKeyLabel;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        accessToken.addListener(accessTokenChangeListener);
    }

    /**
     * The access token that was selected from the account management menu.
     */
    private final ObjectProperty<AccessToken> accessToken = new SimpleObjectProperty(this, "accessToken"); // NOI18N.

    public final AccessToken getAccessToken() {
        return accessToken.get();
    }

    public final void setAccessToken(final AccessToken value) {
        accessToken.set(value);
    }

    public final ObjectProperty<AccessToken> accesTokenProperty() {
        return accessToken;
    }

    /**
     * Called whenever the account of the access token changes.
     */
    private final InvalidationListener accountInvalidationListener = observable -> updateAccountMayBe();

    /**
     * Called whenever the access token on display changes.
     */
    private final ChangeListener<AccessToken> accessTokenChangeListener = (observable, oldValue, newValue) -> {
        accountNameLabel.textProperty().unbind();
        appKeyLabel.setText(null);
        // Configure old.
        final Optional<AccessToken> oldAccessToken = Optional.ofNullable(oldValue);
        oldAccessToken.ifPresent(at -> {
            at.accountProperty().removeListener(accountInvalidationListener);
        });
        // Configure new.
        final Optional<AccessToken> newAccessToken = Optional.ofNullable(newValue);
        newAccessToken.ifPresent(at -> {
            accountNameLabel.textProperty().bind(at.accountNameProperty());
            appKeyLabel.setText(at.getAppKey());
            at.accountProperty().addListener(accountInvalidationListener);
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
        final Optional<AccessToken> accessToken = Optional.ofNullable(getAccessToken());
        accessToken.ifPresent(at -> {
            final Optional<Account> account = Optional.ofNullable(at.getAccount());
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
        final Optional<AccessToken> accessToken = Optional.ofNullable(getAccessToken());
        accessToken.ifPresent(at -> {
            final Optional<AccountListCell> parent = Optional.ofNullable(getNode());
            parent.ifPresent(p -> {
                final Optional<Consumer<AccessToken>> onDeleteAccount = Optional.of(p.getOnDeleteAccount());
                onDeleteAccount.ifPresent(consumer -> consumer.accept(at));
            });
        });
    }
}
