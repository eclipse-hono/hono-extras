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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Singleton;

import org.eclipse.hono.communication.api.service.DatabaseSchemaCreator;
import org.eclipse.hono.communication.api.service.DatabaseService;
import org.eclipse.hono.communication.api.service.VertxHttpHandlerManagerService;
import org.eclipse.hono.communication.core.app.ApplicationConfig;
import org.eclipse.hono.communication.core.app.ServerConfig;
import org.eclipse.hono.communication.core.http.AbstractVertxHttpServer;
import org.eclipse.hono.communication.core.http.HttpEndpointHandler;
import org.eclipse.hono.communication.core.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.quarkus.runtime.Quarkus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.validation.BadRequestException;


/**
 * Vertx HTTP Server for the device communication api.
 */
@Singleton
public class DeviceCommunicationHttpServer extends AbstractVertxHttpServer implements HttpServer {
    private final Logger log = LoggerFactory.getLogger(DeviceCommunicationHttpServer.class);
    private final String serverStartedMsg = "HTTP Server is listening at http://{}:{}";
    private final String serverFailedMsg = "HTTP Server failed to start: {}";
    private final VertxHttpHandlerManagerService httpHandlerManager;

    private final DatabaseService db;
    private final DatabaseSchemaCreator databaseSchemaCreator;
    private List<HttpEndpointHandler> httpEndpointHandlers;


    /**
     * Creates a new DeviceCommunicationHttpServer with all dependencies.
     *
     * @param appConfigs            THe application configurations
     * @param vertx                 The quarkus Vertx instance
     * @param httpHandlerManager    The http handler manager
     * @param databaseService       The database connection
     * @param databaseSchemaCreator The database migrations service
     */
    public DeviceCommunicationHttpServer(final ApplicationConfig appConfigs,
                                         final Vertx vertx,
                                         final VertxHttpHandlerManagerService httpHandlerManager,
                                         final DatabaseService databaseService,
                                         final DatabaseSchemaCreator databaseSchemaCreator) {
        super(appConfigs, vertx);
        this.httpHandlerManager = httpHandlerManager;
        this.databaseSchemaCreator = databaseSchemaCreator;
        this.httpEndpointHandlers = new ArrayList<>();
        this.db = databaseService;
    }


    @Override
    public void start() {
        //Create Database Tables
        databaseSchemaCreator.createDBTables();

        // Create Endpoints Router
        this.httpEndpointHandlers = httpHandlerManager.getAvailableHandlerServices();
        RouterBuilder.create(this.vertx, appConfigs.getServerConfig().getOpenApiFilePath())
                .onSuccess(routerBuilder -> {
                    final Router apiRouter = this.createRouterWithEndpoints(routerBuilder, httpEndpointHandlers);
                    this.startVertxServer(apiRouter);
                })
                .onFailure(error -> {
                    if (error != null) {
                        log.error("Can not create Router: {}", error.getMessage());
                    } else {
                        log.error("Can not create Router");
                    }
                    stop();
                    Quarkus.asyncExit(-1);

                });

        // Wait until application is stopped
        Quarkus.waitForExit();

    }

    /**
     * Creates the Router object and adds endpoints and handlers.
     *
     * @param routerBuilder        Vertx RouterBuilder object
     * @param httpEndpointHandlers All available http endpoint handlers
     * @return The created Router object
     */
    Router createRouterWithEndpoints(final RouterBuilder routerBuilder, final List<HttpEndpointHandler> httpEndpointHandlers) {
        for (HttpEndpointHandler handlerService : httpEndpointHandlers) {
            handlerService.addRoutes(routerBuilder);
        }
        final var apiRouter = Router.router(vertx);
        final var router = routerBuilder.createRouter();

        final var serverConfig = appConfigs.getServerConfig();
        addHealthCheckHandlers(apiRouter, serverConfig);
        final var basePath = String.format("%s*", serverConfig.getBasePath()); // absolut path not allowed only /*

        log.info("API base path: {}", basePath);

        apiRouter.route(basePath).subRouter(router);
        return apiRouter;
    }

    /**
     * Adds readiness and liveness handlers.
     *
     * @param router Created router object
     */
    private void addHealthCheckHandlers(final Router router, final ServerConfig serverConfig) {
        addReadinessHandlers(router, serverConfig.getReadinessPath());
        addLivenessHandlers(router, serverConfig.getLivenessPath());
    }

    private void addReadinessHandlers(final Router router, final String readinessPath) {
        log.info("Adding readiness path: {}", readinessPath);
        final HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);

        healthCheckHandler.register("database-communication-is-ready",
                promise ->
                        db.getDbClient().getConnection(connection -> {
                            if (connection.failed()) {
                                log.error(connection.cause().getMessage());
                                promise.tryComplete(Status.KO());
                            } else {
                                connection.result().close();
                                promise.tryComplete(Status.OK());
                            }
                        })
        );

        router.get(readinessPath).handler(healthCheckHandler);
    }


    private void addLivenessHandlers(final Router router, final String livenessPath) {
        log.info("Adding liveness path: {}", livenessPath);
        final HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
        healthCheckHandler.register("liveness", promise -> promise.tryComplete(Status.OK()));
        router.get(livenessPath).handler(healthCheckHandler);
    }

    /**
     * Starts the server and blocks until application is stopped.
     *
     * @param router The Router object
     */
    void startVertxServer(final Router router) {
        final var serverConfigs = appConfigs.getServerConfig();
        final var serverOptions = new HttpServerOptions()
                .setPort(serverConfigs.getServerPort())
                .setHost(serverConfigs.getServerUrl());

        final var serverCreationFuture = vertx
                .createHttpServer(serverOptions)
                .requestHandler(router)
                .listen();

        serverCreationFuture
                .onSuccess(server -> log.info(this.serverStartedMsg, serverConfigs.getServerUrl()
                        , serverConfigs.getServerPort()))
                .onFailure(error -> log.info(this.serverFailedMsg, error.getMessage()));
    }

    /**
     * Adds status code 400 and sets the error message for the routingContext response.
     *
     * @param routingContext the routing context object
     * @throws NullPointerException – if routingContext is {@code null}.
     */
    void addDefault400ExceptionHandler(final RoutingContext routingContext) {
        Objects.requireNonNull(routingContext);
        final String errorMsg = ((BadRequestException) routingContext.failure()).toJson().toString();
        log.error(errorMsg);
        routingContext.response().setStatusCode(400).end(errorMsg);
    }

    /**
     * Adds status code 404 and sets the error message for the routingContext response.
     *
     * @param routingContext the routing context object
     * @throws NullPointerException – if routingContext is {@code null}.
     */
    void addDefault404ExceptionHandler(final RoutingContext routingContext) {
        Objects.requireNonNull(routingContext);
        if (!routingContext.response().ended()) {
            routingContext.response().setStatusCode(404);

            if (!routingContext.request().method().equals(HttpMethod.HEAD)) {
                routingContext.response()
                        .putHeader(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8")
                        .end("<html><body><h1>Resource not found</h1></body></html>");
            } else {
                routingContext.response().end();
            }
        }
    }


    @Override
    public void stop() {
        // stop server custom functionality
        db.close();

    }
}
