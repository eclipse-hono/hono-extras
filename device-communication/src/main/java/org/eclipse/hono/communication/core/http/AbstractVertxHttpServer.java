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

package org.eclipse.hono.communication.core.http;

import org.eclipse.hono.communication.core.app.ApplicationConfig;

import io.vertx.core.Vertx;


/**
 * Abstract class for creating HTTP server in quarkus.
 * using the managed vertx api
 */
public abstract class AbstractVertxHttpServer {
    protected final ApplicationConfig appConfigs;
    protected final Vertx vertx;


    /**
     * Creates a new AbstractVertxHttpServer.
     *
     * @param appConfigs The application configs
     * @param vertx      The quarkus Vertx instance
     */
    public AbstractVertxHttpServer(final ApplicationConfig appConfigs, final Vertx vertx) {
        this.appConfigs = appConfigs;
        this.vertx = vertx;
    }
}
