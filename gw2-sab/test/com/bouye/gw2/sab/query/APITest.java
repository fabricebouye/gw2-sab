/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.query;

import api.web.gw2.mapping.core.APILevel;
import api.web.gw2.mapping.core.CoinAmount;
import api.web.gw2.mapping.core.EnumValueFactory;
import api.web.gw2.mapping.v2.account.Account;
import api.web.gw2.mapping.v2.characters.Character;
import api.web.gw2.mapping.v2.account.AccountAccessType;
import api.web.gw2.mapping.v2.account.bank.BankSlot;
import api.web.gw2.mapping.v2.account.wallet.CurrencyAmount;
import api.web.gw2.mapping.v2.backstory.answers.BackstoryAnswer;
import api.web.gw2.mapping.v2.backstory.questions.BackstoryQuestion;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import api.web.gw2.mapping.v2.currencies.Currency;
import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.items.ItemRarity;
import api.web.gw2.mapping.v2.items.ItemType;
import api.web.gw2.mapping.v2.skills.Skill;
import api.web.gw2.mapping.v2.skills.SkillType;
import api.web.gw2.mapping.v2.skins.Skin;
import api.web.gw2.mapping.v2.skins.SkinRarity;
import api.web.gw2.mapping.v2.skins.SkinType;
import api.web.gw2.mapping.v2.wvw.MapType;
import api.web.gw2.mapping.v2.wvw.abilities.Ability;
import api.web.gw2.mapping.v2.wvw.objectives.Objective;
import api.web.gw2.mapping.v2.wvw.objectives.ObjectiveType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.function.Function;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.hamcrest.CoreMatchers.is;

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
        System.out.println("testAccount");
        final String expId = SETTINGS.getProperty("account.id"); // NOI18N.
        final String expName = SETTINGS.getProperty("account.name"); // NOI18N.
        final ZonedDateTime expCreated = ZonedDateTime.parse(SETTINGS.getProperty("account.created")); // NOI18N.
        final int expWorld = Integer.parseInt(SETTINGS.getProperty("account.world")); // NOI18N.
        final AccountAccessType expAccess = EnumValueFactory.INSTANCE.mapEnumValue(AccountAccessType.class, SETTINGS.getProperty("account.access")); // NOI18N.
        final boolean expCommander = Boolean.parseBoolean(SETTINGS.getProperty("account.commander")); // NOI18N.
        final int expFractal = Integer.parseInt(SETTINGS.getProperty("account.fractal_level")); // NOI18N.
        assertNotNull(expId);
        assertNotNull(expName);
        //
        final Optional<Account> value = GW2APIClient.create()
                .applicationKey(SETTINGS.getProperty("app.key")) // NOI18N.
                .apiLevel(APILevel.V2)
                .endPoint("account") // NOI18N.
                .queryObject(Account.class);
        assertTrue(value.isPresent());
        assertEquals(expId, value.get().getId());
        assertEquals(expName, value.get().getName());
        assertEquals(expCreated, value.get().getCreated());
        assertEquals(expWorld, value.get().getWorld());
        assertEquals(expAccess, value.get().getAccess());
        assertEquals(expCommander, value.get().isCommander());
        assertEquals(expFractal, value.get().getFractalLevel());
    }

    @Test
    public void testCharacters() {
        System.out.println("testCharacters");
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
        System.out.println("testCharacter");
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

    @Test
    public void testItems() {
        System.out.println("testItems"); // NOI18N.
        final int[] ids = Arrays.stream(SETTINGS.getProperty("items.ids").split(",")) // NOI18N.
                .mapToInt(Integer::parseInt)
                .toArray();
        Arrays.stream(ids)
                .forEach(this::testItem);
    }

    private void testItem(final int idToTest) {
        System.out.printf("testItem(%d)%n", idToTest); // NOI18N.
        final String prefix = String.format("item.%d.", idToTest); // NOI18N.
        final int expId = Integer.parseInt(SETTINGS.getProperty(prefix + "id")); // NOI18N.
        final String expName = SETTINGS.getProperty(prefix + "name"); // NOI18N.
        final Optional<String> expDescription = getOptional(prefix + "description", value -> value); // NOI18N.
        final ItemType expType = EnumValueFactory.INSTANCE.mapEnumValue(ItemType.class, SETTINGS.getProperty(prefix + "type")); // NOI18N.
        final int expLevel = Integer.parseInt(SETTINGS.getProperty(prefix + "level")); // NOI18N.
        final ItemRarity expRarity = EnumValueFactory.INSTANCE.mapEnumValue(ItemRarity.class, SETTINGS.getProperty(prefix + "rarity")); // NOI18N.\
        final CoinAmount expVendorValue = CoinAmount.ofCopper(Integer.parseInt(SETTINGS.getProperty(prefix + "vendor_value"))); // NOI18N.)
        final OptionalInt expDefaultSkin = getOptionalInt(prefix + "default_skin"); // NOI18N.
        assertNotNull(expName);
        //
        final String lang = SETTINGS.getProperty("lang"); // NOI18N.
        final Optional<Item> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .endPoint("items") // NOI18N.
                .language(lang)
                .id(idToTest)
                .queryObject(Item.class);
        assertTrue(value.isPresent());
        assertEquals(expId, value.get().getId());
        assertEquals(expName, value.get().getName());
        assertEquals(expDescription, value.get().getDescription());
        assertEquals(expType, value.get().getType());
        assertEquals(expLevel, value.get().getLevel());
        assertEquals(expRarity, value.get().getRarity());
        assertEquals(expVendorValue, value.get().getVendorValue());
        assertEquals(expDefaultSkin, value.get().getDefaultSkin());
    }

    @Test
    public void testSkins() {
        System.out.println("testSkins"); // NOI18N.
        final int[] ids = Arrays.stream(SETTINGS.getProperty("skins.ids").split(",")) // NOI18N.
                .mapToInt(Integer::parseInt)
                .toArray();
        Arrays.stream(ids)
                .forEach(this::testSkin);
    }

    private void testSkin(final int idToTest) {
        System.out.printf("testSkin(%d)%n", idToTest); // NOI18N.
        final String prefix = String.format("skin.%d.", idToTest); // NOI18N.
        final int expId = Integer.parseInt(SETTINGS.getProperty(prefix + "id")); // NOI18N.
        final String expName = SETTINGS.getProperty(prefix + "name"); // NOI18N.
        final Optional<String> expDescription = getOptional(prefix + "description", value -> value); // NOI18N.
        final SkinType expType = EnumValueFactory.INSTANCE.mapEnumValue(SkinType.class, SETTINGS.getProperty(prefix + "type")); // NOI18N.
        final SkinRarity expRarity = EnumValueFactory.INSTANCE.mapEnumValue(SkinRarity.class, SETTINGS.getProperty(prefix + "rarity")); // NOI18N.\
        assertNotNull(expName);
        //
        final String lang = SETTINGS.getProperty("lang"); // NOI18N.
        final Optional<Skin> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .endPoint("skins") // NOI18N.
                .language(lang)
                .id(idToTest)
                .queryObject(Skin.class);
        assertTrue(value.isPresent());
        assertEquals(expId, value.get().getId());
        assertEquals(expName, value.get().getName());
        assertEquals(expDescription, value.get().getDescription());
        assertEquals(expType, value.get().getType());
        assertEquals(expRarity, value.get().getRarity());
    }

    @Test
    public void testSkills() {
        System.out.println("testSkills"); // NOI18N.
        final int[] ids = Arrays.stream(SETTINGS.getProperty("skills.ids").split(",")) // NOI18N.
                .mapToInt(Integer::parseInt)
                .toArray();
        Arrays.stream(ids)
                .forEach(this::testSkill);
    }

    private void testSkill(final int idToTest) {
        System.out.printf("testSkill(%d)%n", idToTest); // NOI18N.
        final String prefix = String.format("skill.%d.", idToTest); // NOI18N.
        final int expId = Integer.parseInt(SETTINGS.getProperty(prefix + "id")); // NOI18N.
        final String expName = SETTINGS.getProperty(prefix + "name"); // NOI18N.
        final Optional<String> expDescription = getOptional(prefix + "description", value -> value); // NOI18N.
        final SkillType expType = EnumValueFactory.INSTANCE.mapEnumValue(SkillType.class, SETTINGS.getProperty(prefix + "type")); // NOI18N.
        assertNotNull(expName);
        //
        final String lang = SETTINGS.getProperty("lang"); // NOI18N.
        final Optional<Skill> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .endPoint("skills") // NOI18N.
                .language(lang)
                .id(idToTest)
                .queryObject(Skill.class);
        assertTrue(value.isPresent());
        assertEquals(expId, value.get().getId());
        assertEquals(expName, value.get().getName());
        assertEquals(expDescription, value.get().getDescription());
        assertEquals(expType, value.get().getType());
    }

    @Test
    public void testAbilities() {
        System.out.println("testAbilities"); // NOI18N.
        final int[] ids = Arrays.stream(SETTINGS.getProperty("abilities.ids").split(",")) // NOI18N.
                .mapToInt(Integer::parseInt)
                .toArray();
        Arrays.stream(ids)
                .forEach(this::testAbility);
    }

    private void testAbility(final int idToTest) {
        System.out.printf("testAbility(%d)%n", idToTest); // NOI18N.
        final String prefix = String.format("ability.%d.", idToTest); // NOI18N.
        final int expId = Integer.parseInt(SETTINGS.getProperty(prefix + "id")); // NOI18N.
        final String expName = SETTINGS.getProperty(prefix + "name"); // NOI18N.
        final String expDescription = SETTINGS.getProperty(prefix + "description"); // NOI18N.
        assertNotNull(expName);
        //
        final String lang = SETTINGS.getProperty("lang"); // NOI18N.
        final Optional<Ability> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .endPoint("wvw/abilities") // NOI18N.
                .language(lang)
                .id(idToTest)
                .queryObject(Ability.class);
        assertTrue(value.isPresent());
        assertEquals(expId, value.get().getId());
        assertEquals(expName, value.get().getName());
        assertEquals(expDescription, value.get().getDescription());
    }

    @Test
    public void testObjectives() {
        System.out.println("testObjectives"); // NOI18N.
        final String[] ids = SETTINGS.getProperty("objectives.ids").split(","); // NOI18N.
        Arrays.stream(ids)
                .forEach(this::testObjective);
    }

    private void testObjective(final String idToTest) {
        System.out.printf("testObjective(%s)%n", idToTest); // NOI18N.
        final String prefix = String.format("objective.%s.", idToTest); // NOI18N.
        final String expId = SETTINGS.getProperty(prefix + "id"); // NOI18N.
        final String expName = SETTINGS.getProperty(prefix + "name"); // NOI18N.
        final int expSectorId = Integer.parseInt(SETTINGS.getProperty(prefix + "sector_id")); // NOI18N.
        final ObjectiveType expType = EnumValueFactory.INSTANCE.mapEnumValue(ObjectiveType.class, SETTINGS.getProperty(prefix + "type")); // NOI18N.
        final MapType expMapType = EnumValueFactory.INSTANCE.mapEnumValue(MapType.class, SETTINGS.getProperty(prefix + "map_type")); // NOI18N.
        final int expMapId = Integer.parseInt(SETTINGS.getProperty(prefix + "map_id")); // NOI18N.
        assertNotNull(expId);
        assertNotNull(expName);
        //
        final String lang = SETTINGS.getProperty("lang"); // NOI18N.
        final Optional<Objective> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .endPoint("wvw/objectives") // NOI18N.
                .language(lang)
                .id(idToTest)
                .queryObject(Objective.class);
        assertTrue(value.isPresent());
        assertEquals(expId, value.get().getId());
        assertEquals(expName, value.get().getName());
        assertEquals(expSectorId, value.get().getSectorId());
        assertEquals(expType, value.get().getType());
        assertEquals(expMapType, value.get().getMapType());
        assertEquals(expMapId, value.get().getMapId());
    }

    @Test
    public void testBank() {
        System.out.println("testBank"); // NOI18N.
        final int expSlotNumber = Integer.parseInt(SETTINGS.getProperty("bank.slot_number")); // NOI18N.
        //
        final List<BankSlot> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .applicationKey(SETTINGS.getProperty("app.key")) // NOI18N.
                .endPoint("account/bank") // NOI18N.
                .queryArray(BankSlot.class);
        assertFalse(value.isEmpty());
        assertEquals(expSlotNumber, value.size());
    }

    @Test
    public void testWallet() {
        System.out.println("testWallet"); // NOI18N.
        //
        final List<CurrencyAmount> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .applicationKey(SETTINGS.getProperty("app.key")) // NOI18N.
                .endPoint("account/wallet") // NOI18N.
                .queryArray(CurrencyAmount.class);
        assertFalse(value.isEmpty());
    }

    @Test
    public void testCurrencies() {
        System.out.println("testCurrencies"); // NOI18N.
        //
        final String lang = SETTINGS.getProperty("lang"); // NOI18N.
        final List<Currency> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .language(lang)
                .endPoint("currencies") // NOI18N.
                .queryArray(Currency.class);
        assertFalse(value.isEmpty());
    }

    @Test
    public void testBackstoryQuestions() {
        System.out.println("testBackstoryQuestions"); // NOI18N.
        final String[] ids = SETTINGS.getProperty("backstory.questions.ids").split(","); // NOI18N.
        Arrays.stream(ids)
                .mapToInt(Integer::parseInt)
                .forEach(this::testBackstoryQuestion);
    }

    private void testBackstoryQuestion(final int idToTest) {
        System.out.printf("testBackstoryQuestion(%d)%n", idToTest); // NOI18N.
        final String prefix = String.format("backstory.question.%d.", idToTest); // NOI18N.
        final int expId = Integer.parseInt(SETTINGS.getProperty(prefix + "id")); // NOI18N.
        //
        final String lang = SETTINGS.getProperty("lang"); // NOI18N.
        final Optional<BackstoryQuestion> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .endPoint("backstory/questions") // NOI18N.
                .language(lang)
                .id(idToTest)
                .queryObject(BackstoryQuestion.class);
        assertTrue(value.isPresent());
        assertEquals(expId, value.get().getId());
    }

    @Test
    public void testBackstoryAnswers() {
        System.out.println("testBackstoryAnswers"); // NOI18N.
        final String[] ids = SETTINGS.getProperty("backstory.answers.ids").split(","); // NOI18N.
        Arrays.stream(ids)
                .forEach(this::testBackstoryAnswer);
    }

    private void testBackstoryAnswer(final String idToTest) {
        System.out.printf("testBackstoryAnswer(%s)%n", idToTest); // NOI18N.
        final String prefix = String.format("backstory.answer.%s.", idToTest); // NOI18N.
        final String expId = SETTINGS.getProperty(prefix + "id"); // NOI18N.
        assertNotNull(expId);
        //
        final String lang = SETTINGS.getProperty("lang"); // NOI18N.
        final Optional<BackstoryAnswer> value = GW2APIClient.create()
                .apiLevel(APILevel.V2)
                .endPoint("backstory/answers") // NOI18N.
                .language(lang)
                .id(idToTest)
                .queryObject(BackstoryAnswer.class);
        assertTrue(value.isPresent());
        assertEquals(expId, value.get().getId());
    }

    private <T> Optional<T> getOptional(final String property, Function<String, T> converter) {
        final String value = SETTINGS.getProperty(property);
        return (value == null) ? Optional.empty() : Optional.of(converter.apply(value));
    }

    private OptionalInt getOptionalInt(final String property) {
        final String value = SETTINGS.getProperty(property);
        return (value == null) ? OptionalInt.empty() : OptionalInt.of(Integer.parseInt(value));
    }

}
