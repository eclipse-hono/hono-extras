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

package org.eclipse.hono.communication.api.handler;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.api.service.state.DeviceStateService;
import org.eclipse.hono.communication.core.http.HttpEndpointHandler;

import io.vertx.ext.web.openapi.RouterBuilder;

/**
 * Handler for device state endpoints.
 */
@ApplicationScoped
public class DeviceStatesHandler implements HttpEndpointHandler {

    private final DeviceStateService stateService;

    /**
     * Creates a new DeviceStatesHandler.
     *
     * @param stateService The device states service.
     */
    public DeviceStatesHandler(final DeviceStateService stateService) {
        this.stateService = stateService;
    }

    @Override
    public void addRoutes(final RouterBuilder router) {

    }
}
