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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import java.util.List;

import org.eclipse.hono.communication.api.handler.DeviceCommandHandler;
import org.eclipse.hono.communication.api.service.DatabaseSchemaCreator;
import org.eclipse.hono.communication.api.service.DatabaseSchemaCreatorImpl;
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

import io.netty.handler.codec.http.HttpHeaderNames;
import io.quarkus.runtime.Quarkus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.validation.BadRequestException;


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
    private DatabaseSchemaCreator databaseSchemaCreatorMock;
    private Route routeMock;
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
        databaseSchemaCreatorMock = mock(DatabaseSchemaCreatorImpl.class);
        routeMock = mock(Route.class);
        deviceCommunicationHttpServer = new DeviceCommunicationHttpServer(appConfigsMock,
                vertxMock,
                handlerServiceMock,
                dbMock,
                databaseSchemaCreatorMock);

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
                serverConfigMock,
                databaseSchemaCreatorMock,
                routeMock);
    }


    @Test
    void startSucceeded() {
        final var mockedCommandService = mock(DeviceCommandHandler.class);
        Mockito.verifyNoMoreInteractions(mockedCommandService);
        doNothing().when(mockedCommandService).addRoutes(this.routerBuilderMock);
        try (MockedStatic<RouterBuilder> mockedRouterBuilderStatic = mockStatic(RouterBuilder.class)) {

            try (MockedStatic<Router> routerMockedStatic = mockStatic(Router.class)) {

                routerMockedStatic.when(() -> Router.router(any())).thenReturn(routerMock);
                mockedRouterBuilderStatic.when(() -> RouterBuilder.create(any(), any()))
                        .thenReturn(Future.succeededFuture(routerBuilderMock));
                mockedRouterBuilderStatic.verifyNoMoreInteractions();
                when(appConfigsMock.getServerConfig()).thenReturn(serverConfigMock);
                when(handlerServiceMock.getAvailableHandlerServices()).thenReturn(List.of(mockedCommandService));
                when(routerBuilderMock.createRouter()).thenReturn(routerMock);
                when(serverConfigMock.getLivenessPath()).thenReturn("/live");
                when(serverConfigMock.getReadinessPath()).thenReturn("/ready");
                when(routerMock.get(anyString())).thenReturn(routeMock);
                when(routeMock.handler(any())).thenReturn(routeMock);
                when(routerMock.errorHandler(anyInt(), any())).thenReturn(routerMock);
                when(vertxMock.createHttpServer(any(HttpServerOptions.class))).thenReturn(httpServerMock);
                when(httpServerMock.requestHandler(any())).thenReturn(httpServerMock);
                when(httpServerMock.listen()).thenReturn(Future.succeededFuture(httpServerMock));
                when(serverConfigMock.getOpenApiFilePath()).thenReturn("/myPath");
                when(serverConfigMock.getBasePath()).thenReturn("/basePath");
                when(routerMock.route(any())).thenReturn(routeMock);

                try (MockedStatic<Quarkus> quarkusMockedStatic = mockStatic(Quarkus.class)) {
                    final DeviceCommunicationHttpServer deviceCommunicationHttpServerSpy = spy(this.deviceCommunicationHttpServer);
                    deviceCommunicationHttpServerSpy.start();


                    verify(deviceCommunicationHttpServerSpy, times(1)).createRouterWithEndpoints(eq(routerBuilderMock), any());
                    verify(deviceCommunicationHttpServerSpy, times(1)).startVertxServer(any());
                    mockedRouterBuilderStatic.verify(() -> RouterBuilder.create(vertxMock, "/myPath"), times(1));
                    routerMockedStatic.verify(() -> Router.router(vertxMock), times(1));


                    verify(databaseSchemaCreatorMock, times(1)).createDBTables();
                    verify(handlerServiceMock, times(1)).getAvailableHandlerServices();
                    verify(routerBuilderMock, times(1)).createRouter();


                    verify(vertxMock, times(1)).createHttpServer(any(HttpServerOptions.class));
                    verify(httpServerMock, times(1)).requestHandler(any());
                    verify(httpServerMock, times(1)).listen();
                    verify(appConfigsMock, times(3)).getServerConfig();
                    verify(serverConfigMock, times(2)).getServerUrl();
                    verify(serverConfigMock, times(2)).getServerPort();
                    verify(serverConfigMock, times(1)).getLivenessPath();
                    verify(serverConfigMock, times(1)).getReadinessPath();
                    verify(serverConfigMock, times(1)).getBasePath();
                    verify(mockedCommandService, times(1)).addRoutes(routerBuilderMock);
                    verify(serverConfigMock, times(1)).getOpenApiFilePath();
                    verify(routerMock, times(2)).get(anyString());
                    verify(routeMock, times(2)).handler(any());
                    verify(routerMock, times(1)).route(anyString());
                    verify(routeMock, times(1)).subRouter(any());
                    quarkusMockedStatic.verify(Quarkus::waitForExit, times(1));
                    routerMockedStatic.verifyNoMoreInteractions();
                    mockedRouterBuilderStatic.verifyNoMoreInteractions();
                    quarkusMockedStatic.verifyNoMoreInteractions();

                }
            }


        }


    }

    @Test
    void createRouterFailed() {
        final var mockedCommandService = mock(DeviceCommandHandler.class);
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


                verify(dbMock, times(1)).close();
                verify(databaseSchemaCreatorMock, times(1)).createDBTables();
                verify(handlerServiceMock, times(1)).getAvailableHandlerServices();
                verify(appConfigsMock, times(1)).getServerConfig();
                verify(serverConfigMock, times(1)).getOpenApiFilePath();
                quarkusMockedStatic.verify(() -> Quarkus.asyncExit(-1), times(1));
                quarkusMockedStatic.verify(Quarkus::waitForExit, times(1));
                quarkusMockedStatic.verifyNoMoreInteractions();
            }

        }
    }


    @Test
    void createServerFailed() {
        final var mockedCommandService = mock(DeviceCommandHandler.class);
        Mockito.verifyNoMoreInteractions(mockedCommandService);
        doNothing().when(mockedCommandService).addRoutes(this.routerBuilderMock);
        try (MockedStatic<RouterBuilder> mockedRouterBuilderStatic = mockStatic(RouterBuilder.class)) {
            try (MockedStatic<Quarkus> quarkusMockedStatic = mockStatic(Quarkus.class)) {
                try (MockedStatic<Router> routerMockedStatic = mockStatic(Router.class)) {
                    mockedRouterBuilderStatic.when(() -> RouterBuilder.create(any(), any()))
                            .thenReturn(Future.succeededFuture(routerBuilderMock));

                    routerMockedStatic.when(() -> Router.router(any())).thenReturn(routerMock);

                    when(appConfigsMock.getServerConfig()).thenReturn(serverConfigMock);
                    when(handlerServiceMock.getAvailableHandlerServices()).thenReturn(List.of());
                    when(routerBuilderMock.createRouter()).thenReturn(routerMock);
                    when(routerMock.errorHandler(anyInt(), any())).thenReturn(routerMock);
                    when(serverConfigMock.getLivenessPath()).thenReturn("/live");
                    when(serverConfigMock.getReadinessPath()).thenReturn("/ready");
                    when(routerMock.get(anyString())).thenReturn(routeMock);
                    when(routeMock.handler(any())).thenReturn(routeMock);
                    when(vertxMock.createHttpServer(any(HttpServerOptions.class))).thenReturn(httpServerMock);
                    when(httpServerMock.requestHandler(routerMock)).thenReturn(httpServerMock);
                    when(httpServerMock.listen()).thenReturn(Future.failedFuture(new Throwable("Test error on listen()")));
                    when(serverConfigMock.getOpenApiFilePath()).thenReturn("/myPath");
                    when(serverConfigMock.getBasePath()).thenReturn("/basePath");
                    when(routerMock.route(any())).thenReturn(routeMock);
                    final DeviceCommunicationHttpServer deviceCommunicationHttpServerSpy = spy(this.deviceCommunicationHttpServer);
                    deviceCommunicationHttpServerSpy.start();


                    verify(deviceCommunicationHttpServerSpy, times(1)).createRouterWithEndpoints(eq(routerBuilderMock), any());
                    verify(deviceCommunicationHttpServerSpy, times(1)).startVertxServer(any());


                    mockedRouterBuilderStatic.verify(() -> RouterBuilder.create(vertxMock, "/myPath"), times(1));

                    verify(databaseSchemaCreatorMock, times(1)).createDBTables();
                    verify(handlerServiceMock, times(1)).getAvailableHandlerServices();
                    verify(routerBuilderMock, times(1)).createRouter();
                    verify(vertxMock, times(1)).createHttpServer(any(HttpServerOptions.class));
                    verify(httpServerMock, times(1)).requestHandler(routerMock);
                    verify(httpServerMock, times(1)).listen();
                    verify(appConfigsMock, times(3)).getServerConfig();
                    verify(serverConfigMock, times(1)).getOpenApiFilePath();
                    verify(serverConfigMock, times(1)).getServerPort();
                    verify(serverConfigMock, times(1)).getServerUrl();
                    verify(serverConfigMock, times(1)).getLivenessPath();
                    verify(serverConfigMock, times(1)).getReadinessPath();
                    verify(serverConfigMock, times(1)).getBasePath();
                    verify(routerMock, times(2)).get(anyString());
                    verify(routeMock, times(2)).handler(any());
                    verify(routeMock, times(1)).subRouter(any());
                    verify(routerMock, times(1)).route(anyString());
                    routerMockedStatic.verify(() -> Router.router(vertxMock), times(1));
                    quarkusMockedStatic.verify(Quarkus::waitForExit, times(1));
                    mockedRouterBuilderStatic.verifyNoMoreInteractions();
                    routerMockedStatic.verifyNoMoreInteractions();
                    quarkusMockedStatic.verifyNoMoreInteractions();
                }
            }

        }
    }


    @Test
    void addDefault400ExceptionHandler() {
        final var errorMsg = "This is an error message";
        final int code = 400;

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

        final var errorMsg = "This is an error message";
        final int code = 404;

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

        final var errorMsg = "This is an error message";
        final int code = 404;

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
