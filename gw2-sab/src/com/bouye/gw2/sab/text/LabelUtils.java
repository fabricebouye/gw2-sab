/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.text;

import api.web.gw2.mapping.core.JsonpUtils;
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

    private static final String CONTENT = "content"; // NOI18N.
    private static final String LINE_BREAK = "br"; // NOI18N.
    private static final String PARAGRAPH = "p"; // NOI18N.
    private static final String BOLD = "b"; // NOI18N.
    private static final String FONT_AWESOME = "font-awesome"; // NOI18N.
    private static final String LINK = "link"; // NOI18N.

    private final PseudoClass BOLD_PSEUDO_CLASS = PseudoClass.getPseudoClass("strong"); // NOI18N.
    private final PseudoClass FONT_AWESOME_PSEUDO_CLASS = PseudoClass.getPseudoClass(FONT_AWESOME);

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
                final String xmlString = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><%s>%s</%s>", CONTENT, escapedContent, CONTENT); // NOI18N.
                final Map<String, Boolean> styleAttributes = initializeAttributeMap();
                try (final InputStream source = new ByteArrayInputStream(xmlString.getBytes("UTF-8"))) { // NOI18N.
                    saxParser.parse(source, new DefaultHandler() {
                        @Override
                        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
                            switch (qName) {
                                case BOLD:
                                case FONT_AWESOME:
                                case LINK:
                                    styleAttributes.put(qName, Boolean.TRUE);
                                    break;
                                case PARAGRAPH:
                                    final Node paragraphBreak = new Text("\n\n"); // NOI18N.
                                    paragraphBreak.setId("Text"); // NOI18N.
                                    nodeList.add(paragraphBreak);
                                    break;
                                case CONTENT:
                                default:
                            }
                        }

                        @Override
                        public void characters(char[] ch, int start, int length) throws SAXException {
                            final String string = new String(ch, start, length);
                            Node node = null;
                            if (styleAttributes.get(LINK)) {
                                final Hyperlink hyperlink = new Hyperlink(string);
                                hyperlink.setId("Hyperlink"); // NOI18N.
                                hyperlink.setOnAction(actionEvent -> {
                                    if (linkActivator != null) {
                                        linkActivator.accept(string);
                                    }
                                });
                                node = hyperlink;
                            } else {
                                final Text text = new Text(string);
                                text.setId("Text"); // NOI18N.
                                node = text;
                            }
                            node.pseudoClassStateChanged(BOLD_PSEUDO_CLASS, styleAttributes.get(BOLD));
                            node.pseudoClassStateChanged(FONT_AWESOME_PSEUDO_CLASS, styleAttributes.get(FONT_AWESOME));
                            nodeList.add(node);
                        }

                        @Override
                        public void endElement(String uri, String localName, String qName) throws SAXException {
                            switch (qName) {
                                case BOLD:
                                case FONT_AWESOME:
                                case LINK:
                                    styleAttributes.put(qName, Boolean.FALSE);
                                    break;
                                case LINE_BREAK:
                                    final Node lineBreak = new Text("\n"); // NOI18N.
                                    lineBreak.setId("Text"); // NOI18N.
                                    nodeList.add(lineBreak);
                                    break;
                                case CONTENT:
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
        result.put(BOLD, Boolean.FALSE);
        result.put(FONT_AWESOME, Boolean.FALSE);
        result.put(LINK, Boolean.FALSE);
        return result;
    }

    /**
     * Decorator method that makes a label strong.
     * @param value The source string.
     * @return A {@code String} instance, never {@code null}.
     */
    public String strong(final String value) {
        return String.format("<%s>%s</%s>", BOLD, value, BOLD); // NOI18N.        
    }

    /**
     * Decorator method that makes a label awesome.
     * @param value The source string.
     * @return A {@code String} instance, never {@code null}.
     */
    public String fontAwesome(final String value) {
        return String.format("<%s>%s</%s>", FONT_AWESOME, value, FONT_AWESOME); // NOI18N.        
    }

    /**
     * Creates a line break.
     * @return A {@code String} instance, never {@code null}.
     */
    public String lineBreak() {
        return String.format("<%s/>", LINE_BREAK); // NOI18N.      
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
    public List<Node> fromGems(final int value) {
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
    public List<Node> fromCopper(final int value) {
        return fromCopper(value, false);
    }

    /**
     * Create labels for given copper amount.
     * @param value The amount in copper coins.
     * @param showEmpty If {@code true}, 0 values are shown.
     * @return A non-modifiable {@code List<Node>} instance, never {@code null}.
     */
    public List<Node> fromCopper(final int value, final boolean showEmpty) {
        final List<Node> result = new ArrayList<>();
        final int gold = value / GOLD_VALUE;
        final int silver = (value - gold * GOLD_VALUE) / SILVER_VALUE;
        final int copper = (value - gold * GOLD_VALUE - silver * SILVER_VALUE);
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
    private void labelsForCurrency(final CurrencyType currencyType, final int amount, final List<Node> result) {
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
            result = imageView;
        } else {
            final Region coin = new Region();
            final PseudoClass pseudoClass = PseudoClass.getPseudoClass(currencyType.name().toLowerCase());
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

}
