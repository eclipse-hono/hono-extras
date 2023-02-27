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
 * Application configurations.
 */
@Singleton
public class ApplicationConfig {

    @ConfigProperty(name = "app.version")
    String version;
    @ConfigProperty(name = "app.name")
    String componentName;
    private final ServerConfig serverConfig;
    private final DatabaseConfig databaseConfig;


    /**
     * Creates a new ApplicationConfig.
     *
     * @param serverConfig   The server configs
     * @param databaseConfig The database configs
     */
    public ApplicationConfig(final ServerConfig serverConfig, final DatabaseConfig databaseConfig) {
        this.serverConfig = serverConfig;
        this.databaseConfig = databaseConfig;
    }

    public String getVersion() {
        return version;
    }

    public String getComponentName() {
        return componentName;
    }


    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}
