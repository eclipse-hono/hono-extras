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

import static org.mockito.Mockito.*;

import org.eclipse.hono.communication.core.app.DatabaseConfig;
import org.eclipse.hono.communication.core.utils.DbUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;


class DatabaseServiceImplTest {

    private final Vertx vertxMock;
    private final DatabaseConfig databaseConfigMock;

    private final PgPool pgPoolMock;
    private DatabaseService databaseService;

    DatabaseServiceImplTest() {
        vertxMock = mock(Vertx.class);
        databaseConfigMock = mock(DatabaseConfig.class);
        pgPoolMock = mock(PgPool.class);


    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(vertxMock, databaseConfigMock, pgPoolMock);

    }


    @Test
    void getDbClient() {
        try (MockedStatic<DbUtils> dbUtilsMockedStatic = mockStatic(DbUtils.class)) {
            dbUtilsMockedStatic.when(() -> DbUtils.createDbClient(vertxMock, databaseConfigMock)).thenReturn(pgPoolMock);
            databaseService = new DatabaseServiceImpl(databaseConfigMock, vertxMock);

            final var client = databaseService.getDbClient();

            Assertions.assertSame(client, pgPoolMock);

            dbUtilsMockedStatic.verify(() -> DbUtils.createDbClient(vertxMock, databaseConfigMock), times(1));
            dbUtilsMockedStatic.verifyNoMoreInteractions();
        }
    }

    @Test
    void close() {

        try (MockedStatic<DbUtils> dbUtilsMockedStatic = mockStatic(DbUtils.class)) {
            dbUtilsMockedStatic.when(() -> DbUtils.createDbClient(vertxMock, databaseConfigMock)).thenReturn(pgPoolMock);
            databaseService = new DatabaseServiceImpl(databaseConfigMock, vertxMock);

            databaseService.close();
            verify(pgPoolMock, times(1)).close();
            dbUtilsMockedStatic.verify(() -> DbUtils.createDbClient(vertxMock, databaseConfigMock), times(1));
            dbUtilsMockedStatic.verifyNoMoreInteractions();
        }
    }
}
