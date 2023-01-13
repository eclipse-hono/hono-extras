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

package org.eclipse.hono.communication.core.utils;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import org.eclipse.hono.communication.core.app.ApplicationConfig;

/**
 * Database utilities class
 */
public class DbUtils {

    /**
     * Build DB client that is used to manage a pool of connections
     *
     * @param vertx Vertx context
     * @return PostgreSQL pool
     */
    public static PgPool createDbClient(Vertx vertx, ApplicationConfig appConfigs) {
        var dbConfigs = appConfigs.getDatabaseConfig();
        final PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(dbConfigs.getHost())
                .setPort(dbConfigs.getPort())
                .setDatabase(dbConfigs.getName())
                .setUser(dbConfigs.getUserName())
                .setPassword(dbConfigs.getPassword());

        final PoolOptions poolOptions = new PoolOptions().setMaxSize(dbConfigs.getPoolMaxSize());

        return PgPool.pool(vertx, connectOptions, poolOptions);
    }

}
