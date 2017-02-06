/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.text;

import api.web.gw2.mapping.core.CoinAmount;
import api.web.gw2.mapping.core.JsonpUtils;
import api.web.gw2.mapping.v2.items.ItemArmorType;
import api.web.gw2.mapping.v2.items.ItemInfixUpgradeAttribute;
import api.web.gw2.mapping.v2.items.ItemInfusionSlotFlag;
import api.web.gw2.mapping.v2.items.ItemRarity;
import api.web.gw2.mapping.v2.items.ItemTrinketType;
import api.web.gw2.mapping.v2.items.ItemType;
import api.web.gw2.mapping.v2.items.ItemWeaponDamageType;
import api.web.gw2.mapping.v2.items.ItemWeaponType;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.ImageCache;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utilities class used for label and rich text management.
 * @author Fabrice Bouyé
 */
public enum LabelUtils {
    INSTANCE;

    private static final String CONTENT_TAG = "content"; // NOI18N.
    private static final String LINE_BREAK_TAG = "br"; // NOI18N.
    private static final String PARAGRAPH_TAG = "p"; // NOI18N.
    private static final String BOLD_TAG = "b"; // NOI18N.
    private static final String QUOTE_TAG = "quote"; // NOI18N.
    private static final String FONT_AWESOME_TAG = "font-awesome"; // NOI18N.
    private static final String COINS_TAG = "coins"; // NOI18N.
    private static final String STATS_UP_TAG = "stats-up"; // NOI18N.
    private static final String STATS_DOWN_TAG = "stats-down"; // NOI18N.
    private static final String NOTE_TAG = "note"; // NOI18N.
    private static final String LINK_TAG = "link"; // NOI18N.

    private static final String HREF_ATTR = "href"; // NOI18N.
    private static final String AMOUNT_ATTR = "amount"; // NOI18N.

    private final PseudoClass BOLD_PSEUDO_CLASS = PseudoClass.getPseudoClass("strong"); // NOI18N.
    private final PseudoClass QUOTE_PSEUDO_CLASS = PseudoClass.getPseudoClass(QUOTE_TAG); // NOI18N.
    private final PseudoClass FONT_AWESOME_PSEUDO_CLASS = PseudoClass.getPseudoClass(FONT_AWESOME_TAG);
    private final PseudoClass STATS_UP_PSEUDO_CLASS = PseudoClass.getPseudoClass(STATS_UP_TAG); // NOI18N.
    private final PseudoClass STATS_DOWN_PSEUDO_CLASS = PseudoClass.getPseudoClass(STATS_DOWN_TAG); // NOI18N.
    private final PseudoClass NOTE_PSEUDO_CLASS = PseudoClass.getPseudoClass(NOTE_TAG); // NOI18N.
    
    /**
     * Provides a suitable label from an {@code Enum} instance.
     * @param value The value, may be {@code null}.
     * @return A {@code String}, may be {@code null} if {@code value} is {@code null}.
     */
    public String toLabel(final Enum value) {
        return (value == null) ? null : JsonpUtils.INSTANCE.javaEnumToJavaClassName(value);
    }

    /**
     * Provides a {@code PseudoClass} from an {@code Enum} instance.
     * @param value The value.
     * @return A {@code PseudoClass} instance, never {@code null}
     * @throws NullPointerException If {@code value} is {@code null}.
     */
    public PseudoClass toPseudoClass(final Enum value) throws NullPointerException {
        Objects.requireNonNull(value);
        final String pseudoClassName = toLabel(value);
        return PseudoClass.getPseudoClass(pseudoClassName);
    }

    /**
     * Split rich text content into nodes to display on screen.
     * @param string The source rich text, may be {@code null}.
     * @return A non-modifiable {@code List<Node>}, never {@code null}.
     */
    public List<Node> split(final String string) {
        return split(string, null);
    }

    /**
     * Split rich text content into nodes to display on screen.
     * <br>given text is imported as XML and given to a SAX parser.
     * @param string The source rich text, may be {@code null}.
     * @param linkActivator A {@code Consumer<String>} called when clicking on a {@code Hyperlink}, may be {@code null}.
     * @return A non-modifiable {@code List<Node>}, never {@code null}.
     */
    public List<Node> split(final String string, final Consumer<String> linkActivator) {
        List<Node> result = Collections.emptyList();
        if (string != null) {
            final List<Node> nodeList = new LinkedList();
            try {
                final SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setValidating(false);
                final SAXParser saxParser = spf.newSAXParser();
                String escapedContent = string.replaceAll("&", "&amp;"); // NOI18N.
                final String xmlString = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><%s>%s</%s>", CONTENT_TAG, escapedContent, CONTENT_TAG); // NOI18N.
                final Map<String, Boolean> styleAttributes = initializeAttributeMap();
                try (final InputStream source = new ByteArrayInputStream(xmlString.getBytes("UTF-8"))) { // NOI18N.
                    saxParser.parse(source, new DefaultHandler() {
                        private Node node;

                        @Override
                        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
                            node = null;
                            switch (qName) {
                                case BOLD_TAG:
                                case QUOTE_TAG:
                                case FONT_AWESOME_TAG:
                                case STATS_UP_TAG:
                                case STATS_DOWN_TAG:
                                case NOTE_TAG: {
                                    styleAttributes.put(qName, Boolean.TRUE);
                                }
                                break;
                                case LINK_TAG: {
                                    styleAttributes.put(LINK_TAG, Boolean.TRUE);
                                    final String href = attributes.getValue(HREF_ATTR);
                                    final Hyperlink hyperlink = new Hyperlink(string);
                                    hyperlink.setId("Hyperlink"); // NOI18N.
                                    hyperlink.setUserData(href);
                                    hyperlink.setOnAction(actionEvent -> {
                                        if (linkActivator != null) {
                                            linkActivator.accept(href);
                                        }
                                    });
                                    node = hyperlink;
                                }
                                break;
                                case PARAGRAPH_TAG: {
                                    final Node paragraphBreak = new Text("\n\n"); // NOI18N.
                                    paragraphBreak.setId("Text"); // NOI18N.
                                    nodeList.add(paragraphBreak);
                                }
                                break;
                                case COINS_TAG: {
                                    final String amountStr = attributes.getValue(AMOUNT_ATTR);
                                    final int amount = Integer.parseInt(amountStr);
                                    nodeList.addAll(fromCopper(amount));
                                }
                                break;
                                case CONTENT_TAG:
                                default:
                            }
                        }

                        @Override
                        public void characters(char[] ch, int start, int length) throws SAXException {
                            final String string = new String(ch, start, length);
                            if (styleAttributes.get(LINK_TAG)) {
                                final Hyperlink hyperlink = (Hyperlink) node;
                                hyperlink.setText(string);
                            } else {
                                final Text text = new Text(string);
                                text.setId("Text"); // NOI18N.
                                node = text;
                            }
                            node.pseudoClassStateChanged(BOLD_PSEUDO_CLASS, styleAttributes.get(BOLD_TAG));
                            node.pseudoClassStateChanged(QUOTE_PSEUDO_CLASS, styleAttributes.get(QUOTE_TAG));
                            node.pseudoClassStateChanged(FONT_AWESOME_PSEUDO_CLASS, styleAttributes.get(FONT_AWESOME_TAG));
                            node.pseudoClassStateChanged(STATS_UP_PSEUDO_CLASS, styleAttributes.get(STATS_UP_TAG));
                            node.pseudoClassStateChanged(STATS_DOWN_PSEUDO_CLASS, styleAttributes.get(STATS_DOWN_TAG));
                            node.pseudoClassStateChanged(NOTE_PSEUDO_CLASS, styleAttributes.get(NOTE_TAG));
                            nodeList.add(node);
                        }

                        @Override
                        public void endElement(String uri, String localName, String qName) throws SAXException {
                            switch (qName) {
                                case BOLD_TAG:
                                case QUOTE_TAG:
                                case FONT_AWESOME_TAG:
                                case STATS_UP_TAG:
                                case STATS_DOWN_TAG:
                                case NOTE_TAG:
                                case LINK_TAG:
                                    styleAttributes.put(qName, Boolean.FALSE);
                                    break;
                                case LINE_BREAK_TAG:
                                    final Node lineBreak = new Text("\n"); // NOI18N.
                                    lineBreak.setId("Text"); // NOI18N.
                                    nodeList.add(lineBreak);
                                    break;
                                case CONTENT_TAG:
                                default:
                            }
                        }

                    });
                }
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                Logger.getLogger(LabelUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            result = nodeList;
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Initializes an empty attribute map.
     * @return A {@code Map<String, Boolean>} instance, never {@code null}.
     */
    private Map<String, Boolean> initializeAttributeMap() {
        final Map<String, Boolean> result = new HashMap();
        result.put(BOLD_TAG, Boolean.FALSE);
        result.put(QUOTE_TAG, Boolean.FALSE);
        result.put(FONT_AWESOME_TAG, Boolean.FALSE);
        result.put(STATS_UP_TAG, Boolean.FALSE);
        result.put(STATS_DOWN_TAG, Boolean.FALSE);
        result.put(NOTE_TAG, Boolean.FALSE);
        result.put(LINK_TAG, Boolean.FALSE);
        return result;
    }

    /**
     * Decorator method that makes a label a quote.
     * @param value The source string.
     * @return A {@code String} instance, never {@code null}.
     */
    public String toQuote(final String value) {
        return String.format("<%s>%s</%1$s>", QUOTE_TAG, value); // NOI18N.        
    }

    /**
     * Decorator method that makes a label strong.
     * @param value The source string.
     * @return A {@code String} instance, never {@code null}.
     */
    public String toStrong(final String value) {
        return String.format("<%s>%s</%1$s>", BOLD_TAG, value); // NOI18N.        
    }

    /**
     * Decorator method that makes a label awesome.
     * @param value The source string.
     * @return A {@code String} instance, never {@code null}.
     */
    public String toFontAwesome(final String value) {
        return String.format("<%s>%s</%1$s>", FONT_AWESOME_TAG, value); // NOI18N.        
    }

    public String toCoins(final CoinAmount value) {
        final long amount = (value == null) ? 0 : value.toCopper();
        return String.format("<%s %s=\"%d\"/>", COINS_TAG, AMOUNT_ATTR, amount); // NOI18N.        
    }

    public String toStatsUp(final String value) {
        return String.format("<%s>%s</%1$s>", STATS_UP_TAG, value); // NOI18N.        
    }

    public String toStatsDown(final String value) {
        return String.format("<%s>%s</%1$s>", STATS_DOWN_TAG, value); // NOI18N.        
    }

    public String toNote(final String value) {
        return String.format("<%s>%s</%1$s>", NOTE_TAG, value); // NOI18N.        
    }

    public String toLink(final String text) {
        return toLink(text, text);
    }

    public String toLink(final String text, final String url) {
        final String t = (text == null) ? "" : text; // NOI18N.       
        final String u = (url == null) ? t : url;
        return String.format("<%s %s=\"%s\"/>%s</%1$s>", LINK_TAG, HREF_ATTR, u, t); // NOI18N.        
    }

    /**
     * Creates a line break.
     * @return A {@code String} instance, never {@code null}.
     */
    public String lineBreak() {
        return String.format("<%s/>", LINE_BREAK_TAG); // NOI18N.      
    }

    /**
     * Gold coin in copper coins.
     */
    private static final int GOLD_VALUE = 10000;
    /**
     * Silver coin in copper coins.
     */
    private static final int SILVER_VALUE = 100;

    /**
     * Create labels for given gem amount.
     * <br>Value 0 is hidden.
     * @param value The amount in gems.
     * @return A non-modifiable {@code List<Node>} instance, never {@code null}.
     */
    public List<Node> fromGems(final long value) {
        final List<Node> result = new ArrayList<>();
        labelsForCurrency(CurrencyType.GEM, value, result);
        return Collections.unmodifiableList(result);
    }

    /**
     * Create labels for given copper amount.
     * <br>Value 0 is hidden.
     * @param value The amount in copper coins.
     * @return A non-modifiable {@code List<Node>} instance, never {@code null}.
     */
    public List<Node> fromCopper(final long value) {
        return fromCopper(value, false);
    }

    /**
     * Create labels for given copper amount.
     * @param value The amount in copper coins.
     * @param showEmpty If {@code true}, 0 values are shown.
     * @return A non-modifiable {@code List<Node>} instance, never {@code null}.
     */
    public List<Node> fromCopper(final long value, final boolean showEmpty) {
        final List<Node> result = new ArrayList<>();
        final long gold = value / GOLD_VALUE;
        final long silver = (value - gold * GOLD_VALUE) / SILVER_VALUE;
        final long copper = (value - gold * GOLD_VALUE - silver * SILVER_VALUE);
        if (showEmpty || gold > 0) {
            labelsForCurrency(CurrencyType.GOLD, gold, result);
        }
        if (showEmpty || silver > 0) {
            labelsForCurrency(CurrencyType.SILVER, silver, result);
        }
        if (value == 0 || showEmpty || copper > 0) {
            labelsForCurrency(CurrencyType.COPPER, copper, result);
        }
        if (!result.isEmpty()) {
            result.remove(result.size() - 1);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Various currency types.
     * @author Fabrice Bouyé
     */
    private enum CurrencyType {
        GOLD, SILVER, COPPER, GEM;
    }

    /**
     * Number formatter.
     */
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();

    /**
     * Generates labels to represent the given currency amount.
     * @param currencyType The type of currency to use.
     * @param amount The amount to show.
     * @param result The result list.
     */
    private void labelsForCurrency(final CurrencyType currencyType, final long amount, final List<Node> result) {
        final Text amountText = new Text(numberFormat.format(amount));
        amountText.getStyleClass().add("coin-label"); // NOI18N.
        final PseudoClass pseudoClass = toPseudoClass(currencyType);
        amountText.pseudoClassStateChanged(pseudoClass, true);
        result.add(amountText);
        result.add(new Text(" ")); // NOI18N.
        result.add(iconForCurrency(currencyType));
        result.add(new Text(" ")); // NOI18N.        
    }

    /**
     * Create proper currency icon.
     * @param currencyType The type of currency to use.
     * @return A {@code Node}, never {@code null}.
     */
    private Node iconForCurrency(final CurrencyType currencyType) {
        final String imageId = (currencyType == CurrencyType.GEM) ? "ui_gem" : String.format("ui_coin_%s", currencyType.name().toLowerCase()); // NOI18N.
        final Image image = ImageCache.INSTANCE.getImage(imageId);
        Node result = null;
        if (image != null) {
            final ImageView imageView = new ImageView(image);
            imageView.pseudoClassStateChanged(PseudoClass.getPseudoClass("icon"), true); // NOI18N.
            result = imageView;
        } else {
            final Region coin = new Region();
            final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(currencyType);
            coin.pseudoClassStateChanged(pseudoClass, true);
            result = coin;
        }
        final String styleClass = "coin"; // NOI18N.
        result.getStyleClass().add(styleClass);
        return result;
    }

    /**
     * When not using icons, use roman numerals for traits.
     * @param arabic The source number.
     * @return A {@code String} instance, may be {@code null}.
     */
    public String toRomanNumeral(final int arabic) {
        // We have only 9 numbers so far, no need to use a converter API.
        String result = null;
        switch (arabic) {
            case 1:
                result = "I"; // NOI18N.
                break;
            case 2:
                result = "II"; // NOI18N.
                break;
            case 3:
                result = "III"; // NOI18N.
                break;
            case 4:
                result = "IV"; // NOI18N.
                break;
            case 5:
                result = "V"; // NOI18N.
                break;
            case 6:
                result = "VI"; // NOI18N.
                break;
            case 7:
                result = "VII"; // NOI18N.
                break;
            case 8:
                result = "VIII"; // NOI18N.
                break;
            case 9:
                result = "IX"; // NOI18N.
                break;
        }
        return result;
    }

    public String fromItemRarity(final ItemRarity rarity) {
        final String key = String.format("rarity.%s.label", toLabel(rarity).toLowerCase()); // NOI18.
        return SABConstants.I18N.getString(key);
    }

    public String fromItemInfixUpgradeAttribute(final ItemInfixUpgradeAttribute attribute) {
        final String key = String.format("attribute.%s.label", toLabel(attribute).toLowerCase()); // NOI18.
        return SABConstants.I18N.getString(key);
    }

    public String fromItemInfusionSlotFlag(final ItemInfusionSlotFlag flag) {
        final String key = String.format("infusion-slot.%s.label", toLabel(flag).toLowerCase()); // NOI18.
        return SABConstants.I18N.getString(key);
    }

    public String fromItemType(final ItemType type) {
        final String key = String.format("item.%s.label", toLabel(type).toLowerCase()); // NOI18.
        return SABConstants.I18N.getString(key);
    }

    public String fromItemArmorType(final ItemArmorType type) {
        final String key = String.format("armor.%s.label", toLabel(type).toLowerCase()); // NOI18.
        return SABConstants.I18N.getString(key);
    }

    public String fromItemWeaponType(final ItemWeaponType type) {
        final String key = String.format("weapon.%s.label", toLabel(type).toLowerCase()); // NOI18.
        return SABConstants.I18N.getString(key);
    }

    public String fromItemTrinketType(final ItemTrinketType type) {
        final String key = String.format("trinket.%s.label", toLabel(type).toLowerCase()); // NOI18.
        return SABConstants.I18N.getString(key);
    }

    public String fromItemWeaponDamageType(final ItemWeaponDamageType type) {
        final String key = String.format("damage.%s.label", toLabel(type).toLowerCase()); // NOI18.
        return SABConstants.I18N.getString(key);
    }
}
