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

package org.eclipse.hono.communication.api.service;

import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;


/**
 * Abstract device service class.
 */
public abstract class DeviceServiceAbstract {

    protected final ObjectWriter ow = new ObjectMapper().writer();
    protected final ObjectReader or = new ObjectMapper().reader();

    protected final InternalMessagingConfig messagingConfig;
    protected final InternalMessaging internalMessaging;

    /**
     * Creates a new DeviceServiceAbstract.
     *
     * @param messagingConfig   The internal messaging configs
     * @param internalMessaging The internal messaging interface
     */
    protected DeviceServiceAbstract(final InternalMessagingConfig messagingConfig,
                                    final InternalMessaging internalMessaging) {

        this.messagingConfig = messagingConfig;
        this.internalMessaging = internalMessaging;
    }
}
