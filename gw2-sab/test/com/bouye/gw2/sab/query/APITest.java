/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

import api.web.gw2.mapping.core.APILevel;
import api.web.gw2.mapping.core.EnumValueFactory;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.characters.Character;
import api.web.gw2.mapping.v2.account.AccountAccessType;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * API automated tests.
 * @author Fabrice Bouyé
 */
public class APITest {

    public APITest() {
    }

    private static final Properties SETTINGS = new Properties();

    @BeforeClass
    public static void setUpClass() throws IOException {
        final File file = new File("settings.properties"); // NOI18N.
        assertTrue(file.exists());
        assertTrue(file.canRead());
        try (final InputStream input = new FileInputStream(file)) {
            SETTINGS.load(input);
        }
        assertNotNull(SETTINGS.getProperty("app.key")); // NOI18N.
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAccount() {
        final String expName = SETTINGS.getProperty("account.name"); // NOI18N.
        final ZonedDateTime expCreated = ZonedDateTime.parse(SETTINGS.getProperty("account.created")); // NOI18N.
        final int expWorld = Integer.parseInt(SETTINGS.getProperty("account.world")); // NOI18N.
        final AccountAccessType expAccess = EnumValueFactory.INSTANCE.mapEnumValue(AccountAccessType.class, SETTINGS.getProperty("account.access")); // NOI18N.
        final boolean expCommander = Boolean.parseBoolean(SETTINGS.getProperty("account.commander")); // NOI18N.
        final int expFractal = Integer.parseInt(SETTINGS.getProperty("account.fractal_level")); // NOI18N.
        assertNotNull(expName);
        //
        final Optional<Account> value = GW2APIClient.create()
                .applicationKey(SETTINGS.getProperty("app.key")) // NOI18N.
                .apiLevel(APILevel.V2)
                .endPoint("account") // NOI18N.
                .queryObject(Account.class);
        assertTrue(value.isPresent());
        assertEquals(expName, value.get().getName());
        assertEquals(expCreated, value.get().getCreated());
        assertEquals(expWorld, value.get().getWorld());
        assertEquals(expAccess, value.get().getAccess());
        assertEquals(expCommander, value.get().isCommander());
        assertEquals(expFractal, value.get().getFractalLevel());
    }

    @Test
    public void testCharacters() {
        final List<String> expNames = Arrays.asList(SETTINGS.getProperty("characters.names").split(",")); // NOI18N.
        //
        final List<String> value = GW2APIClient.create()
                .applicationKey(SETTINGS.getProperty("app.key")) // NOI18N.
                .apiLevel(APILevel.V2)
                .endPoint("characters") // NOI18N.
                .queryArray(String.class);
        // Order is not important.
        assertThat(new HashSet<>(value), is(new HashSet<>(expNames)));
    }

    @Test
    public void testCharacter() {
        final String expName = SETTINGS.getProperty("character.name"); // NOI18N.
        final int expLevel = Integer.parseInt(SETTINGS.getProperty("character.level")); // NOI18N.
        final CharacterProfession expProfession = EnumValueFactory.INSTANCE.mapEnumValue(CharacterProfession.class, SETTINGS.getProperty("character.profession")); // NOI18N.
        assertNotNull(expName);
        //
        final String id = SETTINGS.getProperty("character.name"); // NOI18N.
        final Optional<Character> value = GW2APIClient.create()
                .applicationKey(SETTINGS.getProperty("app.key")) // NOI18N.
                .apiLevel(APILevel.V2)
                .endPoint("characters") // NOI18N.
                .id(id)
                .queryObject(Character.class);
        assertTrue(value.isPresent());
        assertEquals(expName, value.get().getName());
        assertEquals(expLevel, value.get().getLevel());
        assertEquals(expProfession, value.get().getProfession());
    }
}
