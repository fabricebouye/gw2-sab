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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    /**
     * The default language to be used when retrieving localized resources with the Web API: {@value}.
     */
    public static final String DEFAULT_WEBAPI_LANGUAGE = "en";

    private final Properties version = new Properties();
    private final Properties settings = new Properties();
    /**
     * Contains all supported languages by the Web API.
     */
    private final Set<String> supportedWebApiLanguages;

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
        // Load other settings.
        settings.setProperty("webapi.supported.languages", DEFAULT_WEBAPI_LANGUAGE); // NOI18N.
        final URL settingsURL = getClass().getResource("SAB.properties"); // NOI18N.
        try (final InputStream input = settingsURL.openStream()) {
            settings.load(input);
        } catch (IOException ex) {
            Logger.getLogger(SABConstants.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        final String[] overrideLocations = {
            System.getProperty("user.dir"), // NOI18N.
            System.getProperty("user.home"), // NOI18N.
        };
        Arrays.stream(overrideLocations)
                .forEach(location -> {
                    final Path path = Paths.get(location, "settings.properties");
                    if (Files.exists(path) && Files.isReadable(path)) {
                        try (final InputStream input = Files.newInputStream(path)) {
                            settings.load(input);
                        } catch (IOException ex) {
                            Logger.getLogger(SABConstants.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                });
        final String supportedLanguagesStr = settings.getProperty("webapi.supported.languages"); // NOI18N.
        final Set<String> supportedLanguages = Arrays.stream(supportedLanguagesStr.split(",\\s*")) // NOI18N.
                .collect(Collectors.toSet());
        this.supportedWebApiLanguages = Collections.unmodifiableSet(supportedLanguages);
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

    /**
     * Gets the list of supported languages when querying the Web API for localized resources.
     * @return A non-modifiable {@code Set<String>}, never {@code null},
     */
    public Set<String> getSupportedWebApiLanguages() {
        return supportedWebApiLanguages;
    }

    public boolean isDemo() {
        final String valueStr = settings.getProperty("demo.mode", "false"); // NOI18N.
        final boolean result = Boolean.parseBoolean(valueStr);
        return result;
    }
}
