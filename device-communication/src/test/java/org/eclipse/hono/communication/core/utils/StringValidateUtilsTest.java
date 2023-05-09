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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringValidateUtilsTest {

    @Test
    public void isBase64_valid_true() {
        final String string = "This is a test.";
        final String base64 = Base64.getEncoder().encodeToString(string.getBytes());
        Assertions.assertTrue(StringValidateUtils.isBase64(base64));
    }

    @Test
    public void isBase64_invalid_false() {
        final String string = "This is a test.";
        Assertions.assertFalse(StringValidateUtils.isBase64(string));
    }

}
