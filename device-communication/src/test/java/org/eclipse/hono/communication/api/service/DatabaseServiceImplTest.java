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

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import org.eclipse.hono.communication.core.app.ApplicationConfig;
import org.eclipse.hono.communication.core.utils.DbUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class DatabaseServiceImplTest {

    private final Vertx vertxMock;
    private final ApplicationConfig applicationConfigMock;

    private final PgPool pgPoolMock;
    private DatabaseService databaseService;

    public DatabaseServiceImplTest() {
        vertxMock = mock(Vertx.class);
        applicationConfigMock = mock(ApplicationConfig.class);
        pgPoolMock = mock(PgPool.class);


    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(vertxMock, applicationConfigMock, pgPoolMock);

    }


    @Test
    void getDbClient() {
        try (MockedStatic<DbUtils> dbUtilsMockedStatic = mockStatic(DbUtils.class)) {
            dbUtilsMockedStatic.when(() -> DbUtils.createDbClient(vertxMock, applicationConfigMock)).thenReturn(pgPoolMock);
            databaseService = new DatabaseServiceImpl(applicationConfigMock, vertxMock);

            var client = databaseService.getDbClient();

            Assertions.assertSame(client, pgPoolMock);

            dbUtilsMockedStatic.verify(() -> DbUtils.createDbClient(vertxMock, applicationConfigMock), times(1));
            dbUtilsMockedStatic.verifyNoMoreInteractions();
        }
    }

    @Test
    void close() {

        try (MockedStatic<DbUtils> dbUtilsMockedStatic = mockStatic(DbUtils.class)) {
            dbUtilsMockedStatic.when(() -> DbUtils.createDbClient(vertxMock, applicationConfigMock)).thenReturn(pgPoolMock);
            databaseService = new DatabaseServiceImpl(applicationConfigMock, vertxMock);

            databaseService.close();
            verify(pgPoolMock, times(1)).close();
            dbUtilsMockedStatic.verify(() -> DbUtils.createDbClient(vertxMock, applicationConfigMock), times(1));
            dbUtilsMockedStatic.verifyNoMoreInteractions();
        }
    }
}