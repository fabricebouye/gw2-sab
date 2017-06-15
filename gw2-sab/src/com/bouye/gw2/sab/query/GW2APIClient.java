/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

import api.web.gw2.mapping.core.APILevel;
import api.web.gw2.mapping.core.JsonpContext;
import api.web.gw2.mapping.core.PageResult;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Encapsulate URL construction and calls to the GW2 Web API.
 * @author Fabrice Bouyé
 */
public final class GW2APIClient {

    /**
     * Base URL for all GW2 API endpoints.
     */
    private static final String API_BASE_CODE = "https://api.guildwars2.com"; // NOI18N.
    /**
     * API level to use.
     */
    private APILevel apiLevel = APILevel.V2;
    /**
     * Endpoint at the base URL.
     */
    private String endPoint;
    /**
     * Other parameters.
     */
    private final Map<String, Object> parameters = new LinkedHashMap<>();
    /**
     * JSON-P context to use when parsing results.
     */
    private JsonpContext context = JsonpContext.SAX;

    /**
     * Hidden constructor.
     */
    private GW2APIClient() {
    }

    /**
     * Creates a new empty instance.
     * @return A {@code GW2APIClient}, never {@code null}.
     */
    public static GW2APIClient create() {
        return new GW2APIClient();
    }

    /**
     * Sets the API level.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     * @throws NullPointerException If {@code value} is {@code null}.
     */
    public GW2APIClient apiLevel(final APILevel value) throws NullPointerException {
        Objects.requireNonNull(value);
        apiLevel = value;
        return this;
    }

    /**
     * Sets the end point.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     * @throws NullPointerException If {@code endPoint} is {@code null}.
     */
    public GW2APIClient endPoint(final String value) throws NullPointerException {
        Objects.requireNonNull(value);
        endPoint = value;
        return this;
    }

    /**
     * Sets the language to use for i18n values.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     */
    public GW2APIClient language(final String value) {
        return putParameter("lang", value); // NOI8N.
    }

    /**
     * Sets the application key required for authenticated endpoints.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     */
    public GW2APIClient applicationKey(final String value) {
        return putParameter("access_token", value); // NOI8N.
    }

    /**
     * Sets the integer ids to use on the query.
     * <br>This parameter is mutually exclusive with the {@code id} parameter.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     */
    public GW2APIClient ids(final int... value) {
        putParameter("id", null); // NOI8N.
        return putParameter("ids", idsToString(value)); // NOI8N.
    }

    /**
     * Sets the string ids to use on the query.
     * <br>This parameter is mutually exclusive with the {@code id} parameter.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     */
    public GW2APIClient ids(final String... value) {
        putParameter("id", null); // NOI8N.
        return putParameter("ids", idsToString(value)); // NOI8N.
    }

    /**
     * Sets a single integer id to use on the query.
     * <br>This parameter is mutually exclusive with the {@code ids} parameter.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     */
    public GW2APIClient id(final int value) {
        putParameter("ids", null); // NOI8N.
        return putParameter("id", idsToString(value)); // NOI8N.
    }

    /**
     * Sets a single string id to use on the query.
     * <br>This parameter is mutually exclusive with the {@code ids} parameter.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     */
    public GW2APIClient id(final String value) {
        putParameter("ids", null); // NOI8N.
        return putParameter("id", idsToString(value)); // NOI8N.
    }

    /**
     * Adds an arbitrary parameter to the query.
     * @param key The key to the value.
     * @param value The new value. 
     * <br>If {@code null}, the key is removed from the parameters.
     * @return A {@code GW2APIClient}, never {@code null}.
     * @throws NullPointerException If {@code key} is {@code null}.
     */
    public GW2APIClient putParameter(final String key, final Object value) throws NullPointerException {
        Objects.requireNonNull(key);
        if (value == null) {
            parameters.remove(key);
        } else {
            parameters.put(key, value);
        }
        return this;
    }

    /**
     * Sets the context used to parse the result.
     * @param value The new value.
     * @return A {@code GW2APIClient}, never {@code null}.
     * @throws NullPointerException If {@code value} is {@code null}.
     */
    public GW2APIClient context(final JsonpContext value) {
        Objects.requireNonNull(value);
        context = value;
        return this;
    }

    /**
     * Encode a string parameter.
     * @param value The source value.
     * @return A {@code String} instance, never {@code null}.
     * @throws NullPointerException If {@code value} is {@code null}.
     */
    public static String encodeURLParameter(final String value) throws NullPointerException {
        try {
            String result = URLEncoder.encode(value, "utf-8"); // NOI18N.
            result = result.replaceAll("\\+", "%20"); // NOI18N.
            return result;
        } catch (UnsupportedEncodingException ex) {
            // Should never happen.
            Logger.getLogger(GW2APIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ""; // NOI18N.
    }

    /**
     * Convert given {@code int} ids into a {@code String} for the query.
     * @param ids the ids.
     * @return A {@code String}, never {@code null}.
     * <br>Contains {@code "all"} no id provided.
     */
    public static String idsToString(final int... ids) {
        String result = "all"; // NOI18N.
        if (ids.length > 0) {
            result = Arrays.stream(ids)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining(",")); // NOI18N.
        }
        return result;
    }

    /**
     * Convert given {@code String} ids into a {@code String} for the query.
     * @param ids the ids.
     * @return A {@code String}, never {@code null}.
     * <br>Contains {@code "all"} no id provided.
     */
    public static String idsToString(final String... ids) {
        String result = "all"; // NOI18N.
        if (ids.length > 0) {
            result = Arrays.stream(ids)
                    .map(GW2APIClient::encodeURLParameter)
                    .collect(Collectors.joining(",")); // NOI18N.
        }
        return result;
    }

    /**
     * Construct the URL for the query.
     * @return A {@code String} instance, never {@code null}.
     */
    public String buildQuery() {
        final String baseCode = API_BASE_CODE;
        final String api = apiLevel.name().toLowerCase();
        final String endPoint = this.endPoint;
        final String parametersString = parameters.entrySet()
                .stream()
                .map(entry -> {
                    final String key = entry.getKey();
                    final Object value = entry.getValue();
                    String valueStr = null;
                    if (value instanceof String) {
                        valueStr = (String) value;
                    } else {
                        valueStr = value.toString();
                    }
                    return String.format("%s=%s", key, valueStr); // NOI18N.
                })
                .collect(Collectors.joining("&")); // NOI18N.
        final String pattern = parameters.isEmpty() ? "%s/%s/%s" : "%s/%s/%s?%s"; // NOI18N.
        final String query = String.format(pattern, baseCode, api, endPoint, parametersString);
        return query;
    }

    /**
     * Do a simply query that returns a simple object.
     * @param <T> The type to use.
     * @param targetClass The target class.`
     * @return An {@code Optional<T>} instance, never {@code null}.
     */
    public <T> Optional<T> queryObject(final Class<T> targetClass) {
        Logger.getLogger(WebQuery.class.getName()).entering(getClass().getName(), "queryObject", targetClass); // NOI18N.
        final String query = buildQuery();
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "queryObject " + query); // NOI18N.
        Optional<T> result = Optional.empty();
        try {
            final URL url = new URL(query);
            final T value = context.loadObject(targetClass, url);
            result = Optional.ofNullable(value);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            Logger.getLogger(WebQuery.class.getName()).exiting(getClass().getName(), "queryObject"); // NOI18N.
        }
        return result;
    }

    /**
     * Do a simple query that returns a list of object.
     * @param <T> The type to use.
     * @param targetClass The target class.
     * @return A {@code List<T>} instance, never {@code null}.
     */
    public <T> List<T> queryArray(final Class<T> targetClass) {
        Logger.getLogger(WebQuery.class.getName()).entering(getClass().getName(), "queryArray", targetClass); // NOI18N.
        final String query = buildQuery();
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "queryArray " + query); // NOI18N.
        List<T> result = Collections.EMPTY_LIST;
        try {
            final URL url = new URL(query);
            final Collection<T> value = context.loadObjectArray(targetClass, url);
            result = new ArrayList<>(value);
            result = Collections.unmodifiableList(result);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            Logger.getLogger(WebQuery.class.getName()).exiting(getClass().getName(), "queryArray"); // NOI18N.
        }
        return result;
    }

    /**
     * Do a simple query that returns a list of enum values.
     * @param <T> The type to use.
     * @param targetClass The target class.
     * @return A {@code List<T>} instance, never {@code null}.
     */
    public <T extends Enum> List<T> queryEnumValues(final Class<T> targetClass) {
        Logger.getLogger(WebQuery.class.getName()).entering(getClass().getName(), "queryArray", targetClass); // NOI18N.
        final String query = buildQuery();
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "queryArray " + query); // NOI18N.
        List<T> result = Collections.EMPTY_LIST;
        try {
            final URL url = new URL(query);
            final Collection<T> value = context.loadEnumArray(targetClass, url);
            result = new ArrayList<>(value);
            result = Collections.unmodifiableList(result);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            Logger.getLogger(WebQuery.class.getName()).exiting(getClass().getName(), "queryArray"); // NOI18N.
        }
        return result;
    }

    /**
     * Do a simple query that returns a page.
     * @param <T> The type to use.
     * @param targetClass The target class.
     * @return A {@code List<T>} instance, never {@code null}.
     */
    // @todo Support page query per page index.
    public <T> PageResult<T> queryPage(final Class<T> targetClass) {
        Logger.getLogger(WebQuery.class.getName()).entering(getClass().getName(), "queryPage", targetClass); // NOI18N.
        final String query = buildQuery();
        Logger.getLogger(WebQuery.class.getName()).log(Level.INFO, "queryPage " + query); // NOI18N.
        PageResult<T> result = PageResult.EMPTY;
        try {
            final URL url = new URL(query);
            result = context.loadPage(targetClass, url);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(WebQuery.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            Logger.getLogger(WebQuery.class.getName()).exiting(getClass().getName(), "queryPage"); // NOI18N.
        }
        return result;
    }
}
