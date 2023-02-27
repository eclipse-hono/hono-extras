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

package org.eclipse.hono.communication.api.exception;

import java.util.NoSuchElementException;

/**
 * Device Not found exception code: 404.
 */
public class
DeviceNotFoundException extends NoSuchElementException {

    /**
     * Creates a new  DeviceNotFoundException.
     */
    public DeviceNotFoundException() {
    }

    /**
     * Creates a new  DeviceNotFoundException.
     *
     * @param msg   String message
     * @param cause Throwable
     */
    public DeviceNotFoundException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * Creates a new  DeviceNotFoundException.
     *
     * @param cause Throwable
     */
    public DeviceNotFoundException(final Throwable cause) {
        super(cause);
    }


    /**
     * Creates a new  DeviceNotFoundException.
     *
     * @param msg String message
     */
    public DeviceNotFoundException(final String msg) {
        super(msg);
    }
}
