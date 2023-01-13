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

package org.eclipse.hono.communication.api;


import io.netty.handler.codec.http.HttpHeaderNames;
import io.quarkus.runtime.Quarkus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.validation.BadRequestException;
import org.eclipse.hono.communication.api.handler.DeviceCommandsHandler;
import org.eclipse.hono.communication.api.service.DatabaseService;
import org.eclipse.hono.communication.api.service.DatabaseServiceImpl;
import org.eclipse.hono.communication.api.service.VertxHttpHandlerManagerService;
import org.eclipse.hono.communication.core.app.ApplicationConfig;
import org.eclipse.hono.communication.core.app.ServerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeviceCommunicationHttpServerTest {


    private ApplicationConfig appConfigsMock;
    private VertxHttpHandlerManagerService handlerServiceMock;
    private Vertx vertxMock;
    private Router routerMock;
    private RouterBuilder routerBuilderMock;
    private HttpServer httpServerMock;
    private DeviceCommunicationHttpServer deviceCommunicationHttpServer;
    private RoutingContext routingContextMock;
    private HttpServerResponse httpServerResponseMock;
    private HttpServerRequest httpServerRequestMock;
    private BadRequestException badRequestExceptionMock;
    private JsonObject jsonObjMock;

    private DatabaseService dbMock;
    private ServerConfig serverConfigMock;

    @BeforeEach
    void setUp() {
        handlerServiceMock = mock(VertxHttpHandlerManagerService.class);
        vertxMock = mock(Vertx.class);
        routerMock = mock(Router.class);
        routerBuilderMock = mock(RouterBuilder.class);
        appConfigsMock = mock(ApplicationConfig.class);
        serverConfigMock = mock(ServerConfig.class);
        httpServerMock = mock(HttpServer.class);
        routingContextMock = mock(RoutingContext.class);
        httpServerResponseMock = mock(HttpServerResponse.class);
        httpServerRequestMock = mock(HttpServerRequest.class);
        badRequestExceptionMock = mock(BadRequestException.class);
        jsonObjMock = mock(JsonObject.class);
        dbMock = mock(DatabaseServiceImpl.class);
        deviceCommunicationHttpServer = new DeviceCommunicationHttpServer(appConfigsMock, vertxMock, handlerServiceMock, dbMock);

    }


    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(handlerServiceMock,
                vertxMock,
                routerMock,
                routerBuilderMock,
                appConfigsMock,
                httpServerMock,
                routingContextMock,
                httpServerResponseMock,
                httpServerRequestMock,
                badRequestExceptionMock,
                jsonObjMock,
                dbMock,
                serverConfigMock);
    }


    @Test
    void startSucceeded() {
        var mockedCommandService = mock(DeviceCommandsHandler.class);
        Mockito.verifyNoMoreInteractions(mockedCommandService);
        doNothing().when(mockedCommandService).addRoutes(this.routerBuilderMock);
        try (MockedStatic<RouterBuilder> mockedRouterBuilderStatic = mockStatic(RouterBuilder.class)) {
            mockedRouterBuilderStatic.when(() -> RouterBuilder.create(any(), any()))
                    .thenReturn(Future.succeededFuture(routerBuilderMock));
            mockedRouterBuilderStatic.verifyNoMoreInteractions();
            when(appConfigsMock.getServerConfig()).thenReturn(serverConfigMock);
            when(handlerServiceMock.getAvailableHandlerServices()).thenReturn(List.of(mockedCommandService));
            when(routerBuilderMock.createRouter()).thenReturn(routerMock);
            when(routerMock.errorHandler(anyInt(), any())).thenReturn(routerMock);
            when(vertxMock.createHttpServer(any(HttpServerOptions.class))).thenReturn(httpServerMock);
            when(httpServerMock.requestHandler(routerMock)).thenReturn(httpServerMock);
            when(httpServerMock.listen()).thenReturn(Future.succeededFuture(httpServerMock));
            when(serverConfigMock.getOpenApiFilePath()).thenReturn("/myPath");

            try (MockedStatic<Quarkus> quarkusMockedStatic = mockStatic(Quarkus.class)) {

                this.deviceCommunicationHttpServer.start();

                mockedRouterBuilderStatic.verify(() -> RouterBuilder.create(vertxMock, "/myPath"), times(1));

                verify(handlerServiceMock, times(1)).getAvailableHandlerServices();
                verify(routerBuilderMock, times(1)).createRouter();

                verify(vertxMock, times(1)).createHttpServer(any(HttpServerOptions.class));
                verify(httpServerMock, times(1)).requestHandler(routerMock);
                verify(httpServerMock, times(1)).listen();
                verify(appConfigsMock, times(2)).getServerConfig();
                verify(serverConfigMock, times(2)).getServerUrl();
                verify(serverConfigMock, times(2)).getServerPort();
                verify(mockedCommandService, times(1)).addRoutes(routerBuilderMock);
                verify(serverConfigMock, times(1)).getOpenApiFilePath();
                quarkusMockedStatic.verify(Quarkus::waitForExit, times(1));
                quarkusMockedStatic.verifyNoMoreInteractions();

            }

        }


    }

    @Test
    void createRouterFailed() {
        var mockedCommandService = mock(DeviceCommandsHandler.class);
        Mockito.verifyNoMoreInteractions(mockedCommandService);
        doNothing().when(mockedCommandService).addRoutes(this.routerBuilderMock);
        try (MockedStatic<RouterBuilder> mockedRouterBuilderStatic = mockStatic(RouterBuilder.class)) {
            mockedRouterBuilderStatic.when(() -> RouterBuilder.create(any(), any()))
                    .thenReturn(Future.failedFuture(new RuntimeException()));
            when(appConfigsMock.getServerConfig()).thenReturn(serverConfigMock);
            mockedRouterBuilderStatic.verifyNoMoreInteractions();
            when(serverConfigMock.getOpenApiFilePath()).thenReturn("/myPath");

            try (MockedStatic<Quarkus> quarkusMockedStatic = mockStatic(Quarkus.class)) {
                this.deviceCommunicationHttpServer.start();

                verify(handlerServiceMock, times(1)).getAvailableHandlerServices();
                verify(appConfigsMock, times(1)).getServerConfig();
                verify(serverConfigMock, times(1)).getOpenApiFilePath();
                quarkusMockedStatic.verify(Quarkus::waitForExit, times(1));
                quarkusMockedStatic.verifyNoMoreInteractions();
            }

        }
    }


    @Test
    void createServerFailed() {
        var mockedCommandService = mock(DeviceCommandsHandler.class);
        Mockito.verifyNoMoreInteractions(mockedCommandService);
        doNothing().when(mockedCommandService).addRoutes(this.routerBuilderMock);
        try (MockedStatic<RouterBuilder> mockedRouterBuilderStatic = mockStatic(RouterBuilder.class)) {
            mockedRouterBuilderStatic.when(() -> RouterBuilder.create(any(), any()))
                    .thenReturn(Future.succeededFuture(routerBuilderMock));
            mockedRouterBuilderStatic.verifyNoMoreInteractions();
            when(appConfigsMock.getServerConfig()).thenReturn(serverConfigMock);
            when(handlerServiceMock.getAvailableHandlerServices()).thenReturn(List.of());
            when(routerBuilderMock.createRouter()).thenReturn(routerMock);
            when(routerMock.errorHandler(anyInt(), any())).thenReturn(routerMock);
            when(vertxMock.createHttpServer(any(HttpServerOptions.class))).thenReturn(httpServerMock);
            when(httpServerMock.requestHandler(routerMock)).thenReturn(httpServerMock);
            when(httpServerMock.listen()).thenReturn(Future.failedFuture(new Throwable()));
            when(serverConfigMock.getOpenApiFilePath()).thenReturn("/myPath");

            try (MockedStatic<Quarkus> quarkusMockedStatic = mockStatic(Quarkus.class)) {

                this.deviceCommunicationHttpServer.start();

                mockedRouterBuilderStatic.verify(() -> RouterBuilder.create(vertxMock, "/myPath"), times(1));

                verify(handlerServiceMock, times(1)).getAvailableHandlerServices();
                verify(routerBuilderMock, times(1)).createRouter();
                verify(vertxMock, times(1)).createHttpServer(any(HttpServerOptions.class));
                verify(httpServerMock, times(1)).requestHandler(routerMock);
                verify(httpServerMock, times(1)).listen();
                verify(appConfigsMock, times(2)).getServerConfig();
                verify(serverConfigMock, times(1)).getOpenApiFilePath();
                verify(serverConfigMock, times(1)).getServerPort();
                verify(serverConfigMock, times(1)).getServerUrl();
                quarkusMockedStatic.verify(Quarkus::waitForExit, times(1));
                quarkusMockedStatic.verifyNoMoreInteractions();
            }

        }
    }


    @Test
    void addDefault400ExceptionHandler() {
        var errorMsg = "This is an error message";
        int code = 400;

        when(routingContextMock.failure()).thenReturn(badRequestExceptionMock);
        when(badRequestExceptionMock.toJson()).thenReturn(jsonObjMock);
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.setStatusCode(code)).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.end()).thenReturn(Future.succeededFuture());
        when(jsonObjMock.toString()).thenReturn(errorMsg);

        deviceCommunicationHttpServer.addDefault400ExceptionHandler(routingContextMock);

        verify(routingContextMock, times(1)).response();
        verify(routingContextMock, times(1)).failure();
        verify(badRequestExceptionMock, times(1)).toJson();
        verify(httpServerResponseMock, times(1)).setStatusCode(code);
        verify(httpServerResponseMock, times(1)).end(errorMsg);

    }

    @Test
    void addDefault404ExceptionHandlerPutsHeader() {

        var errorMsg = "This is an error message";
        int code = 404;

        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.putHeader(eq(HttpHeaderNames.CONTENT_TYPE),
                anyString())).thenReturn(httpServerResponseMock);
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(routingContextMock.request()).thenReturn(httpServerRequestMock);
        when(httpServerResponseMock.ended()).thenReturn(Boolean.FALSE);
        when(routingContextMock.failure()).thenReturn(badRequestExceptionMock);
        when(badRequestExceptionMock.toJson()).thenReturn(jsonObjMock);
        when(httpServerRequestMock.method()).thenReturn(HttpMethod.GET);
        when(httpServerResponseMock.setStatusCode(code)).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.end()).thenReturn(Future.succeededFuture());
        when(jsonObjMock.toString()).thenReturn(errorMsg);

        deviceCommunicationHttpServer.addDefault404ExceptionHandler(routingContextMock);

        verify(routingContextMock, times(3)).response();
        verify(routingContextMock, times(1)).request();
        verify(httpServerResponseMock, times(1)).setStatusCode(code);
        verify(httpServerResponseMock, times(1)).putHeader(eq(HttpHeaderNames.CONTENT_TYPE),
                anyString());
        verify(httpServerResponseMock, times(1)).end(anyString());
        verify(httpServerResponseMock, times(1)).ended();
        verify(httpServerRequestMock, times(1)).method();

    }

    @Test
    void addDefault404ExceptionHandlerResponseEnded() {

        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.ended()).thenReturn(Boolean.TRUE);

        deviceCommunicationHttpServer.addDefault404ExceptionHandler(routingContextMock);

        verify(routingContextMock, times(1)).response();
        verify(httpServerResponseMock, times(1)).ended();

    }

    @Test
    void addDefault404ExceptionHandlerMethodEqualsHead() {

        var errorMsg = "This is an error message";
        int code = 404;

        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.ended()).thenReturn(Boolean.FALSE);
        when(routingContextMock.failure()).thenReturn(badRequestExceptionMock);
        when(badRequestExceptionMock.toJson()).thenReturn(jsonObjMock);

        when(routingContextMock.request()).thenReturn(httpServerRequestMock);
        when(httpServerRequestMock.method()).thenReturn(HttpMethod.HEAD);
        when(httpServerResponseMock.setStatusCode(code)).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.end()).thenReturn(Future.succeededFuture());
        when(jsonObjMock.toString()).thenReturn(errorMsg);

        deviceCommunicationHttpServer.addDefault404ExceptionHandler(routingContextMock);

        verify(routingContextMock, times(3)).response();
        verify(routingContextMock, times(1)).request();
        verify(httpServerResponseMock, times(1)).setStatusCode(code);

        verify(httpServerResponseMock, times(1)).end();
        verify(httpServerResponseMock, times(1)).ended();
        verify(httpServerRequestMock, times(1)).method();
    }

    @Test
    void stop() {
        doNothing().when(dbMock).close();
        deviceCommunicationHttpServer.stop();
        verify(dbMock).close();
    }
}