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

package org.eclipse.hono.communication.api.service.command;

import static org.mockito.Mockito.*;

import java.io.IOException;


import org.eclipse.hono.communication.api.data.DeviceCommandRequest;
import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceRepository;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.eclipse.hono.communication.api.service.database.DatabaseServiceImpl;
import org.eclipse.hono.communication.core.app.DatabaseConfig;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;


class DeviceCommandServiceImplTest {

    private final DeviceRepository repositoryMock;
    private final DatabaseService dbMock;
    private final PgPool poolMock;
    private final DeviceConfigMapper mapperMock;
    private final DeviceCommandServiceImpl deviceCommandService;
    private final InternalMessagingConfig communicationConfig;
    private final InternalMessaging internalCommunication;
    private final DatabaseConfig databaseConfig;


    DeviceCommandServiceImplTest() {
        this.repositoryMock = mock(DeviceRepository.class);
        this.dbMock = mock(DatabaseServiceImpl.class);
        this.mapperMock = mock(DeviceConfigMapper.class);
        this.poolMock = mock(PgPool.class);
        this.communicationConfig = mock(InternalMessagingConfig.class);
        this.internalCommunication = mock(InternalMessaging.class);
        this.databaseConfig = mock(DatabaseConfig.class);
        this.deviceCommandService = new DeviceCommandServiceImpl(

                repositoryMock,
                internalCommunication,
                communicationConfig);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(dbMock,
                databaseConfig,
                repositoryMock,
                internalCommunication,
                communicationConfig);
    }


    @Test
    public void postCommand_whenDeviceExists_shouldSucceed() throws Exception {
        final String deviceId = "device123";
        final String tenantId = "tenant123";
        final DeviceCommandRequest commandRequest = new DeviceCommandRequest();
        commandRequest.setBinaryData("test");

        when(repositoryMock.searchForDevice(deviceId, tenantId)).thenReturn(Future.succeededFuture(1));
        when(communicationConfig.getCommandTopicFormat()).thenReturn("%s.command");
        doNothing().when(internalCommunication).publish(anyString(), anyString(), any());

        final Future<Void> result = deviceCommandService.postCommand(commandRequest, tenantId, deviceId);

        verify(repositoryMock).searchForDevice(deviceId, tenantId);
        verify(communicationConfig).getCommandTopicFormat();
        verify(internalCommunication).publish(anyString(), anyString(), any());
        Assertions.assertTrue(result.succeeded());
    }

    @Test
    public void postCommand_whenDeviceDoesNotExist_shouldThrowDeviceNotFoundException() throws Exception {
        final String deviceId = "device123";
        final String tenantId = "tenant123";
        final DeviceCommandRequest commandRequest = new DeviceCommandRequest();
        commandRequest.setBinaryData("test");

        when(repositoryMock.searchForDevice(deviceId, tenantId)).thenReturn(Future.succeededFuture(0));

        final Future<Void> result = deviceCommandService.postCommand(commandRequest, tenantId, deviceId);
        verify(repositoryMock).searchForDevice(deviceId, tenantId);
        Assertions.assertTrue(result.failed());
        Assertions.assertSame(result.cause().getClass(), DeviceNotFoundException.class);
    }

    @Test
    public void postCommand_publish_error_shouldFailed() throws Exception {
        final String deviceId = "device123";
        final String tenantId = "tenant123";
        final DeviceCommandRequest commandRequest = new DeviceCommandRequest();
        commandRequest.setBinaryData("test");

        when(repositoryMock.searchForDevice(deviceId, tenantId)).thenReturn(Future.succeededFuture(1));
        when(communicationConfig.getCommandTopicFormat()).thenReturn("%s.command");
        doThrow(new IOException()).when(internalCommunication).publish(anyString(), anyString(), any());

        final Future<Void> result = deviceCommandService.postCommand(commandRequest, tenantId, deviceId);

        verify(repositoryMock).searchForDevice(deviceId, tenantId);
        verify(communicationConfig).getCommandTopicFormat();
        verify(internalCommunication).publish(anyString(), anyString(), any());
        Assertions.assertTrue(result.failed());
        Assertions.assertSame(result.cause().getClass(), IOException.class);
    }


}
