/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.wrappers;

import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import api.web.gw2.mapping.v2.currencies.Currency;
import java.util.Objects;

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
    private final CurrencyAmount currencyAmount;

    /**
     * Creates a new instance.
     * @param currency The currency.
     * @param currencyAmount The currency amount.
     * @throws NullPointerException If either {@code currency} or {@code currencyAmount} is {@code null}.
     */
    public CurrencyWrapper(final Currency currency, final CurrencyAmount currencyAmount) throws NullPointerException {
        Objects.requireNonNull(currency);
        Objects.requireNonNull(currencyAmount);
        this.currency = currency;
        this.currencyAmount = currencyAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public CurrencyAmount getCurrencyAmount() {
        return currencyAmount;
    }
}
