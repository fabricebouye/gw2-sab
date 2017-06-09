/*
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.account.masteries.AccountMastery;
import api.web.gw2.mapping.v2.masteries.Mastery;
import java.util.Objects;
import java.util.Set;

/**
 * Wraps the account's masteries.
 * @author Fabrice Bouyé
 */
public final class AccountMasteriesWrapper {

    private final Set<Mastery> masteries;
    private final Set<AccountMastery> accountMasteries;

    public AccountMasteriesWrapper(final Set<Mastery> masteries, final Set<AccountMastery> accountMasteries) {
        Objects.requireNonNull(masteries);
        Objects.requireNonNull(accountMasteries);
        this.masteries = masteries;
        this.accountMasteries = accountMasteries;
    }

    public Set<Mastery> getMasteries() {
        return masteries;
    }

    public Set<AccountMastery> getAccountMasteries() {
        return accountMasteries;
    }
}
