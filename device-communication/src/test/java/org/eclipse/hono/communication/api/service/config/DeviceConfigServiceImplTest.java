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

package org.eclipse.hono.communication.api.service.config;


import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigAckResponse;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceConfigRepository;
import org.eclipse.hono.communication.api.repository.DeviceConfigRepositoryImpl;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import io.vertx.core.Future;


class DeviceConfigServiceImplTest {

    private final DeviceConfigRepository repositoryMock;

    private final DeviceConfigMapper mapperMock;
    private final InternalMessagingConfig communicationConfigMock;
    private final InternalMessaging internalCommunicationMock;
    private final String tenantId = "tenant_ID";
    private final String deviceId = "device_ID";
    private final PubsubMessage pubsubMessageMock;
    private final AckReplyConsumer ackReplyConsumerMock;
    private DeviceConfigServiceImpl deviceConfigService;

    DeviceConfigServiceImplTest() {
        this.repositoryMock = mock(DeviceConfigRepositoryImpl.class);
        this.mapperMock = mock(DeviceConfigMapper.class);
        this.communicationConfigMock = mock(InternalMessagingConfig.class);
        this.internalCommunicationMock = mock(InternalMessaging.class);
        this.pubsubMessageMock = mock(PubsubMessage.class);
        this.ackReplyConsumerMock = mock(AckReplyConsumer.class);


    }

    void init_with_success_subscription() {
        when(repositoryMock.listTenants()).thenReturn(Future.succeededFuture(List.of("1", "2")));
        when(communicationConfigMock.getOnConnectEventTopicFormat()).thenReturn("%s.test");
        doNothing().when(internalCommunicationMock).subscribe(anyString(), any());

        this.deviceConfigService = createServiceObj();

        verify(repositoryMock).listTenants();
        verify(communicationConfigMock, times(2)).getOnConnectEventTopicFormat();
        verify(internalCommunicationMock, times(2)).subscribe(anyString(), any());


    }

    void init_with_failed_subscription() {
        when(repositoryMock.listTenants()).thenReturn(Future.failedFuture(new RuntimeException()));

        this.deviceConfigService = createServiceObj();

        verify(repositoryMock).listTenants();

    }

    DeviceConfigServiceImpl createServiceObj() {
        return new DeviceConfigServiceImpl(repositoryMock,
                mapperMock,
                communicationConfigMock,
                internalCommunicationMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repositoryMock,
                mapperMock,
                communicationConfigMock,
                internalCommunicationMock,
                pubsubMessageMock,
                ackReplyConsumerMock);
    }

    @Test
    void modifyCloudToDeviceConfig_success() throws Exception {
        init_with_success_subscription();
        final var deviceConfigRequest = new DeviceConfigRequest();
        final var deviceConfigEntity = new DeviceConfigEntity();
        deviceConfigEntity.setDeviceId("id");
        deviceConfigEntity.setTenantId("id");
        final var deviceConfigEntityResponse = new DeviceConfig();
        deviceConfigEntityResponse.setVersion("1");

        when(repositoryMock.createNew(any())).thenReturn(Future.succeededFuture(deviceConfigEntity));
        when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);
        when(mapperMock.deviceConfigEntityToConfig(deviceConfigEntity)).thenReturn(deviceConfigEntityResponse);
        when(mapperMock.configRequestToDeviceConfigEntity(any())).thenReturn(deviceConfigEntity);
        when(communicationConfigMock.getConfigTopicFormat()).thenReturn("%s.config");
        when(communicationConfigMock.getConfigAckTopicFormat()).thenReturn("%s.ack");
        doNothing().when(internalCommunicationMock).publish(anyString(), anyString(), any());

        final var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);

        verify(repositoryMock).createNew(any());
        verify(internalCommunicationMock, times(1)).publish(anyString(), anyString(), any());
        verify(internalCommunicationMock, times(3)).subscribe(anyString(), any());
        verify(mapperMock, times(1)).configRequestToDeviceConfigEntity(deviceConfigRequest);
        verify(mapperMock, times(1)).deviceConfigEntityToConfig(deviceConfigEntity);
        verify(communicationConfigMock).getConfigTopicFormat();
        verify(communicationConfigMock).getConfigAckTopicFormat();
        Assertions.assertTrue(results.succeeded());
    }

    @Test
    void modifyCloudToDeviceConfig_failure() {
        init_with_failed_subscription();
        final var deviceConfigRequest = new DeviceConfigRequest();
        final var deviceConfigEntity = new DeviceConfigEntity();

        when(repositoryMock.createNew(any())).thenReturn(Future.failedFuture(new NoSuchElementException()));
        when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);

        final var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);

        verify(mapperMock).configRequestToDeviceConfigEntity(any());
        verify(repositoryMock).createNew(any());
        Assertions.assertTrue(results.failed());
    }

    @Test
    void modifyCloudToDeviceConfig_publish_failure() throws Exception {
        init_with_success_subscription();
        final var deviceConfigRequest = new DeviceConfigRequest();
        final var deviceConfigEntity = new DeviceConfigEntity();
        deviceConfigEntity.setDeviceId("id");
        deviceConfigEntity.setTenantId("id");
        final var deviceConfigEntityResponse = new DeviceConfig();
        deviceConfigEntityResponse.setVersion("1");

        when(repositoryMock.createNew(any())).thenReturn(Future.succeededFuture(deviceConfigEntity));
        when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);
        when(mapperMock.deviceConfigEntityToConfig(deviceConfigEntity)).thenReturn(deviceConfigEntityResponse);
        when(mapperMock.configRequestToDeviceConfigEntity(any())).thenReturn(deviceConfigEntity);
        when(communicationConfigMock.getConfigTopicFormat()).thenReturn("%s.config");
        when(communicationConfigMock.getConfigAckTopicFormat()).thenReturn("%s.ack");
        doThrow(new RuntimeException()).when(internalCommunicationMock).publish(anyString(), anyString(), any());

        final var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);

        verify(repositoryMock).createNew(any());
        verify(internalCommunicationMock, times(1)).publish(anyString(), anyString(), any());
        verify(mapperMock, times(1)).configRequestToDeviceConfigEntity(deviceConfigRequest);
        verify(mapperMock, times(1)).deviceConfigEntityToConfig(deviceConfigEntity);
        verify(communicationConfigMock).getConfigTopicFormat();
        verify(communicationConfigMock).getConfigAckTopicFormat();
        Assertions.assertTrue(results.succeeded());
    }

    @Test
    void listAll_success() {
        init_with_success_subscription();
        when(repositoryMock.listAll(deviceId, tenantId, 10)).thenReturn(Future.succeededFuture(List.of(new DeviceConfig())));

        final var results = deviceConfigService.listAll(deviceId, tenantId, 10);

        verify(repositoryMock).listAll(deviceId, tenantId, 10);
        Assertions.assertTrue(results.succeeded());


    }


    @Test
    void listAll_failed() {
        init_with_success_subscription();
        when(repositoryMock.listAll(deviceId, tenantId, 10)).thenReturn(Future.failedFuture(new RuntimeException()));

        final var results = deviceConfigService.listAll(deviceId, tenantId, 10);

        verify(repositoryMock).listAll(deviceId, tenantId, 10);
        Assertions.assertTrue(results.failed());


    }

    @Test
    void updateDeviceAckTime() {
        init_with_success_subscription();
        doReturn(Future.succeededFuture()).when(repositoryMock).updateDeviceAckTime(any(), anyString());

        deviceConfigService.updateDeviceAckTime(new DeviceConfigAckResponse(), Instant.now().toString());

        verify(repositoryMock).updateDeviceAckTime(any(), anyString());


    }

    @Test
    void onDeviceConfigAck() {
        init_with_success_subscription();
        final var deviceConfigSpy = spy(this.deviceConfigService);
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "device-123",
                        "tenantId", "tenant-123",
                        "configVersion", "12"
                ));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getConfigVersionIdKey()).thenReturn("config");

        doNothing().when(deviceConfigSpy).updateDeviceAckTime(any(), anyString());

        deviceConfigSpy.onDeviceConfigAck(pubsubMessageMock, ackReplyConsumerMock);

        verify(deviceConfigSpy).updateDeviceAckTime(any(), anyString());
        verify(pubsubMessageMock).getAttributesMap();
        verify(ackReplyConsumerMock).ack();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(communicationConfigMock).getConfigVersionIdKey();
    }

    @Test
    public void onDeviceConnectEvent_SkipsIfDeviceIdOrTenantIdIsEmpty() {
        init_with_success_subscription();
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "",
                        "tenantId", "tenant-123"
                ));
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8("{\"cause\": \"connected\"}"));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(pubsubMessageMock).getData();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(ackReplyConsumerMock).ack();
    }

    @Test
    public void onDeviceConnectEvent_SkipsEventIsNotConnected() {
        init_with_success_subscription();
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "device-123",
                        "tenantId", "tenant-123"
                ));
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8("{\"cause\": \"notConnected\"}"));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getDeviceConnectPayloadKey()).thenReturn("cause");
        when(communicationConfigMock.getDeviceConnectPayloadValue()).thenReturn("connected");

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(pubsubMessageMock).getData();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(communicationConfigMock).getDeviceConnectPayloadKey();
        verify(communicationConfigMock).getDeviceConnectPayloadValue();
        verify(ackReplyConsumerMock).ack();
    }


    @Test
    public void onDeviceConnectEvent_SkipsPayloadFormatFalse() {
        init_with_success_subscription();
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "device-123",
                        "tenantId", "tenant-123"
                ));
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8("{\"cause\"}"));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getDeviceConnectPayloadKey()).thenReturn("cause");
        when(communicationConfigMock.getDeviceConnectPayloadValue()).thenReturn("connected");

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getData();
        verify(ackReplyConsumerMock).ack();
    }

    @Test
    public void onDeviceConnectEvent_PublishesDeviceConfig() throws Exception {
        init_with_failed_subscription();
        final String deviceId = "device-123";
        final String tenantId = "tenant-123";

        final String topic = "tenant-123-config";
        final String message = "{}";
        final Map<String, String> messageAttributes = Map.of(
                "deviceId", deviceId,
                "tenantId", tenantId
        );
        final var deviceConfigEntity = new DeviceConfigEntity();
        final var deviceConfigEntityResponse = new DeviceConfig();

        when(pubsubMessageMock.getAttributesMap()).thenReturn(messageAttributes);
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8("{\"cause\": \"connected\"}"));
        when(repositoryMock.getDeviceLatestConfig(deviceId, tenantId)).thenReturn(Future.succeededFuture(deviceConfigEntity));
        when(mapperMock.deviceConfigEntityToConfig(deviceConfigEntity)).thenReturn(deviceConfigEntityResponse);
        when(communicationConfigMock.getConfigTopicFormat()).thenReturn("%s-config");
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getDeviceConnectPayloadKey()).thenReturn("cause");
        when(communicationConfigMock.getDeviceConnectPayloadValue()).thenReturn("connected");
        when(communicationConfigMock.getConfigAckTopicFormat()).thenReturn("%s.ack");
        doNothing().when(internalCommunicationMock).publish(topic, message, messageAttributes);
        doNothing().when(internalCommunicationMock).subscribe(anyString(), any());

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(pubsubMessageMock).getData();
        verify(repositoryMock).getDeviceLatestConfig(deviceId, tenantId);
        verify(mapperMock).deviceConfigEntityToConfig(deviceConfigEntity);
        verify(communicationConfigMock).getConfigTopicFormat();
        verify(communicationConfigMock).getDeviceConnectPayloadKey();
        verify(communicationConfigMock).getDeviceConnectPayloadValue();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(communicationConfigMock).getConfigAckTopicFormat();
        verify(internalCommunicationMock).publish(topic, message, messageAttributes);
        verify(internalCommunicationMock, times(1)).subscribe(anyString(), any());
        verify(ackReplyConsumerMock).ack();
    }


    @Test
    public void onDeviceConnectEvent_PublishesDeviceConfig_failed() throws Exception {
        init_with_success_subscription();
        final String deviceId = "device-123";
        final String tenantId = "tenant-123";

        final Map<String, String> messageAttributes = Map.of(
                "deviceId", deviceId,
                "tenantId", tenantId
        );

        when(pubsubMessageMock.getAttributesMap()).thenReturn(messageAttributes);
        when(repositoryMock.getDeviceLatestConfig(deviceId, tenantId)).thenReturn(Future.failedFuture(new RuntimeException()));
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8("{\"cause\": \"connected\"}"));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getDeviceConnectPayloadKey()).thenReturn("cause");
        when(communicationConfigMock.getDeviceConnectPayloadValue()).thenReturn("connected");
        when(communicationConfigMock.getConfigAckTopicFormat()).thenReturn("%s.ack");

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(pubsubMessageMock).getData();
        verify(repositoryMock).getDeviceLatestConfig(deviceId, tenantId);
        verify(ackReplyConsumerMock).ack();
        verify(communicationConfigMock).getDeviceConnectPayloadKey();
        verify(communicationConfigMock).getDeviceConnectPayloadValue();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
    }
}
