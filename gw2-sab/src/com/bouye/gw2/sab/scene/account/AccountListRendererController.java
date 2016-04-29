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
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.session.Session;
import com.bouye.gw2.sab.text.LabelUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class AccountListRendererController extends SABControllerBase<AccountListRenderer> {

    @FXML
    private Region avatarContainer;
    @FXML
    private Region avatar;
    @FXML
    private Button deleteButton;
    @FXML
    private Label accountNameLabel;
    @FXML
    private Label appKeyLabel;

    /**
     * Creates a new instance.
     */
    public AccountListRendererController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        accountNameLabel.setText(null);
        appKeyLabel.setText(null);
        //
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
        //
        final Rectangle avatarClip = new Rectangle();
        avatarClip.widthProperty().bind(avatar.widthProperty());
        avatarClip.heightProperty().bind(avatar.heightProperty());
        avatarClip.arcWidthProperty().bind(avatar.widthProperty());
        avatarClip.arcHeightProperty().bind(avatar.heightProperty());
        avatar.setClip(avatarClip);
    }

    /**
     * Called whenever the session on display changes.
     */
    private final InvalidationListener valueInvalidationListener = observable -> updateUI();

    private BooleanBinding validBinding;

    @Override
    protected void uninstallNode(final AccountListRenderer node) {
        node.sessionProperty().removeListener(valueInvalidationListener);
        node.deletableProperty().removeListener(deletableChangeListener);
        validBinding.removeListener(valueInvalidationListener);
        validBinding.dispose();
        validBinding = null;
        clearOldStyle(node);
    }

    @Override
    protected void installNode(final AccountListRenderer node) {
        node.sessionProperty().addListener(valueInvalidationListener);
        node.deletableProperty().addListener(deletableChangeListener);
        validBinding = Bindings.selectBoolean(node.sessionProperty(), "valid"); // NOI18N.
        validBinding.addListener(valueInvalidationListener);
    }

    @Override
    protected void updateUI() {
        final Optional<AccountListRenderer> parent = parentNode();
        final Session session = parent.isPresent() ? parent.get().getSession() : null;
        final boolean sessionValid = validBinding == null ? false : validBinding.get();
        parent.ifPresent(this::clearOldStyle);
        if (session == null || !sessionValid) {
            accountNameLabel.textProperty().unbind();
            appKeyLabel.setText(null);
        } else {
            accountNameLabel.textProperty().bind(session.accountNameProperty());
            appKeyLabel.setText(session.getAppKey());
            parent.ifPresent(this::installNewStyle);
        }
    }

    /**
     * Called whenever deletable value changes.
     */
    private final ChangeListener<Boolean> deletableChangeListener = (observable, oldValue, newValue) -> {
        deleteButton.setVisible(newValue);
        deleteButton.setManaged(newValue);
    };

    /**
     * Clear old style from given parent.
     * @param parent The parent, never {@code null}.
     */
    private void clearOldStyle(final AccountListRenderer parent) {
        Arrays.stream(AccountAccessType.values()).forEach(accessType -> {
            final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(accessType);
            parent.pseudoClassStateChanged(pseudoClass, false);
        });
    }

    /**
     * Apply new style to given parent.
     * @param parent The parent, never {@code null}.
     */
    private void installNewStyle(final AccountListRenderer parent) {
        final Session session = parent.getSession();
        if (session != null && session.isValid()) {
            final Account account = session.getAccount();
            final AccountAccessType accessType = account.getAccess();
            final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(accessType);
            parent.pseudoClassStateChanged(pseudoClass, true);
        }
    }

    @FXML
    private void handleDeleteButton() {
        parentNode().ifPresent(n -> {
            final Optional<Session> session = Optional.ofNullable(n.getSession());
            session.ifPresent(s -> {
                final Optional<Consumer<Session>> onDeleteAccount = Optional.ofNullable(n.getOnDeleteAccount());
                onDeleteAccount.ifPresent(consumer -> consumer.accept(s));
            });
        });
    }
}
