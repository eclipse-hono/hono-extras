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

package org.eclipse.hono.communication.api.service.state;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.eclipse.hono.communication.api.data.DeviceState;
import org.eclipse.hono.communication.api.data.DeviceStateEntity;
import org.eclipse.hono.communication.api.mapper.DeviceStateMapper;
import org.eclipse.hono.communication.api.repository.DeviceStateRepository;
import org.eclipse.hono.communication.api.repository.DeviceStateRepositoryImpl;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import io.vertx.core.Future;

class DeviceStateServiceImplTest {

    private final DeviceStateRepository repositoryMock;
    private final DeviceStateMapper mapperMock;
    private final InternalMessagingConfig communicationConfigMock;
    private final InternalMessaging internalCommunicationMock;
    private final PubsubMessage pubsubMessageMock;
    private final AckReplyConsumer ackReplyConsumerMock;
    private final String tenantId = "tenant_ID";
    private final String deviceId = "device_ID";
    private final DeviceStateServiceImpl deviceStateService;

    DeviceStateServiceImplTest() {
        this.repositoryMock = mock(DeviceStateRepositoryImpl.class);
        this.mapperMock = mock(DeviceStateMapper.class);
        this.communicationConfigMock = mock(InternalMessagingConfig.class);
        this.internalCommunicationMock = mock(InternalMessaging.class);
        this.pubsubMessageMock = mock(PubsubMessage.class);
        this.ackReplyConsumerMock = mock(AckReplyConsumer.class);
        deviceStateService = new DeviceStateServiceImpl(repositoryMock, mapperMock, communicationConfigMock,
                internalCommunicationMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repositoryMock,
                mapperMock,
                communicationConfigMock,
                internalCommunicationMock,
                pubsubMessageMock);
    }

    @Test
    void testListAll_success() {
        when(repositoryMock.listAll(deviceId, tenantId, 10))
                .thenReturn(Future.succeededFuture(List.of(new DeviceState())));

        final var results = deviceStateService.listAll(deviceId, tenantId, 10);

        verify(repositoryMock).listAll(deviceId, tenantId, 10);
        Assertions.assertTrue(results.succeeded());

    }

    @Test
    void testListAll_failed() {
        when(repositoryMock.listAll(deviceId, tenantId, 10)).thenReturn(Future.failedFuture(new RuntimeException()));

        final var results = deviceStateService.listAll(deviceId, tenantId, 10);

        verify(repositoryMock).listAll(deviceId, tenantId, 10);
        Assertions.assertTrue(results.failed());

    }

    @Test
    public void testOnStateMessage_SkipsIfDeviceIdOrTenantIdIsEmpty() {
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "",
                        "tenantId", "tenant-123"));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");

        deviceStateService.onStateMessage(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
    }

    @Test
    public void testOnStateMessage_SkipsPayloadEmpty() {
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "device-123",
                        "tenantId", "tenant-123"));
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8(""));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");

        deviceStateService.onStateMessage(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(pubsubMessageMock).getData();
        verify(ackReplyConsumerMock).ack();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
    }

    @Test
    public void testOnStateMessage_CreatesNewStateEntryInDB() {
        final String deviceId = "device-123";
        final String tenantId = "tenant-123";

        final Map<String, String> messageAttributes = Map.of(
                "deviceId", deviceId,
                "tenantId", tenantId);
        final var deviceStateEntity = new DeviceStateEntity();

        when(pubsubMessageMock.getAttributesMap()).thenReturn(messageAttributes);
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8("{\"cause\": \"connected\"}"));
        when(repositoryMock.createNew(deviceStateEntity)).thenReturn(Future.succeededFuture(deviceStateEntity));
        when(mapperMock.pubSubMessageToDeviceStateEntity(pubsubMessageMock)).thenReturn(deviceStateEntity);
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");

        deviceStateService.onStateMessage(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(pubsubMessageMock).getData();
        verify(repositoryMock).createNew(deviceStateEntity);
        verify(mapperMock).pubSubMessageToDeviceStateEntity(pubsubMessageMock);
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(ackReplyConsumerMock).ack();
    }
}
