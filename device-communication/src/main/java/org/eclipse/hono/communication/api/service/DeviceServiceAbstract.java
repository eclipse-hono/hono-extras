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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;

/**
 * Abstract device service class
 */
public abstract class DeviceServiceAbstract {

    protected final static ObjectWriter ow = new ObjectMapper().writer();
    protected final static ObjectReader or = new ObjectMapper().reader();

    protected final InternalMessagingConfig messagingConfig;
    protected final InternalMessaging internalMessaging;

    protected DeviceServiceAbstract(InternalMessagingConfig messagingConfig,
                                    InternalMessaging internalMessaging) {

        this.messagingConfig = messagingConfig;
        this.internalMessaging = internalMessaging;
    }
}
