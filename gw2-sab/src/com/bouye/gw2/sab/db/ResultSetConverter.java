/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Converts a result set to a given type.
 * @param <T> The type to return.
 * @author Fabrice Bouyé
 */
interface ResultSetConverter<T> {

    /**
     * Converts the given result set into a {@ code T} instance.
     * @param resultSet The source result set, never {@code null}.
     * @return A {@code T} instance, might be {@code null}.
     * @throws SQLException In case of errors.
     */
    T convert(final ResultSet resultSet) throws SQLException;
}
