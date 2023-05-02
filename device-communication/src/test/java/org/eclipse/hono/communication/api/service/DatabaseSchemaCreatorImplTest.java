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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.pgclient.PgPool;


class DatabaseSchemaCreatorImplTest {

    private final Vertx vertxMock;
    private final DatabaseService dbMock;
    private final FileSystem fileSystemMock;
    private final PgPool pgPoolMock;

    private final DatabaseSchemaCreatorImpl databaseSchemaCreator;

    DatabaseSchemaCreatorImplTest() {
        this.dbMock = mock(DatabaseService.class);
        this.vertxMock = mock(Vertx.class);
        this.fileSystemMock = mock(FileSystem.class);
        this.pgPoolMock = mock(PgPool.class);

        this.databaseSchemaCreator = new DatabaseSchemaCreatorImpl(vertxMock, dbMock);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(vertxMock, dbMock, pgPoolMock, fileSystemMock);
    }

    @Test
    void createDBTables_success() {
        when(vertxMock.fileSystem()).thenReturn(fileSystemMock);
        when(fileSystemMock.readFile(anyString(), any())).thenReturn(fileSystemMock);
        when(dbMock.getDbClient()).thenReturn(pgPoolMock);
        when(pgPoolMock.withTransaction(any())).thenReturn(Future.succeededFuture());

        databaseSchemaCreator.createDBTables();

        verify(vertxMock).fileSystem();
        verify(fileSystemMock).readFile(anyString(), any());
        verify(dbMock).getDbClient();
        verify(pgPoolMock).withTransaction(any());

    }


    @Test
    void createDBTables_failed() {
        when(vertxMock.fileSystem()).thenReturn(fileSystemMock);
        when(fileSystemMock.readFile(anyString(), any())).thenReturn(fileSystemMock);
        when(dbMock.getDbClient()).thenReturn(pgPoolMock);
        when(pgPoolMock.withTransaction(any())).thenReturn(Future.failedFuture(new Throwable()));

        databaseSchemaCreator.createDBTables();

        verify(vertxMock).fileSystem();
        verify(fileSystemMock).readFile(anyString(), any());
        verify(dbMock).getDbClient();
        verify(dbMock).close();
        verify(pgPoolMock).withTransaction(any());

    }
}
