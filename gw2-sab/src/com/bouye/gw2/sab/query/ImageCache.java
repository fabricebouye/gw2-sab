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
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
     * The icon cache.
     * <br>The cache uses soft references that may be garbaged when the VM runs out of memory.
     */
    private final Map<String, SoftReference<Image>> cache = Collections.synchronizedMap(new HashMap());

    /**
     * Retrieves an image from the cache.
     * <br>If the image is not in the cache, an empty image is returned and the data of the image is loaded in the background.
     * @param url The URL of the image.
     * @return An {@code Image} instance, may be {@code null}.
     * @throws NullPointerException If {@code url} is {@code null}.
     */
    public Image getImage(final String url) throws NullPointerException {
        return getImage(url, true);
    }

    /**
     * Retrieves an image from the cache.
     * @param url The URL of the image.
     * @param backgroundLoading If {@code true}, the image is loaded in background.
     * @return An {@code Image} instance, may be {@code null}.
     * @throws NullPointerException If {@code url} is {@code null}.
     */
    public Image getImage(final String url, final boolean backgroundLoading) throws NullPointerException {
        Objects.requireNonNull(url);
        Image result = null;
        synchronized (cache) {
            SoftReference<Image> imageRef = cache.get(url);
            result = (imageRef == null) ? null : imageRef.get();
            if (result == null && !cache.containsKey(url)) {
                try {
                    // For local images, access the data base and ignore the background loading parameter.
                    if (!url.startsWith("https://")) { // NOI18N.
                        result = DBStorage.INSTANCE.getImageFromCache(url);
                    } // Remote image.
                    else if (!SABConstants.INSTANCE.isOffline()) {
                        result = new Image(url, backgroundLoading);
                    } else {
                        // @todo Return a local (as embededed within the app) default non-null image when in offline mode.   
                    }
                    imageRef = new SoftReference<>(result);
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, ex.getMessage(), ex);
                    cache.put(url, null);
                }
                // Storing the key upon failure should prevent from further retrieval attempts.
                cache.put(url, imageRef);
            }
        }
        return result;
    }
}
