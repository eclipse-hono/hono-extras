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
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import org.eclipse.hono.communication.core.app.DatabaseConfig;

/**
 * Database utilities class
 */
public class DbUtils {

    final static Logger log = LoggerFactory.getLogger(DbUtils.class);
    final static String connectionFailedMsg = "Failed to connect to Database: %s";
    final static String connectionSuccessMsg = "Database connection created successfully.";

    /**
     * Build DB client that is used to manage a pool of connections
     *
     * @param vertx Vertx context
     * @return PostgreSQL pool
     */
    public static PgPool createDbClient(Vertx vertx, DatabaseConfig dbConfigs) {


        final PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(dbConfigs.getHost())
                .setPort(dbConfigs.getPort())
                .setDatabase(dbConfigs.getName())
                .setUser(dbConfigs.getUserName())
                .setPassword(dbConfigs.getPassword());

        final PoolOptions poolOptions = new PoolOptions().setMaxSize(dbConfigs.getPoolMaxSize());
        var pool = PgPool.pool(vertx, connectOptions, poolOptions);
        pool.getConnection(connection -> {
            if (connection.failed()) {
                log.error(String.format(connectionFailedMsg, connection.cause().getMessage()));
                System.exit(-1);

            } else {
                log.info(connectionSuccessMsg);
            }
        });
        return pool;

    }

}
