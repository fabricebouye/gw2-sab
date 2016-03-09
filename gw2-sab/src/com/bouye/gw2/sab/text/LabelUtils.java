/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *  Utilities class used for label and rich text management.
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
                final String xmlString = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><%s>%s</%s>", CONTENT, string, CONTENT); // NOI18N.
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
                                case CONTENT: // NOI18N.
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
                                case CONTENT: // NOI18N.
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

    private Map<String, Boolean> initializeAttributeMap() {
        final Map<String, Boolean> result = new HashMap();
        result.put(BOLD, Boolean.FALSE);
        result.put(FONT_AWESOME, Boolean.FALSE);
        result.put(LINK, Boolean.FALSE);
        return result;
    }
}
