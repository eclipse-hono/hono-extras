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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.api.handler.DeviceCommandHandler;
import org.eclipse.hono.communication.api.handler.DeviceConfigsHandler;
import org.eclipse.hono.communication.core.http.HttpEndpointHandler;


/**
 * Provides and Manages available HTTP vertx handlers.
 */
@ApplicationScoped
public class VertxHttpHandlerManagerService {
    /**
     * Available vertx endpoints handler services.
     */
    private final List<HttpEndpointHandler> availableHandlerServices;


    /**
     * Creates a new  VertxHttpHandlerManagerService with all dependencies.
     *
     * @param configHandler  The configuration handler
     * @param commandHandler The command handler
     */
    public VertxHttpHandlerManagerService(final DeviceConfigsHandler configHandler, final DeviceCommandHandler commandHandler) {
        this.availableHandlerServices = List.of(configHandler, commandHandler);
    }

    public List<HttpEndpointHandler> getAvailableHandlerServices() {
        return availableHandlerServices;
    }
}
