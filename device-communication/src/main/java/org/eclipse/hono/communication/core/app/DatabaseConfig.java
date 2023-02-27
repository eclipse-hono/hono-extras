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
 * Database configurations.
 */
@Singleton
public class DatabaseConfig {

    // Datasource properties
    @ConfigProperty(name = "vertx.database.port")
    int port;
    @ConfigProperty(name = "vertx.database.host")
    String host;
    @ConfigProperty(name = "vertx.database.username")
    String userName;
    @ConfigProperty(name = "vertx.database.password")
    String password;
    @ConfigProperty(name = "vertx.database.name")
    String name;
    @ConfigProperty(name = "vertx.database.pool-max-size")
    int poolMaxSize;
    @ConfigProperty(name = "vertx.device-registration.table")
    String deviceRegistrationTableName;
    @ConfigProperty(name = "vertx.device-registration.tenant-id-column")
    String deviceRegistrationTenantIdColumn;
    @ConfigProperty(name = "vertx.device-registration.device-id-column")
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
