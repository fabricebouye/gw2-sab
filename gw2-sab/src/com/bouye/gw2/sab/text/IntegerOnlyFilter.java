/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.text;

import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter;

/**
 * Only accept integer input in text fields.
 * @author Fabrice
 */
public final class IntegerOnlyFilter implements UnaryOperator<TextFormatter.Change> {

    private static final String INTEGER_PATTERN = "-?[0-9]*"; // NOI18N.

    /**
     * The min legal value.
     */
    final int min;
    /**
     * The max legal value.
     */
    final int max;

    final boolean allowEmpty;

    /**
     * Creates a new instance.
     */
    public IntegerOnlyFilter() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    /**
     * Creates a new instance.
     * @param min The min legal value.
     * @param max The max legal value.
     */
    public IntegerOnlyFilter(final int min, final int max) {
        this(min, max, true);
    }

    /**
     * Creates a new instance.
     * @param min The min legal value.
     * @param max The max legal value.
     */
    public IntegerOnlyFilter(final int min, final int max, final boolean allowEmpty) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
        this.allowEmpty = allowEmpty;
    }

    @Override
    public TextFormatter.Change apply(final TextFormatter.Change change) {
        TextFormatter.Change result = null;
        final String nextText = change.getControlNewText().trim();
        System.out.printf("Next text: \"%s\"%n", nextText);
        if ((allowEmpty && nextText.isEmpty()) || (min < 0 && nextText.equals("-"))) {
            result = change;
        } else if (nextText.matches(INTEGER_PATTERN)) {
            try {
                final int value = Integer.parseInt(nextText);
                if (min <= value && value <= max) {
                    result = change;
                }
            } catch (NumberFormatException ex) {
                // Silentely ignore exception.
            }
        }
        return result;
    }
}
