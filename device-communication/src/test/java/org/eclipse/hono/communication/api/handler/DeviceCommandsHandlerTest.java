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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.hono.communication.api.config.DeviceCommandConstants;
import org.eclipse.hono.communication.api.config.DeviceConfigsConstants;
import org.eclipse.hono.communication.api.data.DeviceCommandRequest;
import org.eclipse.hono.communication.api.service.command.DeviceCommandService;
import org.eclipse.hono.communication.api.service.command.DeviceCommandServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.Operation;
import io.vertx.ext.web.openapi.RouterBuilder;

class DeviceCommandsHandlerTest {

    private final DeviceCommandService commandServiceMock;
    private final RouterBuilder routerBuilderMock;
    private final RoutingContext routingContextMock;
    private final Operation operationMock;
    private final DeviceCommandHandler deviceCommandsHandler;

    private final RequestBody requestBodyMock;
    private final HttpServerResponse responseMock;

    DeviceCommandsHandlerTest() {
        operationMock = mock(Operation.class);
        commandServiceMock = mock(DeviceCommandServiceImpl.class);
        routerBuilderMock = mock(RouterBuilder.class);
        routingContextMock = mock(RoutingContext.class);
        deviceCommandsHandler = new DeviceCommandHandler(commandServiceMock);
        requestBodyMock = mock(RequestBody.class);
        responseMock = mock(HttpServerResponse.class);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
                commandServiceMock,
                routerBuilderMock,
                routingContextMock,
                operationMock,
                requestBodyMock,
                responseMock);
    }

    @BeforeEach
    void setUp() {
        verifyNoMoreInteractions(
                commandServiceMock,
                routerBuilderMock,
                routingContextMock,
                operationMock);

    }

    @Test
    void addRoutes() {
        when(routerBuilderMock.operation(anyString())).thenReturn(operationMock);
        when(operationMock.handler(any())).thenReturn(operationMock);

        deviceCommandsHandler.addRoutes(routerBuilderMock);

        verify(routerBuilderMock, times(1)).operation(DeviceCommandConstants.POST_DEVICE_COMMAND_OP_ID);
        verify(operationMock, times(1)).handler(any());

    }

    @Test
    public void testHandlePostCommand() {
        // Arrange
        final JsonObject deviceConfigJson = new JsonObject()
                .put("param1", "value1")
                .put("param2", "value2");

        final String tenantId = "tenant1";
        final String deviceId = "device1";

        final DeviceCommandRequest deviceCommandRequest = new DeviceCommandRequest();
        deviceCommandRequest.setBinaryData("value1");

        // Set up mock behavior for RoutingContext
        when(routingContextMock.body()).thenReturn(requestBodyMock);
        when(requestBodyMock.asJsonObject()).thenReturn(new JsonObject("{}"));
        when(routingContextMock.pathParam(DeviceConfigsConstants.API_COMMON.TENANT_PATH_PARAMS)).thenReturn(tenantId);
        when(routingContextMock.pathParam(DeviceConfigsConstants.API_COMMON.DEVICE_PATH_PARAMS)).thenReturn(deviceId);
        when(routingContextMock.response()).thenReturn(responseMock);
        when(responseMock.setStatusCode(200)).thenReturn(responseMock);

        // Set up mock behavior for CommandService
        when(commandServiceMock.postCommand(any(), any(), any())).thenReturn(Future.succeededFuture());

        // Act
        deviceCommandsHandler.handlePostCommand(routingContextMock);

        // Assert
        verify(routingContextMock, times(1)).body();
        verify(requestBodyMock).asJsonObject();
        verify(routingContextMock).body();
        verify(routingContextMock).response();

        verify(commandServiceMock).postCommand(any(), anyString(), anyString());
        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.API_COMMON.TENANT_PATH_PARAMS);
        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.API_COMMON.DEVICE_PATH_PARAMS);


        verify(responseMock, times(1)).setStatusCode(200);
        verify(responseMock, times(1)).end();


    }

}
