/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene;

import api.web.gw2.mapping.core.JsonpSAXMarshaller;
import api.web.gw2.mapping.v2.tokeninfo.TokenInfo;
import com.bouye.gw2.sab.session.Session;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test support class.
 * @author Fabrice Bouyé
 */
public enum SABTestUtils {
    INSTANCE;

    private final Properties settings = new Properties();

    private SABTestUtils() {
        final File file = new File("settings.properties"); // NOI18N.
        if (file.exists() && file.canRead()) {
            try (final InputStream input = new FileInputStream(file)) {
                settings.load(input);
            } catch (IOException ex) {
                Logger.getLogger(SABTestUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Gets the test session object.
     * @return A {@code Session} instance, never {@code null}.
     */
    public Session getTestSession() {
        final String appKey = settings.getProperty("app.key"); // NOI18N.
        final Session result = new Session(appKey);
        final String jsonTokenInfo = settings.getProperty("token.info"); // NOI18N.
        try (final InputStream input = new ByteArrayInputStream(jsonTokenInfo.getBytes(StandardCharsets.UTF_8))) {
            final TokenInfo tokenInfo = new JsonpSAXMarshaller().loadObject(TokenInfo.class, input);
            result.setTokenInfo(tokenInfo);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }
}
