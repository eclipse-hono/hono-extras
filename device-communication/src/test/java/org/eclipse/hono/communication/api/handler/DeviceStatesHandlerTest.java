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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.hono.communication.api.config.DeviceStatesConstants;
import org.eclipse.hono.communication.api.data.DeviceState;
import org.eclipse.hono.communication.api.data.ListDeviceStatesResponse;
import org.eclipse.hono.communication.api.service.state.DeviceStateService;
import org.eclipse.hono.communication.api.service.state.DeviceStateServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.Operation;
import io.vertx.ext.web.openapi.RouterBuilder;

class DeviceStatesHandlerTest {

    private final RouterBuilder routerBuilderMock;
    private final RoutingContext routingContextMock;
    private final Operation operationMock;
    private final HttpServerResponse httpServerResponseMock;
    private final IllegalArgumentException illegalArgumentExceptionMock;
    private final DeviceStateService stateServiceMock;
    private final DeviceStatesHandler deviceStatesHandler;
    private final String tenantID = "tenant_ID";
    private final String deviceID = "device_ID";
    private final String errorMsg = "test_error";
    private final DeviceState deviceState = new DeviceState();

    DeviceStatesHandlerTest() {
        routerBuilderMock = mock(RouterBuilder.class);
        routingContextMock = mock(RoutingContext.class);
        operationMock = mock(Operation.class);
        httpServerResponseMock = mock(HttpServerResponse.class);
        illegalArgumentExceptionMock = mock(IllegalArgumentException.class);
        stateServiceMock = mock(DeviceStateServiceImpl.class);
        deviceStatesHandler = new DeviceStatesHandler(stateServiceMock);
    }

    @Test
    void testAddRoutes() {
        when(routerBuilderMock.operation(anyString())).thenReturn(operationMock);
        when(operationMock.handler(any())).thenReturn(operationMock);

        deviceStatesHandler.addRoutes(routerBuilderMock);

        verify(routerBuilderMock, times(1)).operation(DeviceStatesConstants.LIST_STATES_OP_ID);
        verify(operationMock, times(1)).handler(any());
    }

    @Test
    void testHandleListStates_success() {
        final var listDeviceStatesResponse = new ListDeviceStatesResponse(List.of(deviceState));
        final MultiMap queryParams = MultiMap.caseInsensitiveMultiMap()
                .add(DeviceStatesConstants.NUM_STATES_QUERY_PARAMS, String.valueOf(10));
        when(routingContextMock.queryParams()).thenReturn(queryParams);
        when(routingContextMock.pathParam(DeviceStatesConstants.API_COMMON.TENANT_PATH_PARAMS)).thenReturn(tenantID);
        when(routingContextMock.pathParam(DeviceStatesConstants.API_COMMON.DEVICE_PATH_PARAMS)).thenReturn(deviceID);
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.setStatusCode(anyInt())).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.putHeader("Content-Type",
                "application/json")).thenReturn(httpServerResponseMock);
        when(stateServiceMock.listAll(deviceID, tenantID, 10))
                .thenReturn(Future.succeededFuture(listDeviceStatesResponse));

        final var results = deviceStatesHandler.handleListStates(routingContextMock);

        verify(stateServiceMock, times(1)).listAll(deviceID, tenantID, 10);
        verify(routingContextMock, times(1)).queryParams();
        verify(routingContextMock, times(1)).pathParam(DeviceStatesConstants.API_COMMON.TENANT_PATH_PARAMS);
        verify(routingContextMock, times(1)).pathParam(DeviceStatesConstants.API_COMMON.DEVICE_PATH_PARAMS);

        verifySuccessResponse(results, listDeviceStatesResponse);
    }

    @Test
    void testHandleListStates_failed() {
        final MultiMap queryParams = MultiMap.caseInsensitiveMultiMap()
                .add(DeviceStatesConstants.NUM_STATES_QUERY_PARAMS, String.valueOf(10));
        when(routingContextMock.queryParams()).thenReturn(queryParams);
        when(routingContextMock.pathParam(DeviceStatesConstants.API_COMMON.TENANT_PATH_PARAMS)).thenReturn(tenantID);
        when(routingContextMock.pathParam(DeviceStatesConstants.API_COMMON.DEVICE_PATH_PARAMS)).thenReturn(deviceID);
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.setStatusCode(anyInt())).thenReturn(httpServerResponseMock);
        when(illegalArgumentExceptionMock.getMessage()).thenReturn(errorMsg);
        when(httpServerResponseMock.putHeader("Content-Type",
                "application/json")).thenReturn(httpServerResponseMock);
        when(stateServiceMock.listAll(deviceID, tenantID, 10))
                .thenReturn(Future.failedFuture(illegalArgumentExceptionMock));

        final var results = deviceStatesHandler.handleListStates(routingContextMock);

        verify(stateServiceMock, times(1)).listAll(deviceID, tenantID, 10);
        verify(routingContextMock, times(1)).queryParams();
        verify(routingContextMock, times(1)).pathParam(DeviceStatesConstants.API_COMMON.TENANT_PATH_PARAMS);

        verify(routingContextMock, times(1)).pathParam(DeviceStatesConstants.API_COMMON.DEVICE_PATH_PARAMS);
        verifyErrorResponse(results);
    }

    void verifyErrorResponse(final Future results) {
        verify(routingContextMock, times(1)).response();
        verify(httpServerResponseMock).setStatusCode(400);
        verify(illegalArgumentExceptionMock).getMessage();
        verify(httpServerResponseMock).putHeader("Content-Type",
                "application/json");
        verify(httpServerResponseMock).end(new JsonObject().put("error", errorMsg).encodePrettily());
        Assertions.assertTrue(results.failed());
    }

    void verifySuccessResponse(final Future results, final Object responseObj) {
        verify(routingContextMock, times(1)).response();
        verify(httpServerResponseMock).setStatusCode(200);
        verify(httpServerResponseMock).putHeader("Content-Type",
                "application/json");
        verify(httpServerResponseMock).end(Json.encodePrettily(responseObj));
        Assertions.assertTrue(results.succeeded());
    }

}
