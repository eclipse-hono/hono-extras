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

import org.eclipse.hono.communication.api.handler.DeviceCommandsHandler;
import org.eclipse.hono.communication.api.handler.DeviceConfigsHandler;
import org.eclipse.hono.communication.core.http.HttpEndpointHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Provides and Manages available HTTP vertx handlers
 */
@ApplicationScoped
public class VertxHttpHandlerManagerService {
    /**
     * Available vertx endpoints handler services
     */
    private final List<HttpEndpointHandler> availableHandlerServices;


    public VertxHttpHandlerManagerService(DeviceConfigsHandler configHandler, DeviceCommandsHandler commandHandler) {
        this.availableHandlerServices = List.of(configHandler, commandHandler);
    }

    public List<HttpEndpointHandler> getAvailableHandlerServices() {
        return availableHandlerServices;
    }
}
