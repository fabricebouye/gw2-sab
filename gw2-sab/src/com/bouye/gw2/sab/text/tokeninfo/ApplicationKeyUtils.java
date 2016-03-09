/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.text.tokeninfo;

/**
 * Allows to validate an app-key.
 * @author Fabrice Bouyé
 */
public enum ApplicationKeyUtils {

    INSTANCE;
    /**
     * App-key pattern.
     */
    private static final String APPLICATION_KEY_PATTERN = "[A-F0-9]{8}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{20}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{12}"; // NOI18N.

    /**
     * Tests if the app-key matches the pattern.
     * <br>This method does not validate demo keys.
     * @param value The key to test, may be {@code null}.
     * @return {@code True} if the test succeeds, {@code false} otherwise.
     */
    public boolean validateApplicationKey(final String value) {
        return (value == null) ? false : value.matches(APPLICATION_KEY_PATTERN);
    }
}
