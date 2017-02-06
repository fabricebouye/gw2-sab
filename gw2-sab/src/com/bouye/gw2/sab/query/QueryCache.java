/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cache for web queries.
 * @author Fabrice Bouyé
 */
enum QueryCache {
    /**
     * The unique instance of this class.
     */
    INSTANCE;

    /**
     * An entry in the cache.
     * @author Fabrice Bouyé
     */
    private static final class Result<T> {

        /**
         * The source JSON.
         */
        String json;
        /**
         * The query result.
         */
        T value;
    }

    /**
     * The query result cache.
     * <br>The cache uses soft references that may be garbaged when the VM runs out of memory.
     */
    private final Map<String, SoftReference<Result>> cache = Collections.synchronizedMap(new HashMap<>());

    /**
     * Retrieves a query result from the cache.
     * @param <T> The type of the object to retrieve.
     * @param url The source URL.
     * @param json The JSON returned by the URL.
     * @param producer Produces the result from the JSON; invoked in case of cache fault.
     * @return A {@code T} instance, may be {@code null}.
     * @throws NullPointerException If {@code url}, {@code json} or {@code producer} is {@code null}.
     */
    public <T> T get(final String url, final String json, final Function<String, T> producer) throws NullPointerException {
        Objects.requireNonNull(url);
        Objects.requireNonNull(json);
        Objects.requireNonNull(producer);
        T result = null;
        synchronized (cache) {
            SoftReference<Result> resultRef = cache.get(url);
            if (resultRef != null) {
                result = (!resultRef.get().json.equals(json)) ? null : (T) resultRef.get().value;
            }
            if (result == null) {
                final Result<T> cacheEntry = new Result();
                cacheEntry.json = json;
                try {
                    cacheEntry.value = producer.apply(json);
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
                resultRef = new SoftReference<>(cacheEntry);
                cache.put(url, resultRef);
            }
        }
        return result;
    }
}
