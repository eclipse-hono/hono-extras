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
import static org.mockito.Mockito.*;

import org.eclipse.hono.communication.api.config.DeviceCommandConstants;
import org.eclipse.hono.communication.api.service.DeviceCommandService;
import org.eclipse.hono.communication.api.service.DeviceCommandServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.Operation;
import io.vertx.ext.web.openapi.RouterBuilder;


class DeviceCommandsHandlerTest {

    private final DeviceCommandService commandServiceMock;
    private final RouterBuilder routerBuilderMock;
    private final RoutingContext routingContextMock;
    private final Operation operationMock;
    private final DeviceCommandHandler deviceCommandsHandler;

    DeviceCommandsHandlerTest() {
        operationMock = mock(Operation.class);
        commandServiceMock = mock(DeviceCommandServiceImpl.class);
        routerBuilderMock = mock(RouterBuilder.class);
        routingContextMock = mock(RoutingContext.class);
        deviceCommandsHandler = new DeviceCommandHandler(commandServiceMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
                commandServiceMock,
                routerBuilderMock,
                routingContextMock,
                operationMock);
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
    void handlePostCommand() {
        doNothing().when(commandServiceMock).postCommand(routingContextMock);

        deviceCommandsHandler.handlePostCommand(routingContextMock);

        verify(commandServiceMock, times(1)).postCommand(routingContextMock);
    }

}
