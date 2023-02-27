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

package org.eclipse.hono.communication.api.service;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.core.app.DatabaseConfig;
import org.eclipse.hono.communication.core.utils.DbUtils;

import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;

/**
 * Service for database.
 */
@ApplicationScoped
public class DatabaseServiceImpl implements DatabaseService {

    private final Logger log = LoggerFactory.getLogger(DatabaseServiceImpl.class);
    private final PgPool dbClient;

    /**
     * Creates a new DatabaseServiceImpl.
     *
     * @param databaseConfigs The database configs
     * @param vertx           The quarkus Vertx instance
     */
    public DatabaseServiceImpl(final DatabaseConfig databaseConfigs, final Vertx vertx) {
        this.dbClient = DbUtils.createDbClient(vertx, databaseConfigs);
        log.debug("Database connection is open.");
    }

    @Override
    public PgPool getDbClient() {
        return dbClient;
    }

    @Override
    public void close() {
        if (this.dbClient != null) {
            this.dbClient.close();
            log.info("Database connection is closed.");
        }
    }

}
