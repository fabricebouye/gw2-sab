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
        accountNameLabel.setText(null);
        appKeyLabel.setText(null);
        //
        final ColumnConstraints columnConstraits = rootPane.getColumnConstraints().get(1);
        columnConstraits.setMinWidth(0);
        columnConstraits.setPrefWidth(0);
        columnConstraits.setMaxWidth(0);
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
    }

    /**
     * Called whenever the session on display changes.
     */
    private final InvalidationListener valueInvalidationListener = observable -> updateUI();
    
    private BooleanBinding validBinding;
    
    @Override
    protected void uninstallNode(final AccountListCell node) {
        node.itemProperty().removeListener(valueInvalidationListener);
        node.deletableProperty().removeListener(deletableChangeListener);
        validBinding.removeListener(valueInvalidationListener);
        validBinding.dispose();
        validBinding = null;
        clearOldStyle(node);
    }
    
    @Override
    protected void installNode(final AccountListCell node) {
        node.itemProperty().addListener(valueInvalidationListener);
        node.deletableProperty().addListener(deletableChangeListener);
        validBinding = Bindings.selectBoolean(node.itemProperty(), "valid"); // NOI18N.
        validBinding.addListener(valueInvalidationListener);
    }
    
    @Override
    protected void updateUI() {
        final Optional<AccountListCell> parent = parentNode();
        final Session session = parent.isPresent() ? parent.get().getItem() : null;
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
        final ColumnConstraints columnConstraits = rootPane.getColumnConstraints().get(1);
        columnConstraits.setMinWidth(newValue ? -1 : 0);
        columnConstraits.setPrefWidth(newValue ? -1 : 0);
        columnConstraits.setMaxWidth(newValue ? -1 : 0);
        deleteButton.setVisible(newValue);
        deleteButton.setManaged(newValue);
    };

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
        final Session session = parent.getItem();
        if (session != null && session.isValid()) {
            final Account account = session.getAccount();
            final AccountAccessType accessType = account.getAccess();
            final String pseudoClassName = JsonpUtils.INSTANCE.javaEnumToJavaClassName(accessType);
            final PseudoClass pseudoClass = PseudoClass.getPseudoClass(pseudoClassName);
            parent.pseudoClassStateChanged(pseudoClass, true);
        }
    }
    
    @FXML
    private void handleDeleteButton() {
        parentNode().ifPresent(n -> {
            final Optional<Session> session = Optional.ofNullable(n.getItem());
            session.ifPresent(s -> {
                final Optional<Consumer<Session>> onDeleteAccount = Optional.of(n.getOnDeleteAccount());
                onDeleteAccount.ifPresent(consumer -> consumer.accept(s));
            });
        });
    }
}
