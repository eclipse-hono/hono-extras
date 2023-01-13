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

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;

/**
 * Database configurations
 */
@Singleton
public class DatabaseConfig {

    // Datasource properties
    @ConfigProperty(name = "quarkus.datasource.port")
    int port;
    @ConfigProperty(name = "quarkus.datasource.host")
    String host;
    @ConfigProperty(name = "quarkus.datasource.username")
    String userName;
    @ConfigProperty(name = "quarkus.datasource.password")
    String password;
    @ConfigProperty(name = "quarkus.datasource.name")
    String name;
    @ConfigProperty(name = "quarkus.datasource.pool-max-size")
    int poolMaxSize;
    @ConfigProperty(name = "quarkus.device-registration.table")
    String deviceRegistrationTableName;
    @ConfigProperty(name = "quarkus.device-registration.tenant-id-column")
    String deviceRegistrationTenantIdColumn;
    @ConfigProperty(name = "quarkus.device-registration.device-id-column")
    String deviceRegistrationDeviceIdColumn;

    public String getDeviceRegistrationTableName() {
        return deviceRegistrationTableName;
    }

    public String getDeviceRegistrationTenantIdColumn() {
        return deviceRegistrationTenantIdColumn;
    }

    public String getDeviceRegistrationDeviceIdColumn() {
        return deviceRegistrationDeviceIdColumn;
    }

    public int getPoolMaxSize() {
        return poolMaxSize;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
