/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.db;

import com.bouye.gw2.sab.session.Session;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton instance for DB access.
 * <br>DB storage uses SQL Lite on all platforms.
 * @author Fabrice Bouyé
 */
public enum DBStorage {
    INSTANCE;
    private static final String DB_PROTOCOL = "jdbc:sqlite:%s"; // NOI18N.
    private static final String DB_NAME = "FlaxseedGrind"; // NOI18N.
    private static final String APP_KEYS_TABLE = "app_keys"; // NOI18N.

    private Connection connection;

    /**
     * Creates a new instance.
     */
    private DBStorage() {
        try {
            Class.forName("org.sqlite.JDBC"); // NOI18N.
            // Create the connection to the DB.
            final String dpPath = String.format("%s/%s", System.getProperty("user.home"), ".MyGW2App");
            final File dbDir = new File(dpPath);
            if (!dbDir.exists() && !dbDir.mkdirs()) {
                throw new IOException("Could not create DB storage folder.");
            }
            final File db = new File(dbDir, DB_NAME);
            final String dbUrl = String.format(DB_PROTOCOL, db.getAbsolutePath());
            System.out.println(dbDir.getAbsolutePath());
            System.out.println(dbUrl);
            connection = DriverManager.getConnection(dbUrl);
            // Initialize app keys table (if needed).
//            simpleUpdate(String.format("drop table if exists %s", APP_KEYS_TABLE));
            simpleUpdate(String.format("create table if not exists %s (id integer primary key, app_key string not null, account_name string)", APP_KEYS_TABLE));
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(DBStorage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Close the DB storage access.
     * <br>After this method is invoked, all calls to DB storage will have not effect and will return default values.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBStorage.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Runs a select statement on the DB.
     * @param <T> The type to return.
     * @param sql The SQL, never {@code null}.
     * @param converter Converts the result set's content into T, never {@code null}.
     * @param defaultValue The default value which will be used if the connection is closed, the result set is empty or if the conversion fails.
     * <br>May be {@code null}.
     * @return A T instance, might be {@code null}.
     * @throws NullPointerException If {@code sql} or {@code converter} is {@code null}.
     */
    private <T> T select(final String sql, final ResultSetConverter<T> converter, final T defaultValue) throws NullPointerException {
        Objects.requireNonNull(sql);
        Objects.requireNonNull(converter);
        T result = defaultValue;
        if (connection != null) {
            try (final Statement stmt = connection.createStatement();
                    final ResultSet resultSet = stmt.executeQuery(sql)) {
                result = converter.convert(resultSet);
            } catch (SQLException ex) {
                Logger.getLogger(DBStorage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return result;
    }

    /**
     * Execute a simple update command on the DB.
     * @param sql The SQL, never {@code null}.
     * @throws NullPointerException If {@code sql} is {@code null}.
     */
    private void simpleUpdate(final String sql) throws NullPointerException {
        Objects.requireNonNull(sql);
        if (connection != null) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(sql);

            } catch (SQLException ex) {
                Logger.getLogger(DBStorage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public List<Session> getApplicationKeys() {
        final String sql = String.format("select * from %s", APP_KEYS_TABLE);
        final List<Session> result = select(sql,
                resultSet -> {
                    final List<Session> list = new LinkedList();
                    while (resultSet.next()) {
                        final Session session = new Session(resultSet.getString("app_key"), resultSet.getString("account_name")); // NOI18N.
                        list.add(session);
                    }
                    return Collections.unmodifiableList(list);
                },
                Collections.EMPTY_LIST);
        return result;
    }

    public void addApplicationKey(final String appKey) {
        Objects.requireNonNull(appKey);
        final String sql = String.format("insert into %s (app_key) values (\"%s\")", APP_KEYS_TABLE, appKey); // NOI18N.
        simpleUpdate(sql);
    }

    public void deleteApplicationKey(final String appKey) {
        Objects.requireNonNull(appKey);
        final String sql = String.format("delete from %s where app_key=\"%s\"", APP_KEYS_TABLE, appKey); // NOI18N.
        simpleUpdate(sql);
    }

    public void setApplicationKeyDetails(final String appKey, final String accountName) {
        Objects.requireNonNull(appKey);
        Objects.requireNonNull(accountName);
        final String sql = String.format("update %s set account_name=\"%s\" where app_key=\"%s\"", APP_KEYS_TABLE, accountName, appKey); // NOI18N.
        simpleUpdate(sql);
    }
}
