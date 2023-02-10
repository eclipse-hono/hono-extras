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

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.Operation;
import io.vertx.ext.web.openapi.RouterBuilder;
import org.eclipse.hono.communication.api.config.DeviceConfigsConstants;
import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.DeviceConfigResponse;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.service.DeviceConfigService;
import org.eclipse.hono.communication.api.service.DeviceConfigServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DeviceConfigsHandlerTest {

    private final DeviceConfigService configServiceMock;
    private final RouterBuilder routerBuilderMock;
    private final RoutingContext routingContextMock;
    private final Operation operationMock;
    private final RequestBody requestBodyMock;
    private final DeviceConfigsHandler deviceConfigsHandler;
    private final JsonObject jsonObjMock;
    private final HttpServerResponse httpServerResponseMock;
    private final IllegalArgumentException illegalArgumentExceptionMock;

    private final String tenantID = "tenant_ID";
    private final String deviceID = "device_ID";
    private final String errorMsg = "test_error";
    DeviceConfigRequest deviceConfigRequest = new DeviceConfigRequest("1", "binary_data");
    DeviceConfigResponse deviceConfigEntity = new DeviceConfigResponse();
    DeviceConfig deviceConfig = new DeviceConfig();

    public DeviceConfigsHandlerTest() {
        operationMock = mock(Operation.class);
        configServiceMock = mock(DeviceConfigServiceImpl.class);
        routerBuilderMock = mock(RouterBuilder.class);
        routingContextMock = mock(RoutingContext.class);
        requestBodyMock = mock(RequestBody.class);
        jsonObjMock = mock(JsonObject.class);
        httpServerResponseMock = mock(HttpServerResponse.class);
        illegalArgumentExceptionMock = mock(IllegalArgumentException.class);
        deviceConfigsHandler = new DeviceConfigsHandler(configServiceMock);

        deviceConfigEntity.setVersion(1);


        deviceConfig.setVersion("");

    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
                configServiceMock,
                routerBuilderMock,
                routingContextMock,
                operationMock,
                requestBodyMock,
                jsonObjMock,
                httpServerResponseMock,
                illegalArgumentExceptionMock);
    }

    @Test
    void addRoutes() {
        when(routerBuilderMock.operation(anyString())).thenReturn(operationMock);
        when(operationMock.handler(any())).thenReturn(operationMock);

        deviceConfigsHandler.addRoutes(routerBuilderMock);

        verify(routerBuilderMock, times(1)).operation(DeviceConfigsConstants.LIST_CONFIG_VERSIONS_OP_ID);
        verify(routerBuilderMock, times(1)).operation(DeviceConfigsConstants.POST_MODIFY_DEVICE_CONFIG_OP_ID);
        verify(operationMock, times(2)).handler(any());
    }

    @Test
    void handleModifyCloudToDeviceConfig_success() {
        when(routingContextMock.pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMETER)).thenReturn(tenantID);
        when(routingContextMock.pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMETER)).thenReturn(deviceID);
        when(routingContextMock.body()).thenReturn(requestBodyMock);
        when(requestBodyMock.asJsonObject()).thenReturn(jsonObjMock);
        when(jsonObjMock.mapTo(DeviceConfigRequest.class)).thenReturn(deviceConfigRequest);
        when(configServiceMock.modifyCloudToDeviceConfig(deviceConfigRequest, deviceID, tenantID)).thenReturn(Future.succeededFuture(deviceConfigEntity));
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.setStatusCode(anyInt())).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.putHeader("Content-Type",
                "application/json")).thenReturn(httpServerResponseMock);

        var results = deviceConfigsHandler.handleModifyCloudToDeviceConfig(routingContextMock);

        verify(configServiceMock).modifyCloudToDeviceConfig(deviceConfigRequest, deviceID, tenantID);
        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMETER);
        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMETER);
        verify(routingContextMock, times(1)).body();
        verify(requestBodyMock, times(1)).asJsonObject();
        verify(jsonObjMock, times(1)).mapTo(DeviceConfigRequest.class);
        verifySuccessResponse(results, deviceConfigEntity);

    }


    @Test
    void handleModifyCloudToDeviceConfig_failure() {
        when(routingContextMock.pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMETER)).thenReturn(tenantID);
        when(routingContextMock.pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMETER)).thenReturn(deviceID);
        when(routingContextMock.body()).thenReturn(requestBodyMock);
        when(requestBodyMock.asJsonObject()).thenReturn(jsonObjMock);
        when(jsonObjMock.mapTo(DeviceConfigRequest.class)).thenReturn(deviceConfigRequest);
        when(illegalArgumentExceptionMock.getMessage()).thenReturn(errorMsg);
        when(configServiceMock.modifyCloudToDeviceConfig(deviceConfigRequest, deviceID, tenantID)).thenReturn(Future.failedFuture(illegalArgumentExceptionMock));
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.setStatusCode(anyInt())).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.putHeader("Content-Type",
                "application/json")).thenReturn(httpServerResponseMock);

        var results = deviceConfigsHandler.handleModifyCloudToDeviceConfig(routingContextMock);

        verify(configServiceMock).modifyCloudToDeviceConfig(deviceConfigRequest, deviceID, tenantID);
        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMETER);

        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMETER);
        verify(routingContextMock, times(1)).body();
        verify(requestBodyMock, times(1)).asJsonObject();
        verify(jsonObjMock, times(1)).mapTo(DeviceConfigRequest.class);

        verifyErrorResponse(results);


    }

    @Test
    void handleListConfigVersions_success() {
        ListDeviceConfigVersionsResponse listDeviceConfigVersionsResponse = new ListDeviceConfigVersionsResponse(List.of(deviceConfig));
        MultiMap queryParams = MultiMap.caseInsensitiveMultiMap().add(DeviceConfigsConstants.NUM_VERSION_QUERY_PARAMS, String.valueOf(10));
        when(routingContextMock.queryParams()).thenReturn(queryParams);
        when(routingContextMock.pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMETER)).thenReturn(tenantID);
        when(routingContextMock.pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMETER)).thenReturn(deviceID);
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.setStatusCode(anyInt())).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.putHeader("Content-Type",
                "application/json")).thenReturn(httpServerResponseMock);
        when(configServiceMock.listAll(deviceID, tenantID, 10)).thenReturn(Future.succeededFuture(listDeviceConfigVersionsResponse));

        var results = deviceConfigsHandler.handleListConfigVersions(routingContextMock);

        verify(configServiceMock, times(1)).listAll(deviceID, tenantID, 10);
        verify(routingContextMock, times(1)).queryParams();
        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMETER);
        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMETER);

        verifySuccessResponse(results, listDeviceConfigVersionsResponse);

    }


    @Test
    void handleListConfigVersions_failed() {
        MultiMap queryParams = MultiMap.caseInsensitiveMultiMap().add(DeviceConfigsConstants.NUM_VERSION_QUERY_PARAMS, String.valueOf(10));
        when(routingContextMock.queryParams()).thenReturn(queryParams);
        when(routingContextMock.pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMETER)).thenReturn(tenantID);
        when(routingContextMock.pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMETER)).thenReturn(deviceID);
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.setStatusCode(anyInt())).thenReturn(httpServerResponseMock);
        when(illegalArgumentExceptionMock.getMessage()).thenReturn(errorMsg);
        when(httpServerResponseMock.putHeader("Content-Type",
                "application/json")).thenReturn(httpServerResponseMock);
        when(configServiceMock.listAll(deviceID, tenantID, 10)).thenReturn(Future.failedFuture(illegalArgumentExceptionMock));

        var results = deviceConfigsHandler.handleListConfigVersions(routingContextMock);

        verify(configServiceMock, times(1)).listAll(deviceID, tenantID, 10);
        verify(routingContextMock, times(1)).queryParams();
        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMETER);

        verify(routingContextMock, times(1)).pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMETER);
        verifyErrorResponse(results);


    }


    void verifyErrorResponse(Future results) {
        verify(routingContextMock, times(1)).response();
        verify(httpServerResponseMock).setStatusCode(400);
        verify(illegalArgumentExceptionMock).getMessage();
        verify(httpServerResponseMock).putHeader("Content-Type",
                "application/json");
        verify(httpServerResponseMock).end(new JsonObject().put("error", errorMsg).encodePrettily());
        Assertions.assertTrue(results.failed());
    }

    void verifySuccessResponse(Future results, Object responseObj) {
        verify(routingContextMock, times(1)).response();
        verify(httpServerResponseMock).setStatusCode(200);
        verify(httpServerResponseMock).putHeader("Content-Type",
                "application/json");
        verify(httpServerResponseMock).end(Json.encodePrettily(responseObj));
        Assertions.assertTrue(results.succeeded());
    }
}