/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.currencies.Currency;
import java.util.Objects;
import api.web.gw2.mapping.v2.account.wallet.AccountCurrencyAmount;

/**
 * Wraps a currency.
 * @author Fabrice Bouyé
 */
public final class CurrencyWrapper {

    /**
     * The currency.
     */
    private final Currency currency;
    /**
     * The currency amount.
     */
    private final AccountCurrencyAmount currencyAmount;

    /**
     * Creates a new instance.
     * @param currency The currency.
     * @param currencyAmount The currency amount, may be {@code null}.
     * @throws NullPointerException If {@code currency} is {@code null}.
     */
    public CurrencyWrapper(final Currency currency, final AccountCurrencyAmount currencyAmount) throws NullPointerException {
        Objects.requireNonNull(currency);
        this.currency = currency;
        this.currencyAmount = currencyAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public AccountCurrencyAmount getCurrencyAmount() {
        return currencyAmount;
    }
}
