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

import org.eclipse.hono.communication.core.app.AbstractServiceApplication;
import org.eclipse.hono.communication.core.app.ApplicationConfig;
import org.eclipse.hono.communication.core.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;

/**
 * Device Communication application.
 */
public class Application extends AbstractServiceApplication {

    private final Logger log = LoggerFactory.getLogger(AbstractServiceApplication.class);
    private final HttpServer server;

    /**
     * Creates new Application with all dependencies.
     *
     * @param vertx      The quarkus Vertx instance
     * @param appConfigs The application configs
     * @param server     The http server
     */
    public Application(final Vertx vertx,
                       final ApplicationConfig appConfigs,
                       final HttpServer server) {
        super(vertx, appConfigs);
        this.server = server;
    }

    @Override
    public void doStart() {
        log.info("Starting HTTP server...");
        server.start();
    }

    @Override
    public void doStop() {
        log.info("Stopping HTTP server...");
        server.stop();
    }


}
