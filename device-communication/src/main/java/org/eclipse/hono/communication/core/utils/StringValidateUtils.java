/*
 * ***********************************************************
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  <p>
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *  <p>
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *  <p>
 *  SPDX-License-Identifier: EPL-2.0
 * **********************************************************
 *
 */

package org.eclipse.hono.communication.core.utils;

import java.util.Base64;

/**
 * String validation utils.
 */
public abstract class StringValidateUtils {

    private StringValidateUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if a given string is base64 encoded.
     *
     * @param stringBase64 String to validate
     * @return True if string is base64 else False
     */
    public static boolean isBase64(final String stringBase64) {
        try {
            Base64.getDecoder().decode(stringBase64);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
