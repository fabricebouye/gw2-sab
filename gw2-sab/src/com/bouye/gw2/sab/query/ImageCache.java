/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

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
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        Image result = cache.get(url);
        if (result == null && !cache.containsKey(url)) {
            try {
                result = new Image(url, true);
                cache.put(url, result);
            } // Happens when URL is invalid.
            catch (IllegalArgumentException ex) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, ex.getMessage(), ex);
                cache.put(url, null);
            }
        }
        return result;
    }
}
