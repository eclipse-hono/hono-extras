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

import org.eclipse.hono.communication.core.app.DatabaseConfig;

import io.quarkus.runtime.Quarkus;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;


/**
 * Database utilities class.
 */
public final class DbUtils {

    static final Logger log = LoggerFactory.getLogger(DbUtils.class);
    static final String connectionFailedMsg = "Failed to connect to Database: %s";
    static final String connectionSuccessMsg = "Database connection created successfully.";

    private DbUtils() {
        // avoid instantiation
    }

    /**
     * Build DB client that is used to manage a pool of connections.
     *
     * @param vertx     The quarkus Vertx instance
     * @param dbConfigs The database configs
     * @return PostgreSQL pool
     */
    public static PgPool createDbClient(final Vertx vertx, final DatabaseConfig dbConfigs) {


        final PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(dbConfigs.getHost())
                .setPort(dbConfigs.getPort())
                .setDatabase(dbConfigs.getName())
                .setUser(dbConfigs.getUserName())
                .setPassword(dbConfigs.getPassword());

        final PoolOptions poolOptions = new PoolOptions().setMaxSize(dbConfigs.getPoolMaxSize());
        final var pool = PgPool.pool(vertx, connectOptions, poolOptions);
        pool.getConnection(connection -> {
            if (connection.failed()) {
                log.error(String.format(connectionFailedMsg, connection.cause().getMessage()));
                Quarkus.asyncExit(-1);
            } else {
                log.info(connectionSuccessMsg);
            }
        });
        return pool;

    }

}
