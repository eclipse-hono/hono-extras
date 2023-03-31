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

import org.eclipse.hono.communication.api.config.DeviceStatesConstants;
import org.eclipse.hono.communication.api.data.ListDeviceStatesResponse;
import org.eclipse.hono.communication.api.service.state.DeviceStateService;
import org.eclipse.hono.communication.core.http.HttpEndpointHandler;
import org.eclipse.hono.communication.core.utils.ResponseUtils;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
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
    public void addRoutes(final RouterBuilder routerBuilder) {
        routerBuilder.operation(DeviceStatesConstants.LIST_STATES_OP_ID).handler(this::handleListStates);
    }

    /**
     * Handles get device states.
     *
     * @param routingContext The RoutingContext
     * @return Future of ListDeviceStatesResponse
     */
    public Future<ListDeviceStatesResponse> handleListStates(final RoutingContext routingContext) {
        final var numStates = routingContext.queryParams().get(DeviceStatesConstants.NUM_STATES_QUERY_PARAMS);

        final var limit = numStates == null ? 0 : Integer.parseInt(numStates);
        final var tenantId = routingContext.pathParam(DeviceStatesConstants.API_COMMON.TENANT_PATH_PARAMS);
        final var deviceId = routingContext.pathParam(DeviceStatesConstants.API_COMMON.DEVICE_PATH_PARAMS);

        return stateService.listAll(deviceId, tenantId, limit)
                .onSuccess(result -> ResponseUtils.successResponse(routingContext, result))
                .onFailure(err -> ResponseUtils.errorResponse(routingContext, err));
    }
}
