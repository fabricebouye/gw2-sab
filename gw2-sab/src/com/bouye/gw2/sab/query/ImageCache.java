/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.db.DBStorage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 * Cache for images and icons provided by the Web API.
 * @author Fabrice Bouyé
 */
public enum ImageCache {

    /**
     * The unique instance of this class.
     */
    INSTANCE;

    /**
     * The cache.
     */
    private final Map<String, Image> cache = new WeakHashMap();

    /**
     * Retrieves an image from the cache.
     * <br>If the image is not in the cache, an empty image is returned and the data of the image is loaded in the background.
     * @param url The URL of the image.
     * @return An {@code Image} instance, may be {@code null} if {@code url} is {@code null} or empty or invalid.
     */
    public Image getImage(final String url) {
        return getImage(url, true);
    }

    /**
     * Retrieves an image from the cache.
     * @param url The URL of the image.
     * @param backgroundLoading If {@code true}, the image is loaded in background.
     * @return An {@code Image} instance, may be {@code null} if {@code url} is {@code null} or empty or invalid.
     */
    public Image getImage(final String url, final boolean backgroundLoading) {
        Image result = null;
        if (url != null && !url.trim().isEmpty()) {
            result = cache.get(url);
            if (result == null && !cache.containsKey(url)) {
                try {
                    // For local images, access the data base and ignore the background loading parameter.
                    if (!url.startsWith("https://")) { // NOI18N.
                        result = DBStorage.INSTANCE.getImageFromCache(url);
                    } // @todo Return a non-null image when in offline mode.
                    else if (!SABConstants.INSTANCE.isOffline()) {
                        // Remote image.
                        result = new Image(url, backgroundLoading);
                    }
                    cache.put(url, result);
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, ex.getMessage(), ex);
                    cache.put(url, null);
                }
            }
        }
        return result;
    }
}
