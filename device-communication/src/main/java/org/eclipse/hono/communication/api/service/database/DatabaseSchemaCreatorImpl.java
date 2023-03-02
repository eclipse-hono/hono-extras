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

package org.eclipse.hono.communication.api.service.database;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.api.config.DeviceConfigsConstants;
import org.eclipse.hono.communication.api.config.DeviceStatesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Quarkus;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.templates.SqlTemplate;

/**
 * Creates all Database tables if they do not exist.
 */

@ApplicationScoped
public class DatabaseSchemaCreatorImpl implements DatabaseSchemaCreator {
    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaCreatorImpl.class);
    private final Vertx vertx;
    private final String tableCreationErrorMsg = "Table %s can not be created {}";
    private final String tableCreationSuccessMsg = "Successfully migrate Table: %s.";
    private final DatabaseService db;


    /**
     * Creates a new DatabaseSchemaCreatorImpl.
     *
     * @param vertx The quarkus Vertx instance
     * @param db    The database service
     */
    public DatabaseSchemaCreatorImpl(final Vertx vertx, final DatabaseService db) {
        this.vertx = vertx;
        this.db = db;
    }

    @Override
    public void createDBTables() {
        createTables(DeviceConfigsConstants.CREATE_SQL_SCRIPT_PATH, "device_config");
        createTables(DeviceStatesConstants.CREATE_SQL_SCRIPT_PATH, "device_status");
    }


    private void createTables(final String filePath, final String tableName) {
        log.info("Running database migration from file {}", filePath);

        final Promise<Buffer> loadScriptTracker = Promise.promise();
        vertx.fileSystem().readFile(filePath, loadScriptTracker);
        createTableIfNotExist(loadScriptTracker, tableName);
    }

    private void createTableIfNotExist(final Promise<Buffer> loadScriptTracker, final String tableName) {
        db.getDbClient().withTransaction(
                        sqlConnection ->
                                loadScriptTracker.future()
                                        .map(Buffer::toString)
                                        .compose(script -> SqlTemplate
                                                .forQuery(sqlConnection, script)
                                                .execute(Map.of())))
                .onSuccess(ok -> log.info(tableCreationSuccessMsg.formatted(tableName)))
                .onFailure(error -> {
                    log.error(tableCreationErrorMsg.formatted(tableName), error.getMessage());
                    db.close();
                    Quarkus.asyncExit(-1);
                });
    }
}
