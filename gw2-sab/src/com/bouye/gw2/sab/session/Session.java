/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.session;

import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.db.DBStorage;
import com.bouye.gw2.sab.demo.DemoSupport;
import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * The application session.
 * @author Fabrice Bouyé
 */
public final class Session {

    /**
     * Creates a new instance.
     * <br>This constructor is called when creating a new token from the UI.
     * @param appKey The application key, never {@code null}
     * @throws NullPointerException If {@code appKey} is {@code null}.
     */
    public Session(final String appKey) throws NullPointerException {
        this(appKey, null);
    }

    /**
     * Creates a new instance.
     * <br>This constructor is called when deserializing the access token from the DB.
     * @param appKey The application key, never {@code null}
     * @param accountName The cached name of the account.
     * @throws NullPointerException If {@code appKey} is {@code null}.
     */
    public Session(final String appKey, final String accountName) throws NullPointerException {
        Objects.requireNonNull(appKey);
        this.appKey = appKey;
        this.accountName.set(accountName);
        valid.bind(account.isNotNull().and(tokenInfo.isNotNull()));
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Session) && appKey.equals(((Session) obj).appKey);
    }

    @Override
    public int hashCode() {
        return appKey.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getAppKey(), getAccountName()); // NOI18N.
    }

    public boolean isDemo() {
        return SABConstants.INSTANCE.isDemo() | DemoSupport.INSTANCE.isDemoApplicationKey(appKey);
    }

    /**
     * The application key.
     */
    private final String appKey;

    public String getAppKey() {
        return appKey;
    }

    /**
     * The cached account name.
     */
    private final ReadOnlyStringWrapper accountName = new ReadOnlyStringWrapper(this, "accountName", null); // NOI18N.

    public final String getAccountName() {
        return accountName.get();
    }

    public final void setAccountName(final String value) {
        accountName.set(value);
        DBStorage.INSTANCE.setApplicationKeyDetails(appKey, value);
    }

    public final ReadOnlyStringProperty accountNameProperty() {
        return accountName.getReadOnlyProperty();
    }

    /**
     * The account.
     */
    private final ReadOnlyObjectWrapper<Account> account = new ReadOnlyObjectWrapper(this, "account", null); // NOI18N.

    public final Account getAccount() {
        return account.get();
    }

    public final void setAccount(final Account value) {
        account.set(value);
    }

    public final ReadOnlyObjectProperty<Account> accountProperty() {
        return account.getReadOnlyProperty();
    }

    /**
     * The token info.
     */
    private final ReadOnlyObjectWrapper<TokenInfo> tokenInfo = new ReadOnlyObjectWrapper(this, "tokenInfo", null); // NOI18N.

    public final TokenInfo getTokenInfo() {
        return tokenInfo.get();
    }

    public final void setTokenInfo(final TokenInfo value) {
        tokenInfo.set(value);
    }

    public final ReadOnlyObjectProperty<TokenInfo> tokenInfoProperty() {
        return tokenInfo.getReadOnlyProperty();
    }

    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(this, "valid", false); // NOI18N.

    public final boolean isValid() {
        return valid.get();
    }

    public final ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }
}
