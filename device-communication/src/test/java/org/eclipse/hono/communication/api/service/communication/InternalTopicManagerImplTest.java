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

package org.eclipse.hono.communication.api.service.communication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.eclipse.hono.client.pubsub.PubSubMessageHelper;
import org.eclipse.hono.communication.api.handler.ConfigTopicEventHandler;
import org.eclipse.hono.communication.api.handler.StateTopicEventHandler;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceRepository;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.eclipse.hono.notification.deviceregistry.LifecycleChange;
import org.eclipse.hono.notification.deviceregistry.TenantChangeNotification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import io.vertx.core.Context;
import io.vertx.core.Vertx;

class InternalTopicManagerImplTest {

    private final DeviceRepository deviceRepositoryMock;
    private final DeviceConfigMapper mapperMock;
    private final InternalMessagingConfig communicationConfigMock;
    private final InternalMessaging internalCommunicationMock;
    private final String tenantId = "tenant_ID";
    private final PubsubMessage pubsubMessageMock;
    private final Context contextMock;
    private final AckReplyConsumer ackReplyConsumerMock;
    private final ByteString byteStringMock;
    private final ConfigTopicEventHandler configTopicEventHandler;
    private final StateTopicEventHandler stateTopicEventHandler;
    private final Vertx vertxMock;
    private final InternalTopicManagerImpl internalTopicManager;

    InternalTopicManagerImplTest() {
        this.deviceRepositoryMock = mock(DeviceRepository.class);
        this.mapperMock = mock(DeviceConfigMapper.class);
        this.communicationConfigMock = mock(InternalMessagingConfig.class);
        this.internalCommunicationMock = mock(InternalMessaging.class);
        this.pubsubMessageMock = mock(PubsubMessage.class);
        this.ackReplyConsumerMock = mock(AckReplyConsumer.class);
        this.contextMock = mock(Context.class);
        this.byteStringMock = mock(ByteString.class);
        this.configTopicEventHandler = mock(ConfigTopicEventHandler.class);
        this.stateTopicEventHandler = mock(StateTopicEventHandler.class);
        this.vertxMock = mock(Vertx.class);
        this.internalTopicManager = new InternalTopicManagerImpl(deviceRepositoryMock, configTopicEventHandler,
                stateTopicEventHandler, internalCommunicationMock, communicationConfigMock, vertxMock);

    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(deviceRepositoryMock,
                mapperMock,
                communicationConfigMock,
                internalCommunicationMock,
                pubsubMessageMock,
                ackReplyConsumerMock,
                byteStringMock,
                configTopicEventHandler,
                stateTopicEventHandler,
                contextMock,
                vertxMock);
    }

    @Test
    public void testOnTenantChanges_ChangeIsUpdate() throws IOException {
        final TenantChangeNotification notification = new TenantChangeNotification(LifecycleChange.UPDATE, tenantId,
                Instant.now(), false, false);
        when(pubsubMessageMock.getData()).thenReturn(byteStringMock);
        when(byteStringMock.toStringUtf8()).thenReturn(new ObjectMapper().writeValueAsString(notification));

        internalTopicManager.onTenantChanges(pubsubMessageMock, ackReplyConsumerMock);

        verify(ackReplyConsumerMock).ack();
        verify(pubsubMessageMock).getData();
        verify(byteStringMock).toStringUtf8();
    }

    @Test
    public void testOnTenantChanges_projectIdIsEmpty() throws IOException {
        final TenantChangeNotification notification = new TenantChangeNotification(LifecycleChange.CREATE, "",
                Instant.now(), false, false);
        when(pubsubMessageMock.getData()).thenReturn(byteStringMock);
        when(byteStringMock.toStringUtf8()).thenReturn(new ObjectMapper().writeValueAsString(notification));

        internalTopicManager.onTenantChanges(pubsubMessageMock, ackReplyConsumerMock);

        verify(ackReplyConsumerMock).ack();
        verify(pubsubMessageMock).getData();
        verify(byteStringMock).toStringUtf8();

    }

    @Test
    public void testOnTenantChanges_success() throws IOException {
        try (MockedStatic<PubSubMessageHelper> mockedPubSubMessageHelper = mockStatic(PubSubMessageHelper.class)) {
            final var credMock = mock(FixedCredentialsProvider.class);
            mockedPubSubMessageHelper.when(PubSubMessageHelper::getCredentialsProvider)
                    .thenReturn(Optional.of(credMock));

            final TenantChangeNotification notification = new TenantChangeNotification(LifecycleChange.CREATE, tenantId,
                    Instant.now(), false, false);
            when(pubsubMessageMock.getData()).thenReturn(byteStringMock);
            when(byteStringMock.toStringUtf8()).thenReturn(new ObjectMapper().writeValueAsString(notification));
            when(communicationConfigMock.getEventTopicFormat()).thenReturn("%s.event");
            when(communicationConfigMock.getStateTopicFormat()).thenReturn("%s.event.state");

            internalTopicManager.onTenantChanges(pubsubMessageMock, ackReplyConsumerMock);

            verify(ackReplyConsumerMock).ack();
            verify(pubsubMessageMock).getData();
            verify(byteStringMock).toStringUtf8();
            verify(communicationConfigMock).getEventTopicFormat();
            verify(communicationConfigMock).getStateTopicFormat();
            verify(communicationConfigMock).getProjectId();
            verify(internalCommunicationMock, times(2)).subscribe(anyString(), any());
        }
    }
}
