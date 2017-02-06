/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * IO utilities class.
 * @author Fabrice Bouyé
 */
public enum IOUtils {
    /**
     * The unique instance of this class.
     */
    INSTANCE;

    /**
     * Default IO buffer size.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Copy a stream into another.
     * @param input The source stream.
     * @param output The destination stream.
     * @throws NullPointerException If [@code input} or {@code output} is {@code null}.
     * @throws IOException In case of IO error.
     */
    public void copy(final InputStream input, final OutputStream output) throws NullPointerException, IOException {
        Objects.requireNonNull(input);
        Objects.requireNonNull(output);
        final byte[] buffer = new byte[BUFFER_SIZE];
        long total = 0;
        while (true) {
            final int read = input.read(buffer);
            if (read == -1) {
                break;
            }
            output.write(buffer, 0, read);
            total += read;
        }
    }
}
