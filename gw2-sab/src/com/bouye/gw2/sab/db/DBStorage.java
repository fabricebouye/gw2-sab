/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.db;

import api.web.gw2.mapping.core.URLReference;
import api.web.gw2.mapping.v2.worlds.World;
import com.bouye.gw2.sab.net.IOUtils;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.session.Session;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 * Singleton instance for DB access.
 * <br>DB storage uses SQL Lite on all platforms.
 * @author Fabrice Bouyé
 */
public enum DBStorage {
    INSTANCE;
    private static final String STORAGE_FOLDER = ".GW2SAB"; // NOI18N.
    private static final String DB_PROTOCOL = "jdbc:sqlite:%s"; // NOI18N.
    private static final String DB_NAME = "SAB"; // NOI18N.
    private static final String APP_KEYS_TABLE = "app_keys"; // NOI18N.
    // Cache tables
    private static final String WORLDS_TABLE = "worlds"; // NOI18N.
    private static final String GUILDS_TABLE = "guilds"; // NOI18N.
    private static final String FILES_TABLE = "files"; // NOI18N.
    //
    private Connection connection;

    /**
     * Creates a new instance.
     */
    private DBStorage() {
    }

    /**
     * Initializes the DB storage.
     */
    public void init() {
        try {
            Class.forName("org.sqlite.JDBC"); // NOI18N.
            // Create the connection to the DB.
            final String dpPath = String.format("%s/%s", System.getProperty("user.home"), STORAGE_FOLDER); // NOI18N.
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
            simpleUpdate(String.format("create table if not exists %s (id integer primary key, app_key string not null, account_name string)", APP_KEYS_TABLE)); // NOI18N.
            // Drop cache tables.
            simpleUpdate(String.format("drop table if exists %s", WORLDS_TABLE)); // NOI18N.
            simpleUpdate(String.format("drop table if exists %s", GUILDS_TABLE)); // NOI18N.
            simpleUpdate(String.format("drop table if exists %s", FILES_TABLE)); // NOI18N.
            // Re-create cache tables.
            simpleUpdate(String.format("create table if not exists %s (id integer primary key, value blob not null)", WORLDS_TABLE)); // NOI18N.
            simpleUpdate(String.format("create table if not exists %s (id string primary key, value blob not null)", FILES_TABLE)); // NOI18N.
            updateWorldList();
            updateImageCache();
            // Populate cache tables that can be populated.
        } catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(DBStorage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void updateWorldList() throws IOException, SQLException {
        final List<World> worlds = WebQuery.INSTANCE.queryWorlds();
//        final LocalDateTime now = LocalDateTime.now();
//        for (final World world : worlds) {
//            final byte[] data = serialize(world);
//            final String sql = String.format("insert or replace into %s (id, value) values (\"%s\", ?)", WORLDS_TABLE, world.getId()); // NOI18N.
//            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setObject(1, data);
//            preparedStatement.executeUpdate();
//        }
    }

    /**
     * Store files served by the API into a local storage.
     * @throws SQLException In case of SQL error.
     */
    private void updateImageCache() throws SQLException {
        final Collection<api.web.gw2.mapping.v2.files.File> files = WebQuery.INSTANCE.queryFiles();
        for (final api.web.gw2.mapping.v2.files.File file : files) {
            final String id = file.getId();
            final URLReference iconURL = file.getIcon();
            if (iconURL.isPresent()) {
                try (final InputStream input = iconURL.get().openStream();
                        final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    IOUtils.INSTANCE.copy(input, output);
                    final byte[] data = output.toByteArray();
                    final String sql = String.format("insert or replace into %s (id, value) values (\"%s\", ?)", FILES_TABLE, id); // NOI18N.
                    final PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setObject(1, data);
                    preparedStatement.executeUpdate();
                    // May happen when download fails (ie: bad connexion, missing image, etc.).
                } catch (IOException ex) {
                    Logger.getLogger(DBStorage.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Gets an image from the local cache.
     * @param id The id of the image.
     * @return An {@code Image} instance, may be {@code null}.
     * @throws SQLException In case of SQL error.
     */
    public Image getImageFromCache(final String id) throws SQLException {
        Image result = null;
        if (id != null && !id.trim().isEmpty()) {
            final String sql = String.format("select value from %s where id=\"%s\"", FILES_TABLE, id); // NOI18N.
            result = select(sql, resultSet -> {
                Image image = null;
                if (resultSet.next()) {
                    final byte[] bytes = resultSet.getBytes(1);
                    try (final InputStream input = new ByteArrayInputStream(bytes)) {
                        image = new Image(input);
                    } catch (IOException ex) {
                        throw new SQLException(ex);
                    }
                }
                return image;
            }, null);
        }
        return result;
    }

    private <T extends Serializable> byte[] serialize(final T value) throws IOException {
        Objects.requireNonNull(value);
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final ObjectOutputStream writer = new ObjectOutputStream(out)) {
            writer.writeObject(value);
            return out.toByteArray();
        }
    }

    private <T extends Serializable> T deserialize(final byte[] data) throws IOException, ClassNotFoundException {
        Objects.requireNonNull(data);
        try (final ByteArrayInputStream in = new ByteArrayInputStream(data);
                final ObjectInputStream reader = new ObjectInputStream(in)) {
            return (T) reader.readObject();
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
                Logger.getLogger(DBStorage.class
                        .getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(DBStorage.class
                        .getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
                Logger.getLogger(DBStorage.class
                        .getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
