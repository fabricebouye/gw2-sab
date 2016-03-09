/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * App constants.
 * @author Fabrice Bouyé
 */
public enum SABConstants {
    /**
     * The unique instance of this class.
     */
    INSTANCE;

    public static final ResourceBundle I18N = ResourceBundle.getBundle(SAB.class.getPackage().getName().replaceAll("\\.", "/") + "/strings"); // NOI18N.
    public static final boolean IS_DEMO = true;

    private final Properties version = new Properties();

    /**
     * Creates a new instance.
     */
    private SABConstants() {
        // Load version file.
        final URL versionURL = getClass().getResource("version.properties"); // NOI18N.
        try (final InputStream input = versionURL.openStream()) {
            version.load(input);
        } catch (IOException ex) {
            Logger.getLogger(SABConstants.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public String getVersion() {
        return String.format("%s.%s.%s-%s (%s)", getMajorVersion(), getMinorVersion(), getReleaseVersion(), getBuildNumber(), getCodeName()); // NOI18N.
    }

    public String getMajorVersion() {
        return version.getProperty("version.major"); // NOI18N.
    }

    public String getMinorVersion() {
        return version.getProperty("version.minor"); // NOI18N.
    }

    public String getReleaseVersion() {
        return version.getProperty("version.release"); // NOI18N.
    }

    public String getBuildNumber() {
        return version.getProperty("build.number"); // NOI18N.
    }

    public String getCodeName() {
        return version.getProperty("version.codename"); // NOI18N.
    }
}
