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

import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.DeviceConfigResponse;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceConfigsRepository;
import org.eclipse.hono.communication.api.repository.DeviceConfigsRepositoryImpl;
import org.eclipse.hono.communication.api.service.config.DeviceConfigService;
import org.eclipse.hono.communication.api.service.config.DeviceConfigServiceImpl;
import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.eclipse.hono.communication.api.service.database.DatabaseServiceImpl;
import org.eclipse.hono.communication.core.app.InternalCommunicationConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DeviceConfigServiceImplTest {

    private final DeviceConfigsRepository repositoryMock;
    private final DatabaseService dbMock;
    private final PgPool poolMock;
    private final DeviceConfigMapper mapperMock;
    private final DeviceConfigService deviceConfigService;
    private final InternalCommunicationConfig communicationConfig;

    private final String tenantId = "tenant_ID";
    private final String deviceId = "device_ID";

    public DeviceConfigServiceImplTest() {
        this.repositoryMock = mock(DeviceConfigsRepositoryImpl.class);
        this.dbMock = mock(DatabaseServiceImpl.class);
        this.mapperMock = mock(DeviceConfigMapper.class);
        this.poolMock = mock(PgPool.class);
        this.communicationConfig = mock(InternalCommunicationConfig.class);
        this.deviceConfigService = new DeviceConfigServiceImpl(repositoryMock, dbMock, mapperMock, communicationConfig, null);
    }


    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repositoryMock, mapperMock, dbMock);
    }

    @Test
    void modifyCloudToDeviceConfig_success() {
        var deviceConfigRequest = new DeviceConfigRequest();
        var deviceConfigEntity = new DeviceConfigEntity();
        var deviceConfigEntityResponse = new DeviceConfigResponse();
        when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);
        when(mapperMock.deviceConfigEntityToResponse(deviceConfigEntity)).thenReturn(deviceConfigEntityResponse);
        when(dbMock.getDbClient()).thenReturn(poolMock);
        when(poolMock.withTransaction(any())).thenReturn(Future.succeededFuture(deviceConfigEntity));


        var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);

        verify(mapperMock, times(1)).configRequestToDeviceConfigEntity(deviceConfigRequest);
        verify(mapperMock, times(1)).deviceConfigEntityToResponse(deviceConfigEntity);
        verify(dbMock, times(1)).getDbClient();
        verify(poolMock, times(1)).withTransaction(any());
        Assertions.assertTrue(results.succeeded());
    }

    @Test
    void modifyCloudToDeviceConfig_failure() {
        var deviceConfigRequest = new DeviceConfigRequest();
        var deviceConfigEntity = new DeviceConfigEntity();
        when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);
        when(dbMock.getDbClient()).thenReturn(poolMock);
        when(poolMock.withTransaction(any())).thenReturn(Future.failedFuture(new Throwable("test_error")));


        var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);

        verify(mapperMock, times(1)).configRequestToDeviceConfigEntity(deviceConfigRequest);
        verify(dbMock, times(1)).getDbClient();
        verify(poolMock, times(1)).withTransaction(any());
        Assertions.assertTrue(results.failed());
    }

    @Test
    void listAll_success() {
        var deviceConfigVersions = new ListDeviceConfigVersionsResponse();
        when(dbMock.getDbClient()).thenReturn(poolMock);
        when(poolMock.withConnection(any())).thenReturn(Future.succeededFuture(deviceConfigVersions));

        var results = deviceConfigService.listAll(deviceId, tenantId, 10);

        verify(dbMock, times(1)).getDbClient();
        verify(poolMock, times(1)).withConnection(any());
        Assertions.assertTrue(results.succeeded());


    }


    @Test
    void listAll_failed() {
        when(dbMock.getDbClient()).thenReturn(poolMock);
        when(poolMock.withConnection(any())).thenReturn(Future.failedFuture(new Throwable("test_error")));

        var results = deviceConfigService.listAll(deviceId, tenantId, 10);

        verify(dbMock, times(1)).getDbClient();
        verify(poolMock, times(1)).withConnection(any());
        Assertions.assertTrue(results.failed());


    }
}