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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import org.mockito.MockedStatic;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

class DeviceConfigServiceImplTest {

    public static final String CONFIG_BASE_64 = "dGVzdCBjb25maWcgMjIyMjIy";
    private final DeviceConfigRepository repositoryMock;
    private final DeviceConfigMapper mapperMock;
    private final InternalMessagingConfig communicationConfigMock;
    private final InternalMessaging internalCommunicationMock;
    private final String tenantId = "tenant_ID";
    private final String deviceId = "device_ID";
    private final PubsubMessage pubsubMessageMock;
    private final Context contextMock;
    private final AckReplyConsumer ackReplyConsumerMock;
    private final ByteString byteStringMock;
    private final DeviceConfigServiceImpl deviceConfigService;

    DeviceConfigServiceImplTest() {
        this.repositoryMock = mock(DeviceConfigRepositoryImpl.class);
        this.mapperMock = mock(DeviceConfigMapper.class);
        this.communicationConfigMock = mock(InternalMessagingConfig.class);
        this.internalCommunicationMock = mock(InternalMessaging.class);
        this.pubsubMessageMock = mock(PubsubMessage.class);
        this.ackReplyConsumerMock = mock(AckReplyConsumer.class);
        this.contextMock = mock(Context.class);
        this.byteStringMock = mock(ByteString.class);
        this.deviceConfigService = createServiceObj();

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
                ackReplyConsumerMock,
                byteStringMock,
                contextMock);
    }

    @Test
    void modifyCloudToDeviceConfig_success() throws Exception {
        try (MockedStatic<Vertx> vertxMockedStatic = mockStatic(Vertx.class)) {
            final var deviceConfigRequest = new DeviceConfigRequest();
            deviceConfigRequest.setBinaryData(CONFIG_BASE_64);
            final var deviceConfigEntity = new DeviceConfigEntity();
            deviceConfigEntity.setDeviceId("id");
            deviceConfigEntity.setTenantId("id");
            final var deviceConfigEntityResponse = new DeviceConfig();
            deviceConfigEntityResponse.setVersion("1");
            deviceConfigEntityResponse.setBinaryData(CONFIG_BASE_64);

            when(repositoryMock.createNew(any())).thenReturn(Future.succeededFuture(deviceConfigEntity));
            when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);
            when(mapperMock.deviceConfigEntityToConfig(deviceConfigEntity)).thenReturn(deviceConfigEntityResponse);
            when(mapperMock.configRequestToDeviceConfigEntity(any())).thenReturn(deviceConfigEntity);
            when(communicationConfigMock.getConfigTopicFormat()).thenReturn("%s.config");
            when(communicationConfigMock.getConfigAckTopicFormat()).thenReturn("%s.ack");
            when(communicationConfigMock.getDeviceIdKey()).thenReturn("device");
            when(communicationConfigMock.getTenantIdKey()).thenReturn("tenant");
            when(communicationConfigMock.getConfigVersionIdKey()).thenReturn("version");
            when(communicationConfigMock.getConfigTopicFormat()).thenReturn("version");
            vertxMockedStatic.when(Vertx::currentContext).thenReturn(contextMock);
            doNothing().when(internalCommunicationMock).publish(anyString(), anyString(), any());

            final var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);

            verify(repositoryMock).createNew(any());
            verify(communicationConfigMock).getConfigVersionIdKey();
            verify(communicationConfigMock).getTenantIdKey();
            verify(communicationConfigMock).getDeviceIdKey();
            verify(mapperMock, times(1)).configRequestToDeviceConfigEntity(deviceConfigRequest);
            verify(mapperMock, times(1)).deviceConfigEntityToConfig(deviceConfigEntity);
            verify(communicationConfigMock).getConfigTopicFormat();
            verify(communicationConfigMock).getConfigAckTopicFormat();
            verify(contextMock).executeBlocking(any());
            vertxMockedStatic.verify(Vertx::currentContext);
            vertxMockedStatic.verifyNoMoreInteractions();
            Assertions.assertTrue(results.succeeded());
        }
    }

    @Test
    void modifyCloudToDeviceConfig_failure() {
        final var deviceConfigRequest = new DeviceConfigRequest();
        deviceConfigRequest.setBinaryData(CONFIG_BASE_64);
        final var deviceConfigEntity = new DeviceConfigEntity();
        deviceConfigEntity.setBinaryData(CONFIG_BASE_64);

        when(repositoryMock.createNew(any())).thenReturn(Future.failedFuture(new NoSuchElementException()));
        when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);

        final var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);

        verify(mapperMock).configRequestToDeviceConfigEntity(any());
        verify(repositoryMock).createNew(any());
        Assertions.assertTrue(results.failed());
    }


    @Test
    void modifyCloudToDeviceConfig_failure_noBase64() {
        final var deviceConfigRequest = new DeviceConfigRequest();
        deviceConfigRequest.setBinaryData("test 2");
        final var deviceConfigEntity = new DeviceConfigEntity();
        deviceConfigEntity.setBinaryData("");

        when(repositoryMock.createNew(any())).thenReturn(Future.failedFuture(new NoSuchElementException()));
        when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);

        final var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);


        Assertions.assertTrue(results.failed());
    }

    @Test
    void modifyCloudToDeviceConfig_publish_failure() {
        try (MockedStatic<Vertx> vertxMockedStatic = mockStatic(Vertx.class)) {
            final var deviceConfigRequest = new DeviceConfigRequest();
            deviceConfigRequest.setBinaryData(CONFIG_BASE_64);
            final var deviceConfigEntity = new DeviceConfigEntity();
            deviceConfigEntity.setDeviceId("id");
            deviceConfigEntity.setTenantId("id");
            final var deviceConfigEntityResponse = new DeviceConfig();
            deviceConfigEntityResponse.setVersion("1");
            deviceConfigEntityResponse.setBinaryData(CONFIG_BASE_64);

            when(repositoryMock.createNew(any())).thenReturn(Future.succeededFuture(deviceConfigEntity));
            when(mapperMock.configRequestToDeviceConfigEntity(deviceConfigRequest)).thenReturn(deviceConfigEntity);
            when(mapperMock.deviceConfigEntityToConfig(deviceConfigEntity)).thenReturn(deviceConfigEntityResponse);
            when(mapperMock.configRequestToDeviceConfigEntity(any())).thenReturn(deviceConfigEntity);
            when(communicationConfigMock.getConfigTopicFormat()).thenReturn("%s.config");
            when(communicationConfigMock.getConfigAckTopicFormat()).thenReturn("%s.ack");
            when(communicationConfigMock.getDeviceIdKey()).thenReturn("device");
            when(communicationConfigMock.getTenantIdKey()).thenReturn("tenant");
            when(communicationConfigMock.getConfigVersionIdKey()).thenReturn("version");
            when(communicationConfigMock.getConfigTopicFormat()).thenReturn("version");
            vertxMockedStatic.when(Vertx::currentContext).thenReturn(contextMock);
            doThrow(new RuntimeException()).when(internalCommunicationMock).subscribe(anyString(), any());

            final var results = deviceConfigService.modifyCloudToDeviceConfig(deviceConfigRequest, deviceId, tenantId);

            verify(repositoryMock).createNew(any());
            verify(communicationConfigMock).getConfigVersionIdKey();
            verify(communicationConfigMock).getTenantIdKey();
            verify(communicationConfigMock).getDeviceIdKey();
            verify(mapperMock, times(1)).configRequestToDeviceConfigEntity(deviceConfigRequest);
            verify(mapperMock, times(1)).deviceConfigEntityToConfig(deviceConfigEntity);
            verify(communicationConfigMock).getConfigTopicFormat();
            verify(communicationConfigMock).getConfigAckTopicFormat();
            verify(contextMock).executeBlocking(any());
            vertxMockedStatic.verify(Vertx::currentContext);
            vertxMockedStatic.verifyNoMoreInteractions();
            Assertions.assertTrue(results.succeeded());
        }
    }

    @Test
    void listAll_success() {
        when(repositoryMock.listAll(deviceId, tenantId, 10))
                .thenReturn(Future.succeededFuture(List.of(new DeviceConfig())));

        final var results = deviceConfigService.listAll(deviceId, tenantId, 10);

        verify(repositoryMock).listAll(deviceId, tenantId, 10);
        Assertions.assertTrue(results.succeeded());

    }

    @Test
    void listAll_failed() {
        when(repositoryMock.listAll(deviceId, tenantId, 10)).thenReturn(Future.failedFuture(new RuntimeException()));

        final var results = deviceConfigService.listAll(deviceId, tenantId, 10);

        verify(repositoryMock).listAll(deviceId, tenantId, 10);
        Assertions.assertTrue(results.failed());

    }

    @Test
    void updateDeviceAckTime() {
        doReturn(Future.succeededFuture()).when(repositoryMock).updateDeviceAckTime(any(), anyString());

        deviceConfigService.updateDeviceAckTime(new DeviceConfigAckResponse(), Instant.now().toString());

        verify(repositoryMock).updateDeviceAckTime(any(), anyString());

    }

    @Test
    void onDeviceConfigAck() {
        final var deviceConfigSpy = spy(this.deviceConfigService);
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "device-123",
                        "tenantId", "tenant-123",
                        "configVersion", "12"));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getConfigVersionIdKey()).thenReturn("configVersion");

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
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "",
                        "tenantId", "tenant-123"));
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8("{\"cause\": \"connected\"}"));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getContentTypeKey()).thenReturn("content-type");

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(communicationConfigMock).getContentTypeKey();
        verify(ackReplyConsumerMock).ack();
    }

    @Test
    public void onDeviceConnectEvent_SkipsEvent() {
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "device-123",
                        "tenantId", "tenant-123",
                        "content-type", "test"));

        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getContentTypeKey()).thenReturn("content-type");
        when(communicationConfigMock.getEmptyNotificationEventContentType()).thenReturn("skip-content");

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(communicationConfigMock).getContentTypeKey();
        verify(communicationConfigMock, times(2)).getEmptyNotificationEventContentType();

        verify(ackReplyConsumerMock).ack();
    }

    @Test
    public void onDeviceConnectEvent_SkipsContentTypeFalse() {
        when(pubsubMessageMock.getAttributesMap())
                .thenReturn(Map.of(
                        "deviceId", "device-123",
                        "tenantId", "tenant-123",
                        "content", "no-event"));

        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getContentTypeKey()).thenReturn("content");
        when(communicationConfigMock.getEmptyNotificationEventContentType()).thenReturn("event");

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(ackReplyConsumerMock).ack();
        verify(communicationConfigMock).getContentTypeKey();
        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(communicationConfigMock, times(2)).getEmptyNotificationEventContentType();
    }

    @Test
    public void onDeviceConnectEvent_PublishesDeviceConfig() throws Exception {
        try (MockedStatic<Vertx> vertxMockedStatic = mockStatic(Vertx.class)) {
            final String deviceId = "device-123";
            final String tenantId = "tenant-123";

            final String topic = "tenant-123-config";
            final String message = "{}";
            final Map<String, String> messageAttributes = Map.of(
                    "deviceId", deviceId,
                    "tenantId", tenantId,
                    "content", "event");
            final var deviceConfigEntity = new DeviceConfigEntity();
            final var deviceConfigEntityResponse = new DeviceConfig();

            when(pubsubMessageMock.getAttributesMap()).thenReturn(messageAttributes);
            when(repositoryMock.getDeviceLatestConfig(deviceId, tenantId))
                    .thenReturn(Future.succeededFuture(deviceConfigEntity));
            when(mapperMock.deviceConfigEntityToConfig(deviceConfigEntity)).thenReturn(deviceConfigEntityResponse);
            when(communicationConfigMock.getConfigTopicFormat()).thenReturn("%s-config");
            when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
            when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
            when(communicationConfigMock.getContentTypeKey()).thenReturn("content");
            when(communicationConfigMock.getEmptyNotificationEventContentType()).thenReturn("event");
            when(communicationConfigMock.getConfigAckTopicFormat()).thenReturn("%s.ack");
            doNothing().when(internalCommunicationMock).publish(topic, message, messageAttributes);
            doNothing().when(internalCommunicationMock).subscribe(anyString(), any());
            vertxMockedStatic.when(Vertx::currentContext).thenReturn(contextMock);

            deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

            verify(pubsubMessageMock).getAttributesMap();
            verify(repositoryMock).getDeviceLatestConfig(deviceId, tenantId);
            verify(mapperMock).deviceConfigEntityToConfig(deviceConfigEntity);
            verify(communicationConfigMock).getConfigTopicFormat();
            verify(communicationConfigMock).getContentTypeKey();
            verify(communicationConfigMock).getEmptyNotificationEventContentType();
            verify(communicationConfigMock).getDeviceIdKey();
            verify(communicationConfigMock).getConfigVersionIdKey();
            verify(communicationConfigMock).getTenantIdKey();
            verify(communicationConfigMock).getConfigAckTopicFormat();
            verify(contextMock).executeBlocking(any());

            verify(ackReplyConsumerMock).ack();
            vertxMockedStatic.verify(Vertx::currentContext);
            vertxMockedStatic.verifyNoMoreInteractions();
        }
    }

    @Test
    public void onDeviceConnectEvent_PublishesDeviceConfig_failed() {
        final String deviceId = "device-123";
        final String tenantId = "tenant-123";

        final Map<String, String> messageAttributes = Map.of(
                "deviceId", deviceId,
                "content", "event",
                "tenantId", tenantId);

        when(pubsubMessageMock.getAttributesMap()).thenReturn(messageAttributes);
        when(repositoryMock.getDeviceLatestConfig(deviceId, tenantId))
                .thenReturn(Future.failedFuture(new RuntimeException()));
        when(pubsubMessageMock.getData()).thenReturn(ByteString.copyFromUtf8("{\"cause\": \"connected\"}"));
        when(communicationConfigMock.getDeviceIdKey()).thenReturn("deviceId");
        when(communicationConfigMock.getTenantIdKey()).thenReturn("tenantId");
        when(communicationConfigMock.getContentTypeKey()).thenReturn("content");
        when(communicationConfigMock.getEmptyNotificationEventContentType()).thenReturn("event");
        when(communicationConfigMock.getConfigAckTopicFormat()).thenReturn("%s.ack");

        deviceConfigService.onDeviceConnectEvent(pubsubMessageMock, ackReplyConsumerMock);

        verify(pubsubMessageMock).getAttributesMap();
        verify(repositoryMock).getDeviceLatestConfig(deviceId, tenantId);
        verify(ackReplyConsumerMock).ack();

        verify(communicationConfigMock).getDeviceIdKey();
        verify(communicationConfigMock).getTenantIdKey();
        verify(communicationConfigMock).getContentTypeKey();
        verify(communicationConfigMock).getEmptyNotificationEventContentType();
    }
}
