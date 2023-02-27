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

package org.eclipse.hono.communication.core.app;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;


/**
 * Server configurations.
 */
@Singleton
public class ServerConfig {

    // Vertx server properties
    @ConfigProperty(name = "vertx.openapi.file")
    String openApiFilePath;
    @ConfigProperty(name = "vertx.server.url")
    String serverUrl;
    @ConfigProperty(name = "vertx.server.port", defaultValue = "8080")
    int serverPort;

    @ConfigProperty(name = "vertx.server.paths.readiness", defaultValue = "/readiness")
    String readinessPath;


    @ConfigProperty(name = "vertx.server.paths.liveness", defaultValue = "/liveness")
    String livenessPath;

    @ConfigProperty(name = "vertx.server.paths.base")
    String basePath;

    public String getBasePath() {
        return basePath;
    }

    public String getReadinessPath() {
        return readinessPath;
    }

    public String getLivenessPath() {
        return livenessPath;
    }

    public String getOpenApiFilePath() {
        return openApiFilePath;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public int getServerPort() {
        return serverPort;
    }
}
